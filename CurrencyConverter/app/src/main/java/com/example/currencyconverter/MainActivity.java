package com.example.currencyconverter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Map<String, Double> currencyRates = new HashMap<>();
    private int firstPreviousPosition;
    private int secondPreviousPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeCurrencyRates();

        Spinner firstCurrencySpinner = findViewById(R.id.first_currency);
        Spinner secondCurrencySpinner = findViewById(R.id.second_currency);

        String[] currencies = {"USD", "RUB", "BYN"};

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstCurrencySpinner.setAdapter(adapter);
        secondCurrencySpinner.setAdapter(adapter);
        secondCurrencySpinner.setSelection(1);

        TextView result = findViewById(R.id.result);

        EditText enterValue = findViewById(R.id.editText);

        firstPreviousPosition = firstCurrencySpinner.getSelectedItemPosition();
        secondPreviousPosition = secondCurrencySpinner.getSelectedItemPosition();

        firstCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstCurrencySpinner.getSelectedItem().equals(secondCurrencySpinner.getSelectedItem())) {
                    secondCurrencySpinner.setSelection(firstPreviousPosition);
                    secondPreviousPosition = firstPreviousPosition;
                }
                firstPreviousPosition = firstCurrencySpinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        secondCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstCurrencySpinner.getSelectedItem().equals(secondCurrencySpinner.getSelectedItem())) {
                    firstCurrencySpinner.setSelection(secondPreviousPosition);
                    firstPreviousPosition = secondPreviousPosition;
                }
                secondPreviousPosition = secondCurrencySpinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button button = findViewById(R.id.convertButton);
        button.setOnClickListener(v -> {
            try {
                double convertedValue = convertCurrency(Double.parseDouble(enterValue.getText().toString()),
                        firstCurrencySpinner.getSelectedItem().toString(),
                        secondCurrencySpinner.getSelectedItem().toString());
                result.setText(String.valueOf(convertedValue));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void initializeCurrencyRates() {
        currencyRates.put("USD-RUB", 100.0);
        currencyRates.put("RUB-USD", 1 / 100.0);
        currencyRates.put("USD-BYN", 3.28);
        currencyRates.put("BYN-USD", 1 / 3.28);
        currencyRates.put("RUB-BYN", 3.28 / 100.0);
        currencyRates.put("BYN-RUB", 100.0 / 3.28);
    }

    private double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        String key = fromCurrency + "-" + toCurrency;
        double rate = currencyRates.get(key);

        if (rate == 0.0) return 0.0;

        return amount * rate;
    }
}