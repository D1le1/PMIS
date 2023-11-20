package by.korsakovegor.notesapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.transition.TransitionInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import by.korsakovegor.notesapplication.databinding.ActivityNoteDetailBinding

class NoteDetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityNoteDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val string = intent.getStringExtra("note")
        binding.note.setText(string)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
        val intent = Intent()
        intent.putExtra("note", binding.note.text.toString())
        intent.putExtra("pos", this.intent.getIntExtra("pos", -1))
        setResult(RESULT_OK, intent)
    }
}