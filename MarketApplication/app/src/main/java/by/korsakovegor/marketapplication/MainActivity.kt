package by.korsakovegor.marketapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.korsakovegor.marketapplication.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Collections

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cart = ArrayList<String>()

        val books: ArrayList<String> = ArrayList(
            listOf(
                "123",
                "222",
                "333",
                "444",
                "555",
                "666",
                "777",
                "888"
            )
        )

        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = RecyclerAdapter(books)
        binding.cartButton.setOnClickListener{
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("cart", cart)
            startActivity(intent)
        }

        val itemHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.START or ItemTouchHelper.END
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val pos = viewHolder.adapterPosition
                val targetPos = target.adapterPosition
                Collections.swap(books, pos, targetPos)
                (binding.recycler.adapter as RecyclerAdapter).notifyItemMoved(pos, targetPos)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val book = books[pos]
                Snackbar.make(binding.recycler, "Book added to cart", Snackbar.LENGTH_LONG).setAction("UNDO") {
                    books.add(pos, book)
                    cart.removeAt(cart.size - 1)
                    (binding.recycler.adapter as RecyclerAdapter).notifyItemInserted(pos)
                }.show()
                cart.add(books[pos])
                (binding.recycler.adapter as RecyclerAdapter).notifyItemRemoved(viewHolder.adapterPosition)
                (binding.recycler.adapter as RecyclerAdapter).notifyItemInserted(viewHolder.adapterPosition)
                Log.d("D1le", "Size: ${cart.size}")
            }

        }

        ItemTouchHelper(itemHelper).apply { attachToRecyclerView(binding.recycler) }
    }
}