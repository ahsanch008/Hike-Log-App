package com.example.myapplication.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HikeLogDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "hikelog.db";
    public static final int DB_VERSION = 4;

    public HikeLogDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + HikeLogContract.Users.TABLE + " ("
                + HikeLogContract.Users._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HikeLogContract.Users.COL_NAME + " TEXT NOT NULL,"
                + HikeLogContract.Users.COL_EMAIL + " TEXT NOT NULL UNIQUE,"
                + HikeLogContract.Users.COL_PASSWORD_HASH + " TEXT NOT NULL,"
                + HikeLogContract.Users.COL_SALT + " TEXT NOT NULL,"
                + HikeLogContract.Users.COL_CREATED_AT + " INTEGER NOT NULL"
                + ")";

        String createTrails = "CREATE TABLE " + HikeLogContract.Trails.TABLE + " ("
                + HikeLogContract.Trails._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HikeLogContract.Trails.COL_NAME + " TEXT NOT NULL,"
                + HikeLogContract.Trails.COL_LOCATION + " TEXT NOT NULL,"
                + HikeLogContract.Trails.COL_DIFFICULTY + " TEXT NOT NULL,"
                + HikeLogContract.Trails.COL_DISTANCE_KM + " REAL NOT NULL,"
                + HikeLogContract.Trails.COL_ESTIMATED_TIME_MIN + " INTEGER NOT NULL,"
                + HikeLogContract.Trails.COL_ELEVATION_M + " INTEGER NOT NULL,"
                + HikeLogContract.Trails.COL_DESCRIPTION + " TEXT,"
                + HikeLogContract.Trails.COL_IMAGE_NAME + " TEXT"
                + ")";

        String createHikes = "CREATE TABLE " + HikeLogContract.Hikes.TABLE + " ("
                + HikeLogContract.Hikes._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HikeLogContract.Hikes.COL_USER_ID + " INTEGER NOT NULL,"
                + HikeLogContract.Hikes.COL_TRAIL_ID + " INTEGER,"
                + HikeLogContract.Hikes.COL_TRAIL_NAME + " TEXT NOT NULL,"
                + HikeLogContract.Hikes.COL_DIFFICULTY + " TEXT NOT NULL,"
                + HikeLogContract.Hikes.COL_DATE + " INTEGER NOT NULL,"
                + HikeLogContract.Hikes.COL_DURATION_MIN + " INTEGER NOT NULL,"
                + HikeLogContract.Hikes.COL_NOTES + " TEXT,"
                + HikeLogContract.Hikes.COL_ELEVATION_POINTS + " TEXT,"
                + "FOREIGN KEY(" + HikeLogContract.Hikes.COL_USER_ID + ") REFERENCES " + HikeLogContract.Users.TABLE + "(" + HikeLogContract.Users._ID + "),"
                + "FOREIGN KEY(" + HikeLogContract.Hikes.COL_TRAIL_ID + ") REFERENCES " + HikeLogContract.Trails.TABLE + "(" + HikeLogContract.Trails._ID + ")"
                + ")";

        String createTrailReviews = "CREATE TABLE " + HikeLogContract.TrailReviews.TABLE + " ("
                + HikeLogContract.TrailReviews._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HikeLogContract.TrailReviews.COL_TRAIL_ID + " INTEGER NOT NULL,"
                + HikeLogContract.TrailReviews.COL_USER_ID + " INTEGER NOT NULL,"
                + HikeLogContract.TrailReviews.COL_RATING + " INTEGER NOT NULL CHECK(" + HikeLogContract.TrailReviews.COL_RATING + " BETWEEN 1 AND 5),"
                + HikeLogContract.TrailReviews.COL_COMMENT + " TEXT,"
                + HikeLogContract.TrailReviews.COL_CREATED_AT + " INTEGER NOT NULL,"
                + "FOREIGN KEY(" + HikeLogContract.TrailReviews.COL_TRAIL_ID + ") REFERENCES " + HikeLogContract.Trails.TABLE + "(" + HikeLogContract.Trails._ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY(" + HikeLogContract.TrailReviews.COL_USER_ID + ") REFERENCES " + HikeLogContract.Users.TABLE + "(" + HikeLogContract.Users._ID + ") ON DELETE CASCADE,"
                + "UNIQUE(" + HikeLogContract.TrailReviews.COL_TRAIL_ID + ", " + HikeLogContract.TrailReviews.COL_USER_ID + ")"
                + ")";

        String createPackingItems = "CREATE TABLE " + HikeLogContract.PackingItems.TABLE + " ("
                + HikeLogContract.PackingItems._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HikeLogContract.PackingItems.COL_TRAIL_ID + " INTEGER,"
                + HikeLogContract.PackingItems.COL_ITEM_NAME + " TEXT NOT NULL,"
                + HikeLogContract.PackingItems.COL_CATEGORY + " TEXT NOT NULL,"
                + HikeLogContract.PackingItems.COL_IS_ESSENTIAL + " INTEGER NOT NULL DEFAULT 0,"
                + HikeLogContract.PackingItems.COL_QUANTITY_DEFAULT + " INTEGER NOT NULL DEFAULT 1,"
                + "FOREIGN KEY(" + HikeLogContract.PackingItems.COL_TRAIL_ID + ") REFERENCES " + HikeLogContract.Trails.TABLE + "(" + HikeLogContract.Trails._ID + ") ON DELETE CASCADE"
                + ")";

        String createUserPackingLists = "CREATE TABLE " + HikeLogContract.UserPackingLists.TABLE + " ("
                + HikeLogContract.UserPackingLists._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HikeLogContract.UserPackingLists.COL_USER_ID + " INTEGER NOT NULL,"
                + HikeLogContract.UserPackingLists.COL_HIKE_ID + " INTEGER NOT NULL,"
                + HikeLogContract.UserPackingLists.COL_ITEM_ID + " INTEGER NOT NULL,"
                + HikeLogContract.UserPackingLists.COL_IS_PACKED + " INTEGER NOT NULL DEFAULT 0,"
                + HikeLogContract.UserPackingLists.COL_QUANTITY + " INTEGER NOT NULL DEFAULT 1,"
                + "FOREIGN KEY(" + HikeLogContract.UserPackingLists.COL_USER_ID + ") REFERENCES " + HikeLogContract.Users.TABLE + "(" + HikeLogContract.Users._ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY(" + HikeLogContract.UserPackingLists.COL_HIKE_ID + ") REFERENCES " + HikeLogContract.Hikes.TABLE + "(" + HikeLogContract.Hikes._ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY(" + HikeLogContract.UserPackingLists.COL_ITEM_ID + ") REFERENCES " + HikeLogContract.PackingItems.TABLE + "(" + HikeLogContract.PackingItems._ID + ") ON DELETE CASCADE,"
                + "UNIQUE(" + HikeLogContract.UserPackingLists.COL_HIKE_ID + ", " + HikeLogContract.UserPackingLists.COL_ITEM_ID + ")"
                + ")";

        String createWildlifeSightings = "CREATE TABLE " + HikeLogContract.WildlifeSightings.TABLE + " ("
                + HikeLogContract.WildlifeSightings._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HikeLogContract.WildlifeSightings.COL_HIKE_ID + " INTEGER NOT NULL,"
                + HikeLogContract.WildlifeSightings.COL_ANIMAL_NAME + " TEXT NOT NULL,"
                + HikeLogContract.WildlifeSightings.COL_SPECIES + " TEXT,"
                + HikeLogContract.WildlifeSightings.COL_QUANTITY + " INTEGER NOT NULL DEFAULT 1,"
                + HikeLogContract.WildlifeSightings.COL_NOTES + " TEXT,"
                + HikeLogContract.WildlifeSightings.COL_TIMESTAMP + " INTEGER NOT NULL,"
                + "FOREIGN KEY(" + HikeLogContract.WildlifeSightings.COL_HIKE_ID + ") REFERENCES " + HikeLogContract.Hikes.TABLE + "(" + HikeLogContract.Hikes._ID + ") ON DELETE CASCADE"
                + ")";

        String createCommonWildlife = "CREATE TABLE " + HikeLogContract.CommonWildlife.TABLE + " ("
                + HikeLogContract.CommonWildlife._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HikeLogContract.CommonWildlife.COL_TRAIL_ID + " INTEGER NOT NULL,"
                + HikeLogContract.CommonWildlife.COL_ANIMAL_NAME + " TEXT NOT NULL,"
                + HikeLogContract.CommonWildlife.COL_DESCRIPTION + " TEXT,"
                + HikeLogContract.CommonWildlife.COL_SEASON + " TEXT,"
                + HikeLogContract.CommonWildlife.COL_LIKELIHOOD + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + HikeLogContract.CommonWildlife.COL_TRAIL_ID + ") REFERENCES " + HikeLogContract.Trails.TABLE + "(" + HikeLogContract.Trails._ID + ") ON DELETE CASCADE"
                + ")";

        db.execSQL(createUsers);
        db.execSQL(createTrails);
        db.execSQL(createHikes);
        db.execSQL(createTrailReviews);
        db.execSQL(createPackingItems);
        db.execSQL(createUserPackingLists);
        db.execSQL(createWildlifeSightings);
        db.execSQL(createCommonWildlife);
        insertDefaultTrails(db);
        insertDefaultPackingItems(db);
        insertDefaultCommonWildlife(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + HikeLogContract.Hikes.TABLE + " ADD COLUMN " + HikeLogContract.Hikes.COL_ELEVATION_POINTS + " TEXT");
            db.execSQL(createTrailReviewsSQL());
            db.execSQL(createPackingItemsSQL());
            db.execSQL(createUserPackingListsSQL());
            db.execSQL(createWildlifeSightingsSQL());
            db.execSQL(createCommonWildlifeSQL());
            insertDefaultPackingItems(db);
            insertDefaultCommonWildlife(db);
        }
    }

    private void insertDefaultTrails(SQLiteDatabase db) {
        String[][] trails = new String[][]{
                {"Sunrise Peak Trail", "Rocky Mountain Park", "Hard", "12.5", "300", "1450", "Challenging ascent with alpine views.", null},
                {"Whispering Pines Loop", "Evergreen Forest Reserve", "Easy", "4.2", "75", "180", "Gentle loop through pine forest.", null},
                {"Ridge Walk", "Highland Reserve", "Medium", "8.0", "210", "650", "Rolling ridges and forest views.", null},
                {"Lake Loop", "Blue Lake", "Easy", "4.0", "100", "80", "Loop around the lake with picnic spots.", null},
                {"Canyon Trek", "Red Canyon", "Medium", "9.5", "240", "400", "Canyon floor and cliffside views.", null}
        };
        for (String[] t : trails) {
            ContentValues cv = new ContentValues();
            cv.put(HikeLogContract.Trails.COL_NAME, t[0]);
            cv.put(HikeLogContract.Trails.COL_LOCATION, t[1]);
            cv.put(HikeLogContract.Trails.COL_DIFFICULTY, t[2]);
            cv.put(HikeLogContract.Trails.COL_DISTANCE_KM, Double.parseDouble(t[3]));
            cv.put(HikeLogContract.Trails.COL_ESTIMATED_TIME_MIN, Integer.parseInt(t[4]));
            cv.put(HikeLogContract.Trails.COL_ELEVATION_M, Integer.parseInt(t[5]));
            cv.put(HikeLogContract.Trails.COL_DESCRIPTION, t[6]);
            if (t.length > 7 && t[7] != null) {
                cv.put(HikeLogContract.Trails.COL_IMAGE_NAME, t[7]);
            }
            db.insert(HikeLogContract.Trails.TABLE, null, cv);
        }
    }

    private void insertDefaultPackingItems(SQLiteDatabase db) {
        String[][] items = new String[][]{
                {"Water", "Hydration", "1", "1"},
                {"Water bottle", "Hydration", "1", "2"},
                {"Snacks/Energy bars", "Food", "1", "4"},
                {"First aid kit", "Safety", "1", "1"},
                {"Map/Trail guide", "Navigation", "1", "1"},
                {"Compass", "Navigation", "1", "1"},
                {"Flashlight/Headlamp", "Safety", "1", "1"},
                {"Extra batteries", "Safety", "1", "1"},
                {"Sunscreen", "Protection", "1", "1"},
                {"Sunglasses", "Protection", "1", "1"},
                {"Hat", "Protection", "1", "1"},
                {"Insect repellent", "Protection", "0", "1"},
                {"Rain jacket", "Clothing", "1", "1"},
                {"Extra layers", "Clothing", "1", "1"},
                {"Hiking boots", "Clothing", "1", "1"},
                {"Socks (extra pair)", "Clothing", "0", "1"},
                {"Multi-tool/Knife", "Tools", "1", "1"},
                {"Fire starter", "Tools", "0", "1"},
                {"Emergency whistle", "Safety", "1", "1"},
                {"Trash bag", "Environment", "1", "1"}
        };
        for (String[] item : items) {
            ContentValues cv = new ContentValues();
            cv.put(HikeLogContract.PackingItems.COL_ITEM_NAME, item[0]);
            cv.put(HikeLogContract.PackingItems.COL_CATEGORY, item[1]);
            cv.put(HikeLogContract.PackingItems.COL_IS_ESSENTIAL, Integer.parseInt(item[2]));
            cv.put(HikeLogContract.PackingItems.COL_QUANTITY_DEFAULT, Integer.parseInt(item[3]));
            db.insert(HikeLogContract.PackingItems.TABLE, null, cv);
        }
    }

    private void insertDefaultCommonWildlife(SQLiteDatabase db) {
        String[][] wildlife = new String[][]{
                {"Golden Eagle", "Majestic bird of prey often seen soaring above the ridgelines", "All Year", "Rare"},
                {"Himalayan Monal", "Colorful pheasant, Pakistan's national bird", "Spring/Summer", "Uncommon"},
                {"Musk Deer", "Small deer species found in high altitude forests", "Autumn/Winter", "Rare"},
                {"Snow Leopard", "Elusive big cat of the high mountains", "Winter", "Extremely Rare"},
                {"Blue Sheep", "Mountain goat species commonly seen on rocky slopes", "All Year", "Common"},
                {"Red Fox", "Adaptable predator found in various habitats", "All Year", "Common"},
                {"Himalayan Black Bear", "Large bear species found in forested areas", "Spring/Autumn", "Rare"},
                {"Leopard", "Spotted cat that sometimes descends from higher elevations", "All Year", "Rare"},
                {"Various Butterflies", "Including Painted Lady and Blue Tiger", "Spring/Summer", "Common"},
                {"Monkeys", "Rhesus macaque groups in lower elevation forests", "All Year", "Common"}
        };
        for (String[] w : wildlife) {
            ContentValues cv = new ContentValues();
            cv.put(HikeLogContract.CommonWildlife.COL_ANIMAL_NAME, w[0]);
            cv.put(HikeLogContract.CommonWildlife.COL_DESCRIPTION, w[1]);
            cv.put(HikeLogContract.CommonWildlife.COL_SEASON, w[2]);
            cv.put(HikeLogContract.CommonWildlife.COL_LIKELIHOOD, w[3]);
            db.insert(HikeLogContract.CommonWildlife.TABLE, null, cv);
        }
    }

    private String createTrailReviewsSQL() {
        return "CREATE TABLE " + HikeLogContract.TrailReviews.TABLE + " ("
                + HikeLogContract.TrailReviews._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HikeLogContract.TrailReviews.COL_TRAIL_ID + " INTEGER NOT NULL,"
                + HikeLogContract.TrailReviews.COL_USER_ID + " INTEGER NOT NULL,"
                + HikeLogContract.TrailReviews.COL_RATING + " INTEGER NOT NULL CHECK(" + HikeLogContract.TrailReviews.COL_RATING + " BETWEEN 1 AND 5),"
                + HikeLogContract.TrailReviews.COL_COMMENT + " TEXT,"
                + HikeLogContract.TrailReviews.COL_CREATED_AT + " INTEGER NOT NULL,"
                + "FOREIGN KEY(" + HikeLogContract.TrailReviews.COL_TRAIL_ID + ") REFERENCES " + HikeLogContract.Trails.TABLE + "(" + HikeLogContract.Trails._ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY(" + HikeLogContract.TrailReviews.COL_USER_ID + ") REFERENCES " + HikeLogContract.Users.TABLE + "(" + HikeLogContract.Users._ID + ") ON DELETE CASCADE,"
                + "UNIQUE(" + HikeLogContract.TrailReviews.COL_TRAIL_ID + ", " + HikeLogContract.TrailReviews.COL_USER_ID + ")"
                + ")";
    }

    private String createPackingItemsSQL() {
        return "CREATE TABLE " + HikeLogContract.PackingItems.TABLE + " ("
                + HikeLogContract.PackingItems._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HikeLogContract.PackingItems.COL_TRAIL_ID + " INTEGER,"
                + HikeLogContract.PackingItems.COL_ITEM_NAME + " TEXT NOT NULL,"
                + HikeLogContract.PackingItems.COL_CATEGORY + " TEXT NOT NULL,"
                + HikeLogContract.PackingItems.COL_IS_ESSENTIAL + " INTEGER NOT NULL DEFAULT 0,"
                + HikeLogContract.PackingItems.COL_QUANTITY_DEFAULT + " INTEGER NOT NULL DEFAULT 1,"
                + "FOREIGN KEY(" + HikeLogContract.PackingItems.COL_TRAIL_ID + ") REFERENCES " + HikeLogContract.Trails.TABLE + "(" + HikeLogContract.Trails._ID + ") ON DELETE CASCADE"
                + ")";
    }

    private String createUserPackingListsSQL() {
        return "CREATE TABLE " + HikeLogContract.UserPackingLists.TABLE + " ("
                + HikeLogContract.UserPackingLists._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HikeLogContract.UserPackingLists.COL_USER_ID + " INTEGER NOT NULL,"
                + HikeLogContract.UserPackingLists.COL_HIKE_ID + " INTEGER NOT NULL,"
                + HikeLogContract.UserPackingLists.COL_ITEM_ID + " INTEGER NOT NULL,"
                + HikeLogContract.UserPackingLists.COL_IS_PACKED + " INTEGER NOT NULL DEFAULT 0,"
                + HikeLogContract.UserPackingLists.COL_QUANTITY + " INTEGER NOT NULL DEFAULT 1,"
                + "FOREIGN KEY(" + HikeLogContract.UserPackingLists.COL_USER_ID + ") REFERENCES " + HikeLogContract.Users.TABLE + "(" + HikeLogContract.Users._ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY(" + HikeLogContract.UserPackingLists.COL_HIKE_ID + ") REFERENCES " + HikeLogContract.Hikes.TABLE + "(" + HikeLogContract.Hikes._ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY(" + HikeLogContract.UserPackingLists.COL_ITEM_ID + ") REFERENCES " + HikeLogContract.PackingItems.TABLE + "(" + HikeLogContract.PackingItems._ID + ") ON DELETE CASCADE,"
                + "UNIQUE(" + HikeLogContract.UserPackingLists.COL_HIKE_ID + ", " + HikeLogContract.UserPackingLists.COL_ITEM_ID + ")"
                + ")";
    }

    private String createWildlifeSightingsSQL() {
        return "CREATE TABLE " + HikeLogContract.WildlifeSightings.TABLE + " ("
                + HikeLogContract.WildlifeSightings._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HikeLogContract.WildlifeSightings.COL_HIKE_ID + " INTEGER NOT NULL,"
                + HikeLogContract.WildlifeSightings.COL_ANIMAL_NAME + " TEXT NOT NULL,"
                + HikeLogContract.WildlifeSightings.COL_SPECIES + " TEXT,"
                + HikeLogContract.WildlifeSightings.COL_QUANTITY + " INTEGER NOT NULL DEFAULT 1,"
                + HikeLogContract.WildlifeSightings.COL_NOTES + " TEXT,"
                + HikeLogContract.WildlifeSightings.COL_TIMESTAMP + " INTEGER NOT NULL,"
                + "FOREIGN KEY(" + HikeLogContract.WildlifeSightings.COL_HIKE_ID + ") REFERENCES " + HikeLogContract.Hikes.TABLE + "(" + HikeLogContract.Hikes._ID + ") ON DELETE CASCADE"
                + ")";
    }

    private String createCommonWildlifeSQL() {
        return "CREATE TABLE " + HikeLogContract.CommonWildlife.TABLE + " ("
                + HikeLogContract.CommonWildlife._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HikeLogContract.CommonWildlife.COL_TRAIL_ID + " INTEGER NOT NULL,"
                + HikeLogContract.CommonWildlife.COL_ANIMAL_NAME + " TEXT NOT NULL,"
                + HikeLogContract.CommonWildlife.COL_DESCRIPTION + " TEXT,"
                + HikeLogContract.CommonWildlife.COL_SEASON + " TEXT,"
                + HikeLogContract.CommonWildlife.COL_LIKELIHOOD + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + HikeLogContract.CommonWildlife.COL_TRAIL_ID + ") REFERENCES " + HikeLogContract.Trails.TABLE + "(" + HikeLogContract.Trails._ID + ") ON DELETE CASCADE"
                + ")";
    }
}
