package com.example.myapplication.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.repository.UserRepository;
import android.content.Intent;
import com.example.myapplication.ui.auth.LoginActivity;

public class RegisterActivity extends AppCompatActivity {
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userRepository = new UserRepository(this);

        EditText name = findViewById(R.id.input_name);
        EditText email = findViewById(R.id.input_email);
        EditText password = findViewById(R.id.input_password);
        Button register = findViewById(R.id.btn_register);
        TextView link = findViewById(R.id.link_signin);

        register.setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            String e = email.getText().toString().trim();
            String p = password.getText().toString();
            if (TextUtils.isEmpty(n) || TextUtils.isEmpty(e) || TextUtils.isEmpty(p) || p.length() < 6) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = userRepository.register(n, e, p);
            if (id > 0) {
                Toast.makeText(this, "Registered", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Email exists", Toast.LENGTH_SHORT).show();
            }
        });

        if (link != null) link.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
