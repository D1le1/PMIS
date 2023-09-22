package by.korsakovegor.taxcalculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import by.korsakovegor.taxcalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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