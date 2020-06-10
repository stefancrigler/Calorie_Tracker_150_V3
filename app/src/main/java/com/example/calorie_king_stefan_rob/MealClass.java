package com.example.calorie_king_stefan_rob;

import java.util.HashMap;
import java.util.Map;

public class MealClass
{
    public Map<String, IngredientClass> ingredients;
    public String name;
    public float nCalories;

    public MealClass(){
        ingredients = new HashMap<>();
        name = "n/a";
        calories = 0;
    }

    public MealClass(Map<String, IngredientClass> i, String n)
    {
        ingredients = i;
        name = n;
        calories = c;
    }
}
