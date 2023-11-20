package by.korsakovegor.notesapplication

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private val notes: ArrayList<String>, private val activity: Activity, private val context: Context) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }
    fun updateNotes(newNotes: List<String>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        val text = holder.itemView.findViewById<TextView>(R.id.text)
        text.text = note
        holder.itemView.setOnClickListener {
            val intent = Intent(context, NoteDetailActivity::class.java)
            intent.putExtra("note", note) // Передайте выбранную заметку в NoteDetailActivity
            intent.putExtra("pos", position)

            val options = ActivityOptions.makeSceneTransitionAnimation(activity)
            activity.startActivityForResult(intent, 100, options.toBundle())
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}