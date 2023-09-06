package com.example.bookency.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookency.databinding.BookReviewRowDetailBinding
import com.example.bookency.models.Review

class ReviewAdapter  : RecyclerView.Adapter<ReviewAdapter.HolderCategory>{

    private val context: Context
    var reviewArrayList: List<Review> = emptyList()

    constructor(context: Context) {
        this.context = context
    }

    inner class HolderCategory(itemView: BookReviewRowDetailBinding) : RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        return HolderCategory(BookReviewRowDetailBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        val review = reviewArrayList[position]

        val user = review.username
        val reviewText = review.review
        val rating = review.rating

        holder.binding.userTv.text = user + " said:"
        holder.binding.descriptionTv.text = reviewText
        holder.binding.ratingBar.rating = rating
    }

    override fun getItemCount(): Int {
        return reviewArrayList.size
    }
}