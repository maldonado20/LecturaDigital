package com.dsm441.lecturadigital

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

// Esta es la actividad principal después del login.
// Controla la navegación con la barra inferior y decide qué pantalla mostrar.
class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = Firebase.auth

        bottomNavView = findViewById(R.id.bottom_navigation)

        // Esto se ejecuta solo la primera vez que se crea la actividad.
        if (savedInstanceState == null) {
            val isLoggedIn = auth.currentUser != null || intent.getBooleanExtra("IS_LOGGED_IN", false)
            if (isLoggedIn) {
                replaceFragment(LoggedInHomeFragment())
                bottomNavView.visibility = View.VISIBLE
            } else {
                replaceFragment(HomeFragment())
                bottomNavView.visibility = View.GONE
            }
        }

        setupBottomNavigation()
    }

    // Configura lo que pasa cuando tocas un icono de la barra de navegacion
    private fun setupBottomNavigation() {
        bottomNavView.setOnItemSelectedListener { item ->
            val isLoggedIn = auth.currentUser != null
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                R.id.nav_home -> {
                    selectedFragment = if (isLoggedIn) LoggedInHomeFragment() else HomeFragment()
                    bottomNavView.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
                }
                R.id.nav_profile -> {
                    if (isLoggedIn) selectedFragment = ProfileFragment() else showLoginPrompt()
                }
                R.id.nav_categories -> {
                    if (isLoggedIn) {

                        Toast.makeText(this, "Categorías (en desarrollo)", Toast.LENGTH_SHORT).show()
                    } else {
                        showLoginPrompt()
                    }
                }
                R.id.nav_favorites -> {
                    if (isLoggedIn) selectedFragment = FavoritesFragment() else showLoginPrompt()
                }
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment)
            }
            true
        }
    }

    // Esta función cambia la pantalla (el fragmento) que se muestra en el contenedor.
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Si el usuario no está logueado, le pedimos que inicie sesión.
    private fun showLoginPrompt() {
        Toast.makeText(this, "Inicia sesión para acceder", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}