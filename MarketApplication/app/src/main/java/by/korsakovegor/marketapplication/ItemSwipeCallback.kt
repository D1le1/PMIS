package by.korsakovegor.marketapplication

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import by.korsakovegor.marketapplication.RecyclerAdapter
import com.google.android.material.snackbar.Snackbar

class ItemSwipeCallback(
    private val adapter: RecyclerAdapter,
    private val books: ArrayList<String>,
    private val cart: ArrayList<String>,
    private val recyclerView: RecyclerView
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = 0
        val swipeFlags = ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        adapter.notifyItemChanged(position)
        cart.add(books[position])
        Snackbar.make(recyclerView, "Book added to cart", Snackbar.LENGTH_LONG).setAction("UNDO") {
            cart.removeAt(cart.size - 1)
        }.show()
    }
}