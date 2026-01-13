package com.example.gymmateapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StartWorkoutActivity extends AppCompatActivity {

    private TextView tvWorkoutName, tvTimer;
    private Button btnStopWorkout;

    private Handler timerHandler = new Handler();
    private long startTime = 0;
    private long targetDurationMillis = 0;

    private SQLiteDatabase db;
    private String workoutName = "Workout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_workout);

        tvWorkoutName = findViewById(R.id.tvWorkoutName);
        tvTimer = findViewById(R.id.tvTimer);
        btnStopWorkout = findViewById(R.id.btnStopWorkout);

        db = openOrCreateDatabase("GymMateDB", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS WorkoutsLog(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, duration INTEGER);");

        // Get workout name from intent
        if (getIntent() != null && getIntent().hasExtra("workout_name")) {
            workoutName = getIntent().getStringExtra("workout_name");
        }

        tvWorkoutName.setText(workoutName);

        // ✅ Get sets and reps from DB to calculate duration
        int sets = 0, reps = 0;
        Cursor cursor = db.rawQuery("SELECT sets, reps FROM workouts WHERE name = ? LIMIT 1", new String[]{workoutName});
        if (cursor.moveToFirst()) {
            sets = cursor.getInt(cursor.getColumnIndex("sets"));
            reps = cursor.getInt(cursor.getColumnIndex("reps"));
        }
        cursor.close();

        // Example: 1 rep = 1 minute (adjust as needed)
        int totalMinutes = sets * reps;
        targetDurationMillis = totalMinutes * 60 * 1000;

        // Start timer
        startTime = System.currentTimeMillis();
        timerHandler.post(timerRunnable);

        // Stop button
        btnStopWorkout.setOnClickListener(v -> stopWorkout());
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsed = System.currentTimeMillis() - startTime;

            // ✅ Auto-stop if target duration reached
            if (elapsed >= targetDurationMillis) {
                stopWorkout();
                return;
            }

            int seconds = (int) (elapsed / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 1000);
        }
    };

    private void stopWorkout() {
        timerHandler.removeCallbacks(timerRunnable);

        long durationMillis = System.currentTimeMillis() - startTime;
        int durationSeconds = (int) (durationMillis / 1000);

        // Save workout log
        ContentValues values = new ContentValues();
        values.put("name", workoutName);
        values.put("duration", durationSeconds);
        db.insert("WorkoutsLog", null, values);

        // ✅ Play notification sound
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ✅ Vibrate phone
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(500); // 0.5 second vibration
        }

        Toast.makeText(this, "Workout complete! Duration: " + tvTimer.getText(), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
        timerHandler.removeCallbacks(timerRunnable);
    }
}
