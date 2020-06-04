package com.example.calorie_king_stefan_rob;

public class Ingredient {
    public String name;
    public String unit;
    public float nUnit;

    public Ingredient(){
        name = "n/a";
        unit = "n/a";
        nUnit = 0;
    }
    public Ingredient(String n,String u, float nU){
        name = n;
        unit = u;
        nUnit = nU;
    }

}
