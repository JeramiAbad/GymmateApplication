package com.example.gymmateapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Check if user is already logged in
        SharedPreferences prefs = getSharedPreferences("GymmatePrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String savedEmail = prefs.getString("memberEmail", "");

        if (isLoggedIn) {
            // User already logged in → go directly to Dashboard
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish();
            return; // exit onCreate to avoid setting layout again
        }

        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // ✅ Prefill email if saved
        if (!savedEmail.isEmpty()) {
            etEmail.setText(savedEmail);
        }

        // Login button click
        btnLogin.setOnClickListener(v -> loginUser());

        // Registration button click → navigate to RegistrationActivity
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {DBHelper.COLUMN_MEMBER_ID, DBHelper.COLUMN_MEMBER_NAME};
        String selection = DBHelper.COLUMN_MEMBER_EMAIL + " = ? AND " + DBHelper.COLUMN_MEMBER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(DBHelper.TABLE_MEMBERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBHelper.COLUMN_MEMBER_NAME);
            String name = (nameIndex >= 0) ? cursor.getString(nameIndex) : "";

            Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_SHORT).show();

            // ✅ Save login status and email
            SharedPreferences prefs = getSharedPreferences("GymmatePrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("memberName", name);
            editor.putString("memberEmail", email);
            editor.apply();

            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) cursor.close();
        db.close();
    }
}
