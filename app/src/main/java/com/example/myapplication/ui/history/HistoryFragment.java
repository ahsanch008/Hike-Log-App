package com.example.myapplication.ui.history;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.repository.HikeRepository;
import com.example.myapplication.model.Hike;

import java.util.List;

public class HistoryFragment extends Fragment {
    private HikeRepository repo;
    private HikeAdapter adapter;
    private long userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences prefs = requireContext().getSharedPreferences("hikelog", requireContext().MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);
        repo = new HikeRepository(requireContext());
        RecyclerView list = view.findViewById(R.id.hikes_list);
        adapter = new HikeAdapter(hike -> {
            Intent i = new Intent(requireContext(), HikeDetailsActivity.class);
            i.putExtra("hike_id", hike.getId());
            startActivity(i);
        }, hike -> {
            int deleted = repo.deleteById(hike.getId());
            if (deleted > 0) {
                android.widget.Toast.makeText(requireContext(), "Hike deleted", android.widget.Toast.LENGTH_SHORT).show();
                load();
            } else {
                android.widget.Toast.makeText(requireContext(), "Delete failed", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        list.setAdapter(adapter);
        view.findViewById(R.id.fab_add_hike).setOnClickListener(v -> startActivity(new Intent(requireContext(), LogHikeActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        List<Hike> hikes = repo.getAllForUser(userId);
        adapter.submitList(hikes);
        View v = getView();
        if (v != null) {
            android.widget.TextView sub = v.findViewById(R.id.history_subtitle);
            if (sub != null) sub.setText(hikes.size() + " hikes recorded");
            View empty = v.findViewById(R.id.empty_state);
            if (empty != null) empty.setVisibility(hikes.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
