package com.dsm441.lecturadigital

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Evita
        if (savedInstanceState == null) {
            // Cargamos HomeFragment por defecto en el contenedor
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        // (Más adelante aquí pondremos la lógica para manejar
        // los clics de la barra de navegación)
    }
}