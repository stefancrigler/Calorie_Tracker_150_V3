package com.example.calorie_king_stefan_rob;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddMealActivity extends AppCompatActivity
{
   private Button addIngredientButton;
   private Button finishAddingMealButton;
   private Button callNutritionxButton;

   private RecyclerView currentMealRecyclerView;
   private RecyclerView.Adapter currentMealAdapter;
   private LinearLayoutManager currentMealLinearLayoutManager;

   private EditText ingredientNameEditText;
   private ArrayAdapter<String> ingredientNameArrayAdapter;
   private ArrayList<String> ingredientNameStringArrayList;
   private ArrayList<ArrayList<String>> unitNamesParallelWithIngredientNameStringArrayList;
   private Map<Pair<String,String>, Pair<Double, Double>> ingredientAndUnitToKCalRatioMap;
   private DocumentReference previouslyUsedIngredientDocReference;
   private DocumentSnapshot previouslyUsedIngredientDoc;
   private EditText unitNameEditText;
   private ArrayAdapter<String> unitNameArrayAdapter;
   private EditText nUnitEditText;
   private EditText nKcalInRatioEditText;
   private EditText nUnitInRatioEditText;

   private TextView kcalRatioTextView;

   private String todaysDateFormatted;

   private FirebaseFirestore db;
   private FirebaseUser currentUser;
   private String currentUserEmail;

   private HashMap ingredientHashMap;

   private static String TAG = "AddMealActivity";

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_meal);
      //
      // restore variables
      //
      if (savedInstanceState != null) // recover from orientation change
      {

      }
      else // instantiate or take in from intent
      {
         Intent homeScreenToMyCalorieHistoryIntent = getIntent();

      }
      currentUser = FirebaseAuth.getInstance().getCurrentUser();
      currentUserEmail = currentUser.getEmail();
      todaysDateFormatted = LocalDate.now().toString();


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

      // get access to the previously used ingredients stored on the database
      db = FirebaseFirestore.getInstance();
      this.previouslyUsedIngredientDocReference =
//              db.collection(currentUserEmail).document("previouslyUsedIngredients");
              db.collection("testUser00").document("previouslyUsedIngredients");
      previouslyUsedIngredientDocReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
      {
         @Override
         public void onComplete(@NonNull Task<DocumentSnapshot> task)
         {
            if (task.isSuccessful())
            {
               previouslyUsedIngredientDoc = task.getResult();
               if (previouslyUsedIngredientDoc.exists())
               {
                  Log.i(TAG, "Retrieved previouslyUsedIngredients doc from Cloud Firestore");
                  autofillTextArrayPopulate();
               }
               else
               {
                  Log.i(TAG, "previouslyUsedIngredients doesn't exist on Firestore");
               }
            }
            else
            {
               Log.i(TAG, "get failed with ", task.getException());
            }
         }
      });

      this.ingredientNameEditText = (AutoCompleteTextView) this.findViewById(R.id.autoCompleteTextView_ingredient_name);

      // addIngredientButton
      addIngredientButton = (Button) this.findViewById(R.id.button_add_ingredient);
      addIngredientButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            ingredientNameEditText = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_ingredient_name);
            unitNameEditText = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_unit_name);
            nUnitEditText = (EditText) findViewById(R.id.editText_n_unit);
            nKcalInRatioEditText = (EditText) findViewById(R.id.editText_nkcal_in_ratio);
            nUnitInRatioEditText = (EditText) findViewById(R.id.editText_unit_in_ratio);

            if (TextUtils.isEmpty(ingredientNameEditText.getText()))
               ingredientNameEditText.setError("This can't be empty");
            else if (TextUtils.isEmpty(unitNameEditText.getText()))
               unitNameEditText.setError("This can't be empty");
            else if (TextUtils.isEmpty(nUnitEditText.getText()))
               nUnitEditText.setError("This can't be empty");            
            else if (TextUtils.isEmpty(nKcalInRatioEditText.getText()))
               nKcalInRatioEditText.setError("This can't be empty");     
            else if (TextUtils.isEmpty(nUnitInRatioEditText.getText()))
               nUnitInRatioEditText.setError("This can't be empty");     
            else
            {


            }

         }
      });

      // editDeleteIngredientButton
      finishAddingMealButton = (Button) this.findViewById(R.id.button_finish_adding_meal);
      finishAddingMealButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
         }
      });

      // callNutritionxButton
      callNutritionxButton = this.findViewById(R.id.button_call_nutritionx);
      callNutritionxButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {

         }
      });
   } // end onCreate

   //TODO make this work
   private void autofillTextArrayPopulate()
   {
      Log.i(TAG, "private void autofillTextArrayPopulate()");
      Map<String, Object> previouslyUsedIngredientsStraightFromDatabaseMap = this.previouslyUsedIngredientDoc.getData();
      int nIngredients = (int) previouslyUsedIngredientsStraightFromDatabaseMap.get("nIngredients");

      // parallel arrays
      this.ingredientNameStringArrayList = new ArrayList<String>(); // < "100 kCal chewy bar", ... >
      this.unitNamesParallelWithIngredientNameStringArrayList = new ArrayList<ArrayList<String>>(); // < <"gram", "bar"> , ... >

      // <"100 kCal chewy bar", "gram"> -> <100,24>, ie (100 kCal / 24 grams of "100 kCal chewy bar")
      this.ingredientAndUnitToKCalRatioMap = new HashMap<Pair<String,String>, Pair<Double, Double>>();

      StringBuilder currIngrIndexStringBuilder = new StringBuilder("ingr00000");
      // ingr00000
      // field 0: "ingrName" -> "tomatoes"
      // field 1: number of units and associated ratios
      // field 2 - 4: unit name, kcal, nUnits
      // field 5 - 7: ....
      ArrayList<Object> currentIngredientArray = new ArrayList<>();
      int nUnitNamesAssociatedWithCurrIngredient;
      ArrayList<String> currentIngredientListOfUnitNames = new ArrayList<>();
      for (int i = 0 ; i < nIngredients; ++i) // cycle thru ingr00000, ingr00001, etc...
      {
         // fetch the array for ingr00000
         currentIngredientArray.add(previouslyUsedIngredientsStraightFromDatabaseMap.get(currIngrIndexStringBuilder.toString()));

         // fetch "100kC chewy bar" here
         this.ingredientNameStringArrayList.add((String) currentIngredientArray.get(0));

         // fetch 2 here
         nUnitNamesAssociatedWithCurrIngredient = (int) currentIngredientArray.get(1);

         // cycle thru 2 sets of three pieces of information associated w/ the unitname
         for (int j = 2 ; j < nUnitNamesAssociatedWithCurrIngredient; j+=3)
         {
            // currentIngredientArray.get(j) is "gram"
            // currentIngredientArray.get(j+1) is 100, the numerator in the kCal/unit ratio
            // currentIngredientArray.get(j+2) is 24, the denominator in the kCal/unit ratio
            currentIngredientListOfUnitNames.add((String) currentIngredientArray.get(j));


         }


         // increment the key to the next ingredient
         if(i < 10) // ingr00000 to ingr00009
         {
            currIngrIndexStringBuilder.replace(8,8,Integer.toString(i));
         }
         else if (i < 100) // ingr00010 to ingr00099
         {
            currIngrIndexStringBuilder.replace(7,8,Integer.toString(i));
         }
         else if (i < 1000) //ingr00100 to ingr00999
         {
            currIngrIndexStringBuilder.replace(6,8,Integer.toString(i));
         }
         else if (i < 10000) //ingr01000 to ingr09999
         {
            currIngrIndexStringBuilder.replace(5,8,Integer.toString(i));
         }
         else if (i < 100000)//ingr10000 to ingr99999
         {
            currIngrIndexStringBuilder.replace(4,8,Integer.toString(i));
         }
         else
         {
            // TODO LOW PRIORITY insert error handling code for >ingr99999
         }
      }
   }
}

//      //DEBUG////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//      // Writing to firebase example code:

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
//                       Log.i(LOG_TAG, "");
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
