package com.example.myapplication.ui.history;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.repository.HikeRepository;

import java.util.Calendar;

public class LogHikeActivity extends AppCompatActivity {
    private long selectedDateMillis = System.currentTimeMillis();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_hike);
        EditText trailName = findViewById(R.id.input_trail_name);
        Spinner difficulty = findViewById(R.id.input_difficulty);
        EditText duration = findViewById(R.id.input_duration);
        EditText notes = findViewById(R.id.input_notes);
        Button pickDate = findViewById(R.id.btn_pick_date);
        Button save = findViewById(R.id.btn_save);

        pickDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dlg = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth, 0, 0, 0);
                selectedDateMillis = c.getTimeInMillis();
                pickDate.setText((month + 1) + "/" + dayOfMonth + "/" + year);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dlg.show();
        });

        save.setOnClickListener(v -> {
            String name = trailName.getText().toString().trim();
            String diff = difficulty.getSelectedItem().toString();
            String durStr = duration.getText().toString().trim();
            String n = notes.getText().toString().trim();
            if (name.isEmpty() || durStr.isEmpty()) { Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show(); return; }
            int dur = Integer.parseInt(durStr);
            SharedPreferences prefs = getSharedPreferences("hikelog", MODE_PRIVATE);
            long userId = prefs.getLong("user_id", -1);
            if (userId <= 0) { Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show(); return; }
            HikeRepository repo = new HikeRepository(this);
            long id = repo.logHike(userId, null, name, diff, selectedDateMillis, dur, n);
            if (id > 0) { Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show(); finish(); } else { Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show(); }
        });
    }
}

