package com.example.bookency.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bumptech.glide.Glide
import com.example.bookency.R
import com.example.bookency.adapters.UserReviewAdapter
import com.example.bookency.databinding.ActivityUserProfileBinding
import com.example.bookency.models.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var reviewAdapter: UserReviewAdapter

    var mainHandler: Handler = Handler(Looper.getMainLooper())
    val reviews: MutableList<Review> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.editBtn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        searchReviews(firebaseAuth.currentUser?.uid)
        reviewAdapter = UserReviewAdapter(this@UserProfileActivity)
        binding.reviewsRv.adapter = reviewAdapter
    }

    fun searchReviews(uid: String?) {
        val ref =
            FirebaseDatabase.getInstance("https://bookency-626ee-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Reviews") //.child("Reviews")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {


                        for (reviewSnapshot in snapshot.children) {

                            // Access individual review data
                            if (reviewSnapshot.child("userId").value.toString() == uid) {
                                println("usao1")


                                try {
                                    val reviewId = reviewSnapshot.key.toString()
                                    val bookId = reviewSnapshot.child("bookId").value.toString()
                                    val bookTitle = reviewSnapshot.child("bookTitle").value.toString()
                                    val picture = reviewSnapshot.child("picture").value.toString()
                                    val username = reviewSnapshot.child("username").value.toString()
                                    val reviewText = reviewSnapshot.child("review").value.toString()
                                    val rating = reviewSnapshot.child("rating").value.toString()
                                    val review = Review(
                                        reviewId,
                                        bookId,
                                        bookTitle,
                                        picture,
                                        uid,
                                        username,
                                        reviewText,
                                        rating.toFloat()
                                    )

                                    if (review != null) {
                                        reviews.removeAll { it.reviewId == review.reviewId }
                                        reviews.add(review)
                                    }
                                } catch (e: Exception) {

                                }
                            }

                            mainHandler.post {
                                reviewAdapter.reviewArrayList = reviews
                                reviewAdapter.notifyDataSetChanged()
                            }
                        }
                        binding.reviewsTv.text = reviews.size.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
    }

    private fun loadUserInfo() {
        val ref =
            FirebaseDatabase.getInstance("https://bookency-626ee-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"
                    //val userType = "${snapshot.child("userType").value}"

                    val formattedDate = Date(timestamp.toLong())

                    binding.nameTv.text = name
                    binding.emailTv.text = email
                    binding.memberDateTv.text = SimpleDateFormat("yyyy-MM-dd").format(formattedDate)

                    try {
                        Glide.with(this@UserProfileActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.ic_person)
                            .into(binding.profileIv)
                    } catch (e: Exception) {

                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }
}