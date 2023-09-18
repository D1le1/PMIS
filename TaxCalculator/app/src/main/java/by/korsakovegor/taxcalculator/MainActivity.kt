package by.korsakovegor.taxcalculator

import android.annotation.SuppressLint
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import by.korsakovegor.taxcalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel = ViewModelProvider(this)[ViewModel::class.java]

        viewModel.liveData.observe(this) {
            if(it.equals("error"))
                binding.error.visibility = View.VISIBLE
            else{
                binding.error.visibility = View.INVISIBLE
                binding.resultText.text = "Tax Result: $it"
            }
        }

        val button = findViewById<Button>(R.id.calculateButton)
        button.setOnClickListener {
            viewModel.calculateClicked(
                binding.incomeEditText.text.toString(),
                binding.expensesEditText.text.toString(),
                binding.deductionsEditText.text.toString(),
                binding.taxRateEditText.text.toString()
            )
        }

    }
}