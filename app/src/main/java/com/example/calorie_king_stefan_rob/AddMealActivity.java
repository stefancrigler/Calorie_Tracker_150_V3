package com.example.calorie_king_stefan_rob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddMealActivity extends AppCompatActivity
{
   private Button addIngredientButton;
   private Button editDeleteIngredientButton;
   private Button callNutritionxButton;

   private RecyclerView currentMealRecyclerView;
   private RecyclerView.Adapter currentMealAdapter;
   private LinearLayoutManager currentMealLinearLayoutManager;

   private EditText ingredientNameEditText;
   private EditText unitNameEditText;
   private EditText nUnitEditText;
   private EditText nKcalInRatioEditText;
   private EditText nUnitInRatioEditText;

   private TextView kcalRatioTextView;

   private FirebaseFirestore db;
//   private FirebaseUser currentUser;
   private String currentUserUID;

   private static String LOG_TAG = "AddMealActivity";

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_meal);

      // currentMealRecyclerView
      currentMealRecyclerView = (RecyclerView) this.findViewById(R.id.recycler_view_todays_log);
//      currentMealRecyclerView.setHasFixedSize(true);
//      currentMealLinearLayoutManager = new LinearLayoutManager(this);
//      currentMealRecyclerView.setLayoutManager(todaysLogLinearLayoutManager);
//      dailyLogAdapter = new MyAdapter
//      todaysLogRecyclerView.setAdapter(todaysLogAdapter);
//      db = FirebaseFirestore.getInstance();

//      currentUserUID = getIntent().getStringExtra("currentUserUID");
//      db.collection(currentUserUID).document().get


      // addIngredientButton
      addIngredientButton = (Button) this.findViewById(R.id.button_add_ingredient);
      addIngredientButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {

         }
      });

      // editDeleteIngredientButton
      editDeleteIngredientButton = (Button) this.findViewById(R.id.button_edit_delete_ingredient);
      editDeleteIngredientButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
         }
      });

      // callNutritionxButton
      callNutritionxButton = (Button) this.findViewById(R.id.button_call_nutritionx);
      callNutritionxButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
         }
      });




//      //DEBUG////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//      Map<String, Object> dataToWrite = new HashMap<>();
//      dataToWrite.put("unit", "pack");
//      dataToWrite.put("name", "sapporo ichiban ramen");
//      dataToWrite.put("nUnit", 1);
//
//      db = FirebaseFirestore.getInstance();
//      try
//      {
//         db.collection("bllooofakeuser")
//                 .document("2020-05-16")
//                 .collection("meal000")
//                 .document("ingredient000")
//                 .set(dataToWrite)
//                 .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                       Log.d(LOG_TAG, "");
//                    }
//                 })
//                 .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                       Log.w(LOG_TAG, "Error writing document", e);
//                    }
//                 });
//      }
//      catch(NumberFormatException e)
//      {
//         Toast.makeText(getApplicationContext(), "Not a Valid Calorie Goal", Toast.LENGTH_SHORT).show();
//      }
//      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////DEBUG
   }
}
