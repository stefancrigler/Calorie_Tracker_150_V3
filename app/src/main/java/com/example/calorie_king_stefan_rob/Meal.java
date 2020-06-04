package com.example.calorie_king_stefan_rob;

import java.util.HashMap;
import java.util.Map;

public class Meal {
    public Map<String,Ingredient> ingredients;
    public String name;
    public float calories;

    public Meal(){
        ingredients = new HashMap<>();
        name = "n/a";
        calories = 0;
    }

    public Meal(Map<String,Ingredient> i, String n, float c){
        ingredients = i;
        name = n;
        calories = c;
    }
}
