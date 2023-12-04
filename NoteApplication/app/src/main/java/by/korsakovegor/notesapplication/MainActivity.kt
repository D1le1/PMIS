package by.korsakovegor.notesapplication

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.korsakovegor.notesapplication.databinding.ActivityMainBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.ArrayList
import java.util.Collections
import java.util.Scanner

class MainActivity : AppCompatActivity() {

    private lateinit var notes: ArrayList<String>
    private lateinit var adapter: NoteAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        notes = ArrayList()

        addNoteList()
        binding.addButton.setOnClickListener {
            val note = ""
            notes.add(note)
            adapter.notifyItemInserted(notes.size)
            val intent = Intent(this, NoteDetailActivity::class.java)
            intent.putExtra("note", note) // Передайте выбранную заметку в NoteDetailActivity
            intent.putExtra("pos", notes.size-1)

            val options = ActivityOptions.makeSceneTransitionAnimation(this)
            startActivityForResult(intent, 100, options.toBundle())
        }
    }

    private fun addNoteList() {
        notes = loadNotesFromFile()

        val recyclerView: RecyclerView = findViewById(R.id.notesRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = NoteAdapter(notes, this, this)
        recyclerView.adapter = adapter

        val itemHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            ItemTouchHelper.START or ItemTouchHelper.END
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val pos = viewHolder.adapterPosition
                val targetPos = target.adapterPosition
                Collections.swap(notes, pos, targetPos)
                (recyclerView.adapter as NoteAdapter).notifyItemMoved(pos, targetPos)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                notes.removeAt(pos)
                binding.notesRecyclerView.adapter?.notifyItemRemoved(pos)
            }

        }


        ItemTouchHelper(itemHelper).attachToRecyclerView(recyclerView)
    }

    private fun updateNoteList() {
        adapter.updateNotes(notes)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (data != null) {

                val pos = data.getIntExtra("pos", -1)
                val updatedNote = data.getStringExtra("note").toString()
                notes[pos] = updatedNote
                adapter.notifyItemChanged(pos)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        saveNotesToFile(notes)
    }

    override fun onDestroy() {
        super.onDestroy()
        saveNotesToFile(notes)
    }

    private fun loadNotesFromFile(): ArrayList<String> {
        val fileName = "notes.txt"

        try {
            openFileInput(fileName).use { fileInputStream ->
                ObjectInputStream(fileInputStream).use { objectInputStream ->
                    val loadedNotes = objectInputStream.readObject() as ArrayList<String>
                    Log.d("TAG", "Заметки загружены из файла: $loadedNotes")
                    return loadedNotes
                }
            }
        } catch (e: FileNotFoundException) {
            Log.e("TAG", "Файл не найден: $fileName")
        } catch (e: IOException) {
            Log.e("TAG", "Ошибка при чтении файла: $fileName")
        } catch (e: ClassNotFoundException) {
            Log.e("TAG", "Ошибка при чтении объекта из файла: $fileName")
        }

        return ArrayList()
    }

    private fun saveNotesToFile(notes: List<String>) {
        val fileName = "notes.txt"

        try {
            openFileOutput(fileName, Context.MODE_PRIVATE).use { fileOutputStream ->
                ObjectOutputStream(fileOutputStream).use { objectOutputStream ->
                    objectOutputStream.writeObject(notes)
                    Log.d("TAG", "Заметки сохранены в файл: $fileName")
                }
            }
        } catch (e: FileNotFoundException) {
            Log.e("TAG", "Файл не найден: $fileName")
        } catch (e: IOException) {
            Log.e("TAG", "Ошибка при записи файла: $fileName")
        }
    }
}