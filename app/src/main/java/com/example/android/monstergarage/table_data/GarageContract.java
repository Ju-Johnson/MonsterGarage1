package com.example.android.monstergarage.table_data;

import android.content.ContentResolver;
import android.graphics.Color;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jujuan on 11/21/2017.
 * This contract class is used for setting up
 * name constants that will be used throughout
 * the garage database instantiations
 */

public final class GarageContract {

    //To prevent someone from accidentally instantiating this contract class,
    //make constructor empty
    private GarageContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.monstergarage";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CARS_TABLE = "cars";


    /**
     * This inner class represents one table/ a single car
     * Defines all constant values for the cars table in the database
     * Each table in the database must implement the BaseColumns class
     */
    public static abstract class CarEntry implements BaseColumns{

        //The content Uri to access the car data table from the content provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CARS_TABLE);
        //The MIME type for a list of cars
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_CARS_TABLE;
        //The MIME type for a single car
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_CARS_TABLE;

        public static final String TABLE_NAME = "cars";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_CAR_MAKE = "make";
        public static final String COLUMN_CAR_MODEL = "model";
        public static final String COLUMN_CAR_YEAR = "year";
        public static final String COLUMN_CAR_COLOR = "color";
        public static final String COLUMN_CAR_PLATE = "plate";

        //Possible color values
        public static final int COLOR_OTHER = Color.WHITE;
        public static final int COLOR_WHITE = Color.WHITE;
        public static final int COLOR_BLACK = Color.BLACK;
        public static final int COLOR_GRAY = Color.GRAY;
        public static final int COLOR_SILVER = Color.GRAY;
        public static final int COLOR_BROWN = Color.parseColor("#795548");
        public static final int COLOR_RED = Color.RED;
        public static final int COLOR_BLUE = Color.BLUE;
        public static final int COLOR_GREEN = Color.GREEN;
        public static final int COLOR_YELLOW = Color.YELLOW;
        public static final int COLOR_ORANGE = Color.parseColor("#FF9800");
        public static final int COLOR_PURPLE = Color.parseColor("#9C27B0");

    }


}
