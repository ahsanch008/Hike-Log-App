package com.example.myapplication.ui.home;

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
import com.example.myapplication.data.repository.TrailRepository;
import com.example.myapplication.model.User;
import com.example.myapplication.ui.trails.TrailAdapter;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {
    private long userId;
    private HikeRepository hikeRepo;
    private UserRepository userRepo;
    private TrailRepository trailRepo;
    private TrailAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences prefs = requireContext().getSharedPreferences("hikelog", requireContext().MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);
        hikeRepo = new HikeRepository(requireContext());
        userRepo = new UserRepository(requireContext());
        trailRepo = new TrailRepository(requireContext());
        trailRepo.seedPakistanTrailsIfMissing();
        User u = userRepo.getUserById(userId);
        ((TextView)view.findViewById(R.id.home_greeting_name)).setText(u != null ? u.getName() : "Explorer");
        updateStats(view);

        androidx.recyclerview.widget.RecyclerView rv = view.findViewById(R.id.home_trails_preview);
        adapter = new TrailAdapter(trail -> {
            android.content.Intent i = new android.content.Intent(requireContext(), com.example.myapplication.ui.trails.TrailDetailsActivity.class);
            i.putExtra("trail_id", trail.getId());
            startActivity(i);
        });
        rv.setAdapter(adapter);
        java.util.List<com.example.myapplication.model.Trail> all = trailRepo.getAll();
        adapter.submitList(all);

        view.findViewById(R.id.link_see_all).setOnClickListener(v -> {
            com.google.android.material.bottomnavigation.BottomNavigationView nav = requireActivity().findViewById(R.id.bottom_nav);
            if (nav != null) nav.setSelectedItemId(R.id.nav_trails);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        View v = getView();
        if (v != null) updateStats(v);
    }

    private void updateStats(View v) {
        int total = hikeRepo.getTotalHikes(userId);
        int sum = hikeRepo.getTotalDuration(userId);
        int week = countInRange(getWeekStart(), System.currentTimeMillis());
        long recentMillis = mostRecentDate();
        ((TextView)v.findViewById(R.id.stat_total_hikes_home)).setText(String.valueOf(total));
        ((TextView)v.findViewById(R.id.stat_total_time_home)).setText(sum + "m");
        ((TextView)v.findViewById(R.id.stat_this_week_home)).setText(String.valueOf(week));
        TextView last = v.findViewById(R.id.stat_last_hike_home);
        TextView lastSub = v.findViewById(R.id.stat_last_hike_sub_home);
        if (recentMillis > 0) {
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            last.setText(df.format(new Date(recentMillis)));
            if (lastSub != null) lastSub.setText("");
        } else {
            last.setText("—");
            if (lastSub != null) lastSub.setText("No hikes yet");
        }
    }

    private long getWeekStart() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private int countInRange(long start, long end) {
        String sql = "SELECT COUNT(*) FROM " + com.example.myapplication.data.db.HikeLogContract.Hikes.TABLE + " WHERE " + com.example.myapplication.data.db.HikeLogContract.Hikes.COL_USER_ID + "=? AND " + com.example.myapplication.data.db.HikeLogContract.Hikes.COL_DATE + ">=? AND " + com.example.myapplication.data.db.HikeLogContract.Hikes.COL_DATE + "<=?";
        android.database.sqlite.SQLiteDatabase db = new com.example.myapplication.data.db.HikeLogDbHelper(requireContext()).getReadableDatabase();
        android.database.Cursor c = db.rawQuery(sql, new String[]{String.valueOf(userId), String.valueOf(start), String.valueOf(end)});
        int count = c.moveToFirst() ? c.getInt(0) : 0; c.close();
        return count;
    }

    private long mostRecentDate() {
        java.util.List<com.example.myapplication.model.Hike> hikes = hikeRepo.getAllForUser(userId);
        return hikes.isEmpty() ? 0 : hikes.get(0).getDateMillis();
    }
}
