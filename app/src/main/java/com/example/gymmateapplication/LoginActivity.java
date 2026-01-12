package com.example.gymmateapplication;

import android.content.Intent;
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
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

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

            if (nameIndex >= 0) { // check to avoid -1
                String name = cursor.getString(nameIndex);
                Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }

// Close resources
        if (cursor != null) cursor.close();
        db.close();

    }
}
