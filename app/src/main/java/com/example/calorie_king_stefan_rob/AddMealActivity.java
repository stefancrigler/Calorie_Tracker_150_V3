package com.example.calorie_king_stefan_rob;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddMealActivity extends    AppCompatActivity
                             implements ConfirmDeleteIngredientDialogFragment.ConfirmDeleteIngredientDialogFragmentDialogListener,
                                        ConfirmAddMealDialogFragment.ConfirmAddMealDialogFragmentDialogListener
{
   private DocumentReference previouslyUsedIngredientDocReference;
   private DocumentSnapshot previouslyUsedIngredientDocumentSnapshot;

   private DocumentReference currentDayMealAndWorkoutLogDocReference;
   private DocumentSnapshot currentDayMealAndWorkoutLogDocumentSnapshot;

   private Button addIngredientButton;
   private Button addMealButton;
   private Button callNutritionxButton;

   private AutoCompleteTextView ingredientNameAutoCompleteTextView;
   private ArrayAdapter<String> ingredientNameAutoCompleteTextViewAdapter;
   private ArrayList<String> databaseIngredientNameStringArrayList;
   private EditText nUnitEditText;
   private Map<String,ArrayList<String>> databaseIngredientNameToAssociatedUnitNames;
   private Map<Pair<String,String>, Pair<Long, Long>> databaseIngredientAndUnitToKCalRatioMap;
   private AutoCompleteTextView unitNameAutoCompleteTextView;
   private ArrayAdapter<String> unitNameAutoCompleteTextViewAdapter;
   private EditText nKcalInRatioEditText;
   private EditText nUnitInRatioEditText;

   private ListView ingredientListView;
   private ArrayAdapter addedIngredientListViewArrayAdapter;
   private ArrayList<String> addedIngredientToStringArrayList;
   private int ingredientIndexConfirmedForDeletion;

   private String mealName;
   private int currentMealCount;

   private ArrayList<IngredientClass> addedIngredientsArrayList;
   private MealClass addedMeal;

   private String todaysDateFormatted;

   private FirebaseFirestore db;
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
      currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
      todaysDateFormatted = LocalDate.now().toString();




      // get access to the previously used ingredients stored on the database
      db = FirebaseFirestore.getInstance();


      this.previouslyUsedIngredientDocReference =
              db.collection(currentUserEmail).document("previouslyUsedIngredients");

      previouslyUsedIngredientDocReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
      {
         @Override
         public void onComplete(@NonNull Task<DocumentSnapshot> task)
         {
            if (task.isSuccessful())
            {
               previouslyUsedIngredientDocumentSnapshot = task.getResult();
               if (previouslyUsedIngredientDocumentSnapshot.exists())
               {
                  Log.i(TAG, "Retrieved previouslyUsedIngredients doc from Cloud Firestore");
                  autofillTextArrayPopulate();
                  setupViews();
                  addedIngredientsArrayList = new ArrayList<>();
               }
               else
               {

                  // TODO code fails if previouslyUsedIngredients is being created for the first time, maybe fixed, check if this code is required
                  Log.i(TAG, "previouslyUsedIngredients doesn't exist on Firestore");
                  HashMap<String, Long> nIngredients = new HashMap<>();
                  nIngredients.put("nIngredients", new Long(0));
                  db.collection(currentUserEmail).document("previouslyUsedIngredients")
                          .set(nIngredients, SetOptions.merge())
                          .addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {
                                Log.d("db", "previouslyUsedIngredients created");
                                previouslyUsedIngredientDocumentSnapshot = task.getResult();
                                autofillTextArrayPopulate();
                                setupViews();
                                addedIngredientsArrayList = new ArrayList<>();
                             }
                          })
                          .addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                Log.w("db", "Error writing document", e);
                             }
                          });

               }
            }
            else
            {
               Log.i(TAG, "get failed with ", task.getException());
            }
         }
      });



   } // end onCreate

   private void autofillTextArrayPopulate()
   {
      Log.i(TAG, "private void autofillTextArrayPopulate()");
      Map<String, Object> previouslyUsedIngredientsStraightFromDatabaseMap = this.previouslyUsedIngredientDocumentSnapshot.getData();
      int nIngredients = ((Long)previouslyUsedIngredientsStraightFromDatabaseMap.get("nIngredients")).intValue();

      //
      // These three objects will be used to inform the context-sensitive autofill behavior
      // for ingredientNameEditText, unitNameEditText,
      //     nKcalInRatioEditText  , nUnitInRatioEditText
      //
      // parallel arrays
      this.databaseIngredientNameStringArrayList = new ArrayList<String>(); // < "100 kCal chewy bar", ... >
      this.databaseIngredientNameToAssociatedUnitNames = new HashMap<>(); // "140 kCal chewy bar" --> <"gram", "bar", "half bar">
      // <"100 kCal chewy bar", "gram"> -> <100,24>, ie (100 kCal / 24 grams of "100 kCal chewy bar")
      this.databaseIngredientAndUnitToKCalRatioMap = new HashMap<Pair<String,String>, Pair<Long, Long>>();

      StringBuilder currIngrIndexStringBuilder = new StringBuilder("ingr00000");
      // ingr00000
      // field 0: "ingrName" -> "tomatoes"
      // field 1: number of units and associated ratios
      // field 2 - 4: unit name, kcal, nUnits
      // field 5 - 7: ....
      ArrayList<Object> currentIngredientArray;
      int nUnitNamesAssociatedWithCurrIngredient;
      ArrayList<String> currentIngredientListOfUnitNames = new ArrayList<>();
      Pair<String, String> ingredientNameUnitNamePair;
      Pair<Long, Long> kCalToNUnitRatioNumberPairAssociatedWithIngredientNameUnitNamePair;
      for (int i = 0 ; i < nIngredients; ++i) // cycle thru ingr00000, ingr00001, etc...
      {
         // fetch the array for ingr00000
         currentIngredientArray = (ArrayList) previouslyUsedIngredientsStraightFromDatabaseMap.get(currIngrIndexStringBuilder.toString());

         // fetch "100kC chewy bar" here
         this.databaseIngredientNameStringArrayList.add((String) currentIngredientArray.get(0));

         // fetch 2 here
         nUnitNamesAssociatedWithCurrIngredient = ((Long)currentIngredientArray.get(1)).intValue();

         // cycle thru 2 sets of three pieces of information associated w/ the unitname
         for (int j = 2 ; j < 1 + 3 * nUnitNamesAssociatedWithCurrIngredient; j+=3)
         {
            // currentIngredientArray.get(j) is "gram"
            // currentIngredientArray.get(j+1) is 100, the numerator in the kCal/unit ratio
            // currentIngredientArray.get(j+2) is 24, the denominator in the kCal/unit ratio
            currentIngredientListOfUnitNames.add((String) currentIngredientArray.get(j));

            ingredientNameUnitNamePair =
                    new Pair<>((String) currentIngredientArray.get(0), (String) currentIngredientArray.get(j));
            kCalToNUnitRatioNumberPairAssociatedWithIngredientNameUnitNamePair =
                    new Pair<Long,Long>((Long)currentIngredientArray.get(j+1), (Long)currentIngredientArray.get(j+2));
            databaseIngredientAndUnitToKCalRatioMap
                    .put(ingredientNameUnitNamePair, kCalToNUnitRatioNumberPairAssociatedWithIngredientNameUnitNamePair);

         }
         databaseIngredientNameToAssociatedUnitNames
                 .put((String) currentIngredientArray.get(0),new ArrayList<String> (currentIngredientListOfUnitNames));


         // increment the key to the next ingredient
         if(i+1 < 10) // ingr00000 to ingr00009
         {
            currIngrIndexStringBuilder.replace(8,9,Integer.toString(i+1));
         }
         else if (i+1 < 100) // ingr00010 to ingr00099
         {
            currIngrIndexStringBuilder.replace(7,9,Integer.toString(i+1));
         }
         else if (i+1 < 1000) //ingr00100 to ingr00999
         {
            currIngrIndexStringBuilder.replace(6,8,Integer.toString(i+1));
         }
         else if (i+1 < 10000) //ingr01000 to ingr09999
         {
            currIngrIndexStringBuilder.replace(5,8,Integer.toString(i+1));
         }
         else if (i+1 < 100000)//ingr10000 to ingr99999
         {
            currIngrIndexStringBuilder.replace(4,8,Integer.toString(i+1));
         }
         else
         {
            // TODO LOW PRIORITY insert error handling code for >ingr99999
         }

         // clear all of the temporary ArrayLists
         currentIngredientListOfUnitNames.clear();
         currentIngredientArray.clear();
      }
   }

   private void setupViews()
   {
      //
      // nKCal in ratio edit text
      //
      nKcalInRatioEditText = (EditText) this.findViewById(R.id.editText_nkcal_in_ratio);

      //
      // nUnit in ratio edit text
      //
      nUnitInRatioEditText = (EditText) this.findViewById(R.id.editText_n_unit_in_ratio);

      //
      // Ingredient Name input
      //
      this.ingredientNameAutoCompleteTextView = (AutoCompleteTextView) this.findViewById(R.id.autoCompleteTextView_ingredient_name);
      this.ingredientNameAutoCompleteTextView.setThreshold(1);
      ingredientNameAutoCompleteTextViewAdapter =
              new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, this.databaseIngredientNameStringArrayList);
      ingredientNameAutoCompleteTextView.setAdapter(ingredientNameAutoCompleteTextViewAdapter);
      this.ingredientNameAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id)
         {
            // if we select ingredientName5 we want units associated with ingredientName5 to show up in the autoComplete
            unitNameAutoCompleteTextViewAdapter =
                    new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            databaseIngredientNameToAssociatedUnitNames.get(parent.getItemAtPosition(position)));
            unitNameAutoCompleteTextView.setAdapter(unitNameAutoCompleteTextViewAdapter);
            unitNameAutoCompleteTextView.setThreshold(1);

         }
      });
      this.ingredientNameAutoCompleteTextView.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            nKcalInRatioEditText.setText("");
            nUnitInRatioEditText.setText("");
         }
      });

      //
      // unit name input
      //
      this.unitNameAutoCompleteTextView = (AutoCompleteTextView) this.findViewById(R.id.autoCompleteTextView_unit_name);
      this.unitNameAutoCompleteTextView.setThreshold(1);
      // TODO set to a list of all unit names encountered thus far by default
      this.unitNameAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id)
         {
            // if we have ingredientName5 and unitName3 selected we want to autofill the calorie information
            Pair<String,String> ingredientNameUnitNamePair =
                    new Pair<>(ingredientNameAutoCompleteTextView.getText().toString(), unitNameAutoCompleteTextView.getText().toString());
            if (databaseIngredientAndUnitToKCalRatioMap.containsKey(ingredientNameUnitNamePair))
            {
               nKcalInRatioEditText.setText((databaseIngredientAndUnitToKCalRatioMap.get(ingredientNameUnitNamePair).first).toString());
               nUnitInRatioEditText.setText((databaseIngredientAndUnitToKCalRatioMap.get(ingredientNameUnitNamePair).second).toString());
            }
         }

      });
      //
      // addIngredientButton
      //
      addIngredientButton = (Button) this.findViewById(R.id.button_add_ingredient);
      addIngredientButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            ingredientNameAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_ingredient_name);
            unitNameAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_unit_name);
            nUnitEditText = (EditText) findViewById(R.id.editText_n_unit);
            nKcalInRatioEditText = (EditText) findViewById(R.id.editText_nkcal_in_ratio);
            nUnitInRatioEditText = (EditText) findViewById(R.id.editText_n_unit_in_ratio);

            if (TextUtils.isEmpty(ingredientNameAutoCompleteTextView.getText()))
               ingredientNameAutoCompleteTextView.setError("This can't be empty");
            else if (TextUtils.isEmpty(unitNameAutoCompleteTextView.getText()))
               unitNameAutoCompleteTextView.setError("This can't be empty");
            else if (TextUtils.isEmpty(nUnitEditText.getText()))
               nUnitEditText.setError("This can't be empty");
            else if (TextUtils.isEmpty(nKcalInRatioEditText.getText()))
               nKcalInRatioEditText.setError("This can't be empty");
            else if (TextUtils.isEmpty(nUnitInRatioEditText.getText()))
               nUnitInRatioEditText.setError("This can't be empty");
            else
            {
               addedIngredientsArrayList.add(new IngredientClass(
                       ingredientNameAutoCompleteTextView.getText().toString(),
                       unitNameAutoCompleteTextView.getText().toString(),
                       Long.valueOf(nUnitEditText.getText().toString()),
                       Long.valueOf(nKcalInRatioEditText.getText().toString()),
                       Long.valueOf(nUnitInRatioEditText.getText().toString())
               ));
               addedIngredientToStringArrayList
                       .add(addedIngredientsArrayList.get(addedIngredientsArrayList.size() - 1).toString());
               ingredientNameAutoCompleteTextView.setText(null);
               unitNameAutoCompleteTextView.setText(null);
               nUnitEditText.setText(null);
               nKcalInRatioEditText.setText(null);
               nUnitInRatioEditText.setText(null);
               addedIngredientListViewArrayAdapter.notifyDataSetChanged();
            }

         }
      });

      //
      // addMealButton
      //
      addMealButton = (Button) this.findViewById(R.id.button_add_meal);
      addMealButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            if (!addedIngredientsArrayList.isEmpty())
            {
               DialogFragment confirmAddMealDialogFragment = ConfirmAddMealDialogFragment.newInstance();
               confirmAddMealDialogFragment.show(getSupportFragmentManager(), "confirmAddMealDialogFragment");

            }



         }
      });

      //
      // callNutritionxButton
      //
      callNutritionxButton = this.findViewById(R.id.button_call_nutritionx);
      callNutritionxButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            // TODO implement nutritionx ingredient lookup
         }
      });

      //
      // ingredientListView
      //
      this.addedIngredientToStringArrayList = new ArrayList<>();
      this.ingredientListView = findViewById(R.id.listView_ingredient_list);
      this.addedIngredientListViewArrayAdapter =
              new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addedIngredientToStringArrayList);
      ingredientListView.setAdapter(addedIngredientListViewArrayAdapter);

      ingredientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id)
         {
            Bundle confirmDeleteIngredientDialogFragmentBundle = new Bundle();
            confirmDeleteIngredientDialogFragmentBundle.putString("ingredientName", addedIngredientsArrayList.get(position).ingredientName);
            ingredientIndexConfirmedForDeletion = position;
            DialogFragment confirmDeleteIngredientDialogFragment =
                    ConfirmDeleteIngredientDialogFragment.newInstance(confirmDeleteIngredientDialogFragmentBundle);
            confirmDeleteIngredientDialogFragment.show(getSupportFragmentManager(), "confirmDeleteIngredientDialogFragment");
         }
      });
   }

   @Override
   public void onDeletionDialogPositiveClick(DialogFragment dialog)
   {
      this.addedIngredientsArrayList.remove(this.ingredientIndexConfirmedForDeletion);
      this.addedIngredientToStringArrayList.remove(this.ingredientIndexConfirmedForDeletion);
      this.addedIngredientListViewArrayAdapter.notifyDataSetChanged();

   }

   @Override
   public void onDeletionDialogNegativeClick(DialogFragment dialog) {}

   @Override
   public void onBackPressed(){
      Intent addMealActivityToDailyLogActivityIntent = new Intent(this, DailyLogActivity.class);
      startActivity(addMealActivityToDailyLogActivityIntent);
      finish();
   }

   @Override
   public void onConfirmAddMealDialogPositiveClick(DialogFragment dialog)
   {
      // get the meal name from the user
      EditText confirmAddMealDialogEditText = (EditText) dialog.getDialog().findViewById(R.id.editText_enter_meal_name);
      this.mealName = confirmAddMealDialogEditText.getText().toString();
      if(this.mealName.isEmpty())
         this.mealName = "Unnamed meal";

      // get nMeals from the database
      this.currentDayMealAndWorkoutLogDocReference =
              db.collection(currentUserEmail).document(todaysDateFormatted);
      currentDayMealAndWorkoutLogDocReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
      {
         @Override
         public void onComplete(@NonNull Task<DocumentSnapshot> task)
         {
            if (task.isSuccessful())
            {
               currentDayMealAndWorkoutLogDocumentSnapshot = task.getResult();
               if (currentDayMealAndWorkoutLogDocumentSnapshot.exists())
               {
                  Log.i(TAG, "Retrieved currentDayMealAndWorkoutLogDoc from Cloud Firestore");
                  currentMealCount = (int) (long) currentDayMealAndWorkoutLogDocumentSnapshot.get("nMeals")+1;
                  addMealToDatabase();
                  updateDatabasePreviouslyUsedIngredients();
                  clearCurrentIngredientsList();
                  Toast.makeText(getApplicationContext(), "Meal has been added!", Toast.LENGTH_SHORT).show();
                  Intent addMealActivityToDailyLogActivityIntent = new Intent(getApplicationContext(), DailyLogActivity.class);
                  startActivity(addMealActivityToDailyLogActivityIntent);
                  finish();

               }
               else
               {
                  Log.i(TAG, "currentDayMealAndWorkoutLogDoc doesn't exist on Firestore");
                  HashMap<String, Long> nMeals = new HashMap<>();
                  currentMealCount = 1;
                  nMeals.put("nMeals", new Long(1));
                  db.collection(currentUserEmail).document(todaysDateFormatted)
                          .set(nMeals, SetOptions.merge())
                          .addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {
                                Log.d("db", "today's meal/workout log created");
                                addMealToDatabase();
                                updateDatabasePreviouslyUsedIngredients();
                                clearCurrentIngredientsList();
                                Toast.makeText(getApplicationContext(), "Meal has been added!", Toast.LENGTH_SHORT).show();
                                Intent addMealActivityToDailyLogActivityIntent = new Intent(getApplicationContext(), DailyLogActivity.class);
                                startActivity(addMealActivityToDailyLogActivityIntent);
                                finish();

                             }
                          })
                          .addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                Log.w("db", "Error writing document", e);
                             }
                          });

               }
            }
            else
            {
               Log.i(TAG, "get failed with ", task.getException());
            }
         }
      });

   }

   @Override
   public void onConfirmAddMealDialogNegativeClick(DialogFragment dialog) { }

   private void addMealToDatabase()
   {
      HashMap<String, Long> nMealsUpdate;
      StringBuilder currMealIndexStringBuilder = new StringBuilder("meal00000");
      if(currentMealCount-1 < 10) // ingr00000 to ingr00009
      {
         currMealIndexStringBuilder.replace(8,9,Integer.toString(currentMealCount-1));
      }
      else if (currentMealCount-1 < 100) // ingr00010 to ingr00099
      {
         currMealIndexStringBuilder.replace(7,9,Integer.toString(currentMealCount-1));
      }
      else if (currentMealCount-1 < 1000) //ingr00100 to ingr00999
      {
         currMealIndexStringBuilder.replace(6,8,Integer.toString(currentMealCount-1));
      }
      else if (currentMealCount-1 < 10000) //ingr01000 to ingr09999
      {
         currMealIndexStringBuilder.replace(5,8,Integer.toString(currentMealCount-1));
      }
      else if (currentMealCount-1 < 100000)//ingr10000 to ingr99999
      {
         currMealIndexStringBuilder.replace(4,8,Integer.toString(currentMealCount-1));
      }
      else
      {
         // TODO LOW PRIORITY insert error handling code for >ingr99999
      }

      HashMap<String, IngredientClass> ingredientsInCurrentMeal = new HashMap<>();
      StringBuilder currIngrIndexStringBuilder = new StringBuilder("ingr00000");
      for (int i = 0; i < addedIngredientsArrayList.size(); ++i)
      {
         ingredientsInCurrentMeal.put(currIngrIndexStringBuilder.toString(), addedIngredientsArrayList.get(i));

         // increment the key to the next ingredient
         if(i+1 < 10) // ingr00000 to ingr00009
         {
            currIngrIndexStringBuilder.replace(8,9,Integer.toString(i+1));
         }
         else if (i+1 < 100) // ingr00010 to ingr00099
         {
            currIngrIndexStringBuilder.replace(7,9,Integer.toString(i+1));
         }
         else if (i+1 < 1000) //ingr00100 to ingr00999
         {
            currIngrIndexStringBuilder.replace(6,8,Integer.toString(i+1));
         }
         else if (i+1 < 10000) //ingr01000 to ingr09999
         {
            currIngrIndexStringBuilder.replace(5,8,Integer.toString(i+1));
         }
         else if (i+1 < 100000)//ingr10000 to ingr99999
         {
            currIngrIndexStringBuilder.replace(4,8,Integer.toString(i+1));
         }
         else
         {
            // TODO LOW PRIORITY insert error handling code for >ingr99999
         }
      }
      MealClass currentMeal = new MealClass(ingredientsInCurrentMeal, mealName);
      HashMap <String, MealClass> currentMealEntry = new HashMap<>();
      currentMealEntry.put(currMealIndexStringBuilder.toString(), currentMeal);

      db.collection(currentUserEmail).document(todaysDateFormatted)
              .set(currentMealEntry, SetOptions.merge())
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {
                    Log.d("db", "meal added");
                 }
              })
              .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                    Log.w("db", "Error writing document", e);
                 }
              });

      nMealsUpdate = new HashMap<>();
      nMealsUpdate.put("nMeals", new Long(currentMealCount));
      db.collection(currentUserEmail).document(todaysDateFormatted)
              .set(nMealsUpdate, SetOptions.merge())
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {
                    Log.d("db", "nMeals updated");
                 }
              })
              .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                    Log.w("db", "Error writing document", e);
                 }
              });

      this.currentMealCount++;
   }

   private void updateDatabasePreviouslyUsedIngredients()
   {
      boolean matchingUnitNameFound;
      boolean matchingIngredientNameFound;
      ArrayList<Object> newOrRectifiedIngredientArrayList = new ArrayList<>();
      Pair<String,String> databaseIngredientAndUnitPair;
      HashMap <String, ArrayList<Object>> currentReplacementIngrEntry;
      HashMap <String, Long> nIngredientsUpdate;
      long currentSizeOfDatabasePreviouslyUsedIngredientsList = this.databaseIngredientNameStringArrayList.size();
      // cycle thru addedIngredients
      for(IngredientClass addedIngredientEntry: this.addedIngredientsArrayList)
      {
         // if an addedIngredient has the same name as a database Ingredient
         // then check to see if there are any new units associated with
         // addedIngredient. if so, add that unit to the database

         // if an addedIngredient doesn't appear as a database Ingredient then
         // add it to the database

         // cycle thru databaseIngredients
         matchingIngredientNameFound = false;
         for (int j = 0; j < this.databaseIngredientNameStringArrayList.size(); ++j)
         {
            if (addedIngredientEntry.ingredientName.equals(databaseIngredientNameStringArrayList.get(j)))
            {
               matchingIngredientNameFound = true;
               matchingUnitNameFound = false;
               // cycle thru the units associated with this databaseIngredient
               int k;
               for (k = 0 ;
                    k < databaseIngredientNameToAssociatedUnitNames.get(databaseIngredientNameStringArrayList.get(j)).size();
                    ++k)
               {
                  if (databaseIngredientNameToAssociatedUnitNames
                          .get(databaseIngredientNameStringArrayList.get(j))
                          .get(k).equalsIgnoreCase(addedIngredientEntry.unitName))
                  {
                     matchingUnitNameFound = true;
                     break;
                  }

                  // TODO adding a new unit to an existing ingredient is not working
                  if (!matchingUnitNameFound) //add the new unit to the database ingredient entry
                  {

                     StringBuilder currRectifiedIngrIndexStringBuilder = new StringBuilder("ingr00000");
                     if(j < 10) // ingr00000 to ingr00009
                     {
                        currRectifiedIngrIndexStringBuilder.replace(8,9,Integer.toString(j));
                     }
                     else if (currentMealCount < 100) // ingr00010 to ingr00099
                     {
                        currRectifiedIngrIndexStringBuilder.replace(7,9,Integer.toString(j));
                     }
                     else if (currentMealCount < 1000) //ingr00100 to ingr00999
                     {
                        currRectifiedIngrIndexStringBuilder.replace(6,8,Integer.toString(j));
                     }
                     else if (currentMealCount < 10000) //ingr01000 to ingr09999
                     {
                        currRectifiedIngrIndexStringBuilder.replace(5,8,Integer.toString(j));
                     }
                     else if (currentMealCount < 100000)//ingr10000 to ingr99999
                     {
                        currRectifiedIngrIndexStringBuilder.replace(4,8,Integer.toString(j));
                     }
                     else
                     {
                        // TODO LOW PRIORITY insert error handling code for >ingr99999
                     }
                     // construct the replacement ingrXXXXX entry
                     newOrRectifiedIngredientArrayList.add(databaseIngredientNameStringArrayList.get(j));
                     newOrRectifiedIngredientArrayList.add(new Float((float) k));
                     int n = 0; // unit name counter
                     for (int m = 3; m < 1 + 3*(k-1); ++m) // add the units already there
                     {
                        // add unit name
                        newOrRectifiedIngredientArrayList.add(databaseIngredientNameToAssociatedUnitNames.get((String)databaseIngredientNameStringArrayList.get(j)));

                        databaseIngredientAndUnitPair = new Pair<>(
                           (String) this.databaseIngredientNameStringArrayList.get(j),
                           (String) this.databaseIngredientNameToAssociatedUnitNames.get(this.databaseIngredientNameStringArrayList.get(j)).get(n));
                        // add kCal in ratio
                        newOrRectifiedIngredientArrayList.add(this.databaseIngredientAndUnitToKCalRatioMap.get(databaseIngredientAndUnitPair).first);
                        // add nUnit in ratio
                        newOrRectifiedIngredientArrayList.add(this.databaseIngredientAndUnitToKCalRatioMap.get(databaseIngredientAndUnitPair).second);
                     }
                     // add the info associated with the new unit
                     newOrRectifiedIngredientArrayList.add(addedIngredientEntry.unitName);
                     newOrRectifiedIngredientArrayList.add(addedIngredientEntry.calorieRatioNKCal);
                     newOrRectifiedIngredientArrayList.add(addedIngredientEntry.calorieRatioNUnits);

                     currentReplacementIngrEntry = new HashMap<>();
                     currentReplacementIngrEntry.put(currRectifiedIngrIndexStringBuilder.toString(), newOrRectifiedIngredientArrayList);

                     db.collection(currentUserEmail)
//                     db.collection("testUser00")
                             .document("previouslyUsedIngredients")
                             .set(currentReplacementIngrEntry, SetOptions.merge())
                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                   Log.d("db", "ingredient successfully updated");
                                }
                             })
                             .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                   Log.w("db", "Error updating ingredient", e);
                                }
                             });
                     currentReplacementIngrEntry.clear();
                     newOrRectifiedIngredientArrayList.clear();

                  }
               }
            }
         }
         // addedIngredient doesn't appear in the database ingredient list, so add it
         if (!matchingIngredientNameFound)
         {
            currentSizeOfDatabasePreviouslyUsedIngredientsList++;

            StringBuilder currRectifiedIngrIndexStringBuilder = new StringBuilder("ingr00000");
            if(currentSizeOfDatabasePreviouslyUsedIngredientsList - 1 < 10) // ingr00000 to ingr00009
            {
               currRectifiedIngrIndexStringBuilder.replace(8,9,Long.toString(currentSizeOfDatabasePreviouslyUsedIngredientsList - 1));
            }
            else if (currentSizeOfDatabasePreviouslyUsedIngredientsList - 1 < 100) // ingr00010 to ingr00099
            {
               currRectifiedIngrIndexStringBuilder.replace(7,9,Long.toString(currentSizeOfDatabasePreviouslyUsedIngredientsList - 1));
            }
            else if (currentSizeOfDatabasePreviouslyUsedIngredientsList - 1 < 1000) //ingr00100 to ingr00999
            {
               currRectifiedIngrIndexStringBuilder.replace(6,8,Long.toString(currentSizeOfDatabasePreviouslyUsedIngredientsList - 1));
            }
            else if (currentSizeOfDatabasePreviouslyUsedIngredientsList - 1 < 10000) //ingr01000 to ingr09999
            {
               currRectifiedIngrIndexStringBuilder.replace(5,8,Long.toString(currentSizeOfDatabasePreviouslyUsedIngredientsList - 1));
            }
            else if (currentSizeOfDatabasePreviouslyUsedIngredientsList - 1 < 100000)//ingr10000 to ingr99999
            {
               currRectifiedIngrIndexStringBuilder.replace(4,8,Long.toString(currentSizeOfDatabasePreviouslyUsedIngredientsList - 1));
            }
            else
            {
               // TODO LOW PRIORITY insert error handling code for >ingr99999
            }
            // construct the additional ingrXXXXX entry at the end of previouslyUsedIngredients
            newOrRectifiedIngredientArrayList.add(addedIngredientEntry.ingredientName);
            newOrRectifiedIngredientArrayList.add(new Long(1));
            newOrRectifiedIngredientArrayList.add(addedIngredientEntry.unitName);
            newOrRectifiedIngredientArrayList.add(addedIngredientEntry.calorieRatioNKCal);
            newOrRectifiedIngredientArrayList.add(addedIngredientEntry.calorieRatioNUnits);

            currentReplacementIngrEntry = new HashMap<>();
            currentReplacementIngrEntry.put(currRectifiedIngrIndexStringBuilder.toString(), newOrRectifiedIngredientArrayList);
            db.collection(currentUserEmail)
                    .document("previouslyUsedIngredients")
                    .set(currentReplacementIngrEntry, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                          Log.d("db", "ingredient successfully updated");
                       }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                          Log.w("db", "Error updating ingredient", e);
                       }
                    });

            nIngredientsUpdate = new HashMap<>();
            nIngredientsUpdate.put("nIngredients", new Long(currentSizeOfDatabasePreviouslyUsedIngredientsList));
            db.collection(currentUserEmail)
                    .document("previouslyUsedIngredients")
                    .set(nIngredientsUpdate, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                          Log.d("db", "ingredient successfully updated");
                       }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                          Log.w("db", "Error updating ingredient", e);
                       }
                    });

            currentReplacementIngrEntry.clear();
            newOrRectifiedIngredientArrayList.clear();
         }

      }

   }

   private void clearCurrentIngredientsList()
   {
      this.addedIngredientsArrayList.clear();
      this.addedIngredientToStringArrayList.clear();
      this.addedIngredientListViewArrayAdapter.notifyDataSetChanged();
   }
}

