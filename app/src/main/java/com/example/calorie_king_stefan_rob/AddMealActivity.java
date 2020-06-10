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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddMealActivity extends    AppCompatActivity
                             implements ConfirmDeleteIngredientDialogFragment.ConfirmDeleteIngredientDialogFragmentDialogListener
{
   private DocumentReference previouslyUsedIngredientDocReference;
   private DocumentSnapshot previouslyUsedIngredientDoc;

   private Button addIngredientButton;
   private Button finishAddingMealButton;
   private Button callNutritionxButton;

   private AutoCompleteTextView ingredientNameAutoCompleteTextView;
   private ArrayAdapter<String> ingredientNameAutoCompleteTextViewAdapter;
   private ArrayList<String> ingredientNameStringArrayList;
   private EditText nUnitEditText;
   private Map<String,ArrayList<String>> ingredientNameToAssociatedUnitNames;
   private Map<Pair<String,String>, Pair<Long, Long>> ingredientAndUnitToKCalRatioMap;
   private AutoCompleteTextView unitNameAutoCompleteTextView;
   private ArrayAdapter<String> unitNameAutoCompleteTextViewAdapter;
   private EditText nKcalInRatioEditText;
   private EditText nUnitInRatioEditText;

   private ListView ingredientListView;
   private ArrayAdapter ingredientListViewArrayAdapter;
   private ArrayList<String> ingredientToStringArrayList;
   private int ingredientIndexConfirmedForDeletion;

   private ArrayList<Ingredient> ingredientsArrayList;
   private MealObject addedMeal;

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
      currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
      todaysDateFormatted = LocalDate.now().toString();




      // get access to the previously used ingredients stored on the database
      db = FirebaseFirestore.getInstance();
      this.previouslyUsedIngredientDocReference =
//              db.collection(currentUserEmail).document("previouslyUsedIngredients");
              // DEBUG/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
              db.collection("testUser00").document("previouslyUsedIngredients");
              // DEBUG/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
                  setupViews();
                  ingredientsArrayList = new ArrayList<>();
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



   } // end onCreate

   private void autofillTextArrayPopulate()
   {
      Log.i(TAG, "private void autofillTextArrayPopulate()");
      Map<String, Object> previouslyUsedIngredientsStraightFromDatabaseMap = this.previouslyUsedIngredientDoc.getData();
      int nIngredients = ((Long)previouslyUsedIngredientsStraightFromDatabaseMap.get("nIngredients")).intValue();

      //
      // These three objects will be used to inform the context-sensitive autofill behavior
      // for ingredientNameEditText, unitNameEditText,
      //     nKcalInRatioEditText  , nUnitInRatioEditText
      //
      // parallel arrays
      this.ingredientNameStringArrayList = new ArrayList<String>(); // < "100 kCal chewy bar", ... >
      this.ingredientNameToAssociatedUnitNames = new HashMap<>(); // "140 kCal chewy bar" --> <"gram", "bar", "half bar">
      // <"100 kCal chewy bar", "gram"> -> <100,24>, ie (100 kCal / 24 grams of "100 kCal chewy bar")
      this.ingredientAndUnitToKCalRatioMap = new HashMap<Pair<String,String>, Pair<Long, Long>>();

      StringBuilder currIngrIndexStringBuilder = new StringBuilder("ingr00000");
      // ingr00000
      // field 0: "ingrName" -> "tomatoes"
      // field 1: number of units and associated ratios
      // field 2 - 4: unit name, kcal, nUnits
      // field 5 - 7: ....
      ArrayList<Object> currentIngredientArray = new ArrayList<>();
      int nUnitNamesAssociatedWithCurrIngredient;
      ArrayList<String> currentIngredientListOfUnitNames = new ArrayList<>();
      Pair<String, String> ingredientNameUnitNamePair;
      Pair<Long, Long> kCalToNUnitRatioNumberPairAssociatedWithIngredientNameUnitNamePair;
      for (int i = 0 ; i < nIngredients; ++i) // cycle thru ingr00000, ingr00001, etc...
      {
         // fetch the array for ingr00000
         currentIngredientArray = (ArrayList) previouslyUsedIngredientsStraightFromDatabaseMap.get(currIngrIndexStringBuilder.toString());

         // fetch "100kC chewy bar" here
         this.ingredientNameStringArrayList.add((String) currentIngredientArray.get(0));

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
            ingredientAndUnitToKCalRatioMap
                    .put(ingredientNameUnitNamePair, kCalToNUnitRatioNumberPairAssociatedWithIngredientNameUnitNamePair);

         }
         ingredientNameToAssociatedUnitNames
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
              new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, this.ingredientNameStringArrayList);
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
                            ingredientNameToAssociatedUnitNames.get(parent.getItemAtPosition(position)));
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
            if (ingredientAndUnitToKCalRatioMap.containsKey(ingredientNameUnitNamePair))
            {
               nKcalInRatioEditText.setText((ingredientAndUnitToKCalRatioMap.get(ingredientNameUnitNamePair).first).toString());
               nUnitInRatioEditText.setText((ingredientAndUnitToKCalRatioMap.get(ingredientNameUnitNamePair).second).toString());
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
               ingredientsArrayList.add(new Ingredient(
                       ingredientNameAutoCompleteTextView.getText().toString(),
                       unitNameAutoCompleteTextView.getText().toString(),
                       Long.valueOf(nUnitEditText.getText().toString()),
                       Long.valueOf(nKcalInRatioEditText.getText().toString()),
                       Long.valueOf(nUnitInRatioEditText.getText().toString())
               ));
               ingredientToStringArrayList
                       .add(ingredientsArrayList.get(ingredientsArrayList.size() - 1).toString());
               ingredientNameAutoCompleteTextView.setText(null);
               unitNameAutoCompleteTextView.setText(null);
               nUnitEditText.setText(null);
               nKcalInRatioEditText.setText(null);
               nUnitInRatioEditText.setText(null);
            }

         }
      });

      //
      // finishAddingMealButton
      //
      finishAddingMealButton = (Button) this.findViewById(R.id.button_finish_adding_meal);
      finishAddingMealButton.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {

            //TODO enter meal name dialog
            //TODO send meal to databases
            //TODO parse ingredients, add new ingredients to previouslyUsedIngredients, update old ingredients as required


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
      this.ingredientToStringArrayList = new ArrayList<>();
      this.ingredientListView = findViewById(R.id.listView_ingredient_list);
      this.ingredientListViewArrayAdapter =
              new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ingredientToStringArrayList);
      ingredientListView.setAdapter(ingredientListViewArrayAdapter);

      ingredientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id)
         {
            Bundle confirmDeleteIngredientDialogFragmentBundle = new Bundle();
            confirmDeleteIngredientDialogFragmentBundle.putString("ingredientName", ingredientsArrayList.get(position).ingredientName);
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
      this.ingredientsArrayList.remove(this.ingredientIndexConfirmedForDeletion);
      this.ingredientToStringArrayList.remove(this.ingredientIndexConfirmedForDeletion);
      this.ingredientListViewArrayAdapter.notifyDataSetChanged();
   }

   @Override
   public void onDeletionDialogNegativeClick(DialogFragment dialog) {}

   @Override
   public void onBackPressed(){
      Intent addMealActivityToDailyLogActivityIntent = new Intent(this, DailyLogActivity.class);
      startActivity(addMealActivityToDailyLogActivityIntent);
      finish();
   }
}

