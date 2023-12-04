package by.korsakovegor.marketapplication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private val items: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerAdapter.BookViewHolder>() {

    class BookViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookName: TextView? = itemView.findViewById(R.id.bookName)
        val bookAuthor: TextView? = itemView.findViewById(R.id.bookAuthor)
        val bookPages: TextView? = itemView.findViewById(R.id.bookPages)
        val bookPublication: TextView? = itemView.findViewById(R.id.bookPublication)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_item, parent, false)
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bookName?.text = items[position]
    }

    override fun getItemCount(): Int {
        return items.size
    }
}