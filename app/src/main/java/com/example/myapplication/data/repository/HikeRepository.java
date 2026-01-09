package com.example.myapplication.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.data.db.HikeLogContract;
import com.example.myapplication.data.db.HikeLogDbHelper;
import com.example.myapplication.model.Hike;

import java.util.ArrayList;
import java.util.List;

public class HikeRepository {
    private final HikeLogDbHelper dbHelper;

    public HikeRepository(Context context) {
        this.dbHelper = new HikeLogDbHelper(context);
    }

    public long logHike(long userId, Long trailId, String trailName, String difficulty, long dateMillis, int durationMin, String notes) {
        return logHike(userId, trailId, trailName, difficulty, dateMillis, durationMin, notes, null);
    }

    public long logHike(long userId, Long trailId, String trailName, String difficulty, long dateMillis, int durationMin, String notes, String elevationPoints) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(HikeLogContract.Hikes.COL_USER_ID, userId);
        if (trailId != null) cv.put(HikeLogContract.Hikes.COL_TRAIL_ID, trailId);
        cv.put(HikeLogContract.Hikes.COL_TRAIL_NAME, trailName);
        cv.put(HikeLogContract.Hikes.COL_DIFFICULTY, difficulty);
        cv.put(HikeLogContract.Hikes.COL_DATE, dateMillis);
        cv.put(HikeLogContract.Hikes.COL_DURATION_MIN, durationMin);
        cv.put(HikeLogContract.Hikes.COL_NOTES, notes);
        if (elevationPoints != null) cv.put(HikeLogContract.Hikes.COL_ELEVATION_POINTS, elevationPoints);
        return db.insert(HikeLogContract.Hikes.TABLE, null, cv);
    }

    public List<Hike> getAllForUser(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(HikeLogContract.Hikes.TABLE, null, HikeLogContract.Hikes.COL_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, HikeLogContract.Hikes.COL_DATE + " DESC");
        List<Hike> list = new ArrayList<>();
        while (c.moveToNext()) list.add(map(c));
        c.close();
        return list;
    }

    public int deleteById(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(HikeLogContract.Hikes.TABLE, HikeLogContract.Hikes._ID + "=?", new String[]{String.valueOf(id)});
    }

    public int clearAllForUser(long userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(HikeLogContract.Hikes.TABLE, HikeLogContract.Hikes.COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
    }

    public int getTotalHikes(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + HikeLogContract.Hikes.TABLE + " WHERE " + HikeLogContract.Hikes.COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        int count = c.moveToFirst() ? c.getInt(0) : 0;
        c.close();
        return count;
    }

    public int getTotalDuration(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COALESCE(SUM(" + HikeLogContract.Hikes.COL_DURATION_MIN + "),0) FROM " + HikeLogContract.Hikes.TABLE + " WHERE " + HikeLogContract.Hikes.COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        int sum = c.moveToFirst() ? c.getInt(0) : 0;
        c.close();
        return sum;
    }

    public int getLongestDuration(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COALESCE(MAX(" + HikeLogContract.Hikes.COL_DURATION_MIN + "),0) FROM " + HikeLogContract.Hikes.TABLE + " WHERE " + HikeLogContract.Hikes.COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        int max = c.moveToFirst() ? c.getInt(0) : 0;
        c.close();
        return max;
    }

    public double getAverageDuration(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COALESCE(AVG(" + HikeLogContract.Hikes.COL_DURATION_MIN + "),0) FROM " + HikeLogContract.Hikes.TABLE + " WHERE " + HikeLogContract.Hikes.COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        double avg = c.moveToFirst() ? c.getDouble(0) : 0.0;
        c.close();
        return avg;
    }

    public int getCountByDifficulty(long userId, String difficulty) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + HikeLogContract.Hikes.TABLE + " WHERE " + HikeLogContract.Hikes.COL_USER_ID + "=? AND " + HikeLogContract.Hikes.COL_DIFFICULTY + "=?", new String[]{String.valueOf(userId), difficulty});
        int count = c.moveToFirst() ? c.getInt(0) : 0;
        c.close();
        return count;
    }

    public int updateElevationPoints(long hikeId, String elevationPointsJson) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(HikeLogContract.Hikes.COL_ELEVATION_POINTS, elevationPointsJson);
        return db.update(HikeLogContract.Hikes.TABLE, cv, HikeLogContract.Hikes._ID + "=?", new String[]{String.valueOf(hikeId)});
    }

    private Hike map(Cursor c) {
        Long trailId = c.isNull(c.getColumnIndexOrThrow(HikeLogContract.Hikes.COL_TRAIL_ID)) ? null : c.getLong(c.getColumnIndexOrThrow(HikeLogContract.Hikes.COL_TRAIL_ID));
        String elevationPoints = safeGetString(c, HikeLogContract.Hikes.COL_ELEVATION_POINTS);
        return new Hike(
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.Hikes._ID)),
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.Hikes.COL_USER_ID)),
                trailId,
                c.getString(c.getColumnIndexOrThrow(HikeLogContract.Hikes.COL_TRAIL_NAME)),
                c.getString(c.getColumnIndexOrThrow(HikeLogContract.Hikes.COL_DIFFICULTY)),
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.Hikes.COL_DATE)),
                c.getInt(c.getColumnIndexOrThrow(HikeLogContract.Hikes.COL_DURATION_MIN)),
                c.getString(c.getColumnIndexOrThrow(HikeLogContract.Hikes.COL_NOTES)),
                elevationPoints
        );
    }

    private String safeGetString(Cursor c, String col) {
        int idx = c.getColumnIndex(col);
        if (idx == -1) return null;
        return c.getString(idx);
    }
}
