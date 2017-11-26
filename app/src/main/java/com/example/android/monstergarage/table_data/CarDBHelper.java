package com.example.android.monstergarage.table_data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jujuan on 11/21/2017.
 * This class instantiates a database helper object (CarDBHelper) for the app
 * which connects to the actual raw SQLite database in memory(garage.db)
 * Once a new table is created, the garage.db then returns a
 * (SQLiteDatabase) object to the app, which acts as a middle-man that
 * safely manages database queries
 */

public class CarDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "garage.db";
    private static final int DATABASE_VERSION = 1;

    //Constructs a new instance of this database helper object
    public CarDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_CARS_TABLE = "CREATE TABLE " + GarageContract.CarEntry.TABLE_NAME + "("
                + GarageContract.CarEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GarageContract.CarEntry.COLUMN_CAR_MAKE + " TEXT NOT NULL, "
                + GarageContract.CarEntry.COLUMN_CAR_MODEL + " TEXT NOT NULL, "
                + GarageContract.CarEntry.COLUMN_CAR_YEAR + " TEXT NOT NULL, "
                + GarageContract.CarEntry.COLUMN_CAR_COLOR + " INTEGER NOT NULL, "
                + GarageContract.CarEntry.COLUMN_CAR_PLATE + " TEXT NOT NULL" + ");";

        //Execute the above SQL statement using the Helper's execSQL method
        db.execSQL(SQL_CREATE_CARS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
