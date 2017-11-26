package com.example.android.monstergarage;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Jujuan on 11/23/2017.
 */

public class CarCursorAdapter extends CursorAdapter {

    public CarCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.main_listview_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //Find fields that will be populated in UI list view
        ImageView carColorView = (ImageView) view.findViewById(R.id.colorImageView);
        TextView carMakeModelView = (TextView) view.findViewById(R.id.makeModeltextView);
        TextView carYearView = (TextView) view.findViewById(R.id.yearTextView);
        TextView carPlateView = (TextView) view.findViewById(R.id.platesTextView);

        //Extract the required properties from cursor
        int carColor = cursor.getInt(cursor.getColumnIndexOrThrow("color"));
        String carMake = cursor.getString(cursor.getColumnIndexOrThrow("make"));
        String carModel = cursor.getString(cursor.getColumnIndexOrThrow("model"));
        String carYear = cursor.getString(cursor.getColumnIndexOrThrow("year"));
        String carPlate = cursor.getString(cursor.getColumnIndexOrThrow("plate"));

        //Assign or populate list view fields with extracted properties
        carColorView.setBackgroundColor(carColor);
        carMakeModelView.setText(carMake + " " + carModel);
        carYearView.setText("Yr: "+ carYear);
        carPlateView.setText("LP#: " +carPlate);

    }


}
