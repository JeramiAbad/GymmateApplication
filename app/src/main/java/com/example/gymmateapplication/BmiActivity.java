package com.example.gymmateapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BmiActivity extends AppCompatActivity {

    EditText etHeight, etWeight, etAge;
    Spinner spGender;
    Button btnCalculate;
    TextView tvResult;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etAge = findViewById(R.id.etAge);
        spGender = findViewById(R.id.spGender);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);

        db = new DatabaseHelper(this);

        // Spinner
        String[] genders = {"male", "female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, genders);
        spGender.setAdapter(adapter);

        btnCalculate.setOnClickListener(v -> calculateBMI());
    }

    private void calculateBMI() {
        String h = etHeight.getText().toString();
        String w = etWeight.getText().toString();
        String a = etAge.getText().toString();

        if (h.isEmpty() || w.isEmpty() || a.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        float height = Float.parseFloat(h);
        float weight = Float.parseFloat(w);
        int age = Integer.parseInt(a);
        String gender = spGender.getSelectedItem().toString();

        // API Call
        ApiClient.getApi().calculate(weight, height, age, gender).enqueue(new Callback<MetricsResponse>() {
            @Override
            public void onResponse(Call<MetricsResponse> call, Response<MetricsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double bmi = response.body().bmi;
                    double bmr = response.body().bmr;

                    tvResult.setText("BMI: " + String.format("%.2f", bmi) +
                            "\nBMR: " + String.format("%.2f", bmr));

                    // Save to SQLite
                    boolean saved = db.insertBmi(height, weight, age, gender, bmi, bmr);
                    if (!saved) {
                        Toast.makeText(BmiActivity.this, "Failed to save BMI", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(BmiActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MetricsResponse> call, Throwable t) {
                Toast.makeText(BmiActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
