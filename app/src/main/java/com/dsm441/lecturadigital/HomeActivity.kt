package com.dsm441.lecturadigital

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dsm441.lecturadigital.data.LibroFirestore
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val homeFragment: HomeFragment by lazy { HomeFragment() }
    private val loggedInHomeFragment: LoggedInHomeFragment by lazy { LoggedInHomeFragment() }
    private val profileFragment: ProfileFragment by lazy { ProfileFragment() }

    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = Firebase.auth

        if (savedInstanceState == null) {
            val isLoggedIn = auth.currentUser != null
            val initialFragment: Fragment

            val isLoggedInIntent = intent.getBooleanExtra("IS_LOGGED_IN", false)

            val bookToShow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("BOOK_TO_SHOW", LibroFirestore::class.java)
            } else {
                @Suppress("DEPRECATION")
                // FIX: Cambiado 'BookItem' por 'Book'
                intent.getParcelableExtra<LibroFirestore>("BOOK_TO_SHOW")
            }

            // (Ver "¡Ojo con este bug!" abajo)

            if (isLoggedIn || isLoggedInIntent) { // <-- Lógica de login mejorada
                if (bookToShow != null) {
                    loggedInHomeFragment.arguments = Bundle().apply {
                        // Esta línea ahora funciona
                        putParcelable("FEATURED_BOOK", bookToShow)
                    }
                }
                initialFragment = loggedInHomeFragment
            } else {
                initialFragment = homeFragment
            }

            activeFragment = initialFragment

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, initialFragment, "home")
                .commit()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavView.setOnItemSelectedListener { item ->
            val isLoggedIn = auth.currentUser != null
            var selectedFragmentTag = ""
            var targetFragment: Fragment? = null

            when (item.itemId) {
                R.id.nav_home -> {
                    targetFragment = if (isLoggedIn) loggedInHomeFragment else homeFragment
                    selectedFragmentTag = "home"
                }
                R.id.nav_profile -> {
                    if (!isLoggedIn) {
                        showLoginPrompt()
                    } else {
                        targetFragment = profileFragment
                        selectedFragmentTag = "profile"
                    }
                }
                R.id.nav_categories -> {
                    if (!isLoggedIn) showLoginPrompt()
                    else Toast.makeText(this, "Categorías (requiere login)", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_library -> {
                    if (!isLoggedIn) showLoginPrompt()
                    else Toast.makeText(this, "Mi Biblioteca (requiere login)", Toast.LENGTH_SHORT).show()
                }
            }

            if (targetFragment != null && targetFragment != activeFragment) {
                val transaction = supportFragmentManager.beginTransaction()

                if (supportFragmentManager.findFragmentByTag(selectedFragmentTag) != null) {
                    transaction.hide(activeFragment).show(targetFragment)
                } else {
                    transaction.add(R.id.fragment_container, targetFragment, selectedFragmentTag).hide(activeFragment)
                }

                transaction.commit()
                activeFragment = targetFragment
            }
            true
        }
    }

    private fun showLoginPrompt() {
        Toast.makeText(this, "Inicia sesión para acceder", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}