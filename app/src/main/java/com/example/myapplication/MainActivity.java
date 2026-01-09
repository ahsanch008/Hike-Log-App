package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.ui.auth.LoginActivity;
import com.example.myapplication.ui.history.HistoryFragment;
import com.example.myapplication.ui.home.HomeFragment;
import com.example.myapplication.ui.profile.ProfileFragment;
import com.example.myapplication.ui.stats.StatsFragment;
import com.example.myapplication.ui.trails.TrailsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("hikelog", MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        if (userId <= 0) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            Fragment f;
            int id = item.getItemId();
            if (id == R.id.nav_home) f = new HomeFragment();
            else if (id == R.id.nav_trails) f = new TrailsFragment();
            else if (id == R.id.nav_history) f = new HistoryFragment();
            else if (id == R.id.nav_stats) f = new StatsFragment();
            else if (id == R.id.nav_profile) f = new ProfileFragment();
            else f = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
            return true;
        });
        int startTab = getIntent().getIntExtra("selected_tab_id", R.id.nav_trails);
        nav.setSelectedItemId(startTab);
    }
}
