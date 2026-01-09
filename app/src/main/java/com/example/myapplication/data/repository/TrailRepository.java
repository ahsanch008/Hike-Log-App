package com.example.myapplication.data.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.data.db.HikeLogContract;
import com.example.myapplication.data.db.HikeLogDbHelper;
import com.example.myapplication.model.Trail;

import java.util.ArrayList;
import java.util.List;

public class TrailRepository {
    private final HikeLogDbHelper dbHelper;

    public TrailRepository(Context context) {
        this.dbHelper = new HikeLogDbHelper(context);
    }

    public List<Trail> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(HikeLogContract.Trails.TABLE, null, null, null, null, null, HikeLogContract.Trails.COL_NAME + " ASC");
        List<Trail> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(map(c));
        }
        c.close();
        return list;
    }

    public List<Trail> searchByName(String q) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(HikeLogContract.Trails.TABLE, null, HikeLogContract.Trails.COL_NAME + " LIKE ?", new String[]{"%" + q + "%"}, null, null, HikeLogContract.Trails.COL_NAME + " ASC");
        List<Trail> list = new ArrayList<>();
        while (c.moveToNext()) list.add(map(c));
        c.close();
        return list;
    }

    public List<Trail> filterByDifficulty(String difficulty) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(HikeLogContract.Trails.TABLE, null, HikeLogContract.Trails.COL_DIFFICULTY + "=?", new String[]{difficulty}, null, null, HikeLogContract.Trails.COL_NAME + " ASC");
        List<Trail> list = new ArrayList<>();
        while (c.moveToNext()) list.add(map(c));
        c.close();
        return list;
    }

    public void seedPakistanTrailsIfMissing() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[][] pk = new String[][]{
                {"Margalla Trail 5", "Islamabad, Pakistan", "Medium", "5.5", "180", "400", "Popular scenic trail in Margalla Hills with steady ascent.", "trail_margalla_trail_5"},
                {"Margalla Trail 3 (Saidpur)", "Islamabad, Pakistan", "Medium", "4.8", "160", "350", "Historic Saidpur start, forested path to the ridgeline.", "trail_margalla_trail_3_saidpur"},
                {"Dunga Gali Pipeline Track", "Ayubia National Park, Pakistan", "Easy", "3.5", "90", "150", "Flat, family-friendly walk along the old pipeline with views.", "trail_dunga_gali_pipeline"},
                {"Mushkpuri Top", "Nathia Gali, Pakistan", "Medium", "4.0", "120", "600", "Pine forest trail to panoramic Mushkpuri summit.", "trail_mushkpuri_top"},
                {"Fairy Meadows Trek", "Diamer, Gilgit-Baltistan, Pakistan", "Hard", "8.0", "300", "800", "Alpine meadows with Nanga Parbat views; steep sections.", "trail_fairy_meadows"},
                {"Hunza Eagle's Nest", "Duikar, Hunza, Pakistan", "Easy", "2.0", "60", "250", "Short climb to sunset viewpoint above Hunza Valley.", "trail_hunza_eagles_nest"},
                {"Rakaposhi Base Camp", "Minapin, Nagar, Pakistan", "Hard", "14.0", "480", "1200", "Long ascent through Minapin to glacier base camp.", "trail_rakaposhi_base_camp"}
        };
        for (String[] t : pk) {
            Cursor c = db.query(HikeLogContract.Trails.TABLE, new String[]{HikeLogContract.Trails._ID}, HikeLogContract.Trails.COL_NAME + "=?", new String[]{t[0]}, null, null, null);
            boolean exists = c.moveToFirst();
            c.close();
            if (!exists) {
                android.content.ContentValues cv = new android.content.ContentValues();
                cv.put(HikeLogContract.Trails.COL_NAME, t[0]);
                cv.put(HikeLogContract.Trails.COL_LOCATION, t[1]);
                cv.put(HikeLogContract.Trails.COL_DIFFICULTY, t[2]);
                cv.put(HikeLogContract.Trails.COL_DISTANCE_KM, Double.parseDouble(t[3]));
                cv.put(HikeLogContract.Trails.COL_ESTIMATED_TIME_MIN, Integer.parseInt(t[4]));
                cv.put(HikeLogContract.Trails.COL_ELEVATION_M, Integer.parseInt(t[5]));
                cv.put(HikeLogContract.Trails.COL_DESCRIPTION, t[6]);
                cv.put(HikeLogContract.Trails.COL_IMAGE_NAME, t[7]);
                db.insert(HikeLogContract.Trails.TABLE, null, cv);
            }
        }
    }

    private Trail map(Cursor c) {
        long id = c.getLong(c.getColumnIndexOrThrow(HikeLogContract.Trails._ID));
        String name = safeGetString(c, HikeLogContract.Trails.COL_NAME, "");
        String loc = safeGetString(c, HikeLogContract.Trails.COL_LOCATION, "");
        String diff = safeGetString(c, HikeLogContract.Trails.COL_DIFFICULTY, "");
        double dist = safeGetDouble(c, HikeLogContract.Trails.COL_DISTANCE_KM, 0.0);
        int time = safeGetInt(c, HikeLogContract.Trails.COL_ESTIMATED_TIME_MIN, 0);
        int elev = safeGetInt(c, HikeLogContract.Trails.COL_ELEVATION_M, 0);
        String desc = safeGetString(c, HikeLogContract.Trails.COL_DESCRIPTION, "");
        String img = safeGet(c, HikeLogContract.Trails.COL_IMAGE_NAME);
        return new Trail(id, name, loc, diff, dist, time, elev, desc, img);
    }

    private String safeGet(Cursor c, String col) {
        int idx = c.getColumnIndex(col);
        if (idx == -1) return null;
        return c.getString(idx);
    }

    private String safeGetString(Cursor c, String col, String def) {
        int idx = c.getColumnIndex(col);
        if (idx == -1 || c.isNull(idx)) return def;
        return c.getString(idx);
    }

    private int safeGetInt(Cursor c, String col, int def) {
        int idx = c.getColumnIndex(col);
        if (idx == -1 || c.isNull(idx)) return def;
        return c.getInt(idx);
    }

    private double safeGetDouble(Cursor c, String col, double def) {
        int idx = c.getColumnIndex(col);
        if (idx == -1 || c.isNull(idx)) return def;
        return c.getDouble(idx);
    }
}
