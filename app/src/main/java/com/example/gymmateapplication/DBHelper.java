package com.example.gymmateapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GymMateDB";
    private static final int DATABASE_VERSION = 1;

    // Members table
    public static final String TABLE_MEMBERS = "members";
    public static final String COLUMN_MEMBER_ID = "id";
    public static final String COLUMN_MEMBER_NAME = "name";
    public static final String COLUMN_MEMBER_EMAIL = "email";
    public static final String COLUMN_MEMBER_PASSWORD = "password";

    // Workouts table
    public static final String TABLE_WORKOUTS = "workouts";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_REPS = "reps";
    public static final String COL_SETS = "sets";

    // BMI table
    public static final String TABLE_BMI = "bmi";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Members
        String createMembersTable = "CREATE TABLE IF NOT EXISTS " + TABLE_MEMBERS + " (" +
                COLUMN_MEMBER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MEMBER_NAME + " TEXT, " +
                COLUMN_MEMBER_EMAIL + " TEXT UNIQUE, " +
                COLUMN_MEMBER_PASSWORD + " TEXT" +
                ");";
        db.execSQL(createMembersTable);

        // Workouts
        String createWorkoutsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_WORKOUTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_REPS + " INTEGER NOT NULL, " +
                COL_SETS + " INTEGER NOT NULL" +
                ");";
        db.execSQL(createWorkoutsTable);

        // BMI
        String createBmiTable = "CREATE TABLE IF NOT EXISTS " + TABLE_BMI + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "height REAL, " +
                "weight REAL, " +
                "age INTEGER, " +
                "gender TEXT, " +
                "bmi REAL, " +
                "bmr REAL" +
                ");";
        db.execSQL(createBmiTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BMI);
        onCreate(db);
    }
}
