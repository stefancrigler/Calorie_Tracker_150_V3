package com.example.calorie_king_stefan_rob;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DailyLogActivity extends AppCompatActivity
{
   private Button addMealButton;
   private Button addWorkoutButton;
   private Button addPreparationButton;
   private Button addScaleReadingButton;
   private Button addFatSecretButton;

   private RecyclerView todaysLogRecyclerView;
   private RecyclerView.Adapter todaysLogAdapter;
   private LinearLayoutManager todaysLogLinearLayoutManager;

   private FirebaseFirestore db;

   private FirebaseUser currentUser;
   private String currentUserEmail;

   private String todaysDateFormatted;
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_daily_log);

      addMealButton = (Button) this.findViewById(R.id.button_add_meal);
      addWorkoutButton = (Button) this.findViewById(R.id.button_add_workout);
      addPreparationButton = (Button) this.findViewById(R.id.button_add_preparation);
      addScaleReadingButton = (Button) this.findViewById(R.id.button_add_scale_reading);
//      addFatSecretButton = (Button) this.findViewById(R.id.button_add_fatsecret);

      todaysLogRecyclerView = (RecyclerView) this.findViewById(R.id.recycler_view_todays_log);

      currentUser = FirebaseAuth.getInstance().getCurrentUser();
      currentUserEmail = currentUser.getEmail();

      todaysDateFormatted = LocalDate.now().toString();


      //addMealButton
      addMealButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            Intent dailyLogToAddMealIntent = new Intent(DailyLogActivity.this, AddMealActivity.class);
            //just get the date again in AddMealActivity to avoid bugs when activity changes are between dates
//            dailyLogToAddMealIntent.putExtra("todaysDateFormatted", todaysDateFormatted);
            dailyLogToAddMealIntent.putExtra("currentUserEmail", currentUserEmail);
            startActivity(dailyLogToAddMealIntent);
            finish();
         }
      });

//      //add database meal
//      addFatSecretButton.setOnClickListener(new View.OnClickListener()
//      {
//         @Override
//         public void onClick(View v)
//         {
//            Intent dailyLogToAdFatSecretIntent = new Intent(DailyLogActivity.this, FatSecret_Search_Meal.class);
//            dailyLogToAdFatSecretIntent.putExtra("todaysDateFormatted", todaysDateFormatted);
//            dailyLogToAdFatSecretIntent.putExtra("currentUserUID", currentUserUID);
//            startActivity(dailyLogToAdFatSecretIntent);
//            finish();
//         }
//      });



      //addWorkoutButton
      addWorkoutButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            Intent dailyLogToAddWorkoutIntent = new Intent(DailyLogActivity.this, AddWorkoutActivity.class);
            startActivity(dailyLogToAddWorkoutIntent);
            finish();
         }
      });

      //addPreparationButton
      addPreparationButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            Intent dailyLogToAddPreparationIntent = new Intent(DailyLogActivity.this, AddPreparationActivity.class);
            startActivity(dailyLogToAddPreparationIntent);
            finish();
         }
      });

      //addScaleReadingButton
      addScaleReadingButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            Intent dailyLogToAddScaleReadingIntent = new Intent(DailyLogActivity.this, AddScaleReadingActivity.class);
            startActivity(dailyLogToAddScaleReadingIntent);
            finish();
         }
      });

      // list of today's meals and workouts
//      todaysLogRecyclerView.setHasFixedSize(true);
//      todaysLogLinearLayoutManager = new LinearLayoutManager(this);
//      todaysLogRecyclerView.setLayoutManager(todaysLogLinearLayoutManager);

//      dailyLogAdapter = new MyAdapter
//      todaysLogRecyclerView.setAdapter(todaysLogAdapter);

//      db = FirebaseFirestore.getInstance();
   }
}
