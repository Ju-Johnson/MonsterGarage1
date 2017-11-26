package com.example.android.monstergarage;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.android.monstergarage.table_data.CarDBHelper;
import com.example.android.monstergarage.table_data.GarageContract.CarEntry;


/**
 * This activity displays a list of current cars in a database and allows users to
 * edit by clicking on them, or delete all from options menu
 *
 * *************** RECOMMENDED UPDATE ****************
 * This class should use Cursor Loader by implementing LoaderManager.LoaderCallbacks<Cursor>
 * to increase efficiency by loading/querying data from the database in a background thread
 * Data should never be loaded on the main thread - slows the app and prevent user interaction
 */
public class MainActivity extends AppCompatActivity {

    //Database helper object that will provide us access to garage database
    private CarDBHelper carDBHelper;
    //List view for displaying cars from the database to the users in main UI
    private ListView carsListView;
    CarCursorAdapter carCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Find list view to populate with cars in main UI
        carsListView = (ListView) findViewById(R.id.mainListView);
        //Find and setup empty view on list view, to prompt user to add
        //a new car if no cars are currently there to display on screen
        View emptyView = findViewById(R.id.emptyView);
        carsListView.setEmptyView(emptyView);

        //Find and setup FAB button which allows user to add a new car to database
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editorIntent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(editorIntent);
            }
        });


        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        carDBHelper = new CarDBHelper(this);

        carCursorAdapter = new CarCursorAdapter(this, null);
        carsListView.setAdapter(carCursorAdapter);

        carsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Create new intent to got to Editor Activity when car is clicked on from list
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                Uri currentCarUri = ContentUris.withAppendedId(CarEntry.CONTENT_URI, id);
                intent.setData(currentCarUri);
                startActivity(intent);
            }
        });

    }

    /**
     * Loading/Querying data from database onto main thread UI list view
     * Here is where you should user Cursor Loader methods instead
     */
    private void displayDatabase(){

        //Bundle list of columns you want returned from the database
        String[] columns = {
                CarEntry._ID,
                CarEntry.COLUMN_CAR_MAKE,
                CarEntry.COLUMN_CAR_MODEL,
                CarEntry.COLUMN_CAR_YEAR,
                CarEntry.COLUMN_CAR_COLOR,
                CarEntry.COLUMN_CAR_PLATE };

        //Safely perform database query using (GarageProvider)content provider's
        //query method, which checks for appropriate content URI path request such as
        //"content://com.example.android.monstergarage/cars" first, then executes query
        Cursor returnedQuery_Cursor = getContentResolver().query(CarEntry.CONTENT_URI,
                columns, null, null, null);

        //Setup cursor adapter for displaying the cars in the main list view UI
        CarCursorAdapter carAdapter = new CarCursorAdapter(this, returnedQuery_Cursor);
        //Attach cursor adapter to the list view to fill list view with cars
        carsListView.setAdapter(carAdapter);

    }//End of displayDatabase

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all cars?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "DeleteAll" button, so delete all cars.
                deleteAllCars();
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

    private void deleteAllCars(){
        int rowsDeleted = getContentResolver().delete(CarEntry.CONTENT_URI, null, null);

        //Check if delete was successful or not
        if (rowsDeleted == 0) {
            Toast.makeText(this, "Error deleting cars", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, rowsDeleted + " Cars successfully deleted", Toast.LENGTH_SHORT).show();
        }

        //Close the this activity and return to home screen
        finish();
    }


    /**
     * Called when user returns from editor activity
     * Refreshes Main UI list view to reflect all new updated changes
     */
    @Override
    protected void onStart(){
        super.onStart();
        //call method to display a refreshed list of the new changes
        displayDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action app bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_deleteAllEntries) {
            showDeleteConfirmationDialog();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
