package com.dsm441.lecturadigital

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button // <-- AÑADIDO
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsm441.lecturadigital.data.LibroFirestore
import com.dsm441.lecturadigital.ui.BookAdapter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoggedInHomeFragment : Fragment() {

    private lateinit var rvLibros: RecyclerView
    private lateinit var adaptadorLibros: BookAdapter
    private lateinit var pbCargando: ProgressBar

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Reutilizamos el layout del invitado
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        rvLibros = view.findViewById(R.id.rvFeaturedBooks)
        pbCargando = view.findViewById(R.id.pbCargandoLibros)


        // Ocultamos el botón de Login
        val btnGoToLogin: Button = view.findViewById(R.id.btnGoToLogin_Home)
        btnGoToLogin.visibility = View.GONE

        configurarRecyclerView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buscarLibrosDeFirestore()
    }

    private fun configurarRecyclerView() {
        adaptadorLibros = BookAdapter(emptyList(), isClickable = true)
        rvLibros.adapter = adaptadorLibros
        rvLibros.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    // (Las funciones buscarLibrosDeFirestore() y mostrarError() son
    // idénticas a las de HomeFragment.kt)
    private fun buscarLibrosDeFirestore() {
        pbCargando.visibility = View.VISIBLE
        rvLibros.visibility = View.GONE
        lifecycleScope.launch {
            try {
                val snapshot = db.collection("libros").get().await()
                val listaLibros = snapshot.toObjects<LibroFirestore>()
                if (listaLibros.isNotEmpty()) {
                    adaptadorLibros.updateBooks(listaLibros)
                } else {
                    mostrarError("No se encontraron libros en Firestore")
                }
            } catch (e: Exception) {
                mostrarError("Error de red (Firestore): ${e.message}")
                Log.e("LoggedInHomeFragment", "Error al buscar en Firestore", e)
            } finally {
                pbCargando.visibility = View.GONE
                rvLibros.visibility = View.VISIBLE
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
    }
}