package com.example.bookency.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bookency.databinding.ActivityAddReviewBinding
import com.example.bookency.models.Book
import com.example.bookency.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddReviewBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    var username = ""

    private var review = ""
    private var grade: Float = 0F
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReviewBinding.inflate(layoutInflater)

        val book = intent.getParcelableExtra<Book>("book")
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        firebaseAuth = FirebaseAuth.getInstance()

        if(book?.title!!.length>37){
            binding.bookTitleTv.text = book.title.substring(0,36)+"..."
        }
        else{
            binding.bookTitleTv.text = book.title

        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.addReviewBtn.setOnClickListener {
            if (firebaseAuth.currentUser != null) {
                val user = firebaseAuth.currentUser

                if (user != null && !User.userReviewed(user.uid, book.id)) {
                    getUserName(book, user)

                } else {
                    Toast.makeText(this, "You already reviewed this book", Toast.LENGTH_SHORT)
                        .show()

                }
            }

        }
    }

    private fun getUserName(book: Book, user: FirebaseUser) {

        review = binding.contentEt.text.toString().trim()
        grade = binding.ratingBar.rating
        val ref =
            FirebaseDatabase.getInstance("https://bookency-626ee-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
        ref.child(user.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    username = "${snapshot.child("name").value}"
                    saveReview(book, user.uid, username)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


    }

    private fun saveReview(book: Book?, uid: String, username: String) {
        if (review.isEmpty()) {
            Toast.makeText(this, "Enter review..", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog.setMessage("Saving review info...")

            var timestamp = System.currentTimeMillis()

            val hashMap: HashMap<String, Any?> = HashMap()
            hashMap["bookId"] = book?.id
            hashMap["bookTitle"] = book?.title
            hashMap["userId"] = uid
            hashMap["username"] = username
            hashMap["review"] = review
            hashMap["rating"] = grade
            hashMap["picture"] = book?.picture
            hashMap["timestamp"] = timestamp

            val ref =
                FirebaseDatabase.getInstance("https://bookency-626ee-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Reviews")
            ref.push()
                .setValue(hashMap)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Review added", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this, BookReviewsActivity::class.java)
//                    intent.putExtra("book", book)
//                    startActivity(intent)
//                    finish()
                    onBackPressed()

                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(
                        this,
                        "Saving review failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}