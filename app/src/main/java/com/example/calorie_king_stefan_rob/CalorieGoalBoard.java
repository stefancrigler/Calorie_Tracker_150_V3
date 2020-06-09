package com.example.calorie_king_stefan_rob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CalorieGoalBoard extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String[] TestNameArray = {"Stefan", "Rob", "Yoga", "Matt", "Kyle" , "Ryan"};
    String[] TestScoreArray = {"100","98","97","96","63","43"};
    Integer[] ImageArray = {R.drawable.gold_crown,R.drawable.silver_medal,R.drawable.bronze_medal
    ,R.drawable.white_background,R.drawable.white_background,R.drawable.white_background};
    ListView listView;

    int counter = 0;
    Context context;

    private static Button back_to_groups;
    private static TextView group_display;

    CustomListAdapter_scoreboard calorie_board;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_goal_board);
        context = this.context;

        TextView group_display = (TextView) findViewById(R.id.group_display);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String group = prefs.getString("group_name",null);
        group_display.setText("My Group: " + group);

        final String[] NameArray = {"","","","","",""};
        final String[] ScoreArray= {"","","","","",""};


        calorie_board = new CustomListAdapter_scoreboard(CalorieGoalBoard.this, TestNameArray, TestScoreArray, ImageArray);
        listView = (ListView) findViewById(R.id.calories_board);
        listView.setAdapter(calorie_board);


        db.collection("calorie_goals")
                .whereEqualTo("group",group)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("db", document.getId() + " => " + document.get("calorie_goal").toString());
                                if(counter == 0){
                                    NameArray[counter] = document.get("username").toString();
                                    ScoreArray[counter] = document.get("calorie_goal").toString();
                                    Log.d("db", "first one");
                                }
                                else {
                                    try {
                                        Log.d("db", "attempting");
                                        int temp = Integer.parseInt(ScoreArray[counter - 1]);
                                        String nameholder;
                                        String scoreholder;
                                        int back = 1;
                                        if (temp < Integer.parseInt(document.get("calorie_goal").toString())) {
                                            Log.d("db","to while");
                                            while (temp < Integer.parseInt(document.get("calorie_goal").toString())) {
                                                if (counter - back < 0) {
                                                    break;
                                                } else {
                                                    Log.d("db","att");
                                                    temp = Integer.parseInt(ScoreArray[counter - 1]);
                                                    nameholder = NameArray[counter - back];
                                                    scoreholder = ScoreArray[counter - back];
                                                    Log.d("db",nameholder);
                                                    NameArray[counter - back] = document.get("username").toString();
                                                    ScoreArray[counter - back] = document.get("calorie_goal").toString();
                                                    NameArray[counter - back + 1] = nameholder;
                                                    ScoreArray[counter - back + 1] = scoreholder;
                                                    back = back + 1;

                                                }
                                            }
                                        } else {
                                            NameArray[counter] = document.get("username").toString();
                                            ScoreArray[counter] = document.get("calorie_goal").toString();
                                        }
                                    }
                                    catch(NumberFormatException e){
                                        break;
                                    }
                                }

                                Log.d("db", "The NameArray at " + counter + " is " + NameArray[counter]);
                                counter  = counter + 1;
                            }
                            Log.d("db", "Trying to change");
                            calorie_board = new CustomListAdapter_scoreboard(CalorieGoalBoard.this, NameArray, ScoreArray, ImageArray);
                            listView.setAdapter(calorie_board);

                        } else {
                            Log.w("db", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    public void back_groups(View view){
        Intent intent = new Intent(CalorieGoalBoard.this,My_Groups_Page.class);
        startActivity(intent);
        finish();
    }
}

