package com.example.calorie_king_stefan_rob;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.LocalDate;

import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


public class MyCalorieHistoryActivity extends AppCompatActivity
{
   private String date;
   private TextView date_display;
   FirebaseFirestore db = FirebaseFirestore.getInstance();

   int counter = 0;
   boolean done;
   Double sum = 0.0;

   TextView Goal_Progress;
   ListView listView;

   String[] TestNameArray = {"Stefan", "Rob", "Yoga", "Matt", "Kyle" , "Ryan"};
   String[] TestScoreArray = {"100","98","97","96","63","43"};
   Integer[] ImageArray = {R.drawable.white_background,R.drawable.white_background,R.drawable.white_background,R.drawable.white_background,R.drawable.white_background,R.drawable.white_background};

   String[] meals = {"meal000","meal001","meal002","meal003","meal004","meal005","meal006","meal007","meal008","meal009"};

   Context context;
   CustomListAdapter_scoreboard calorie_board;

   final String[] NameArray = {"","","","","",""};
   final String[] CalorieArray= {"","","","","",""};


   final String[] emptyArray = {"","","","","",""};


   @Override
   public void onBackPressed(){
      Intent intent = new Intent(MyCalorieHistoryActivity.this, HomeScreen.class);
      startActivity(intent);
      finish();
   }


   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_my_calorie_history);

      final ConstraintLayout mConstraintLayout = findViewById(R.id.theLayout);

      date_display = (TextView) findViewById(R.id.date_display);
      LocalDate date = LocalDate.now();

      final String display_value;
      display_value = date.toString();
      final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
      SharedPreferences.Editor editor = prefs.edit();
      editor.putString("calorie_history_date",display_value);
      editor.apply();
      date_display.setText(display_value);

      //Now we want to grab from firebase the correct food for the day
      //DocumentReference day = db.collection("testuser00").document(prefs.getString("calorie_history_date",null));
      done = false;
      calorie_board = new CustomListAdapter_scoreboard(MyCalorieHistoryActivity.this, NameArray, CalorieArray, ImageArray);
      Log.d("db","Made it after the adapter");
      listView = (ListView) findViewById(R.id.log_list);
      listView.setAdapter(calorie_board);
      Log.d("db","to loop");
      Log.d("db",prefs.getString("calorie_history_date",null));
      grab_data();

      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final int posID = (int) id;
            String name = calorie_board.nameArray[position];
            Intent intent = new Intent(MyCalorieHistoryActivity.this, Ingredient_Display.class);
            intent.putExtra("meal",name);

            startActivity(intent);
            finish();
         }
      });









      mConstraintLayout.setOnTouchListener(new OnSwipeTouchListener(MyCalorieHistoryActivity.this) {
         @SuppressLint("ClickableViewAccessibility")
         @Override
         public void onSwipeLeft() {
            super.onSwipeLeft();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String date = prefs.getString("calorie_history_date", null);
            LocalDate newDate = LocalDate.parse(date).plusDays(1);
            String newDateString = newDate.toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("calorie_history_date",newDateString);
            editor.apply();
            date_display.setText(newDateString);
            calorie_board = new CustomListAdapter_scoreboard(MyCalorieHistoryActivity.this,emptyArray,emptyArray,ImageArray);
            listView.setAdapter(calorie_board);
            grab_data();

         }
         @Override
         public void onSwipeRight() {
            super.onSwipeRight();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String date = prefs.getString("calorie_history_date", null);
            LocalDate newDate = LocalDate.parse(date).minusDays(1);
            String newDateString = newDate.toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("calorie_history_date",newDateString);
            editor.apply();
            date_display.setText(newDateString);
            calorie_board = new CustomListAdapter_scoreboard(MyCalorieHistoryActivity.this,emptyArray,emptyArray,ImageArray);
            listView.setAdapter(calorie_board);
            grab_data();
         }
      });
   }

   public void grab_data(){

      final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
      SharedPreferences.Editor editor = prefs.edit();
      final String calorie_goal = Integer.toString(prefs.getInt("calorie goal",2000));
      Goal_Progress = findViewById(R.id.Goal_Progress);

      db.collection("testUser02")
              .get()
              .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                       for (QueryDocumentSnapshot document : task.getResult()) {
                          Log.d("db",document.getId());
                          if(document.getId().toString().equals(prefs.getString("calorie_history_date",null))) {
                             Map<String,Object> all_inputs = ( Map<String,Object>) document.getData();
                             Iterator it =all_inputs.entrySet().iterator();
                             while(it.hasNext()) {
                                Map.Entry pair = (Map.Entry) it.next();

                                Map<String, Object> meal = (Map<String,Object>) pair.getValue();
                                NameArray[counter] = (String) meal.get("name");
                                Double cal = (Double) meal.get("calories");
                                CalorieArray[counter] = Double.toString(cal);
                                sum = sum + cal;
                                counter = counter + 1;
                                Log.d("db", document.getId() + " => " + document.getData());
                             }
                             calorie_board = new CustomListAdapter_scoreboard(MyCalorieHistoryActivity.this, NameArray, CalorieArray, ImageArray);
                             listView.setAdapter(calorie_board);
                             StringBuilder stringBuilder = new StringBuilder(100);
                             stringBuilder.append("Calories: ");
                             stringBuilder.append(Double.toString(sum));
                             stringBuilder.append("/");
                             stringBuilder.append(calorie_goal);
                             Goal_Progress.setText(stringBuilder.toString());
                             sum = 0.0;
                          }
                          else{
                             Log.d("db","Missed the loop");
                          }
                       }
                       Log.d("db","should be entered");
                       Log.d("db",NameArray[0]);
                       Log.d("db",CalorieArray[0]);
                       counter = 0;
                    } else {
                       Log.w("db", "Error getting documents.", task.getException());
                    }
                 }
              });
   }

}

