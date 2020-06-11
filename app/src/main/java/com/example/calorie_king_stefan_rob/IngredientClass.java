package com.example.calorie_king_stefan_rob;

public class IngredientClass
{
    public String ingredientName;
    public String unitName;
    public long nUnit;
    public long calorieRatioNKCal;
    public long calorieRatioNUnits;
    public long calories;

//    public Ingredient()
//    {
//        name = "n/a";
//        unit = "n/a";
//        nUnit = 0;
//    }
    public IngredientClass(String ingredientName, String unitName, long nUnit, long calorieRatioNKCal, long calorieRatioNUnits)
    {
        this.ingredientName = ingredientName;
        this.unitName = unitName;
        this.nUnit = nUnit;
        this.calorieRatioNKCal = calorieRatioNKCal;
        this.calorieRatioNUnits = calorieRatioNUnits;
        this.calories = this.nUnit*this.calorieRatioNKCal/this.calorieRatioNUnits;
    }

    public String toString()
    {
        return String.format("%-30s/%-5d%-10s/%-6d calories", this.ingredientName, this.nUnit, this.unitName, this.calories );
    }

}
