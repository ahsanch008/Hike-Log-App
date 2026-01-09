package com.example.myapplication.data.db;

import android.provider.BaseColumns;

public final class HikeLogContract {
    private HikeLogContract() {}

    public static final class Users implements BaseColumns {
        public static final String TABLE = "users";
        public static final String COL_NAME = "name";
        public static final String COL_EMAIL = "email";
        public static final String COL_PASSWORD_HASH = "password_hash";
        public static final String COL_SALT = "salt";
        public static final String COL_CREATED_AT = "created_at";
    }

    public static final class Trails implements BaseColumns {
        public static final String TABLE = "trails";
        public static final String COL_NAME = "name";
        public static final String COL_LOCATION = "location";
        public static final String COL_DIFFICULTY = "difficulty";
        public static final String COL_DISTANCE_KM = "distance_km";
        public static final String COL_ESTIMATED_TIME_MIN = "estimated_time_min";
        public static final String COL_ELEVATION_M = "elevation_m";
        public static final String COL_DESCRIPTION = "description";
        public static final String COL_IMAGE_NAME = "image_name";
    }

    public static final class Hikes implements BaseColumns {
        public static final String TABLE = "hikes";
        public static final String COL_USER_ID = "user_id";
        public static final String COL_TRAIL_ID = "trail_id";
        public static final String COL_TRAIL_NAME = "trail_name";
        public static final String COL_DIFFICULTY = "difficulty";
        public static final String COL_DATE = "date";
        public static final String COL_DURATION_MIN = "duration_min";
        public static final String COL_NOTES = "notes";
        public static final String COL_ELEVATION_POINTS = "elevation_points";
    }

    public static final class TrailReviews implements BaseColumns {
        public static final String TABLE = "trail_reviews";
        public static final String COL_TRAIL_ID = "trail_id";
        public static final String COL_USER_ID = "user_id";
        public static final String COL_RATING = "rating";
        public static final String COL_COMMENT = "comment";
        public static final String COL_CREATED_AT = "created_at";
    }

    public static final class PackingItems implements BaseColumns {
        public static final String TABLE = "packing_items";
        public static final String COL_TRAIL_ID = "trail_id";
        public static final String COL_ITEM_NAME = "item_name";
        public static final String COL_CATEGORY = "category";
        public static final String COL_IS_ESSENTIAL = "is_essential";
        public static final String COL_QUANTITY_DEFAULT = "quantity_default";
    }

    public static final class UserPackingLists implements BaseColumns {
        public static final String TABLE = "user_packing_lists";
        public static final String COL_USER_ID = "user_id";
        public static final String COL_HIKE_ID = "hike_id";
        public static final String COL_ITEM_ID = "item_id";
        public static final String COL_IS_PACKED = "is_packed";
        public static final String COL_QUANTITY = "quantity";
    }

    public static final class WildlifeSightings implements BaseColumns {
        public static final String TABLE = "wildlife_sightings";
        public static final String COL_HIKE_ID = "hike_id";
        public static final String COL_ANIMAL_NAME = "animal_name";
        public static final String COL_SPECIES = "species";
        public static final String COL_QUANTITY = "quantity";
        public static final String COL_NOTES = "notes";
        public static final String COL_TIMESTAMP = "timestamp";
    }

    public static final class CommonWildlife implements BaseColumns {
        public static final String TABLE = "common_wildlife";
        public static final String COL_TRAIL_ID = "trail_id";
        public static final String COL_ANIMAL_NAME = "animal_name";
        public static final String COL_DESCRIPTION = "description";
        public static final String COL_SEASON = "season";
        public static final String COL_LIKELIHOOD = "likelihood";
    }
}
