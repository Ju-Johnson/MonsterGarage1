package com.example.android.monstergarage;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.monstergarage.table_data.GarageContract;

/**
 * This activity allows users to create and save a new car into the database
 * or edit and delete an existing car
 *
 * * *************** RECOMMENDED UPDATE ****************
 * This class activity should use Cursor Loader by implementing LoaderManager.LoaderCallbacks<Cursor>
 * to increase efficiency by loading/querying data from the database in a background thread
 * Data should never be loaded on the main thread - slows the app and prevent user interaction
 */
public class EditorActivity extends AppCompatActivity {
    private EditText mCarYearView;
    private EditText mCarMakeView;
    private EditText mCarModelView;
    private EditText mCarPlateView;
    private Spinner mCarColorSpinner;
    private int mColor = GarageContract.CarEntry.COLOR_WHITE;
    Uri currentCarUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);

        //assign all required views needed to read user input from
        mCarYearView = (EditText) findViewById(R.id.yearEditText);
        mCarMakeView = (EditText) findViewById(R.id.makeEditText);
        mCarModelView = (EditText) findViewById(R.id.modelEditText);
        mCarColorSpinner = (Spinner) findViewById(R.id.spinnerColors);
        mCarPlateView = (EditText) findViewById(R.id.platesEditText);

        //custom method call
        setupSpinner();

        //Get the intent coming from the Main Activity that
        //indicates when a single car has been selected from the
        //list view for editing
        Intent intent = getIntent();
        currentCarUri = intent.getData();

        //If the intent is null then no car was selected, which
        //means this Activity will function as adding a new car to database
        if(currentCarUri == null){
            setTitle("Add a Car");

            //Invalidate the options menu, so the "Delete" menu option can be hidden
            //It doesn't make sense to delete a pet that hasn't been created yet
            invalidateOptionsMenu();

        }else {
            // Otherwise this is an existing car, so change app bar to say "Edit this Car"
            setTitle("Edit this Car");
            editCurrentCarSetup();
        }

    }

    /**
     * Gets user input from editor views and saves into database as  a new car
     */
    private void saveCarToDatabase() {



        //If Saving a new in "Add a Car" selection
        if (currentCarUri == null) {
            //Read from input view fields
            String carMake = mCarMakeView.getText().toString().trim();
            String carModel = mCarModelView.getText().toString().trim();
            String carYear = mCarYearView.getText().toString().trim();
            int carColor = mColor;
            String carPlate = mCarPlateView.getText().toString().trim();

            //If user tries to save a blank car, prevent crash by closing activity and return home
            if (currentCarUri == null && TextUtils.isEmpty(carMake) && TextUtils.isEmpty(carModel) && TextUtils.isEmpty(carYear) &&
                    TextUtils.isEmpty(carPlate)) {
                return;
            }

            //A simple Key-value pair data structure suitable for
            //quickly adding data to appropriate database columns
            //Column names are the key, values are from edit input views
            ContentValues carBundledValues = new ContentValues();
            carBundledValues.put(GarageContract.CarEntry.COLUMN_CAR_MAKE, carMake);
            carBundledValues.put(GarageContract.CarEntry.COLUMN_CAR_MODEL, carModel);
            carBundledValues.put(GarageContract.CarEntry.COLUMN_CAR_YEAR, carYear);
            carBundledValues.put(GarageContract.CarEntry.COLUMN_CAR_COLOR, carColor);
            carBundledValues.put(GarageContract.CarEntry.COLUMN_CAR_PLATE, carPlate);

            //Safely insert a new car into the database through the (GarageProvider)content provider's
            //insert method which returns the new content URI path and ID for the new car.
            Uri newUri = getContentResolver().insert(GarageContract.CarEntry.CONTENT_URI, carBundledValues);
            //Check if save was successful or not
            if (newUri == null) {
                Toast.makeText(this, "Error saving this car", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Car saved, new id: " + newUri, Toast.LENGTH_SHORT).show();
            }
        }

        //If Editing an already existing car in "Edit this Car" selection
        else {
            //Read from input view fields
            String carMake = mCarMakeView.getText().toString().trim();
            String carModel = mCarModelView.getText().toString().trim();
            String carYear = mCarYearView.getText().toString().trim();
            int carColor = mColor;
            String carPlate = mCarPlateView.getText().toString().trim();

            //A simple Key-value pair data structure suitable for
            //quickly adding data to appropriate database columns
            //Column names are the key, values are from edit input views
            ContentValues carBundledValues = new ContentValues();
            carBundledValues.put(GarageContract.CarEntry.COLUMN_CAR_MAKE, carMake);
            carBundledValues.put(GarageContract.CarEntry.COLUMN_CAR_MODEL, carModel);
            carBundledValues.put(GarageContract.CarEntry.COLUMN_CAR_YEAR, carYear);
            carBundledValues.put(GarageContract.CarEntry.COLUMN_CAR_COLOR, carColor);
            carBundledValues.put(GarageContract.CarEntry.COLUMN_CAR_PLATE, carPlate);

            //Safely insert a new car into the database through the (GarageProvider)content provider's
            //insert method which returns the new content URI path and ID for the new car.
            int rowUpdated = getContentResolver().update(currentCarUri, carBundledValues, null, null);
            //Check if save was successful or not
            if (rowUpdated == 0) {
                Toast.makeText(this, "Error updating this car", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, rowUpdated + " Car successfully updated", Toast.LENGTH_SHORT).show();
            }

        }


    }

    /**
     * Loading/Querying data from database to fill the edit text fields
     * on the main Editor Acivity thread
     * Here is where you should user Cursor Loader methods instead
     */
    private void editCurrentCarSetup(){

        //Bundle list of columns you want returned from the database
        String[] columns = {
                GarageContract.CarEntry.COLUMN_CAR_MAKE,
                GarageContract.CarEntry.COLUMN_CAR_MODEL,
                GarageContract.CarEntry.COLUMN_CAR_YEAR,
                GarageContract.CarEntry.COLUMN_CAR_COLOR,
                GarageContract.CarEntry.COLUMN_CAR_PLATE };

        //Safely perform database query using (GarageProvider)content provider's
        //query method, which checks for appropriate content URI path request such as
        //"content://com.example.android.monstergarage/cars/2" first, then executes query
        Cursor returnedQuery_Cursor = getContentResolver().query(currentCarUri,
                columns, null, null, null);

        returnedQuery_Cursor.moveToFirst();

        //Extract values from database columns to prepopulate appropriate Edit Text fields
        //in "Edit this Car" Activity
        String carMake = returnedQuery_Cursor.getString(returnedQuery_Cursor.getColumnIndex(
                GarageContract.CarEntry.COLUMN_CAR_MAKE));
        String carModel = returnedQuery_Cursor.getString(returnedQuery_Cursor.getColumnIndex(
                GarageContract.CarEntry.COLUMN_CAR_MODEL));
        String carYear = returnedQuery_Cursor.getString(returnedQuery_Cursor.getColumnIndex(
                GarageContract.CarEntry.COLUMN_CAR_YEAR));
        int carColor = returnedQuery_Cursor.getInt(returnedQuery_Cursor.getColumnIndex(
                GarageContract.CarEntry.COLUMN_CAR_COLOR));
        String carPlate = returnedQuery_Cursor.getString(returnedQuery_Cursor.getColumnIndex(
                GarageContract.CarEntry.COLUMN_CAR_PLATE));

        //Assign values to those fields
        mCarMakeView.setText(carMake);
        mCarModelView.setText(carModel);
        mCarYearView.setText(carYear);
        mColor = carColor;
        mCarPlateView.setText(carPlate);

        returnedQuery_Cursor.close();

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this car?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the car.
                deleteCar();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the car.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the car in the database.
     */
    private void deleteCar() {
        // Only perform the delete if this is an existing car
        if(currentCarUri != null ){
            // Call the ContentResolver to delete the car at the given content URI.
            // Pass in null for the where and where_args because the currentCarUri
            // content URI already identifies the car that we want.
            int rowDeleted = getContentResolver().delete(currentCarUri, null, null);
            //Check if delete was successful or not
            if (rowDeleted == 0) {
                Toast.makeText(this, "Error deleting this car", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, rowDeleted + " Car successfully deleted", Toast.LENGTH_SHORT).show();
            }
        }

        //Close the this activity and return to home screen
        finish();

    }

    /**
     * Setup the dropdown spinner options that allows users to select car color
     */
    private void setupSpinner(){

        //Create adapter for the spinner using the String array in (strings.xml) for list options
        //along with spinner's default layout
        ArrayAdapter colorSpinner = ArrayAdapter.createFromResource(this,
                R.array.colors_array, android.R.layout.simple_spinner_item);
        //Specify dropdown layout style: simple list view, one item per line
        colorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        mCarColorSpinner.setAdapter(colorSpinner);

        //Set color indicator based on selection
        mCarColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String selection = (String) parent.getItemAtPosition(position);

                if(!TextUtils.isEmpty(selection)){
                    //Values from GarageContract.java file in table_data folder
                    if(selection.equals("White")){
                        mColor = GarageContract.CarEntry.COLOR_WHITE;

                    }else if(selection.equals("Black")){
                        mColor = GarageContract.CarEntry.COLOR_BLACK;

                    }else if(selection.equals("Gray")){
                        mColor = GarageContract.CarEntry.COLOR_GRAY;

                    }else if(selection.equals("Silver")){
                        mColor = GarageContract.CarEntry.COLOR_GRAY;

                    }else if(selection.equals("Brown")){
                        mColor = GarageContract.CarEntry.COLOR_BROWN;

                    }else if(selection.equals("Red")){
                        mColor = GarageContract.CarEntry.COLOR_RED;

                    }else if(selection.equals("Blue")){
                        mColor = GarageContract.CarEntry.COLOR_BLUE;

                    }else if(selection.equals("Green")){
                        mColor = GarageContract.CarEntry.COLOR_GREEN;

                    }else if(selection.equals("Yellow")){
                        mColor = GarageContract.CarEntry.COLOR_YELLOW;

                    }else if(selection.equals("Orange")){
                        mColor = GarageContract.CarEntry.COLOR_ORANGE;

                    }else if(selection.equals("Purple")) {
                        mColor = GarageContract.CarEntry.COLOR_PURPLE;

                    }else{
                        mColor = GarageContract.CarEntry.COLOR_WHITE;

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mColor = GarageContract.CarEntry.COLOR_WHITE;
            }
        });

    }//end of setup spinner method

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds menu items to the action app bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor_activity, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        //If this is a new car, hide the "Delete" menu item
        if(currentCarUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){

            case R.id.action_save:
                saveCarToDatabase();
                finish(); //Exit Editor Activity after save
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case R.id.action_home:
                //Navigate back to parent activity when back arrow pressed
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}//end of Editor Activity class
