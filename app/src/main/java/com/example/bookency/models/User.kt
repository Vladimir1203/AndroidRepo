package com.example.bookency.models

import android.os.Parcelable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class User{
    companion object{
         fun userReviewed(uid: String, bookId: String): Boolean{
            var isExisting = false
            val ref =
                FirebaseDatabase.getInstance("https://bookency-626ee-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Reviews")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (reviewSnapshot in snapshot.children) {
                                if (reviewSnapshot.child("userId").value.toString() == uid&& reviewSnapshot.child("bookId").value.toString() == bookId) {
                                    isExisting = true
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            println("userevieved: "+isExisting.toString())

            return isExisting
        }
    }
}

