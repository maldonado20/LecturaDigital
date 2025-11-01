package com.dsm441.lecturadigital

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar // Asegúrate de importar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsm441.lecturadigital.data.Book
import com.dsm441.lecturadigital.network.RetrofitClient
import com.dsm441.lecturadigital.ui.BookAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    // Declaramos las vistas
    private lateinit var rvLibros: RecyclerView
    private lateinit var adaptadorLibros: BookAdapter
    private lateinit var pbCargando: ProgressBar

    private val listaDeLibros = mutableListOf<Book>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inicializar vistas
        rvLibros = view.findViewById(R.id.rvFeaturedBooks)
        pbCargando = view.findViewById(R.id.pbCargandoLibros)

        configurarRecyclerView()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Al crear la vista, buscar solo nuestros libros destacados
        buscarLibrosDestacados()
    }

    private fun configurarRecyclerView() {
        adaptadorLibros = BookAdapter(listaDeLibros)
        rvLibros.adapter = adaptadorLibros
        rvLibros.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    /**
     * Función que llama a la API para libros específicos
     */
    private fun buscarLibrosDestacados() {
        pbCargando.visibility = View.VISIBLE
        rvLibros.visibility = View.GONE

        adaptadorLibros.updateBooks(emptyList())

        // IDs de libros que sabemos que tienen PDF
        val idsCurados = "1513,345,84"

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Hacemos la llamada a la API con los IDs
                val response = RetrofitClient.apiService.getBooksByIds(idsCurados)

                if (response.isSuccessful) {
                    val todosLosLibros = response.body()?.results

                    if (todosLosLibros != null && todosLosLibros.isNotEmpty()) {

                        // ¡Re-activamos el filtro!
                        // Solo para estar 100% seguros de que tienen PDF
                        val librosConPdf = todosLosLibros.filter { libro ->
                            !libro.formats.applicationPdf.isNullOrEmpty()
                        }

                        if (librosConPdf.isNotEmpty()) {
                            adaptadorLibros.updateBooks(librosConPdf)
                        } else {
                            mostrarError("No se encontraron libros con PDF")
                        }
                    } else {
                        mostrarError("No se encontraron libros")
                    }
                } else {
                    mostrarError("Error de servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                mostrarError("Error de red. ¿Permiso de INTERNET?")
                Log.e("HomeFragment", "Error al buscar libros", e)
            } finally {
                pbCargando.visibility = View.GONE
                rvLibros.visibility = View.VISIBLE
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
    }
}