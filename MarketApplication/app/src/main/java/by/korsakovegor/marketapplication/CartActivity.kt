package by.korsakovegor.marketapplication

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.korsakovegor.marketapplication.databinding.ActivityCartBinding
import by.korsakovegor.marketapplication.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Collections

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var cart = intent.getSerializableExtra("cart") as ArrayList<String>

        Log.d("D1le", cart.size.toString())

        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = RecyclerAdapter(cart)

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
                Collections.swap(cart, pos, targetPos)
                (binding.recycler.adapter as RecyclerAdapter).notifyItemMoved(pos, targetPos)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val book = cart[pos]
                Snackbar.make(binding.recycler, "Book added to cart", Snackbar.LENGTH_LONG).setAction("UNDO") {
                    cart.add(pos, book)
                    (binding.recycler.adapter as RecyclerAdapter).notifyItemInserted(pos)
                }.show()
                cart.removeAt(pos)
                (binding.recycler.adapter as RecyclerAdapter).notifyItemRemoved(viewHolder.adapterPosition)
            }

        }

        ItemTouchHelper(itemHelper).apply { attachToRecyclerView(binding.recycler) }
    }
}