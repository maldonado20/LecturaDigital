package com.dsm441.lecturadigital.ui

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dsm441.lecturadigital.DetailActivity
import com.dsm441.lecturadigital.R
import com.dsm441.lecturadigital.data.LibroFirestore
import com.dsm441.lecturadigital.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookAdapter(private var books: List<LibroFirestore>) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {
    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBookCover: ImageView = itemView.findViewById(R.id.BookCover)
        val tvBookTitle: TextView = itemView.findViewById(R.id.BookTitle)
        val tvBookAuthor: TextView = itemView.findViewById(R.id.BookAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book,parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val libro = books[position]

        holder.tvBookTitle.text = libro.titulo
        holder.tvBookAuthor.text = libro.autor


        //Buscamos a la portada en la api
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val query = "${libro.titulo} ${libro.autor}"
                val response = RetrofitClient.apiService.searchBook(query)

                if (response.isSuccessful) {
                    // Obtener la URL de la portada
                    val imageUrl = response.body()?.items?.firstOrNull()?.volumeInfo?.imageLinks?.thumbnail

                    if (imageUrl != null) {
                        holder.ivBookCover.load(imageUrl.replaceFirst("http://", "https://")) {
                            crossfade(true)
                            placeholder(R.color.seccion)
                            error(R.drawable.logo1)
                        }
                    } else {
                        holder.ivBookCover.setImageResource(R.drawable.logo1)
                    }
                } else {
                    holder.ivBookCover.setImageResource(R.drawable.logo1)
                }
            } catch (e: Exception) {
                Log.e("BookAdapter", "Error al cargar portada", e)
                holder.ivBookCover.setImageResource(R.drawable.logo1)
            }
        }

        //configurar el clic para ir a DetailActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)

            //Se envia el objeto firetores que tiene PDF
            intent.putExtra("BOOK_FIRESTORE", libro)
            context.startActivity(intent)
        }
    }

    fun updateBooks(newBooks: List<LibroFirestore>) {
        books = newBooks
        notifyDataSetChanged()
    }
}