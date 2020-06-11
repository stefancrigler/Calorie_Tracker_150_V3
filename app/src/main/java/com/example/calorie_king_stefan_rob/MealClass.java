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
        nCalories = 0;
    }

    public MealClass(String workoutName, float calories)
    {
        this.name = workoutName;
        this.nCalories = calories;
    }

    public MealClass(Map<String, IngredientClass> i, String n)
    {
        this.nCalories = 0;
        ingredients = i;
        for(Map.Entry ingredientEntry: this.ingredients.entrySet())
        {
            this.nCalories += ((float) ((IngredientClass) ingredientEntry.getValue()).calories);
        }

        name = n;

    }

    public MealClass(Map<String, IngredientClass> i, String n, float nCalories)
    {
        this.nCalories = nCalories;
        ingredients = i;
        name = n;

    }

    public String toString()
    {
        return String.format("%-50s/%-6d calories", this.name, this.nCalories );
    }
}
