package com.example.bookency.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val reviewId: String = "",
    val bookId: String = "",
    val bookTitle: String = "",
    val picture: String = "",
    val userId: String = "",
    val username: String = "",
    val review: String = "",
    val rating: Float
) : Parcelable