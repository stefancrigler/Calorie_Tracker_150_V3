package com.example.calorie_king_stefan_rob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.LocalDate;

import java.util.Iterator;
import java.util.Map;

public class CalorieGoalBoardActivity extends AppCompatActivity {
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
    private static TextView date_show;
    final String[] emptyArray = {"","","","","",""};
    String users_group;
    boolean isFound = false;

    CustomListAdapter_scoreboard calorie_board;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_goal_board);
        context = this.context;
        TextView date_show = findViewById(R.id.date_show);
        LocalDate date = LocalDate.now();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("calorie_board_date",date.toString());
        editor.apply();
        date_show.setText(date.toString());
        final ConstraintLayout mConstraintLayout = findViewById(R.id.layout2);
        TextView group_display = (TextView) findViewById(R.id.group_display);

        final String group = prefs.getString("group_name", null);
        String email = prefs.getString("email", null);
        group_display.setText("My Group: " + group);

        final String[] NameArray = {"", "", "", "", "", ""};
        final String[] ScoreArray = {"", "", "", "", "", ""};


        calorie_board = new CustomListAdapter_scoreboard(CalorieGoalBoardActivity.this, NameArray, ScoreArray, ImageArray);
        listView = (ListView) findViewById(R.id.calories_board);
        listView.setAdapter(calorie_board);
        get_data();




            Log.d("db","in true loop");

        mConstraintLayout.setOnTouchListener(new OnSwipeTouchListener(CalorieGoalBoardActivity.this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Log.d("db","swipe left");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String date = prefs.getString("calorie_board_date", null);
                LocalDate newDate = LocalDate.parse(date).plusDays(1);
                String newDateString = newDate.toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("calorie_board_date",newDateString);
                editor.apply();
                date_show.setText(newDateString);
                calorie_board = new CustomListAdapter_scoreboard(CalorieGoalBoardActivity.this,emptyArray,emptyArray,ImageArray);
                listView.setAdapter(calorie_board);
                get_data();

            }
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Log.d("db","swipe right");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String date = prefs.getString("calorie_board_date", null);
                LocalDate newDate = LocalDate.parse(date).minusDays(1);
                String newDateString = newDate.toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("calorie_board_date",newDateString);
                editor.apply();
                date_show.setText(newDateString);
                calorie_board = new CustomListAdapter_scoreboard(CalorieGoalBoardActivity.this,emptyArray,emptyArray,ImageArray);
                listView.setAdapter(calorie_board);
                get_data();
            }
        });

    }




    public void back_groups(View view){
        Intent intent = new Intent(CalorieGoalBoardActivity.this, MyGroupsPageActivity.class);
        startActivity(intent);
        finish();
    }

    public void get_data(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String group = prefs.getString("group_name", null);
        String email = prefs.getString("email", null);
        group_display = findViewById(R.id.group_display);
        group_display.setText("My Group: " + group);
        db.collection("calorie_goals").document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                users_group = document.get("group").toString();
                                group_data(users_group);
                                Log.d("db","found");

                            } else {
                                Log.d("db", "No such document");
                            }
                        } else {
                            Log.d("db", "get failed with ", task.getException());
                        }
                    }
                });
    }

    public void group_data(String users_group){
        counter = 0;
        Log.d("db","in group data");
        final String[] NameArray = {"", "", "", "", "", ""};
        final String[] ScoreArray = {"", "", "", "", "", ""};
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //final String group = prefs.getString("group_name", null);
        String email = prefs.getString("email", null);
        db.collection(users_group)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("db", "was successful");
                            Log.d("db",prefs.getString("calorie_board_date",null));
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().toString().equals(prefs.getString("calorie_board_date", null))) {
                                    Log.d("db", "found date");
                                    Map<String, Object> all_inputs = (Map<String, Object>) document.getData();
                                    Iterator it = all_inputs.entrySet().iterator();
                                    while (it.hasNext()) {
                                        Map.Entry pair = (Map.Entry) it.next();
                                        if (counter == 0) {
                                            Map<String, Object> user = (Map<String, Object>) pair.getValue();
                                            Double value = (Double) user.get("score");
                                            NameArray[counter] = pair.getKey().toString();
                                            ScoreArray[counter] = Double.toString(value);
                                        } else {
                                            try {
                                                Log.d("db", "attempting");
                                                Map<String, Object> user = (Map<String, Object>) pair.getValue();
                                                Double value = (Double) user.get("score");
                                                Log.d("db", "attempting 2");
                                                Double temp = Double.parseDouble(ScoreArray[counter - 1]);
                                                String nameholder;
                                                String scoreholder;
                                                int back = 1;
                                                Log.d("db", "attempting 3");
                                                if (temp < value) {
                                                    Log.d("db", "to while");
                                                    while (temp < value) {
                                                        if (counter - back < 0) {
                                                            Log.d("db", "break");
                                                            break;
                                                        } else {
                                                            Log.d("db", "att");
                                                            temp = Double.parseDouble(ScoreArray[counter - 1]);
                                                            nameholder = NameArray[counter - back];
                                                            scoreholder = ScoreArray[counter - back];
                                                            Log.d("db", nameholder);
                                                            NameArray[counter - back] = pair.getKey().toString();
                                                            ScoreArray[counter - back] = Double.toString(value);
                                                            NameArray[counter - back + 1] = nameholder;
                                                            ScoreArray[counter - back + 1] = scoreholder;
                                                            back = back + 1;

                                                        }
                                                    }
                                                } else {
                                                    NameArray[counter] = pair.getKey().toString();
                                                    ScoreArray[counter] = Double.toString(value);
                                                    Log.d("db","wasnt larger");
                                                }
                                            } catch (NumberFormatException e) {
                                                Log.d("db","exception");
                                                break;
                                            }
                                        }

                                        Log.d("db", "The NameArray at " + counter + " is " + NameArray[counter]);
                                        counter = counter + 1;
                                    }
                                    Log.d("db", "Trying to change");
                                    calorie_board = new CustomListAdapter_scoreboard(CalorieGoalBoardActivity.this, NameArray, ScoreArray, ImageArray);
                                    listView.setAdapter(calorie_board);
                                }
                            }

                        } else {
                            Log.w("db", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}

