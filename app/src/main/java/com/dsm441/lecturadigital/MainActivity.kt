package com.dsm441.lecturadigital

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {


    //declaramos las variables firebase y las vistas
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var btnLogout: Button
    private lateinit var tvWelcomeUser: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = Firebase.auth
        db = Firebase.firestore

        //inicializar vistas
        btnLogout = findViewById(R.id.btnLogout)
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser)


        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            db.collection("Usuarios").document(uid).get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombre = document.getString("nombre")
                    tvWelcomeUser.text = "Bienvenido, $nombre"
                } else {
                    tvWelcomeUser.text = "Bienvenido, ${currentUser.email}"
                }
            }.addOnFailureListener {
                tvWelcomeUser.text = "Bienvenido, ${currentUser.email}"
            }
        }else {
            goToLoginActivity()
        }

        //configurar el Click listener de Logout
        btnLogout.setOnClickListener {
            signOut()
        }
    }

    //funcion para salir de la sesion
    private fun signOut(){
        auth.signOut()
        goToLoginActivity()
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() //Destruimos la actividad para que el usuario no pueda volver atras
    }

}