package com.example.gymmateapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EditWorkoutsActivity extends AppCompatActivity {

    private AutoCompleteTextView actvWorkoutName;
    private EditText etReps, etSets;
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

        // Initialize views
        actvWorkoutName = findViewById(R.id.actvWorkoutName);
        etReps = findViewById(R.id.etReps);
        etSets = findViewById(R.id.etSets);
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout);
        recyclerWorkoutHistory = findViewById(R.id.recyclerWorkoutHistory);

        // Initialize DB
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        // Load workout names into AutoCompleteTextView
        loadWorkoutNames();

        // Initialize RecyclerView
        workoutList = new ArrayList<>();
        adapter = new WorkoutHistoryAdapter(workoutList);
        recyclerWorkoutHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerWorkoutHistory.setAdapter(adapter);

        // Load saved workouts
        loadWorkouts();

        // Save button click
        btnSaveWorkout.setOnClickListener(v -> saveWorkout());
    }

    // Load workout names into AutoCompleteTextView
    private void loadWorkoutNames() {
        ArrayList<String> workoutNames = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT DISTINCT name FROM workouts ORDER BY name ASC", null);
        if (cursor.moveToFirst()) {
            do {
                int colIndex = cursor.getColumnIndex("name");
                if (colIndex >= 0) {
                    workoutNames.add(cursor.getString(colIndex));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Add default workouts if DB is empty
        if (workoutNames.isEmpty()) {
            workoutNames.add("Chest & Triceps");
            workoutNames.add("Back & Biceps");
            workoutNames.add("Legs");
            workoutNames.add("Shoulders");
            workoutNames.add("Abs");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                workoutNames
        );
        actvWorkoutName.setAdapter(adapter);
        actvWorkoutName.setThreshold(1); // start showing suggestions after 1 character
    }

    // Save workout to database
    private void saveWorkout() {
        String workoutName = actvWorkoutName.getText().toString().trim();
        String repsStr = etReps.getText().toString().trim();
        String setsStr = etSets.getText().toString().trim();

        if (workoutName.isEmpty()) {
            Toast.makeText(this, "Please enter a workout name", Toast.LENGTH_SHORT).show();
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
        } else {
            Toast.makeText(this, "Error saving workout", Toast.LENGTH_SHORT).show();
        }

        // Clear input fields
        etReps.setText("");
        etSets.setText("");
        actvWorkoutName.setText("");

        // Refresh AutoCompleteTextView suggestions and RecyclerView
        loadWorkoutNames();
        loadWorkouts();
    }

    // Load all workouts into RecyclerView
    private void loadWorkouts() {
        workoutList.clear();
        Cursor cursor = db.rawQuery("SELECT * FROM workouts ORDER BY id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int reps = cursor.getInt(cursor.getColumnIndex("reps"));
                int sets = cursor.getInt(cursor.getColumnIndex("sets"));

                workoutList.add(new Workout(id, name, reps, sets));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}
