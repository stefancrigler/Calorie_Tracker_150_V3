package com.example.calorie_king_stefan_rob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HomeScreen extends AppCompatActivity {

    private ProgressBar calorieProgress;
    private ImageView daily_log;
    private ImageView my_calorie_history;
    private ImageView settings;
    private ImageView my_groups;
    private ProgressBar calorie_progress;
    private int goal_value;
    public int counter;
    public Double sum = 0.0;
    public Double score;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        final String email = prefs.getString("email",null);
        goal_value = prefs.getInt("calorie goal", 2000);
        calorie_progress.setMax(goal_value);
        update_user_score();




        //to daily log
        daily_log.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.i("Daily Log","Going to Daily Log");
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

    public void update_user_score(){
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        String email = prefs.getString("email",null);


        db.collection(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("db", document.getId());
                                    if (document.getId().toString().equals(prefs.getString("calorie_history_date", null))) {
                                        Map<String, Object> all_inputs = (Map<String, Object>) document.getData();
                                        Iterator it = all_inputs.entrySet().iterator();
                                        while (it.hasNext()) {
                                            Map.Entry pair = (Map.Entry) it.next();
                                            Map<String, Object> meal = (Map<String, Object>) pair.getValue();
                                            Double cal = (Double) meal.get("calories");
                                            sum = sum + cal;
                                            counter = counter + 1;
                                            Log.d("db", document.getId() + " => " + document.getData());
                                        }
                                        //now have the double value in sum
                                        final int goal = prefs.getInt("calorie goal",1);
                                        //now easy algorithm to get score
                                        final Double goal1 = new Double(goal);
                                        Log.d("db",sum.toString());
                                        Log.d("db",goal1.toString());

                                        score  = 100.0 - ((Math.abs(sum-goal1)/goal1)*100.0);

                                        final String group = prefs.getString("group_name",null);
                                        final String date = prefs.getString("current date",null);
                                        Map<String, Map<String,Double>> userMap = new HashMap<>();
                                        Map<String,Double> scoreMap = new HashMap<>();
                                        scoreMap.put("score",score);
                                        userMap.put(email,scoreMap);
                                        Log.d("db","made the data");
                                        db.collection(group).document(date)
                                                .set(userMap, SetOptions.merge())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("db", "DocumentSnapshot successfully written!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("db", "Error writing document", e);
                                                    }
                                                });

                                    } else {
                                        Log.d("db", "Missed the loop");
                                    }
                                }
                                Log.d("db", "should be entered");
                                counter = 0;
                            }
                            else{
                                sum = 0.0;
                            }
                        } else {
                            Log.w("db", "Error getting documents.", task.getException());
                        }
                    }
                });

    }




}
