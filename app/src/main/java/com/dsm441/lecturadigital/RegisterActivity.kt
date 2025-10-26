package com.dsm441.lecturadigital

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RegisterActivity : AppCompatActivity() {

    //Declarar Auth y Firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    //Todas las vistas
    private lateinit var etNombreRegister: TextInputEditText
    private lateinit var etEmailRegister: TextInputEditText
    private lateinit var etPasswordRegister: TextInputEditText
    private lateinit var etConfirmPasswordRegister: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var tvGoToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Inicializar Firebase y Firestore
        auth = Firebase.auth
        db = Firebase.firestore

        //Inicializar vistas
        etNombreRegister = findViewById(R.id.etNombreRegister)
        etEmailRegister = findViewById(R.id.etEmailRegister)
        etPasswordRegister = findViewById(R.id.etPasswordRegister)
        etConfirmPasswordRegister = findViewById(R.id.etConfirmPasswordRegister)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)


        //configurar el onClickListener del botón de registro
        btnRegister.setOnClickListener {
            singUpUser()
        }

        //configurar el onClickListener del texto de registro
        tvGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    //Funcion para registrar un nuevo usuario cuando el btnRegister es clickeado
    private fun singUpUser () {
        //obtenemos los datos de cada campo y lo guardamos en una variable
        val nombre = etNombreRegister.text.toString().trim()
        val email = etEmailRegister.text.toString().trim()
        val password = etPasswordRegister.text.toString().trim()
        val confirmPassword = etConfirmPasswordRegister.text.toString().trim()

        //validamos que los campos no esten vacios
        if (nombre.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Ningún campo puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        //validar que una contraseña sea igual a la otra
        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        //registrar el usuario en Firebase y firestore 
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Autenticación exitosa, ahora guardamos en Firestore

                    // Obtenemos el ID del usuario recién creado
                    val userId = auth.currentUser?.uid

                    if (userId != null) {
                        // Creamos un mapa con los datos del usuario
                        val user = hashMapOf(
                            "nombre" to nombre,
                            "email" to email,
                            "progresoLectura" to hashMapOf<String, Any>(), // Mapa vacío para progreso
                            "preferencias" to "", // Puedes poner valores default
                            "favoritos" to listOf<String>() // Lista vacía para favoritos
                        )

                        // Guardamos el documento en Firestore
                        db.collection("Usuarios").document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                // ÉXITO: Auth y Firestore creados
                                Toast.makeText(this, "Registro Exitoso", Toast.LENGTH_SHORT).show()

                                // Navegar a Login Activity
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                // Fallo solo en Firestore
                                Toast.makeText(this, "Registro fallido (DB): ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "Error al obtener ID de usuario.", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    // Fallo en el registro de Auth
                    Toast.makeText(
                        this,
                        "El registro falló (Auth): ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}