package com.example.myapplication.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.data.repository.HikeRepository;
import com.example.myapplication.data.repository.UserRepository;
import com.example.myapplication.model.User;

public class ProfileFragment extends Fragment {
    private long userId;
    private UserRepository userRepo;
    private HikeRepository hikeRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences prefs = requireContext().getSharedPreferences("hikelog", requireContext().MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);
        userRepo = new UserRepository(requireContext());
        hikeRepo = new HikeRepository(requireContext());

        User u = userRepo.getUserById(userId);
        TextView nameTv = view.findViewById(R.id.profile_name_text);
        TextView emailTv = view.findViewById(R.id.profile_email);
        if (nameTv != null) nameTv.setText(u != null ? u.getName() : "Explorer");
        if (emailTv != null) emailTv.setText(u != null ? u.getEmail() : "");

        TextView totalTv = view.findViewById(R.id.profile_total_hikes);
        if (totalTv != null) totalTv.setText(String.valueOf(hikeRepo.getTotalHikes(userId)));

        TextView lastTv = view.findViewById(R.id.profile_last_hike);
        java.util.List<com.example.myapplication.model.Hike> hikes = hikeRepo.getAllForUser(userId);
        if (lastTv != null) {
            if (!hikes.isEmpty()) {
                long d = hikes.get(0).getDateMillis();
                java.text.DateFormat df = java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM);
                lastTv.setText(df.format(new java.util.Date(d)));
            } else {
                lastTv.setText("—");
            }
        }

        View editBtn = view.findViewById(R.id.btn_edit_name);
        if (editBtn != null && nameTv != null) editBtn.setOnClickListener(v -> {
            android.widget.EditText input = new android.widget.EditText(requireContext());
            input.setText(nameTv.getText());
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Edit Name")
                    .setView(input)
                    .setPositiveButton("Save", (d, w) -> {
                        String newName = input.getText().toString().trim();
                        if (!newName.isEmpty() && userId > 0) {
                            userRepo.updateName(userId, newName);
                            nameTv.setText(newName);
                            android.widget.Toast.makeText(requireContext(), "Saved", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        View clearCard = view.findViewById(R.id.btn_clear_hikes_card);
        if (clearCard != null) clearCard.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Clear All Hikes")
                    .setMessage("Remove all logged hikes for this account?")
                    .setPositiveButton("Clear", (d, w) -> {
                        int rows = hikeRepo.clearAllForUser(userId);
                        android.widget.Toast.makeText(requireContext(), rows > 0 ? "Cleared" : "Nothing to clear", android.widget.Toast.LENGTH_SHORT).show();
                        if (totalTv != null) totalTv.setText(String.valueOf(hikeRepo.getTotalHikes(userId)));
                        if (lastTv != null) lastTv.setText("—");
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        TextView appNameTv = view.findViewById(R.id.profile_app_name);
        TextView versionTv = view.findViewById(R.id.profile_version);
        if (appNameTv != null) appNameTv.setText(getString(R.string.app_name));
        String vn = null;
        try {
            android.content.pm.PackageManager pm = requireContext().getPackageManager();
            android.content.pm.PackageInfo info = pm.getPackageInfo(requireContext().getPackageName(), 0);
            vn = info.versionName;
        } catch (Exception ignored) {}
        if (versionTv != null) versionTv.setText("Version " + (vn != null ? vn : "1.0.0"));

        View logoutCard = view.findViewById(R.id.btn_logout_card);
        if (logoutCard != null) logoutCard.setOnClickListener(v -> {
            prefs.edit().remove("user_id").apply();
            startActivity(new Intent(requireContext(), com.example.myapplication.ui.auth.LoginActivity.class));
            requireActivity().finish();
        });
    }
}
