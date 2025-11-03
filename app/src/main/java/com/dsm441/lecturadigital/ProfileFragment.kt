package com.dsm441.lecturadigital

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class ProfileFragment : Fragment() {

    // Declarar Auth y Firestore (estilo guía)
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    // Declarar Vistas
    private lateinit var btnLogout: Button
    private lateinit var tvProfileUserName: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Inicializar Vistas
        btnLogout = view.findViewById(R.id.btnLogout)
        tvProfileUserName = view.findViewById(R.id.tvProfileUserName)

        // Cargar los datos del perfil del usuario
        auth.currentUser?.let { user ->
            loadUserProfile(user.uid)
        }

        // Configurar el listener para el botón de logout
        btnLogout.setOnClickListener {
            signOut()
        }

        return view
    }

    /**
     * Obtiene los datos del usuario desde Firestore usando su UID
     */
    private fun loadUserProfile(userId: String) {
        db.collection("Usuarios").document(userId)
            .get() // Pide obtener el documento una sola vez
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombre = document.getString("nombre")
                    tvProfileUserName.text = nombre ?: "Nombre no encontrado"
                } else {
                    Log.d("ProfileFragment", "No se encontró el documento del usuario")
                    tvProfileUserName.text = "Usuario sin datos"
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileFragment", "Error al obtener datos", exception)
                tvProfileUserName.text = "Error al cargar"
            }
    }

    /**
     * Cierra la sesión del usuario y lo redirige al Login
     */
    private fun signOut() {
        auth.signOut()

        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }
}