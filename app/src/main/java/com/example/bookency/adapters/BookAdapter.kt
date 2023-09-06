package com.example.bookency.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookency.activities.BookReviewsActivity
import com.example.bookency.databinding.BookRowBinding
import com.example.bookency.models.Book

class BookAdapter : RecyclerView.Adapter<BookAdapter.HolderCategory> {

    private val context: Context
    var bookArrayList: List<Book> = emptyList()

    constructor(context: Context) {
        this.context = context
    }

    inner class HolderCategory(itemView: BookRowBinding) : RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        return HolderCategory(BookRowBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        val book = bookArrayList[position]
        val title = book.title

        holder.binding.root.setOnClickListener {
            val intent = Intent(holder.binding.root.context, BookReviewsActivity::class.java)
            intent.putExtra("book", book)
            holder.binding.root.context.startActivity(intent)
        }

        holder.binding.nameTv.text = title
        Glide.with(holder.binding.bookIv).load(book.picture?.replace("http://", "https://")).into(holder.binding.bookIv)
    }

    override fun getItemCount(): Int {
        return bookArrayList.size
    }
}