package com.example.gymmateapplication;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StartWorkoutActivity extends AppCompatActivity {

    private TextView tvWorkoutName, tvTimer;
    private Button btnStopWorkout;

    private Handler timerHandler = new Handler();
    private long startTime = 0;

    private SQLiteDatabase db;
    private String workoutName = "Workout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_workout);

        // Initialize views
        tvWorkoutName = findViewById(R.id.tvWorkoutName);
        tvTimer = findViewById(R.id.tvTimer);
        btnStopWorkout = findViewById(R.id.btnStopWorkout);

        // Initialize database
        db = openOrCreateDatabase("GymMateDB", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS WorkoutsLog(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, duration INTEGER);");

        // Get workout name from intent
        if (getIntent() != null && getIntent().hasExtra("workout_name")) {
            workoutName = getIntent().getStringExtra("workout_name");
        }

        tvWorkoutName.setText(workoutName);

        // Start timer
        startTime = System.currentTimeMillis();
        timerHandler.post(timerRunnable);

        // Stop button click
        btnStopWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWorkout();
            }
        });
    }

    // Runnable to update timer
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;

            tvTimer.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            timerHandler.postDelayed(this, 1000);
        }
    };

    // Stop workout and save to database
    private void stopWorkout() {
        timerHandler.removeCallbacks(timerRunnable);

        long durationMillis = System.currentTimeMillis() - startTime;
        int durationSeconds = (int) (durationMillis / 1000);

        // Save workout log to database
        ContentValues values = new ContentValues();
        values.put("name", workoutName);
        values.put("duration", durationSeconds); // Save duration in seconds
        db.insert("WorkoutsLog", null, values);

        Toast.makeText(this, "Workout stopped! Duration: " + tvTimer.getText(), Toast.LENGTH_LONG).show();
        finish(); // Return to previous activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}
