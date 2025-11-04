package com.dsm441.lecturadigital

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsm441.lecturadigital.data.LibroFirestore
import com.dsm441.lecturadigital.ui.BookAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects

class FavoritesFragment : Fragment() {

    // Vistas
    private lateinit var rvFavorites: RecyclerView
    private lateinit var adaptadorLibros: BookAdapter
    private lateinit var pbCargando: ProgressBar
    private lateinit var tvNoFavorites: TextView

    // Firebase
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var favoritesListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        // Inicializar Vistas
        rvFavorites = view.findViewById(R.id.rvFavorites)
        pbCargando = view.findViewById(R.id.pbCargandoFavorites)
        tvNoFavorites = view.findViewById(R.id.tvNoFavorites)

        configurarRecyclerView()
        return view
    }

    override fun onResume() {
        super.onResume()
        escucharCambiosEnFavoritos()
    }

    override fun onPause() {
        super.onPause()
        favoritesListener?.remove()
    }

    private fun configurarRecyclerView() {
        adaptadorLibros = BookAdapter(emptyList(), isClickable = true)
        rvFavorites.adapter = adaptadorLibros
        rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun escucharCambiosEnFavoritos() {
        pbCargando.visibility = View.VISIBLE
        rvFavorites.visibility = View.GONE
        tvNoFavorites.visibility = View.GONE

        val uid = auth.currentUser?.uid
        if (uid == null) {
            mostrarError("No se pudo identificar al usuario")
            return
        }

        val userDocRef = db.collection("Usuarios").document(uid)
        favoritesListener = userDocRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                mostrarError("Error al cargar favoritos.")
                return@addSnapshotListener
            }

            val listaDeIds = snapshot?.get("favoritos") as? List<String>

            if (listaDeIds.isNullOrEmpty()) {
                adaptadorLibros.updateBooks(emptyList())
                pbCargando.visibility = View.GONE
                tvNoFavorites.visibility = View.VISIBLE
                rvFavorites.visibility = View.GONE
            } else {
                tvNoFavorites.visibility = View.GONE
                buscarDetallesDeLibros(listaDeIds)
            }
        }
    }

    private fun buscarDetallesDeLibros(ids: List<String>) {
        db.collection("libros").whereIn(FieldPath.documentId(), ids).get()
            .addOnSuccessListener { bookSnapshot ->
                val listaLibros = bookSnapshot.toObjects<LibroFirestore>()
                adaptadorLibros.updateBooks(listaLibros)
                pbCargando.visibility = View.GONE
                rvFavorites.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                mostrarError("Error al buscar detalles de libros.")
            }
    }

    private fun mostrarError(mensaje: String) {
        if (isAdded) {
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
        }
    }
}