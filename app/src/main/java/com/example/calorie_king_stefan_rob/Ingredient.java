package com.example.calorie_king_stefan_rob;

public class Ingredient
{
    public String ingredientName;
    public String unitName;
    public long nUnit;
    public long calorieRatioNKCal;
    public long calorieRatioNUnits;

//    public Ingredient()
//    {
//        name = "n/a";
//        unit = "n/a";
//        nUnit = 0;
//    }
    public Ingredient(String ingredientName, String unitName, long nUnit, long calorieRatioNKCal, long calorieRatioNUnits)
    {
        this.ingredientName = ingredientName;
        this.unitName = unitName;
        this.nUnit = nUnit;
        this.calorieRatioNKCal = calorieRatioNKCal;
        this.calorieRatioNUnits = calorieRatioNUnits;
    }

    public String toString()
    {
        long totalCalories = this.nUnit*calorieRatioNKCal/calorieRatioNUnits;
        return String.format("%-30s/%5d%-10s/%6d calories", this.ingredientName, this.nUnit, this.unitName, totalCalories );
    }

}
