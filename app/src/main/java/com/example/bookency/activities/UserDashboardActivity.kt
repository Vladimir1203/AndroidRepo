package com.example.bookency.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.bookency.adapters.BookAdapter
import com.example.bookency.databinding.ActivityUserDashboardBinding
import com.example.bookency.models.Book
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

val client = OkHttpClient()

class UserDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityUserDashboardBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private val apiKey = "AIzaSyALgae2u5LPtvdNVdPoBepQ6THi8-oPMUE"
    private val baseUrl = "https://www.googleapis.com/books/v1/volumes"

    var mainHandler: Handler = Handler(Looper.getMainLooper())

    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.booksRv.setHasFixedSize(true)
        bookAdapter = BookAdapter(this@UserDashboard)
        binding.booksRv.adapter = bookAdapter

        firebaseAuth = FirebaseAuth.getInstance()

        checkUser()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.searchEt.addTextChangedListener {
            val query = binding.searchEt.text.toString()
            if (query.length > 2) {
                searchBooks(query)
            }
        }
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }
    }

    private fun searchBooks(query: String) {

        val url = "$baseUrl?q=$query&key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .build()

        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    println(e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    if (!responseData.isNullOrEmpty()) {
                        val jsonObject = JSONObject(responseData)
                        val itemsArray = jsonObject.optJSONArray("items")

                        if (itemsArray != null) {
                            val booksList = mutableListOf<Book>()
                            for (i in 0 until itemsArray.length()) {
                                val itemObject = itemsArray.getJSONObject(i)
                                val id = itemObject.optString("id")
                                val volumeInfoObject = itemObject.optJSONObject("volumeInfo")
                                val title = volumeInfoObject?.optString("title") ?: ""
                                val authors = volumeInfoObject?.optJSONArray("authors")
                                val description = volumeInfoObject?.optString("description")?:""
                                val picture = volumeInfoObject?.optJSONObject("imageLinks")
                                    ?.getString("thumbnail")

                                val authorsList = ArrayList<String>()
                                if (authors != null) {
                                    for (j in 0 until authors.length()) {
                                        val author = authors.optString(j)
                                        authorsList.add(author)
                                    }
                                }
                                val book = Book(id, title, authorsList, description, picture)
                                booksList.add(book)
                            }
                            mainHandler.post {
                                bookAdapter.bookArrayList = booksList
                                bookAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            })

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Fetching books failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            binding.backBtn.visibility = View.VISIBLE
            binding.profileBtn.visibility = View.GONE
            binding.logoutBtn.visibility = View.GONE
        } else {
            binding.profileBtn.visibility = View.VISIBLE
            binding.logoutBtn.visibility = View.VISIBLE
        }
    }

}