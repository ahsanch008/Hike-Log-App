package com.example.myapplication.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import com.example.myapplication.data.db.HikeLogContract;
import com.example.myapplication.data.db.HikeLogDbHelper;
import com.example.myapplication.model.User;

import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UserRepository {
    private final HikeLogDbHelper dbHelper;

    public UserRepository(Context context) {
        this.dbHelper = new HikeLogDbHelper(context);
    }

    public long register(String name, String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(HikeLogContract.Users.TABLE, new String[]{HikeLogContract.Users._ID}, HikeLogContract.Users.COL_EMAIL + "=?", new String[]{email}, null, null, null);
        if (c.moveToFirst()) {
            c.close();
            return -1;
        }
        c.close();
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        String saltStr = Base64.encodeToString(salt, Base64.NO_WRAP);
        String hash = hashPassword(password, salt);
        ContentValues cv = new ContentValues();
        cv.put(HikeLogContract.Users.COL_NAME, name);
        cv.put(HikeLogContract.Users.COL_EMAIL, email);
        cv.put(HikeLogContract.Users.COL_PASSWORD_HASH, hash);
        cv.put(HikeLogContract.Users.COL_SALT, saltStr);
        cv.put(HikeLogContract.Users.COL_CREATED_AT, System.currentTimeMillis());
        return db.insert(HikeLogContract.Users.TABLE, null, cv);
    }

    public long authenticate(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(HikeLogContract.Users.TABLE,
                new String[]{HikeLogContract.Users._ID, HikeLogContract.Users.COL_SALT, HikeLogContract.Users.COL_PASSWORD_HASH},
                HikeLogContract.Users.COL_EMAIL + "=?",
                new String[]{email}, null, null, null);
        if (!c.moveToFirst()) {
            c.close();
            return -1;
        }
        long id = c.getLong(0);
        String saltStr = c.getString(1);
        String storedHash = c.getString(2);
        c.close();
        byte[] salt = Base64.decode(saltStr, Base64.NO_WRAP);
        String calc = hashPassword(password, salt);
        if (storedHash.equals(calc)) return id;
        return -1;
    }

    public User getUserById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(HikeLogContract.Users.TABLE,
                new String[]{HikeLogContract.Users._ID, HikeLogContract.Users.COL_NAME, HikeLogContract.Users.COL_EMAIL},
                HikeLogContract.Users._ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        User u = new User(c.getLong(0), c.getString(1), c.getString(2));
        c.close();
        return u;
    }

    public int updateName(long id, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(HikeLogContract.Users.COL_NAME, name);
        return db.update(HikeLogContract.Users.TABLE, cv, HikeLogContract.Users._ID + "=?", new String[]{String.valueOf(id)});
    }

    private String hashPassword(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

