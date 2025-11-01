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

class LoginActivity : AppCompatActivity() {

    // declaramos FirebaseAuth
    private lateinit var auth: FirebaseAuth

    // declaramos la vista
    private lateinit var etEmailLogin: TextInputEditText
    private lateinit var etPasswordLogin: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoToRegister: Button
    private lateinit var tvForgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        //Iniciazalizar vistas
        etEmailLogin = findViewById(R.id.etEmailLogin)
        etPasswordLogin = findViewById(R.id.etPasswordLogin)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoToRegister = findViewById(R.id.btnRegister)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        // Configurar el Click Listener para el botón de Login
        btnLogin.setOnClickListener {
            loginUser()
        }

        // para que el boton vaya a RegisterActivity
        btnGoToRegister.setOnClickListener {
            val intent_register = Intent(this, RegisterActivity::class.java)
            startActivity(intent_register)
        }

        //El click listener para el boton de recuperar contraseña
        tvForgotPassword.setOnClickListener {
            sendPasswordReset()
        }
    }

    //Funcion para enviar el correo de recuperacion de contra
    private fun sendPasswordReset(){
        val email = etEmailLogin.text.toString().trim()


        if (email.isBlank()) {
            Toast.makeText(this, "Introduce tu email para restablecer la contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Correo electrónico de recuperación de contraseña enviado, por favor revise su bandeja de entrada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


     //Funcion para iniciar sesión con Firebase Auth.
    private fun loginUser() {

        val email = etEmailLogin.text.toString().trim()
        val pass = etPasswordLogin.text.toString().trim()

        //Validar que los campos no esten vacíos
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Ningún campo puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        // Iniciar sesión en Firebase
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login exitoso damos el mensaje breve
                    Toast.makeText(this, "Inicio de Sesión Exitoso", Toast.LENGTH_SHORT).show()

                    // Navegar a la pantalla principal asumiento que el login salio bien
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // destruimos la activida para que el usiario no pueda volver atras
                } else {
                    // Si el login falla damos un pequeño mensaje
                    Toast.makeText(
                        this,
                        "El correo electrónico o contraseña incorrectos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}