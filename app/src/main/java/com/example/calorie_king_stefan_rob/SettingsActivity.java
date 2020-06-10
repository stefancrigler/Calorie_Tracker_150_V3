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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private Button calorie_goal_update;
    private TextView goal_update;
    private TextView calorie_goal;
    private int daily_goal;
    private Object goal;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TextView username_show = findViewById((R.id.username_show));
        TextView calorie_goal = findViewById(R.id.calorie_goal);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = prefs.getString("username","guest1");
        int goal = prefs.getInt("calorie goal", 2000);
        calorie_goal.setText("Calorie Goal: " + Integer.toString(goal));
        username_show.setText("Username: " + username);

        //Test
        /*
        Ingredient i1 = new Ingredient("flour","ounces",3);
        Ingredient i2 = new Ingredient("yeast","tablespoon",2);
        Ingredient i3 = new Ingredient("cinnamon","teaspoon",1);
        Map<String,Ingredient> m = new HashMap<>();
        m.put("Ingredient000",i1);
        m.put("Ingredient001",i2);
        m.put("Ingredient002",i3);
        Meal meal = new Meal(m,"cinnabread",100);
        Map<String, Meal> meal000 = new HashMap<>();
        meal000.put("meal000",meal);
        db.collection("testUser02").document("2020-06-03")
                .set(meal000, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("db", "meal000!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("db", "Error writing document", e);
                    }
                });

        //Test
        Ingredient i11 = new Ingredient("bacon","ounces",4);
        Ingredient i21 = new Ingredient("hot dog","ounces",8);
        Map<String,Ingredient> m1 = new HashMap<>();
        m1.put("Ingredient000",i11);
        m1.put("Ingredient001",i21);
        Meal meal1 = new Meal(m1,"bacon wrapped hot dog",480);
        Map<String, Meal> meal001 = new HashMap<>();
        meal001.put("meal001",meal1);
        db.collection("testUser02").document("2020-06-03")
                .set(meal001, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("db", "meal001!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("db", "Error writing document", e);
                    }
                });

        //Test
        Ingredient i12 = new Ingredient("spinach","ounces",8);
        Ingredient i22 = new Ingredient("ranch dressing","tablespoon",6);
        Ingredient i32 = new Ingredient("tomato","ounces",3);
        Ingredient i42 = new Ingredient("olives","ounces",1);
        Map<String,Ingredient> m2 = new HashMap<>();
        m2.put("Ingredient000",i12);
        m2.put("Ingredient001",i22);
        m2.put("Ingredient002",i32);
        m2.put("Ingredient003",i42);
        Meal meal2 = new Meal(m2,"salad",340);
        Map<String, Meal> meal002 = new HashMap<>();
        meal002.put("meal002",meal2);
        db.collection("testUser02").document("2020-06-03")
                .set(meal002, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("db", "meal002!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("db", "Error writing document", e);
                    }
                });

        Meal w0 = new Meal(new HashMap<String, Ingredient>(),"Bike Ride",-72);
        Map<String, Meal> workout000 = new HashMap<>();
        workout000.put("workout000",w0);
        db.collection("testUser02").document("2020-06-03")
                .set(workout000, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("db", "workout000!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("db", "Error writing document", e);
                    }
                });

         */



        Log.d("db", "here");
        db.collection("calorie_goals")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("db", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("db", "Error getting documents.", task.getException());
                        }
                    }
                });

    }



    public void update_goal(View View){
        TextView calorie_goal = findViewById(R.id.calorie_goal);
        TextView goal_update = findViewById(R.id.goal_update);
        TextView username_show = findViewById((R.id.username_show));
        TextView username_update = findViewById(R.id.editText);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String email = prefs.getString("email", null);
        try{
            Integer.parseInt(goal_update.getText().toString());
            calorie_goal.setText("Calorie Goal: " + goal_update.getText().toString());
            daily_goal = Integer.parseInt(goal_update.getText().toString()); //set the goal to new value
            //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("calorie goal" ,daily_goal);
            if(username_update.getText().toString()!=""){
                editor.putString("username",username_update.getText().toString());
                username_show.setText("Username: "+ username_update.getText().toString());
            }
            editor.apply();
            Log.d("db","Beginning add");
            Map<String, String> cg = new HashMap<>();
            cg.put("calorie_goal",goal_update.getText().toString());
            cg.put("current_day","300");
            cg.put("username",username_update.getText().toString());
            db.collection("calorie_goals").document(email)
                    .set(cg)
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
        }
        catch(NumberFormatException e){
            Toast.makeText(SettingsActivity.this, "Not a Valid Calorie Goal",Toast.LENGTH_SHORT).show();
        }
    }
    public void back_to_home(View view){
        Intent intent = new Intent(SettingsActivity.this, HomeScreenActivity.class);
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("db", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("db", "Error getting documents.", task.getException());
                        }
                    }
                });
        startActivity(intent);
        finish();
    }
}
