package com.example.bookency.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.bookency.adapters.ReviewAdapter
import com.example.bookency.databinding.ActivityBookReviewsBinding
import com.example.bookency.models.Book
import com.example.bookency.models.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookReviewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookReviewsBinding
    val reviews: MutableList<Review> = mutableListOf()
    private lateinit var reviewAdapter: ReviewAdapter
    var mainHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val book = intent.getParcelableExtra<Book>("book")

        firebaseAuth = FirebaseAuth.getInstance()


        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.nameTv.text = book?.title.toString()
        binding.authorTv.text = "Written by " + book?.authors?.joinToString(", ")
        if(book?.description!!.length>600){
            println("STIGLO1"+book.description.length)
            binding.descriptionTv.text = book.description.substring(0,596)+"..."
        }
        else{
            binding.descriptionTv.text = book.description

        }

        //binding.descriptionTv.text = book?.description.toString()
        Glide.with(binding.bookIv).load(book?.picture?.replace("http://", "https://"))
            .into(binding.bookIv)

        reviewAdapter = ReviewAdapter(this@BookReviewsActivity)
        binding.reviewsRv.adapter = reviewAdapter

        checkUser(book!!)

        binding.addReviewBtn.setOnClickListener {
            val intent = Intent(this, AddReviewActivity::class.java)
            intent.putExtra("book", book)
            //intent.putExtra("bookTitle", book?.title.toString())
            binding.root.context.startActivity(intent)
        }

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        searchReviews(book!!)

    }


    private fun checkUserReview(uid: String, bookId: String) {
        val ref =
            FirebaseDatabase.getInstance("https://bookency-626ee-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Reviews")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (reviewSnapshot in snapshot.children) {
                            if (reviewSnapshot.child("userId").value.toString() == uid && reviewSnapshot.child(
                                    "bookId"
                                ).value.toString() == bookId
                            ) {
                                binding.addReviewBtn.isEnabled = false
                            }
                            mainHandler.post {
                                reviewAdapter.reviewArrayList = reviews
                                reviewAdapter.notifyDataSetChanged()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
    }

    private fun checkUser(book: Book) {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            binding.addReviewBtn.isVisible = false
            binding.backBtn.isVisible = true
            binding.profileBtn.visibility = View.GONE
            binding.logoutBtn.visibility = View.GONE
        } else {
            //val email = firebaseUser.email
            //binding.subTitleTv.text = email
            binding.profileBtn.visibility = View.VISIBLE
            binding.logoutBtn.visibility = View.VISIBLE
            checkUserReview(firebaseAuth.currentUser?.uid!!, book.id)

        }
    }

    private fun searchReviews(book: Book) {
        val ref =
            FirebaseDatabase.getInstance("https://bookency-626ee-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Reviews") //.child("Reviews")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (reviewSnapshot in snapshot.children) {

                            // Access individual review data
                            if (reviewSnapshot.child("bookId").value.toString() == book.id)
                                try {
                                    val reviewId = reviewSnapshot.key.toString()
                                    val userId = reviewSnapshot.child("userId").value.toString()
                                    val username = reviewSnapshot.child("username").value.toString()
                                    val reviewText = reviewSnapshot.child("review").value.toString()
                                    val rating = reviewSnapshot.child("rating").value.toString()
                                    val review = Review(
                                        reviewId,
                                        book.id,
                                        book.title,
                                        book.picture!!,
                                        userId,
                                        username,
                                        reviewText,
                                        rating.toFloat()
                                    )

                                        reviews.removeAll { it.reviewId == review.reviewId }
                                        reviews.add(review)

                                } catch (e: Exception) {
                                }
                        }

                        mainHandler.post {
                            reviewAdapter.reviewArrayList = reviews
                            reviewAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

    }
}