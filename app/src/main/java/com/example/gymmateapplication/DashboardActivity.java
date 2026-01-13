package com.example.gymmateapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvWorkoutName, tvWorkoutTime, tvProgressPercent, tvLastBmi;
    private ProgressBar progressWeekly;

    private SQLiteDatabase workoutDb;
    private DBHelper dbHelper;            // For workouts/members
    private DatabaseHelper bmiDbHelper;   // For BMI

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvWorkoutName = findViewById(R.id.tvWorkoutName);
        tvWorkoutTime = findViewById(R.id.tvWorkoutTime);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        tvLastBmi = findViewById(R.id.tvLastBmi);
        progressWeekly = findViewById(R.id.progressWeekly);

        Button btnStartWorkout = findViewById(R.id.btnStartWorkout);
        Button btnEditWorkout = findViewById(R.id.btnEditWorkout);
        Button btnBmi = findViewById(R.id.btnBmi);

        // Initialize databases
        dbHelper = new DBHelper(this);
        workoutDb = dbHelper.getReadableDatabase();

        bmiDbHelper = new DatabaseHelper(this); // Use this for BMI

        // Load data
        loadLatestWorkout();
        loadLastBmi();

        // Button listeners
        btnStartWorkout.setOnClickListener(v -> startWorkout());
        btnEditWorkout.setOnClickListener(v -> editWorkouts());
        btnBmi.setOnClickListener(v -> startActivity(new Intent(this, BmiActivity.class)));
    }

    /** Load the latest workout from DB */
    private void loadLatestWorkout() {
        try (Cursor cursor = workoutDb.rawQuery("SELECT * FROM workouts ORDER BY id DESC LIMIT 1", null)) {
            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex("name");
                int repsIndex = cursor.getColumnIndex("reps");
                int setsIndex = cursor.getColumnIndex("sets");

                if (nameIndex != -1 && repsIndex != -1 && setsIndex != -1) {
                    String workoutName = cursor.getString(nameIndex);
                    int reps = cursor.getInt(repsIndex);
                    int sets = cursor.getInt(setsIndex);
                    int duration = reps * sets;

                    tvWelcome.setText("Welcome back!");
                    tvWorkoutName.setText(workoutName);
                    tvWorkoutTime.setText("Duration: " + duration + " mins");
                    progressWeekly.setProgress(0);
                    tvProgressPercent.setText("0% Complete");
                    return;
                }
            }
            // No workout saved
            tvWelcome.setText("Welcome back!");
            tvWorkoutName.setText("No workout saved");
            tvWorkoutTime.setText("Duration: 0 mins");
            progressWeekly.setProgress(0);
            tvProgressPercent.setText("0% Complete");
        }
    }

    /** Load the last saved BMI from DB */
    private void loadLastBmi() {
        try (Cursor cursor = bmiDbHelper.getLastBmi()) {  // Use correct DB helper
            if (cursor.moveToFirst()) {
                int bmiIndex = cursor.getColumnIndex("bmi");
                int bmrIndex = cursor.getColumnIndex("bmr");

                if (bmiIndex != -1 && bmrIndex != -1) {
                    double lastBmi = cursor.getDouble(bmiIndex);
                    double lastBmr = cursor.getDouble(bmrIndex);
                    tvLastBmi.setText(String.format("Last BMI: %.2f | BMR: %.2f", lastBmi, lastBmr));
                    return;
                }
            }
            tvLastBmi.setText("Last BMI: --");
        }
    }

    /** Start workout */
    private void startWorkout() {
        String workoutName = tvWorkoutName.getText().toString();
        if (workoutName.equals("No workout saved")) return;

        Intent intent = new Intent(this, StartWorkoutActivity.class);
        intent.putExtra("workout_name", workoutName);
        startActivity(intent);
    }

    /** Edit workouts */
    private void editWorkouts() {
        startActivity(new Intent(this, EditWorkoutsActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (workoutDb != null) workoutDb.close();
    }
}
