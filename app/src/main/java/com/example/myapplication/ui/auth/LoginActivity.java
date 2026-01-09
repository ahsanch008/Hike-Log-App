package com.example.myapplication.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.data.repository.UserRepository;

public class LoginActivity extends AppCompatActivity {
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userRepository = new UserRepository(this);

        SharedPreferences prefs = getSharedPreferences("hikelog", MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        if (userId > 0) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        EditText email = findViewById(R.id.input_email);
        EditText password = findViewById(R.id.input_password);
        Button login = findViewById(R.id.btn_login);
        TextView link = findViewById(R.id.link_signup);

        login.setOnClickListener(v -> {
            String e = email.getText().toString().trim();
            String p = password.getText().toString();
            if (TextUtils.isEmpty(e) || TextUtils.isEmpty(p) || p.length() < 6) {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = userRepository.authenticate(e, p);
            if (id > 0) {
                prefs.edit().putLong("user_id", id).apply();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });

        if (link != null) link.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }
}
