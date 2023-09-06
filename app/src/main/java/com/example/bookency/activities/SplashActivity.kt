package com.example.bookency.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.bookency.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        firebaseAuth = FirebaseAuth.getInstance()
        Handler().postDelayed(Runnable {
            checkUser()
        }, 1000)


    }

    private fun checkUser() {

        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            val ref =
                FirebaseDatabase.getInstance("https://bookency-626ee-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Users")
            ref.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        startActivity(Intent(this@SplashActivity, UserDashboard::class.java))
                        finish()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }
    }
}