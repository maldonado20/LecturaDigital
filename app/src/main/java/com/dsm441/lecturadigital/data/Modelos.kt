package com.dsm441.lecturadigital.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// 1. Modelo para nuestro FIRESTORE
@Parcelize
data class LibroFirestore(
    val titulo: String = "",
    val autor: String = "",
    val pdfUrl: String = ""

) : Parcelable

//Modelo para Google Books API
data class GoogleBookResponse(
    @SerializedName("items")
    val items: List<GoogleBookItem>?
)

data class GoogleBookItem(
    @SerializedName("volumeInfo")
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    @SerializedName("description")
    val description: String?,
    @SerializedName("imageLinks")
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    @SerializedName("thumbnail")
    val thumbnail: String?
)