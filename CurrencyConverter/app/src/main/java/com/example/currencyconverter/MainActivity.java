package com.example.currencyconverter;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private Map<String, Double> currencyRates = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeCurrencyRates();

        Spinner firstCurrencySpinner = findViewById(R.id.first_currency);
        Spinner secondCurrencySpinner = findViewById(R.id.second_currency);

        List<String> curr = Arrays.asList("USD", "RUB", "BYN");
        ArrayList<String> firstCurrencies = new ArrayList<>(curr);
        ArrayList<String> secondCurrencies = new ArrayList<>(curr);


        ArrayAdapter<String> firstAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, firstCurrencies);
        ArrayAdapter<String> secondAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, secondCurrencies);

        firstAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secondAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstCurrencySpinner.setAdapter(firstAdapter);
        secondCurrencySpinner.setAdapter(secondAdapter);

        TextView result = findViewById(R.id.result);

        EditText enterValue = findViewById(R.id.editText);

        firstCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondCurrencies.clear();
                secondAdapter.addAll(curr);
                String selectedItem = firstCurrencySpinner.getSelectedItem().toString();
                if(secondCurrencies.contains(selectedItem))
                {
                    secondCurrencies.remove(selectedItem);
                    secondAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        secondCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firstCurrencies.clear();
                firstCurrencies.addAll(curr);
                String selectedItem = secondCurrencySpinner.getSelectedItem().toString();
                if(firstCurrencies.contains(selectedItem))
                {
                    firstCurrencies.remove(selectedItem);
                    firstAdapter.notifyDataSetChanged();
                }
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