package com.example.bookency.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bookency.databinding.ActivityReviewEditBinding
import com.example.bookency.models.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class EditReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewEditBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private lateinit var review: Review

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        review = intent.getParcelableExtra<Review>("review")!!
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)


        binding.contentEt.setText(review?.review)
        binding.ratingBar.rating = review.rating

        if(review?.bookTitle!!.length>37){
            binding.bookTitleTv.text = review?.bookTitle!!.substring(0,36)+"..."
        }
        else{
            binding.bookTitleTv.text = review?.bookTitle

        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.editReviewBtn.setOnClickListener {
            editReview()
        }


    }

    private fun editReview() {

        progressDialog.setMessage("Updating review...")

        //val uid = firebaseAuth.uid
        val hashMap: HashMap<String, Any?> = HashMap()

        val newreview = binding.contentEt.text.toString().trim()
        if (newreview.isEmpty()) {
            Toast.makeText(this, "Review content can not be empty", Toast.LENGTH_SHORT).show()
        }
        val newrate = binding.ratingBar.rating
        if (newrate==null) {
            Toast.makeText(this, "Rating can not be empty", Toast.LENGTH_SHORT).show()
        }

        hashMap["review"] = newreview
        hashMap["rating"] = newrate

        val ref =
            FirebaseDatabase.getInstance("https://bookency-626ee-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Reviews")
        ref.child(review.reviewId!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Review updated", Toast.LENGTH_SHORT).show()
                onBackPressed()

            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Updating review failed due to ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}