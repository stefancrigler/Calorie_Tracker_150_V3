package com.example.calorie_king_stefan_rob;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DailyLogActivity extends AppCompatActivity
{
   private Button addMealButton;
   private Button addWorkoutButton;
   private Button addScaleReadingButton;
//   private Button addFatSecretButton;

   private ListView todaysLogListView;
   private ArrayAdapter todaysLogListViewArrayAdapter;
   private ArrayList<String> todaysLogListViewArrayList;

   private FirebaseFirestore db;
   private DocumentReference todaysMealsDocReference;
   private DocumentSnapshot todaysMealsDocumentSnapshot;

   private String currentUserEmail;
   private String todaysDateFormatted;

   private static String TAG = "DailyLogActivity";

   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_daily_log);

      addMealButton = (Button) this.findViewById(R.id.button_add_meal);
      addWorkoutButton = (Button) this.findViewById(R.id.button_add_workout);
      addScaleReadingButton = (Button) this.findViewById(R.id.button_add_scale_reading);
//      addFatSecretButton = (Button) this.findViewById(R.id.button_add_fatsecret);


      currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

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
            // TODO dialog that collects workout information
         }
      });


      //addScaleReadingButton
      addScaleReadingButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            // TODO dialog that collects bathroom scale reading
         }
      });

      // get access to today's meals stored on the database
      db = FirebaseFirestore.getInstance();


      this.todaysMealsDocReference =
              db.collection(currentUserEmail).document(todaysDateFormatted);

      todaysMealsDocReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
      {
         @Override
         public void onComplete(@NonNull Task<DocumentSnapshot> task)
         {
            if (task.isSuccessful())
            {
               todaysMealsDocumentSnapshot = task.getResult();
               if(todaysMealsDocumentSnapshot.exists())
               {
                  Log.i(TAG, "Retrieved todays meals doc from Cloud Firestore");
                  populateDailyLogList();
               }
            }
            else
            {
               Log.i(TAG, "get failed with ", task.getException());
            }

         }
      });


   }

   private void populateDailyLogList()
   {
      Map<String, Object> todaysMealsStraightFromDatabaseMap = this.todaysMealsDocumentSnapshot.getData();
      int nMeals = ((Long)todaysMealsStraightFromDatabaseMap.get("nMeals")).intValue();
      StringBuilder currMealIndexStringBuilder = new StringBuilder("meal00000");
      String currMealListItemNameString;
      double currMealListItemNCalories;
      this.todaysLogListViewArrayList = new ArrayList<>();
      for (int i = 0 ; i < nMeals; ++i) // cycle thru ingr00000, ingr00001, etc...
      {
         currMealListItemNameString = (String)((HashMap) todaysMealsStraightFromDatabaseMap.get(currMealIndexStringBuilder.toString())).get("name");
         currMealListItemNCalories =
                 (double) ((HashMap) todaysMealsStraightFromDatabaseMap.get(currMealIndexStringBuilder.toString())).get("nCalories");
         this.todaysLogListViewArrayList.add(
                 String.format("%-30s/%-6d calories", currMealListItemNameString, (int) currMealListItemNCalories)
         );

         if(i+1 < 10) // ingr00000 to ingr00009
         {
            currMealIndexStringBuilder.replace(8,9,Integer.toString(i+1));
         }
         else if (i+1 < 100) // ingr00010 to ingr00099
         {
            currMealIndexStringBuilder.replace(7,9,Integer.toString(i+1));
         }
         else if (i+1 < 1000) //ingr00100 to ingr00999
         {
            currMealIndexStringBuilder.replace(6,8,Integer.toString(i+1));
         }
         else if (i+1 < 10000) //ingr01000 to ingr09999
         {
            currMealIndexStringBuilder.replace(5,8,Integer.toString(i+1));
         }
         else if (i+1 < 100000)//ingr10000 to ingr99999
         {
            currMealIndexStringBuilder.replace(4,8,Integer.toString(i+1));
         }
         else
         {
            // TODO LOW PRIORITY insert error handling code for >ingr99999
         }
      }

      this.todaysLogListView = findViewById(R.id.listView_todays_log);
      this.todaysLogListViewArrayAdapter =
              new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, todaysLogListViewArrayList);
      todaysLogListView.setAdapter(todaysLogListViewArrayAdapter);

   }

   @Override
   public void onBackPressed()
   {
      Intent dailyLogActivityToHomeScreenActivityIntent = new Intent(this, HomeScreenActivity.class);
      startActivity(dailyLogActivityToHomeScreenActivityIntent);
      finish();
   }
}
