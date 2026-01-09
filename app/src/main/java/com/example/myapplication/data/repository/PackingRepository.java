package com.example.myapplication.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.data.db.HikeLogContract;
import com.example.myapplication.data.db.HikeLogDbHelper;
import com.example.myapplication.model.PackingItem;
import com.example.myapplication.model.UserPackingList;

import java.util.ArrayList;
import java.util.List;

public class PackingRepository {
    private final HikeLogDbHelper dbHelper;

    public PackingRepository(Context context) {
        this.dbHelper = new HikeLogDbHelper(context);
    }

    public List<PackingItem> getDefaultItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(HikeLogContract.PackingItems.TABLE, null, null, null, null, null, HikeLogContract.PackingItems.COL_CATEGORY + ", " + HikeLogContract.PackingItems.COL_IS_ESSENTIAL + " DESC");
        List<PackingItem> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(mapItem(c));
        }
        c.close();
        return list;
    }

    public List<PackingItem> getItemsForTrail(long trailId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(HikeLogContract.PackingItems.TABLE, null, HikeLogContract.PackingItems.COL_TRAIL_ID + "=? OR " + HikeLogContract.PackingItems.COL_TRAIL_ID + " IS NULL", new String[]{String.valueOf(trailId)}, null, null, HikeLogContract.PackingItems.COL_CATEGORY + ", " + HikeLogContract.PackingItems.COL_IS_ESSENTIAL + " DESC");
        List<PackingItem> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(mapItem(c));
        }
        c.close();
        return list;
    }

    public List<UserPackingList> getPackingListForHike(long hikeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                HikeLogContract.UserPackingLists.TABLE + " LEFT JOIN " + HikeLogContract.PackingItems.TABLE + " ON " + HikeLogContract.UserPackingLists.COL_ITEM_ID + "=" + HikeLogContract.PackingItems._ID,
                new String[]{HikeLogContract.UserPackingLists._ID, HikeLogContract.UserPackingLists.COL_USER_ID, HikeLogContract.UserPackingLists.COL_HIKE_ID, HikeLogContract.UserPackingLists.COL_ITEM_ID, HikeLogContract.PackingItems.COL_ITEM_NAME, HikeLogContract.PackingItems.COL_CATEGORY, HikeLogContract.UserPackingLists.COL_IS_PACKED, HikeLogContract.UserPackingLists.COL_QUANTITY},
                HikeLogContract.UserPackingLists.COL_HIKE_ID + "=?",
                new String[]{String.valueOf(hikeId)},
                null, null, HikeLogContract.PackingItems.COL_CATEGORY + ", " + HikeLogContract.PackingItems.COL_IS_ESSENTIAL + " DESC"
        );
        List<UserPackingList> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(mapUserList(c));
        }
        c.close();
        return list;
    }

    public long addPackingItem(long userId, long hikeId, long itemId, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(HikeLogContract.UserPackingLists.COL_USER_ID, userId);
        cv.put(HikeLogContract.UserPackingLists.COL_HIKE_ID, hikeId);
        cv.put(HikeLogContract.UserPackingLists.COL_ITEM_ID, itemId);
        cv.put(HikeLogContract.UserPackingLists.COL_QUANTITY, quantity);
        cv.put(HikeLogContract.UserPackingLists.COL_IS_PACKED, 0);
        return db.insert(HikeLogContract.UserPackingLists.TABLE, null, cv);
    }

    public int updatePackedStatus(long id, boolean isPacked) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(HikeLogContract.UserPackingLists.COL_IS_PACKED, isPacked ? 1 : 0);
        return db.update(HikeLogContract.UserPackingLists.TABLE, cv, HikeLogContract.UserPackingLists._ID + "=?", new String[]{String.valueOf(id)});
    }

    public int updateQuantity(long id, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(HikeLogContract.UserPackingLists.COL_QUANTITY, quantity);
        return db.update(HikeLogContract.UserPackingLists.TABLE, cv, HikeLogContract.UserPackingLists._ID + "=?", new String[]{String.valueOf(id)});
    }

    public int deletePackedItem(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(HikeLogContract.UserPackingLists.TABLE, HikeLogContract.UserPackingLists._ID + "=?", new String[]{String.valueOf(id)});
    }

    public void createPackingListForHike(long userId, long hikeId, List<PackingItem> items) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (PackingItem item : items) {
            ContentValues cv = new ContentValues();
            cv.put(HikeLogContract.UserPackingLists.COL_USER_ID, userId);
            cv.put(HikeLogContract.UserPackingLists.COL_HIKE_ID, hikeId);
            cv.put(HikeLogContract.UserPackingLists.COL_ITEM_ID, item.getId());
            cv.put(HikeLogContract.UserPackingLists.COL_QUANTITY, item.getQuantityDefault());
            cv.put(HikeLogContract.UserPackingLists.COL_IS_PACKED, 0);
            db.insert(HikeLogContract.UserPackingLists.TABLE, null, cv);
        }
    }

    private PackingItem mapItem(Cursor c) {
        return new PackingItem(
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.PackingItems._ID)),
                safeGetLong(c, HikeLogContract.PackingItems.COL_TRAIL_ID),
                c.getString(c.getColumnIndexOrThrow(HikeLogContract.PackingItems.COL_ITEM_NAME)),
                c.getString(c.getColumnIndexOrThrow(HikeLogContract.PackingItems.COL_CATEGORY)),
                c.getInt(c.getColumnIndexOrThrow(HikeLogContract.PackingItems.COL_IS_ESSENTIAL)) == 1,
                c.getInt(c.getColumnIndexOrThrow(HikeLogContract.PackingItems.COL_QUANTITY_DEFAULT))
        );
    }

    private UserPackingList mapUserList(Cursor c) {
        return new UserPackingList(
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.UserPackingLists._ID)),
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.UserPackingLists.COL_USER_ID)),
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.UserPackingLists.COL_HIKE_ID)),
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.UserPackingLists.COL_ITEM_ID)),
                c.getString(c.getColumnIndexOrThrow(HikeLogContract.PackingItems.COL_ITEM_NAME)),
                c.getString(c.getColumnIndexOrThrow(HikeLogContract.PackingItems.COL_CATEGORY)),
                c.getInt(c.getColumnIndexOrThrow(HikeLogContract.UserPackingLists.COL_IS_PACKED)) == 1,
                c.getInt(c.getColumnIndexOrThrow(HikeLogContract.UserPackingLists.COL_QUANTITY))
        );
    }

    private Long safeGetLong(Cursor c, String col) {
        int idx = c.getColumnIndex(col);
        if (idx == -1) return null;
        if (c.isNull(idx)) return null;
        return c.getLong(idx);
    }
}
