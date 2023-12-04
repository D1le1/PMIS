package by.korsakovegor.marketapplication

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.korsakovegor.marketapplication.databinding.ActivityMainBinding
import by.korsakovegor.marketapplication.RecyclerAdapter
import com.google.android.material.snackbar.Snackbar
import java.util.Collections

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var cart = ArrayList<String>()
    private lateinit var books: ArrayList<String>
    private lateinit var adapter: RecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        books = ArrayList(
            listOf(
                "To Kill a Mockingbird",
                "1984",
                "The Great Gatsby",
                "Pride and Prejudice",
                "The Catcher in the Rye",
                "The Lord of the Rings",
                "The Chronicles of Narnia",
                "Brave New World",
                "The Hobbit",
                "Moby-Dick",
                "The Odyssey",
                "War and Peace",
                "The Adventures of Huckleberry Finn",
                "The Scarlet Letter",
                "Don Quixote"
            )
        )

        adapter = RecyclerAdapter(books)
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        val itemSwipeCallback = ItemSwipeCallback(adapter, books, cart, binding.recycler)
        val itemTouchHelper = ItemTouchHelper(itemSwipeCallback)
        itemTouchHelper.attachToRecyclerView(binding.recycler)
        binding.cartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("cart", cart)
            startActivityForResult(intent, 101)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            101 -> {
                val newCart = data?.getStringArrayListExtra("cart") as ArrayList<String>
                cart.clear()
                cart.addAll(newCart)
            }
        }
    }
}