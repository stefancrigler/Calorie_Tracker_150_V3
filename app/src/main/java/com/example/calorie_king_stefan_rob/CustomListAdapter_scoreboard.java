package com.example.calorie_king_stefan_rob;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter_scoreboard extends ArrayAdapter {
    //to reference the Activity
    private final Activity context;

    //to store the animal images
    private final Integer[] imageIDarray;

    //to store the list of countries
    private final String[] nameArray;

    //to store the list of countries
    private final String[] scoreArray;

    public CustomListAdapter_scoreboard(Activity context, String[] nameArrayParam, String[] infoArrayParam, Integer[] imageIDArrayParam){

        super(context,R.layout.calorie_board_row , nameArrayParam);
        this.context=context;
        this.imageIDarray = imageIDArrayParam;
        this.nameArray = nameArrayParam;
        this.scoreArray = infoArrayParam;
    }
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.calorie_board_row, null,true);

        //this code gets references to objects in the calorie_board_row.xml file
        TextView nameTextField = (TextView) rowView.findViewById(R.id.name);
        TextView scoreTextField = (TextView) rowView.findViewById(R.id.score);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);

        //this code sets the values of the objects to values from the arrays
        nameTextField.setText(nameArray[position]);
        scoreTextField.setText(scoreArray[position]);
        imageView.setImageResource(imageIDarray[position]);

        return rowView;

    };

}
