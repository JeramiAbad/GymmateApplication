package com.example.gymmateapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DashboardActivity extends AppCompatActivity {

    // Views
    private ImageView imgLogo;
    private TextView tvDashboardTitle, tvWelcome, tvWorkoutName, tvWorkoutTime, tvProgressPercent, tvFooter;
    private ProgressBar progressWeekly;
    private CardView cardTodaysWorkout, cardProgress, cardActions;
    private Button btnStartWorkout, btnEditWorkout;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        imgLogo = findViewById(R.id.imgLogo);
        tvDashboardTitle = findViewById(R.id.tvDashboardTitle);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvWorkoutName = findViewById(R.id.tvWorkoutName);
        tvWorkoutTime = findViewById(R.id.tvWorkoutTime);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        tvFooter = findViewById(R.id.tvFooter);

        progressWeekly = findViewById(R.id.progressWeekly);

        cardTodaysWorkout = findViewById(R.id.cardTodaysWorkout);
        cardProgress = findViewById(R.id.cardProgress);
        cardActions = findViewById(R.id.cardActions);

        btnStartWorkout = findViewById(R.id.btnStartWorkout);
        btnEditWorkout = findViewById(R.id.btnEditWorkout);

        // Initialize DB
        dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();

        // Load latest workout from DB
        loadLatestWorkout();

        // Button listeners
        btnStartWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWorkout();
            }
        });

        btnEditWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editWorkouts();
            }
        });
    }

    // Load latest workout from DB
    private void loadLatestWorkout() {
        Cursor cursor = db.rawQuery("SELECT * FROM workouts ORDER BY id DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            String workoutName = cursor.getString(cursor.getColumnIndex("name"));
            int reps = cursor.getInt(cursor.getColumnIndex("reps"));
            int sets = cursor.getInt(cursor.getColumnIndex("sets"));
            int duration = sets * reps; // Example: simple calculation

            tvWelcome.setText("Welcome back!");
            tvWorkoutName.setText(workoutName);
            tvWorkoutTime.setText("Duration: " + duration + " mins");
            progressWeekly.setProgress(0); // Or calculate progress dynamically
            tvProgressPercent.setText("0% Complete");
        } else {
            // No workout saved yet
            tvWelcome.setText("Welcome back!");
            tvWorkoutName.setText("No workout saved");
            tvWorkoutTime.setText("Duration: 0 mins");
            progressWeekly.setProgress(0);
            tvProgressPercent.setText("0% Complete");
        }
        cursor.close();
    }

    // Start workout
    private void startWorkout() {
        String workoutName = tvWorkoutName.getText().toString();
        if (workoutName.equals("No workout saved")) {
            // Do nothing or show a Toast
            return;
        }

        Intent intent = new Intent(this, StartWorkoutActivity.class);
        intent.putExtra("workout_name", workoutName);
        startActivity(intent);
    }

    // Edit workouts
    private void editWorkouts() {
        startActivity(new Intent(this, EditWorkoutsActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}
