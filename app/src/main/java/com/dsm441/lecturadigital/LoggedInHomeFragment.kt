package com.dsm441.lecturadigital

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import coil.load
import com.dsm441.lecturadigital.data.LibroFirestore
import com.dsm441.lecturadigital.network.RetrofitClient
import kotlinx.coroutines.launch

class LoggedInHomeFragment : Fragment() {

   private var LibroDestacado: LibroFirestore? = null

    //las vistas
    private lateinit var llFeaturedBook: LinearLayout
    private lateinit var tvFeaturedBookTitle: TextView
    private lateinit var ivFeaturedBookCover: ImageView
    private lateinit var tvWelcomeMessage: TextView
    private lateinit var pbCargando: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //recibir el libro enviado de HomeActivity
        arguments?.let {
            LibroDestacado = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                it.getParcelable("FEATURED_BOOK", LibroFirestore::class.java)
            }else {
                @Suppress("DEPRECATION")
                it.getParcelable("FEATURED_BOOK")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_logged_in_home, container, false)

        llFeaturedBook = view.findViewById(R.id.llFeaturedBook)
        ivFeaturedBookCover = view.findViewById(R.id.ivFeaturedBookCover)
        tvFeaturedBookTitle = view.findViewById(R.id.tvFeaturedBookTitle)
        tvWelcomeMessage = view.findViewById(R.id.tvWelcomeMessage)
        pbCargando = view.findViewById(R.id.pbCargandoLogin)

        LibroDestacado?.let { libro ->
            mostrarLibrosDestacados(libro)
        } ?: run {
            pbCargando.visibility = View.GONE
            tvWelcomeMessage.visibility = View.VISIBLE
        }
        return inflater.inflate(R.layout.fragment_logged_in_home, container, false)
    }

    private fun mostrarLibrosDestacados(libro: LibroFirestore){
        llFeaturedBook.visibility = View.VISIBLE
        tvFeaturedBookTitle.text = libro.titulo

        tvFeaturedBookTitle.text = libro.titulo

        lifecycleScope.launch {
            try {
                val query = "${libro.titulo} ${libro.autor}"
                val response = RetrofitClient.apiService.searchBook(query)

                if (response.isSuccessful) {
                    val imageUrl = response.body()?.items?.firstOrNull()?.volumeInfo?.imageLinks?.thumbnail

                    if (imageUrl != null) {
                        ivFeaturedBookCover.load(imageUrl.replaceFirst("http://", "https://")) {
                            crossfade(true)
                            error(R.drawable.logo1)
                        }
                    } else {
                        ivFeaturedBookCover.setImageResource(R.drawable.logo1)
                    }
                } else {
                    ivFeaturedBookCover.setImageResource(R.drawable.logo1)
                }
            } catch (e: Exception) {
                Log.e("LoggedInHomeFragment", "Error al cargar portada", e)
                ivFeaturedBookCover.setImageResource(R.drawable.logo1)
            } finally {
                // Ocultar ProgressBar y mostrar el libro
                pbCargando.visibility = View.GONE
                llFeaturedBook.visibility = View.VISIBLE
            }
        }
    }
    }
