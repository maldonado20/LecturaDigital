package com.dsm441.lecturadigital

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dsm441.lecturadigital.data.LibroFirestore
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

    // Variable para guardar el libro que recibimos de DetailActivity
    private var bookToShowAfterLogin: LibroFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        // Revisamos si DetailActivity nos envió un libro
        bookToShowAfterLogin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("BOOK_TO_SHOW_AFTER_LOGIN", LibroFirestore::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("BOOK_TO_SHOW_AFTER_LOGIN")
        }

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
                    Toast.makeText(this, "Correo electrónico de recuperación de contraseña enviado...", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    //Funcion para iniciar sesión con Firebase Auth.
    private fun loginUser() {

        val email = etEmailLogin.text.toString().trim()
        val pass = etPasswordLogin.text.toString().trim()

        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Ningún campo puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de Sesión Exitoso", Toast.LENGTH_SHORT).show()

                    // Navegar a la pantalla principal
                    val intent = Intent(this, HomeActivity::class.java)

                    intent.putExtra("IS_LOGGED_IN", true)

                    if (bookToShowAfterLogin != null) {
                        intent.putExtra("BOOK_TO_SHOW", bookToShowAfterLogin)
                    }

                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "El correo electrónico o contraseña incorrectos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}