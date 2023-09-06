package com.example.bookency.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.bookency.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private var name = ""
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.registerBtn.setOnClickListener {
            validateData()
        }

    }

    private fun validateData() {
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val cnfPasword = binding.cnfPasswordEt.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Enter your name..", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email address..", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Enter password..", Toast.LENGTH_SHORT).show()
        } else if (cnfPasword.isEmpty()) {
            Toast.makeText(this, "Confirm password..", Toast.LENGTH_SHORT).show()
        } else if (password != cnfPasword) {
            Toast.makeText(this, "Password doesn't match..", Toast.LENGTH_SHORT).show()
        } else {
            createUserAccount()
        }

    }

    private fun createUserAccount() {
        progressDialog.setMessage("Creating account...")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Register failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        progressDialog.setMessage("Saving user info...")

        var timestamp = System.currentTimeMillis()

        val uid = firebaseAuth.uid
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = ""
        hashMap["timestamp"] = timestamp

        val ref =
            FirebaseDatabase.getInstance("https://bookency-626ee-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, UserDashboard::class.java))
                finish()

            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Saving user failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }


    }
}