package by.korsakovegor.taxcalculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ViewModel : ViewModel() {
    private var data = MutableLiveData<String>()
    var liveData: LiveData<String> = data



    fun calculateClicked(income: Double, expenses: Double, deductions: Double, taxRate: Double) {
        var taxableIncome = income - expenses - deductions

        if (taxableIncome < 0)
            taxableIncome = 0.0

        data.value = (taxableIncome * taxRate / 100).toString()
    }

    fun calculateClicked(income: String, expenses: String, deductions: String, taxRate: String) {
        try {
            var taxableIncome = income.toDouble() - expenses.toDouble() - deductions.toDouble()

            if (taxableIncome < 0)
                taxableIncome = 0.0

            data.value = (taxableIncome * taxRate.toDouble() / 100).toString()
        }catch (_:Exception){
            data.value = "error"
        }

    }
}