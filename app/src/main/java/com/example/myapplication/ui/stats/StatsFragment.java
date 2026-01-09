package com.example.myapplication.ui.stats;

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

import java.util.Calendar;

public class StatsFragment extends Fragment {
    private HikeRepository repo;
    private long userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repo = new HikeRepository(requireContext());
        SharedPreferences prefs = requireContext().getSharedPreferences("hikelog", requireContext().MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);
        update(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        View v = getView();
        if (v != null) update(v);
    }

    private void update(View v) {
        int total = repo.getTotalHikes(userId);
        int sum = repo.getTotalDuration(userId);
        double avg = repo.getAverageDuration(userId);
        int longest = repo.getLongestDuration(userId);
        int easy = repo.getCountByDifficulty(userId, "Easy");
        int med = repo.getCountByDifficulty(userId, "Medium");
        int hard = repo.getCountByDifficulty(userId, "Hard");
        int week = countInRange(getWeekStart(), getWeekEnd());
        int month = countInRange(getMonthStart(), getMonthEnd());

        ((TextView)v.findViewById(R.id.stat_total_hikes)).setText(String.valueOf(total));
        ((TextView)v.findViewById(R.id.stat_total_duration)).setText(sum + " min");
        ((TextView)v.findViewById(R.id.stat_avg_duration)).setText(String.format("%.1f min", avg));
        ((TextView)v.findViewById(R.id.stat_longest_duration)).setText(longest + " min");
        ((TextView)v.findViewById(R.id.stat_easy)).setText(String.valueOf(easy));
        ((TextView)v.findViewById(R.id.stat_medium)).setText(String.valueOf(med));
        ((TextView)v.findViewById(R.id.stat_hard)).setText(String.valueOf(hard));
        ((TextView)v.findViewById(R.id.stat_week)).setText(String.valueOf(week));
        ((TextView)v.findViewById(R.id.stat_month)).setText(String.valueOf(month));
    }

    private long getWeekStart() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
    private long getWeekEnd() { return System.currentTimeMillis(); }
    private long getMonthStart() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
    private long getMonthEnd() { return System.currentTimeMillis(); }

    private int countInRange(long start, long end) {
        String sql = "SELECT COUNT(*) FROM " + com.example.myapplication.data.db.HikeLogContract.Hikes.TABLE + " WHERE " + com.example.myapplication.data.db.HikeLogContract.Hikes.COL_USER_ID + "=? AND " + com.example.myapplication.data.db.HikeLogContract.Hikes.COL_DATE + ">=? AND " + com.example.myapplication.data.db.HikeLogContract.Hikes.COL_DATE + "<=?";
        android.database.sqlite.SQLiteDatabase db = new com.example.myapplication.data.db.HikeLogDbHelper(requireContext()).getReadableDatabase();
        android.database.Cursor c = db.rawQuery(sql, new String[]{String.valueOf(userId), String.valueOf(start), String.valueOf(end)});
        int count = c.moveToFirst() ? c.getInt(0) : 0; c.close();
        return count;
    }
}
