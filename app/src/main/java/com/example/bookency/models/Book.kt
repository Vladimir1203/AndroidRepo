package com.example.bookency.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val id: String = "",
    val title: String = "",
    val authors: ArrayList<String>?,
    val description: String = "",
    val picture: String? = null
) : Parcelable