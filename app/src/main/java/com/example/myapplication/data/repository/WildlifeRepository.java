package com.example.myapplication.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.data.db.HikeLogContract;
import com.example.myapplication.data.db.HikeLogDbHelper;
import com.example.myapplication.model.WildlifeSighting;

import java.util.ArrayList;
import java.util.List;

public class WildlifeRepository {
    private final HikeLogDbHelper dbHelper;

    public WildlifeRepository(Context context) {
        this.dbHelper = new HikeLogDbHelper(context);
    }

    public long addSighting(long hikeId, String animalName, String species, int quantity, String notes) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(HikeLogContract.WildlifeSightings.COL_HIKE_ID, hikeId);
        cv.put(HikeLogContract.WildlifeSightings.COL_ANIMAL_NAME, animalName);
        cv.put(HikeLogContract.WildlifeSightings.COL_SPECIES, species);
        cv.put(HikeLogContract.WildlifeSightings.COL_QUANTITY, quantity);
        cv.put(HikeLogContract.WildlifeSightings.COL_NOTES, notes);
        cv.put(HikeLogContract.WildlifeSightings.COL_TIMESTAMP, System.currentTimeMillis());
        return db.insert(HikeLogContract.WildlifeSightings.TABLE, null, cv);
    }

    public List<WildlifeSighting> getSightingsForHike(long hikeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(HikeLogContract.WildlifeSightings.TABLE, null, HikeLogContract.WildlifeSightings.COL_HIKE_ID + "=?", new String[]{String.valueOf(hikeId)}, null, null, HikeLogContract.WildlifeSightings.COL_TIMESTAMP + " DESC");
        List<WildlifeSighting> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(map(c));
        }
        c.close();
        return list;
    }

    public int deleteSighting(long sightingId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(HikeLogContract.WildlifeSightings.TABLE, HikeLogContract.WildlifeSightings._ID + "=?", new String[]{String.valueOf(sightingId)});
    }

    public int getTotalSightingsForUser(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + HikeLogContract.WildlifeSightings.TABLE + " ws JOIN " + HikeLogContract.Hikes.TABLE + " h ON ws." + HikeLogContract.WildlifeSightings.COL_HIKE_ID + "=h." + HikeLogContract.Hikes._ID + " WHERE h." + HikeLogContract.Hikes.COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        int count = c.moveToFirst() ? c.getInt(0) : 0;
        c.close();
        return count;
    }

    private WildlifeSighting map(Cursor c) {
        return new WildlifeSighting(
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.WildlifeSightings._ID)),
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.WildlifeSightings.COL_HIKE_ID)),
                c.getString(c.getColumnIndexOrThrow(HikeLogContract.WildlifeSightings.COL_ANIMAL_NAME)),
                safeGetString(c, HikeLogContract.WildlifeSightings.COL_SPECIES),
                c.getInt(c.getColumnIndexOrThrow(HikeLogContract.WildlifeSightings.COL_QUANTITY)),
                safeGetString(c, HikeLogContract.WildlifeSightings.COL_NOTES),
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.WildlifeSightings.COL_TIMESTAMP))
        );
    }

    private String safeGetString(Cursor c, String col) {
        int idx = c.getColumnIndex(col);
        if (idx == -1) return null;
        return c.getString(idx);
    }
}
