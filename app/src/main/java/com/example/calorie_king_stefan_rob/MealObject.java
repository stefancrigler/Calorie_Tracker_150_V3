package com.example.calorie_king_stefan_rob;

import java.util.HashMap;
import java.util.Map;

public class MealObject
{
    public Map<String,Ingredient> ingredients;
    public String name;
    public float calories;

    public MealObject(){
        ingredients = new HashMap<>();
        name = "n/a";
        calories = 0;
    }

    public MealObject(Map<String,Ingredient> i, String n, float c){
        ingredients = i;
        name = n;
        calories = c;
    }
}
