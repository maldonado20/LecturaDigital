package com.dsm441.lecturadigital


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.dsm441.lecturadigital.data.LibroFirestore
import com.dsm441.lecturadigital.network.RetrofitClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch


class DetailActivity : AppCompatActivity() {

   private lateinit var ivPortada: ImageView
   private lateinit var tvTitulo: TextView
   private lateinit var tvAutor: TextView
   private lateinit var tvDescripcion: TextView
   private lateinit var btnLeer: Button
   private lateinit var auth: FirebaseAuth

   private var libroFirestore: LibroFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail_activiy)

        auth = Firebase.auth

        //ids de los elementos en xml
        ivPortada = findViewById(R.id.PortadaDetalle)
        tvTitulo = findViewById(R.id.TituloDetalle)
        tvAutor = findViewById(R.id.AutorDetalle)
        tvDescripcion = findViewById(R.id.DescripcionDetalle)
        btnLeer = findViewById(R.id.btnLeer)

        //Recibimos el objeto LibroFirestore
        libroFirestore = obtenerLibroDelIntent()

        if(libroFirestore != null) {
            mostrarDatos(libroFirestore!!)
        }else {
            Toast.makeText(this, "Error al cargar el libro", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnLeer.setOnClickListener {
            if (auth.currentUser != null) {
                //Usuario logeado
                if(libroFirestore == null){
                    Toast.makeText(this, "Error al cargar el libro", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                //obtener la url del pdf
                val pdfUrl = libroFirestore!!.pdfUrl

                if(pdfUrl.isEmpty()){
                    Toast.makeText(this, "No se encontro el PDF", Toast.LENGTH_SHORT).show()
                }else {
                    val intent = Intent(this, PdfViewerActivity::class.java)
                    intent.putExtra("PDF_URL", pdfUrl)
                    startActivity(intent)
                }
            }else {
                //Usuario no logeado
                Toast.makeText(this, "Debes iniciar sesión para leer", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                if(libroFirestore != null) {
                    intent.putExtra("BOOK_TO_SHOW_AFTER_lOGIN", libroFirestore)
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun obtenerLibroDelIntent(): LibroFirestore? {
        val llave = "BOOK_FIRESTORE"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(llave, LibroFirestore::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(llave)
        }
    }

    //Rellenar las vistas con los datos del libro
    private fun mostrarDatos(libro: LibroFirestore){
        tvTitulo.text = libro.titulo
        tvAutor.text = libro.autor
        tvDescripcion.text = "Cargando descripción..."

        lifecycleScope.launch {
            try {
                val query = "${libro.titulo} ${libro.autor}"
                val response = RetrofitClient.apiService.searchBook(query)

                if (response.isSuccessful) {
                    val volumeInfo = response.body()?.items?.firstOrNull()?.volumeInfo

                    // Cargar Portada
                    val imageUrl = volumeInfo?.imageLinks?.thumbnail
                    if (imageUrl != null) {
                        ivPortada.load(imageUrl.replaceFirst("http://", "https://")) {
                            crossfade(true)
                            error(R.drawable.logo1)
                        }
                    } else {
                        ivPortada.setImageResource(R.drawable.logo1)
                    }

                    // Cargar Descripción
                    val description = volumeInfo?.description
                    if (!description.isNullOrEmpty()) {
                        tvDescripcion.text = description
                    } else {
                        tvDescripcion.text = "Descripción no disponible."
                    }

                } else {
                    ivPortada.setImageResource(R.drawable.logo1)
                    tvDescripcion.text = "Descripción no disponible."
                }
            } catch (e: Exception) {
                Log.e("DetailActivity", "Error al cargar datos de API", e)
            }
        }
    }
}