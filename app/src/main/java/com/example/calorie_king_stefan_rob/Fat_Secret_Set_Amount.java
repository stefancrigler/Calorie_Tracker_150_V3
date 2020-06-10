package com.example.calorie_king_stefan_rob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fatsecret.platform.model.CompactFood;
import com.fatsecret.platform.model.CompactRecipe;
import com.fatsecret.platform.model.Food;
import com.fatsecret.platform.model.Recipe;
import com.fatsecret.platform.model.Serving;
import com.fatsecret.platform.services.Response;
import com.fatsecret.platform.services.android.Request;
import com.fatsecret.platform.services.android.ResponseListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fat_Secret_Set_Amount extends AppCompatActivity {

    private TextView food_description;
    private TextView calories;
    private TextView servings;
    private Button update;
    private Button add;
    private BigDecimal cals;
    private Double d;
    private String name;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fat__secret__set__amount);
        Intent intent = getIntent();
        name = intent.getStringExtra("food_name");
        Long id = intent.getLongExtra("food_id",0);
        String key = "163485bd0ec542e9a48c564298abaf11";
        String secret = "ef51845af1854d2c9413ac42966c85aa";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Listener listener = new Listener();
        Request req = new Request(key, secret, listener);
        req.getFood(requestQueue,id);


    }
    class Listener implements ResponseListener {
        @Override
        public void onFoodListRespone(Response<CompactFood> response) {
            Log.d("db", "food list response");
            //System.out.println("TOTAL FOOD ITEMS: " + response.getTotalResults());
            //Log.d("db",Integer.toString(response.getTotalResults()));
            List<CompactFood> foods = response.getResults();
            //This list contains summary information about the food items

            //System.out.println("=========FOODS============");
            for (CompactFood food : foods) {
                System.out.println(food.getName());
                Log.d("db", food.getName());
            }
        }

        @Override
        public void onRecipeListRespone(Response<CompactRecipe> response) {
            Log.d("db", "recipe list response");
            //System.out.println("TOTAL RECIPES: " + response.getTotalResults());

            List<CompactRecipe> recipes = response.getResults();
            //System.out.println("=========RECIPES==========");
            for (CompactRecipe recipe : recipes) {
                //System.out.println(recipe.getName());
            }
        }

        @Override
        public void onFoodResponse(Food food) {
            //System.out.println("FOOD NAME: " + food.getName());
            Log.d("db", "response found");
            //food was found
            update = findViewById(R.id.update);
            add = findViewById(R.id.add);
            calories = findViewById(R.id.calories);
            food_description = findViewById(R.id.food_description);
            food_description.setText(food.getServings().get(0).toString());
            if (food.getServings() != null && food.getServings().size() > 0) {
                // The following line will give you Serving object.
                Serving serving = food.getServings().get(0);
                String cal = serving.getCalories().toString();
                cals = serving.getCalories();
                calories.setText("Calories: " + cal);

            }


        }

        @Override
        public void onRecipeResponse(Recipe recipe) {
            //System.out.println("RECIPE NAME: " + recipe.getName());
            Log.d("db", "recipe response found");
        }
    }
    public void update_serving(View view){
        calories = (TextView) findViewById(R.id.calories);
        servings = findViewById(R.id.servings);
        try{
            Integer.parseInt(servings.getText().toString());
            BigDecimal new_cal = new BigDecimal(Integer.parseInt(servings.getText().toString()));
            BigDecimal calor = new_cal.multiply(cals);
            cals = calor;
            Log.d("db","BigDecimal");
            Log.d("db",calor.toString());
            d = calor.doubleValue();
            calories.setText("Calories: " + calor.toString());
        }
        catch(NumberFormatException e){
            Toast.makeText(Fat_Secret_Set_Amount.this, "Not a Valid Calorie Goal",Toast.LENGTH_SHORT).show();
        }
    }
    public void add_meal(View view){

        LocalDate date = LocalDate.now();
        MealObject meal = new MealObject(new HashMap<>(),name, cals.intValue());
        Map<String, MealObject> meal005 = new HashMap<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String email = prefs.getString("email", null);
        int num = prefs.getInt("current number",000);
        meal005.put("meal" + Integer.toString(num), meal);
        db.collection(email).document(date.toString())
                .set(meal005, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("db", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("db", "Error writing document", e);
                    }
                });
        SharedPreferences.Editor editor = prefs.edit();

        num = num + 1;
        editor.putInt("current number",num);
        editor.apply();
        Intent intent = new Intent(Fat_Secret_Set_Amount.this, HomeScreenActivity.class);
        startActivity(intent);
        finish();



    }



}