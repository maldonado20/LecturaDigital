package com.dsm441.lecturadigital

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.View // <-- Importar View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope // <-- Importar Corutinas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class PdfViewerActivity : AppCompatActivity() {

    private val TAG = "PDF_VIEWER_DEBUG"

    // Vistas
    private lateinit var ivPdfPage: ImageView
    private lateinit var btnPreviousPage: Button
    private lateinit var btnNextPage: Button
    private lateinit var tvPageNumber: TextView
    private lateinit var pbCargando: ProgressBar


    // Variables del PDF
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var parcelFileDescriptor: ParcelFileDescriptor? = null
    private var currentPageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        // Inicializar vistas
        ivPdfPage = findViewById(R.id.ivPdfPage)
        btnPreviousPage = findViewById(R.id.btnPreviousPage)
        btnNextPage = findViewById(R.id.btnNextPage)
        tvPageNumber = findViewById(R.id.tvPageNumber)
        pbCargando = findViewById(R.id.pbCargando)



        // Recibir la URL del PDF
        val pdfUrl = intent.getStringExtra("PDF_URL")
        Log.d(TAG, "Intent recibido. PDF URL: $pdfUrl")

        if (pdfUrl.isNullOrEmpty()) {
            mostrarError("Error: URL del PDF no válida")
            finish()
            return
        }

        // Configurar botones
        btnPreviousPage.setOnClickListener {
            mostrarPagina(currentPageIndex - 1)
        }
        btnNextPage.setOnClickListener {
            mostrarPagina(currentPageIndex + 1)
        }

        // Iniciar la descarga y el renderizado
        cargarPdfDesdeUrl(pdfUrl)
    }

    private fun cargarPdfDesdeUrl(pdfUrl: String) {
        mostrarCargando(true)

        // Iniciar Corutina para descargar el archivo en un hilo de fondo
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Descargar el archivo
                val archivoDescargado = descargarArchivo(pdfUrl)
                Log.d(TAG, "Archivo descargado en: ${archivoDescargado.path}")

                // 2. Abrir el PDF (en el hilo principal)
                withContext(Dispatchers.Main) {
                    abrirPdfRenderer(archivoDescargado)
                    mostrarPagina(0)
                    mostrarCargando(false)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error al descargar o abrir PDF", e)
                withContext(Dispatchers.Main) {
                    mostrarError("Error al cargar PDF: ${e.message}")
                    mostrarCargando(false)
                    finish()
                }
            }
        }
    }

    /**
     * Descarga el archivo desde una URL y lo guarda en el caché
     */
    @Throws(IOException::class)
    private fun descargarArchivo(pdfUrl: String): File {
        val url = URL(pdfUrl)
        val conexion = url.openConnection() as HttpURLConnection
        conexion.connect()

        if (conexion.responseCode != HttpURLConnection.HTTP_OK) {
            throw IOException("Error de servidor: ${conexion.responseCode}")
        }

        val archivo = File(cacheDir, "temp.pdf")
        val outputStream = FileOutputStream(archivo)
        val inputStream: InputStream = conexion.inputStream

        inputStream.copyTo(outputStream) // Copia el archivo

        outputStream.close()
        inputStream.close()
        conexion.disconnect()

        return archivo
    }

    /**
     * Abre el PdfRenderer desde un archivo local
     */
    @Throws(IOException::class)
    private fun abrirPdfRenderer(archivo: File) {
        parcelFileDescriptor = ParcelFileDescriptor.open(archivo, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(parcelFileDescriptor!!)
        Log.d(TAG, "PdfRenderer abierto. Total páginas: ${pdfRenderer?.pageCount}")
    }

    /**
     * Muestra una página específica en el ImageView
     */
    private fun mostrarPagina(index: Int) {
        val pageCount = pdfRenderer?.pageCount ?: 0
        if (pageCount <= 0) {
            Log.e(TAG, "¡Error! pageCount es 0. El PDF puede estar corrupto.")
            mostrarError("Error: No se pueden leer las páginas del PDF.")
            return
        }

        if (index < 0 || index >= pageCount) {
            return // Página fuera de rango
        }

        currentPage?.close()
        currentPage = pdfRenderer?.openPage(index)
        currentPageIndex = index
        val bitmap = Bitmap.createBitmap(currentPage!!.width, currentPage!!.height, Bitmap.Config.ARGB_8888)
        currentPage?.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        ivPdfPage.setImageBitmap(bitmap)

        // Actualizar UI
        btnPreviousPage.isEnabled = (index > 0)
        btnNextPage.isEnabled = (index + 1 < pageCount)
        tvPageNumber.text = "${index + 1} / $pageCount"
    }

    private fun mostrarCargando(estaCargando: Boolean) {
        // (Idealmente, aquí mostrarías/ocultarías un ProgressBar)
        if (estaCargando) {
            pbCargando.visibility = View.VISIBLE // (Esto no funcionará bien)
            ivPdfPage.visibility = View.GONE
        } else {
            pbCargando.visibility = View.GONE
            ivPdfPage.visibility = View.VISIBLE
        }
    }

    private fun cerrarPdfRenderer() {
        try {
            currentPage?.close()
            pdfRenderer?.close()
            parcelFileDescriptor?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error cerrando PDF", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cerrarPdfRenderer()
    }

    private fun mostrarError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}