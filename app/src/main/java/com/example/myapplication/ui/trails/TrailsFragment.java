package com.example.myapplication.ui.trails;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.repository.TrailRepository;
import com.example.myapplication.model.Trail;

import java.util.List;

public class TrailsFragment extends Fragment {
    private TrailRepository trailRepo;
    private TrailAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trails, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        trailRepo = new TrailRepository(requireContext());
        trailRepo.seedPakistanTrailsIfMissing();
        RecyclerView list = view.findViewById(R.id.trails_list);
        adapter = new TrailAdapter(trail -> {
            Intent i = new Intent(requireContext(), TrailDetailsActivity.class);
            i.putExtra("trail_id", trail.getId());
            startActivity(i);
        });
        list.setAdapter(adapter);
        loadAll();
        EditText search = view.findViewById(R.id.search_input);
        ImageButton filterBtn = view.findViewById(R.id.btn_filter);

        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { applyFilters(); }
        });
        filterBtn.setOnClickListener(v -> showFilterMenu(filterBtn));
    }

    private void loadAll() {
        List<Trail> trails = trailRepo.getAll();
        adapter.submitList(trails);
        android.widget.TextView subtitle = getView().findViewById(R.id.trails_subtitle);
        if (subtitle != null) subtitle.setText(trails.size() + " trails available");
    }

    private void applyFilters() {
        View v = getView();
        if (v == null) return;
        EditText search = v.findViewById(R.id.search_input);
        String q = search.getText().toString().trim();
        String d = selectedDifficulty;
        List<Trail> result;
        if (!q.isEmpty()) {
            result = trailRepo.searchByName(q);
        } else if (!d.equals("All")) {
            result = trailRepo.filterByDifficulty(d);
        } else {
            result = trailRepo.getAll();
        }
        adapter.submitList(result);
        android.widget.TextView subtitle = getView().findViewById(R.id.trails_subtitle);
        if (subtitle != null) subtitle.setText(result.size() + " trails available");
    }

    private String selectedDifficulty = "All";
    private void showFilterMenu(View anchor) {
        androidx.appcompat.widget.PopupMenu menu = new androidx.appcompat.widget.PopupMenu(requireContext(), anchor);
        menu.getMenu().add("All");
        menu.getMenu().add("Easy");
        menu.getMenu().add("Medium");
        menu.getMenu().add("Hard");
        menu.setOnMenuItemClickListener(item -> { selectedDifficulty = item.getTitle().toString(); applyFilters(); return true; });
        menu.show();
    }
}
