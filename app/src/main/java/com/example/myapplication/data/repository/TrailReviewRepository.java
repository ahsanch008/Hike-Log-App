package com.example.myapplication.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.data.db.HikeLogContract;
import com.example.myapplication.data.db.HikeLogDbHelper;
import com.example.myapplication.model.TrailReview;

import java.util.ArrayList;
import java.util.List;

public class TrailReviewRepository {
    private final HikeLogDbHelper dbHelper;

    public TrailReviewRepository(Context context) {
        this.dbHelper = new HikeLogDbHelper(context);
    }

    public long addReview(long trailId, long userId, int rating, String comment) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(HikeLogContract.TrailReviews.COL_TRAIL_ID, trailId);
        cv.put(HikeLogContract.TrailReviews.COL_USER_ID, userId);
        cv.put(HikeLogContract.TrailReviews.COL_RATING, rating);
        cv.put(HikeLogContract.TrailReviews.COL_COMMENT, comment);
        cv.put(HikeLogContract.TrailReviews.COL_CREATED_AT, System.currentTimeMillis());
        return db.insert(HikeLogContract.TrailReviews.TABLE, null, cv);
    }

    public List<TrailReview> getReviewsForTrail(long trailId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT tr." + HikeLogContract.TrailReviews._ID + ", " +
                        "tr." + HikeLogContract.TrailReviews.COL_TRAIL_ID + ", " +
                        "tr." + HikeLogContract.TrailReviews.COL_USER_ID + ", " +
                        "u." + HikeLogContract.Users.COL_NAME + ", " +
                        "tr." + HikeLogContract.TrailReviews.COL_RATING + ", " +
                        "tr." + HikeLogContract.TrailReviews.COL_COMMENT + ", " +
                        "tr." + HikeLogContract.TrailReviews.COL_CREATED_AT + " " +
                "FROM " + HikeLogContract.TrailReviews.TABLE + " tr " +
                "LEFT JOIN " + HikeLogContract.Users.TABLE + " u " +
                "ON tr." + HikeLogContract.TrailReviews.COL_USER_ID + " = u." + HikeLogContract.Users._ID + " " +
                "WHERE tr." + HikeLogContract.TrailReviews.COL_TRAIL_ID + " = ? " +
                "ORDER BY tr." + HikeLogContract.TrailReviews.COL_CREATED_AT + " DESC",
                new String[]{String.valueOf(trailId)}
        );
        List<TrailReview> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(map(c));
        }
        c.close();
        return list;
    }

    public TrailReview getReviewByUserAndTrail(long userId, long trailId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT tr." + HikeLogContract.TrailReviews._ID + ", " +
                        "tr." + HikeLogContract.TrailReviews.COL_TRAIL_ID + ", " +
                        "tr." + HikeLogContract.TrailReviews.COL_USER_ID + ", " +
                        "u." + HikeLogContract.Users.COL_NAME + ", " +
                        "tr." + HikeLogContract.TrailReviews.COL_RATING + ", " +
                        "tr." + HikeLogContract.TrailReviews.COL_COMMENT + ", " +
                        "tr." + HikeLogContract.TrailReviews.COL_CREATED_AT + " " +
                "FROM " + HikeLogContract.TrailReviews.TABLE + " tr " +
                "LEFT JOIN " + HikeLogContract.Users.TABLE + " u " +
                "ON tr." + HikeLogContract.TrailReviews.COL_USER_ID + " = u." + HikeLogContract.Users._ID + " " +
                "WHERE tr." + HikeLogContract.TrailReviews.COL_USER_ID + " = ? " +
                "AND tr." + HikeLogContract.TrailReviews.COL_TRAIL_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(trailId)}
        );
        TrailReview review = null;
        if (c.moveToFirst()) {
            review = map(c);
        }
        c.close();
        return review;
    }

    public double getAverageRating(long trailId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COALESCE(AVG(" + HikeLogContract.TrailReviews.COL_RATING + "),0) FROM " + HikeLogContract.TrailReviews.TABLE + " WHERE " + HikeLogContract.TrailReviews.COL_TRAIL_ID + "=?", new String[]{String.valueOf(trailId)});
        double avg = c.moveToFirst() ? c.getDouble(0) : 0.0;
        c.close();
        return avg;
    }

    public int getReviewCount(long trailId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + HikeLogContract.TrailReviews.TABLE + " WHERE " + HikeLogContract.TrailReviews.COL_TRAIL_ID + "=?", new String[]{String.valueOf(trailId)});
        int count = c.moveToFirst() ? c.getInt(0) : 0;
        c.close();
        return count;
    }

    public int updateReview(long reviewId, int rating, String comment) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(HikeLogContract.TrailReviews.COL_RATING, rating);
        cv.put(HikeLogContract.TrailReviews.COL_COMMENT, comment);
        return db.update(HikeLogContract.TrailReviews.TABLE, cv, HikeLogContract.TrailReviews._ID + "=?", new String[]{String.valueOf(reviewId)});
    }

    public int deleteReview(long reviewId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(HikeLogContract.TrailReviews.TABLE, HikeLogContract.TrailReviews._ID + "=?", new String[]{String.valueOf(reviewId)});
    }

    private TrailReview map(Cursor c) {
        return new TrailReview(
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.TrailReviews._ID)),
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.TrailReviews.COL_TRAIL_ID)),
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.TrailReviews.COL_USER_ID)),
                c.getString(c.getColumnIndexOrThrow(HikeLogContract.Users.COL_NAME)),
                c.getInt(c.getColumnIndexOrThrow(HikeLogContract.TrailReviews.COL_RATING)),
                c.getString(c.getColumnIndexOrThrow(HikeLogContract.TrailReviews.COL_COMMENT)),
                c.getLong(c.getColumnIndexOrThrow(HikeLogContract.TrailReviews.COL_CREATED_AT))
        );
    }
}
