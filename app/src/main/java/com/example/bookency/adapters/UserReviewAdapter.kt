package com.example.bookency.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookency.activities.EditReviewActivity
import com.example.bookency.activities.UserProfileActivity
import com.example.bookency.databinding.UserReviewsRowDetailBinding
import com.example.bookency.models.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserReviewAdapter : RecyclerView.Adapter<UserReviewAdapter.HolderCategory> {

    private val context: UserProfileActivity
    var reviewArrayList: List<Review> = emptyList()

    constructor(context: UserProfileActivity) {
        this.context = context
    }

    inner class HolderCategory(itemView: UserReviewsRowDetailBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        return HolderCategory(
            UserReviewsRowDetailBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        val review = reviewArrayList[position]

        val title = review.bookTitle
        val reviewText = review.review
        val rating = review.rating
        val picture = review.picture

        holder.binding.editBtn.setOnClickListener {
            val intent = Intent(holder.binding.root.context, EditReviewActivity::class.java)
            intent.putExtra("review", review)
            holder.binding.root.context.startActivity(intent)
        }

        holder.binding.deleteBtn.setOnClickListener {

            showConfirmationDialog(it, review)

        }

        holder.binding.bookTv.text = title
        holder.binding.descriptionTv.text = reviewText
        holder.binding.ratingBar.rating = rating
        Glide.with(holder.binding.bookIv).load(picture?.replace("http://", "https://"))
            .into(holder.binding.bookIv)

    }

    private fun showConfirmationDialog(it: View, review: Review) {
        val builder = AlertDialog.Builder(it.context)
        builder.setTitle("Confirmation")
        builder.setMessage("Are you sure you want to delete this review?")

        builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
            // Handle positive button click
            deleteReview(review)
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
            // Handle negative button click or simply dismiss the dialog
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteReview(review: Review) {
        val ref =
            FirebaseDatabase.getInstance("https://bookency-626ee-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Reviews")
        ref.child(review.reviewId)
            .removeValue()
            .addOnSuccessListener {

                context.searchReviews(FirebaseAuth.getInstance().currentUser?.uid)
            }
            .addOnFailureListener { e ->
            }
    }

    override fun getItemCount(): Int {
        return reviewArrayList.size
    }
}