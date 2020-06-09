package com.example.calorie_king_stefan_rob;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class HomeScreen extends AppCompatActivity {

    private ProgressBar calorieProgress;
    private ImageView daily_log;
    private ImageView my_calorie_history;
    private ImageView settings;
    private ImageView my_groups;
    private ProgressBar calorie_progress;
    private int goal_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        final ImageView daily_log = (ImageView) findViewById(R.id.daily_log);
        final ImageView my_calorie_history = (ImageView) findViewById(R.id.my_calorie_history);
        final ImageView settings = (ImageView) findViewById(R.id.settings);
        final ImageView my_groups = (ImageView) findViewById(R.id.my_groups);

        final ProgressBar calorie_progress = (ProgressBar) findViewById((R.id.calorie_progress));
        //set the progress bar
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        goal_value = prefs.getInt("calorie goal", 2000);
        calorie_progress.setMax(goal_value);


        //to daily log
        daily_log.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.i("Daily Log","Going to Daily Log");
//                Intent homeScreenToDailyLogIntent = new Intent(HomeScreen.this, FatSecret_Search_Meal.class);
                Intent homeScreenToDailyLogIntent = new Intent(HomeScreen.this, DailyLogActivity.class);
                startActivity(homeScreenToDailyLogIntent);
                finish();
            }
        });
        //to settings
        settings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.i("Settings","Going to Settings");
                //will take user to new activity
                Intent intent = new Intent(HomeScreen.this, Settings.class);
                startActivity(intent);
                finish();
            }
        });
        //to groups
        my_groups.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.i("My Groups","Going to My Groups");
                //will take user to new activity
                Intent intent = new Intent(HomeScreen.this, My_Groups_Page.class);
                startActivity(intent);
                finish();
            }
        });

        //to calorie history
        my_calorie_history.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.i("My Calorie History","Going to My Calorie History");
                Intent homeScreenToMyCalorieHistoryIntent =
                        new Intent(HomeScreen.this, MyCalorieHistoryActivity.class);
                startActivity(homeScreenToMyCalorieHistoryIntent);
                finish();
            }
        });


    }
    public void test(View view){
        calorieProgress = (ProgressBar)findViewById(R.id.calorie_progress);
        calorieProgress.incrementProgressBy(10);
    }




}
