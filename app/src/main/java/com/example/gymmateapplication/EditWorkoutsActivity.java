package com.example.gymmateapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EditWorkoutsActivity extends AppCompatActivity {

    private EditText etWorkoutName, etReps, etSets;
    private Button btnSaveWorkout;
    private RecyclerView recyclerWorkoutHistory;

    private WorkoutHistoryAdapter adapter;
    private ArrayList<Workout> workoutList;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workouts);

        // Views
        etWorkoutName = findViewById(R.id.actWorkoutName);
        etReps = findViewById(R.id.etReps);
        etSets = findViewById(R.id.etSets);
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout);
        recyclerWorkoutHistory = findViewById(R.id.recyclerWorkoutHistory);

        // DB
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        // RecyclerView
        workoutList = new ArrayList<>();
        adapter = new WorkoutHistoryAdapter(workoutList, workout -> showDeleteDialog(workout));
        recyclerWorkoutHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerWorkoutHistory.setAdapter(adapter);

        loadWorkouts();

        btnSaveWorkout.setOnClickListener(v -> saveWorkout());
    }
    private void showDeleteDialog(Workout workout) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Workout")
                .setMessage("Delete this workout?")
                .setPositiveButton("Delete", (dialog, which) ->
                        deleteWorkout(workout.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void deleteWorkout(int id) {

        int rows = db.delete("workouts", "id = ?",
                new String[]{String.valueOf(id)});

        if (rows > 0) {
            Toast.makeText(this, "Workout deleted", Toast.LENGTH_SHORT).show();
            loadWorkouts(); // refresh RecyclerView
        } else {
            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
        }
    }
    // Save workout
    private void saveWorkout() {

        String workoutName = etWorkoutName.getText().toString().trim();
        String repsStr = etReps.getText().toString().trim();
        String setsStr = etSets.getText().toString().trim();

        if (workoutName.isEmpty()) {
            Toast.makeText(this, "Please enter workout name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (repsStr.isEmpty() || setsStr.isEmpty()) {
            Toast.makeText(this, "Please enter reps and sets", Toast.LENGTH_SHORT).show();
            return;
        }

        int reps = Integer.parseInt(repsStr);
        int sets = Integer.parseInt(setsStr);

        ContentValues values = new ContentValues();
        values.put("name", workoutName);
        values.put("reps", reps);
        values.put("sets", sets);

        long result = db.insert("workouts", null, values);

        if (result != -1) {
            Toast.makeText(this, "Workout saved!", Toast.LENGTH_SHORT).show();
            loadWorkouts();
        } else {
            Toast.makeText(this, "Error saving workout", Toast.LENGTH_SHORT).show();
        }

        etWorkoutName.setText("");
        etReps.setText("");
        etSets.setText("");
    }

    // Load workouts to RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    private void loadWorkouts() {

        workoutList.clear();

        Cursor cursor = db.rawQuery("SELECT * FROM workouts ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                int reps = cursor.getInt(cursor.getColumnIndexOrThrow("reps"));
                int sets = cursor.getInt(cursor.getColumnIndexOrThrow("sets"));

                workoutList.add(new Workout(id, name, reps, sets));

            } while (cursor.moveToNext());

            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}
