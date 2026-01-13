package com.example.gymmateapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "gymmate.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_BMI = "bmi";
    private static final String COL_ID = "id";
    private static final String COL_HEIGHT = "height";
    private static final String COL_WEIGHT = "weight";
    private static final String COL_AGE = "age";
    private static final String COL_GENDER = "gender";
    private static final String COL_BMI = "bmi";
    private static final String COL_BMR = "bmr";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createBmiTable = "CREATE TABLE " + TABLE_BMI + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_HEIGHT + " REAL," +
                COL_WEIGHT + " REAL," +
                COL_AGE + " INTEGER," +
                COL_GENDER + " TEXT," +
                COL_BMI + " REAL," +
                COL_BMR + " REAL)";
        db.execSQL(createBmiTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BMI);
        onCreate(db);
    }

    // Insert BMI result
    public boolean insertBmi(float height, float weight, int age, String gender, double bmi, double bmr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_HEIGHT, height);
        cv.put(COL_WEIGHT, weight);
        cv.put(COL_AGE, age);
        cv.put(COL_GENDER, gender);
        cv.put(COL_BMI, bmi);
        cv.put(COL_BMR, bmr);
        long result = db.insert(TABLE_BMI, null, cv);
        return result != -1;
    }

    // Get last BMI result
    public Cursor getLastBmi() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BMI + " ORDER BY " + COL_ID + " DESC LIMIT 1", null);
    }
}
