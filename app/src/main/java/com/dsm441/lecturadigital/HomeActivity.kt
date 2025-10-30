package com.dsm441.lecturadigital

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.dsm441.lecturadigital.HomeFragment
import com.dsm441.lecturadigital.LoggedInHomeFragment


class HomeActivity : AppCompatActivity() {

    // --- FIX: Declarar 'auth' como miembro de la clase ---
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // --- FIX: Inicializar 'auth' aquí ---
        auth = Firebase.auth

        if (savedInstanceState == null) {
            // Verificamos si el Intent trae la señal de login O si ya hay un usuario logueado
            val isLoggedInIntent = intent.getBooleanExtra("IS_LOGGED_IN", false)
            val isLoggedInAuth = auth.currentUser != null // Comprobamos directamente

            val initialFragment: Fragment = if (isLoggedInIntent || isLoggedInAuth) {
                // Si viene del login O ya había sesión, cargamos el fragmento logueado
                LoggedInHomeFragment()
            } else {
                // Si no, cargamos el original
                HomeFragment()
            }

            // Cargamos el fragmento decidido
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, initialFragment)
                .commit()
        }

        // Llamamos a la configuración de la BottomNavigationView
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        // No es necesario declarar 'auth' aquí de nuevo
        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            // Usamos la variable 'auth' de la clase
            val isLoggedIn = auth.currentUser != null

            when (item.itemId) {
                R.id.nav_home -> {
                    selectedFragment = if (isLoggedIn) LoggedInHomeFragment() else HomeFragment()
                }
                R.id.nav_categories -> {
                    if (!isLoggedIn) showLoginPrompt()
                    // else selectedFragment = CategoriesFragment() // <-- FIX: Comentado hasta crear CategoriesFragment
                    else Toast.makeText(this, "Categorías (requiere login)", Toast.LENGTH_SHORT).show() // Placeholder
                }
                R.id.nav_library -> {
                    if (!isLoggedIn) showLoginPrompt()
                    // else selectedFragment = LibraryFragment() // <-- FIX: Comentado hasta crear LibraryFragment
                    else Toast.makeText(this, "Mi Biblioteca (requiere login)", Toast.LENGTH_SHORT).show() // Placeholder
                }
                R.id.nav_profile -> {
                    if (!isLoggedIn) showLoginPrompt()
                    // else selectedFragment = ProfileFragment() // <-- FIX: Comentado hasta crear ProfileFragment
                    else Toast.makeText(this, "Perfil (requiere login)", Toast.LENGTH_SHORT).show() // Placeholder
                }
            }

            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()
            }
            true
        }
    }

    // Función auxiliar para pedir login (Ejemplo)
    private fun showLoginPrompt() {
        Toast.makeText(this, "Inicia sesión para acceder a esta sección", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        // Opcional: Podrías querer evitar añadir Login al historial si ya estás en Home
        // intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivity(intent)
    }
}