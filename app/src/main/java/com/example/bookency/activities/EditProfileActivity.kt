package com.example.bookency.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.bookency.R
import com.example.bookency.databinding.ActivityProfileEditBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private var imageUri: Uri? = null

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.profileIv.setOnClickListener {
            showImageMenu()
        }
        binding.updateBtn.setOnClickListener {

            validateData()
        }
    }

    private var name = ""

    private fun validateData() {
        name = binding.nameEt.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Enter your name..", Toast.LENGTH_SHORT).show()
        } else {
            if (imageUri == null) {
                updateUserAccount("")

            } else {
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        progressDialog.setMessage("Uploading image")
        progressDialog.show()

        val filePathAndName = firebaseAuth.uid + "/profileImage"
        println(filePathAndName)

        val reference =
            FirebaseStorage.getInstance("gs://bookency-626ee.appspot.com")
                .getReference(filePathAndName)
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->

                var uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val imageUrl = "${uriTask.result}"
                updateUserAccount(imageUrl)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Image upload failed: ", Toast.LENGTH_SHORT).show()

            }


    }

    private fun updateUserAccount(imageUrl: String) {

        progressDialog.setMessage("Updating user info...")

        val uid = firebaseAuth.uid
        val hashMap: HashMap<String, Any?> = HashMap()

        hashMap["name"] = "${name}"
        if(imageUrl!=""){
            hashMap["profileImage"] = imageUrl
        }

        val ref =
            FirebaseDatabase.getInstance("https://bookency-626ee-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
        ref.child(uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Account updated", Toast.LENGTH_SHORT).show()
//                startActivity(Intent(this@EditProfileActivity, UserDashboard::class.java))
//                finish()
                onBackPressed()

            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Updating user failed due to ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun showImageMenu() {
        val popupMenu = PopupMenu(this, binding.profileIv)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == 0) {
                pickImageGallery()
            }
            true
        }
    }

    private fun pickImageGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data!!.data
                Glide.with(this@EditProfileActivity)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_person)
                    .into(binding.profileIv)
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private fun loadUserInfo() {
        val ref =
            FirebaseDatabase.getInstance("https://bookency-626ee-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"

                    val formattedDate = Date(timestamp.toLong())

                    binding.nameEt.setText(name)
                    SimpleDateFormat("yyyy-MM-dd").format(formattedDate)


                    try {
                        Glide.with(this@EditProfileActivity)
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