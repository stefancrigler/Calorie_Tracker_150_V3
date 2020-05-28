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

import java.sql.Time;
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

   private RecyclerView todaysLogRecyclerView;
   private RecyclerView.Adapter todaysLogAdapter;
   private LinearLayoutManager todaysLogLinearLayoutManager;

   private FirebaseFirestore db;

   private FirebaseUser currentUser;
   private String currentUserUID;

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

      todaysLogRecyclerView = (RecyclerView) this.findViewById(R.id.recycler_view_todays_log);

      currentUser = FirebaseAuth.getInstance().getCurrentUser();
      currentUserUID = currentUser.getUid();

      Date c = Calendar.getInstance().getTime();
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      todaysDateFormatted = df.format(c);


      //addMealButton
      addMealButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            Intent dailyLogToAddMealIntent = new Intent(DailyLogActivity.this, AddMealActivity.class);
            dailyLogToAddMealIntent.putExtra("todaysDateFormatted", todaysDateFormatted);
            dailyLogToAddMealIntent.putExtra("currentUserUID", currentUserUID);
            startActivity(dailyLogToAddMealIntent);
            finish();
         }
      });

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
