package com.example.calorie_king_stefan_rob;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fatsecret.platform.model.CompactFood;
import com.fatsecret.platform.model.CompactRecipe;
import com.fatsecret.platform.model.Food;
import com.fatsecret.platform.model.Recipe;
import com.fatsecret.platform.services.Response;
import com.fatsecret.platform.services.android.Request;
import com.fatsecret.platform.services.android.ResponseListener;

import java.util.ArrayList;
import java.util.List;

public class FatSecret_Search_Meal extends AppCompatActivity {

    private TextView meal_search;
    private Button search;
    private ArrayList<String> foodArray;
    private ArrayAdapter adapter;
    private Context context;
    private ListView response_list;
    private List<CompactFood> foods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fat_secret__search__meal);
        meal_search = findViewById(R.id.meal_search);
        search = findViewById(R.id.search);
        foodArray = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(FatSecret_Search_Meal.this,android.R.layout.simple_list_item_1, foodArray);
        response_list = findViewById(R.id.response_list);
        response_list.setAdapter(adapter);



        response_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int posID = (int) id;
                String name = foodArray.get(position);
                CompactFood food = foods.get(position);
                Intent intent = new Intent(FatSecret_Search_Meal.this, Fat_Secret_Set_Amount.class);
                intent.putExtra("food_name",name);
                intent.putExtra("food_id",foods.get(position).getId());
                startActivity(intent);
                finish();
            }
        });
    }


    public void search_food(View view){
        meal_search = findViewById(R.id.meal_search);
        String query = meal_search.getText().toString();
        String key = "163485bd0ec542e9a48c564298abaf11";
        String secret = "ef51845af1854d2c9413ac42966c85aa";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Listener listener = new Listener();
        Request req = new Request(key, secret, listener);
        req.getFoods(requestQueue,query,1);
    }
    class Listener implements ResponseListener {
        @Override
        public void onFoodListRespone(Response<CompactFood> response) {
            Log.d("db", "food list response");
            //System.out.println("TOTAL FOOD ITEMS: " + response.getTotalResults());
            //Log.d("db",Integer.toString(response.getTotalResults()));
            foods = response.getResults();
            foodArray = new ArrayList<String>();
            for(int i = 0; i < foods.size();i++){
                foodArray.add(foods.get(i).getName());
            }
            adapter = new ArrayAdapter<String>(FatSecret_Search_Meal.this,android.R.layout.simple_list_item_1, foodArray);
            response_list = findViewById(R.id.response_list);
            response_list.setAdapter(adapter);

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
        }

        @Override
        public void onRecipeResponse(Recipe recipe) {
            //System.out.println("RECIPE NAME: " + recipe.getName());
            Log.d("db", "recipe response found");
        }
    }




}