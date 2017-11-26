package com.example.android.monstergarage.table_data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Jujuan on 11/22/2017.
 */

public class GarageProvider extends ContentProvider {

    private static final String LOG_TAG = GarageProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int CARS_TABLE_CODE = 100;
    private static final int CARS_ID_CODE = 101;
    private CarDBHelper carDBHelper;

    static {
        sUriMatcher.addURI(GarageContract.CONTENT_AUTHORITY, "cars", CARS_TABLE_CODE);
        sUriMatcher.addURI(GarageContract.CONTENT_AUTHORITY, "cars/#", CARS_ID_CODE);
    }


    @Override
    public boolean onCreate() {

        carDBHelper = new CarDBHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String where, @Nullable String[] where_args, @Nullable String sortOrder) {

        //Open/get readable database
        SQLiteDatabase garageDb = carDBHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor returnedTable_cursor = null;

        // Figure out if the URI matcher can match the URI to a specific path code
        int match = sUriMatcher.match(uri);
        switch (match){
            //Query request that operates on the whole cars table
            case CARS_TABLE_CODE:

                //Returns the entire Cars table as a cursor object
                returnedTable_cursor = garageDb.query(GarageContract.CarEntry.TABLE_NAME,
                        columns, where, where_args, null, null, sortOrder);
                break;
            //Query request that operates on one individual row/car in table
            case CARS_ID_CODE:
                 /**
                    String columns = {CarEntry.COLUMN_CAR_MAKE, CarEntry.COLUMN_CAR_MODEL};
                    String where = CarEntry.COLUMN_CAR_YEAR + "=?";
                    String[] where_args = {"2000"};
                    Cursor returnedQuery_Cursor = garageDb.query(
                            CarEntry.TABLE_NAME, columns, where, where_args, null, null, null);

                  //This query example says: SELECT make, model FROM cars WHERE year = "2000"
                  //The query method's input constructor format is:
                  //TABLE - COLUMNS - WHERE - WHERE_ARGS - GROUP BY - HAVING - SORT BY
                  */

                // For every "?" in the where clause, we need to have a searchable element in the where_ars
                // that will fill in the "?". Since we have 1 question mark in the
                // where, we have 1 String in the where_ars String array.
                where = GarageContract.CarEntry._ID + "=?";
                where_args = new String[] {String.valueOf(ContentUris.parseId(uri))};

                //Query that returns a specific row from the Cars table, based on the
                //given searchable element,in this case the car unique ID.
                returnedTable_cursor = garageDb.query(GarageContract.CarEntry.TABLE_NAME,
                        columns, where, where_args, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //Set notification URI on the cursor so we know what content URI the cursor
        //was created for. If the data at the URI changes, then we update the cursor
        returnedTable_cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnedTable_cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match){
            case CARS_TABLE_CODE:
                return GarageContract.CarEntry.CONTENT_LIST_TYPE;
            case CARS_ID_CODE:
                return GarageContract.CarEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI "+ uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);
        switch (match){
            //Only one case because a new car is always inserted to the whole table
            case CARS_TABLE_CODE:
                return insertCarHelper(uri, values);
            //If path's code does not match the whole table entry code then error operation
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    /**
     * Insert a car into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertCarHelper(Uri uri, ContentValues values){

        //Sanity checks to ensure that the user inputs all data correctly
        //Before inputting harmful data or format into database
        String make = values.getAsString(GarageContract.CarEntry.COLUMN_CAR_MAKE);
        String model = values.getAsString(GarageContract.CarEntry.COLUMN_CAR_MODEL);
        String year = values.getAsString(GarageContract.CarEntry.COLUMN_CAR_YEAR);
        String plate = values.getAsString(GarageContract.CarEntry.COLUMN_CAR_PLATE);

        if(make == null){
            throw new IllegalArgumentException("Must enter car make");
        }
        if(model == null){
            throw new IllegalArgumentException("Must enter car model");
        }
        if(year == null){
            throw new IllegalArgumentException("Must enter car year");
        }
        if(plate == null){
            throw new IllegalArgumentException("Must enter car license plate");
        }

        //Proceed to inserting data by opening/getting writeable database
        SQLiteDatabase garageDb = carDBHelper.getWritableDatabase();

        // Insert the new car with the given values
        //int is returned that determines insert successful or not
        long id = garageDb.insert(GarageContract.CarEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify al listeners that the data has changed for the specific car content URI
        //such as "content://com.example.android.monstergarage/cars"
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String where, @Nullable String[] where_args) {
        //Opening/getting writeable database
        SQLiteDatabase garageDb = carDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match){
            case CARS_TABLE_CODE:

                //Delete all rows in the entire table that match the where and where_args
                rowsDeleted = garageDb.delete(GarageContract.CarEntry.TABLE_NAME, where, where_args);

                if(rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;

            case CARS_ID_CODE:

                //Delete a single row in the table that matches the given unique ID in the URI
                where = GarageContract.CarEntry._ID + "=?";
                where_args = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = garageDb.delete(GarageContract.CarEntry.TABLE_NAME, where, where_args);

                if(rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String where, @Nullable String[] where_args) {

        final int match = sUriMatcher.match(uri);
        switch (match){
            case CARS_TABLE_CODE:
                return updateCarHelper(uri, values, where, where_args);

            case CARS_ID_CODE:
                // For the CAR_ID code, extract out the ID from the URI,
                // so we know which row to update. where clause will be "_id=?" and where
                // arguments will be a String array containing the actual ID we want to match.
                where = GarageContract.CarEntry._ID + "=?";
                where_args = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateCarHelper(uri, values, where, where_args);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    /**
     * Update cars in the database with the given content values. Apply the changes to the rows
     * specified in the where and where_args(which could be 0 or 1 or more cars).
     * Return the number of rows that were successfully updated.
     */
    private int updateCarHelper(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String where, @Nullable String[] where_args){
        //If no new values have been added/updated then don't try to update database
        if(values.size() == 0){
            return 0;
        }
        //Yet since this is an update call, not all fields may be updated so check
        //to see which new values were added/updated first, then sanity check it
        if(values.containsKey(GarageContract.CarEntry.COLUMN_CAR_MAKE)){
            String make = values.getAsString(GarageContract.CarEntry.COLUMN_CAR_MAKE);
            if(make == null){
                throw new IllegalArgumentException("Must enter car make");
            }
        }
        if(values.containsKey(GarageContract.CarEntry.COLUMN_CAR_MAKE)){
            String model = values.getAsString(GarageContract.CarEntry.COLUMN_CAR_MODEL);
            if(model == null){
                throw new IllegalArgumentException("Must enter car model");
            }
        }
        if(values.containsKey(GarageContract.CarEntry.COLUMN_CAR_MAKE)){
            String year = values.getAsString(GarageContract.CarEntry.COLUMN_CAR_YEAR);
            if(year == null){
                throw new IllegalArgumentException("Must enter car year");
            }
        }
        if(values.containsKey(GarageContract.CarEntry.COLUMN_CAR_MAKE)){
            String plate = values.getAsString(GarageContract.CarEntry.COLUMN_CAR_PLATE);
            if(plate == null){
                throw new IllegalArgumentException("Must enter car license plate");
            }
        }

        //Proceed to updating data by opening/getting writeable database
        SQLiteDatabase garageDb = carDBHelper.getWritableDatabase();


        //Returns the number of database rows affected by the update statement
        int rowsUpdated = garageDb.update(GarageContract.CarEntry.TABLE_NAME, values, where, where_args);

        if(rowsUpdated !=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }



}//End of entire Garage Provider class
