package com.example.myapplication.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.data.db.HikeLogContract;
import com.example.myapplication.data.db.HikeLogDbHelper;

public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        Button logout = v.findViewById(R.id.btn_logout);
        logout.setOnClickListener(view -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("hikelog", requireContext().MODE_PRIVATE);
            prefs.edit().remove("user_id").apply();
        });
        Button clear = v.findViewById(R.id.btn_clear_hikes);
        clear.setOnClickListener(view -> {
            android.database.sqlite.SQLiteDatabase db = new HikeLogDbHelper(requireContext()).getWritableDatabase();
            db.delete(HikeLogContract.Hikes.TABLE, null, null);
        });
        return v;
    }
}
