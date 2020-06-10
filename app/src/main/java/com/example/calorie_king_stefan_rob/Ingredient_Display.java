package com.example.calorie_king_stefan_rob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Iterator;
import java.util.Map;


public class Ingredient_Display extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    CustomListAdapter_scoreboard calorie_board;

    final String[] NameArray = {"","","","","",""};
    final String[] AmountArray= {"","","","","",""};

    Integer[] ImageArray = {R.drawable.white_background,R.drawable.white_background,R.drawable.white_background,R.drawable.white_background,R.drawable.white_background,R.drawable.white_background};

    final String[] emptyArray = {"","","","","",""};
    int counter = 0;
    int sepCounter = 0;
    boolean done;

    ListView listView;

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Ingredient_Display.this, MyCalorieHistoryActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient__display);

        final Intent intent = getIntent();
        final String meal_name = intent.getStringExtra("meal");
        Log.d("db",meal_name);

        calorie_board = new CustomListAdapter_scoreboard(Ingredient_Display.this, NameArray, AmountArray, ImageArray);
        Log.d("db","Made it after the adapter");
        listView = (ListView) findViewById(R.id.ingred_list);
        listView.setAdapter(calorie_board);

        db.collection("testUser02")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            String date = prefs.getString("calorie_history_date", null);
                            Log.d("db","was successful");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(date)){
                                    Log.d("db","found correct date");
                                    //have the correct day
                                    //need the correct meal
                                    Map<String,Object> all_inputs = ( Map<String,Object>) document.getData();
                                    Iterator it =all_inputs.entrySet().iterator();
                                    while(it.hasNext()) {
                                        Map.Entry pair = (Map.Entry) it.next();
                                        Log.d("db",pair.getKey().toString());
                                        Map<String,Object> m1 = (Map<String,Object>) pair.getValue();
                                        String m_name = (String) m1.get("name");
                                        if(meal_name.equals(m_name)){
                                            Log.d("db","found the right meal");
                                            Log.d("db",m_name);
                                            Map<String, Object> m = (Map<String,Object>) pair.getValue();
                                            Map<String, IngredientClass> ingredients_list =  (Map<String, IngredientClass>) m.get("ingredients");
                                            Iterator i = ingredients_list.entrySet().iterator();
                                            while(i.hasNext()){
                                                Log.d("db","in the while loop");
                                                Map.Entry ingred = (Map.Entry) i.next();
                                                Map<String,Object> temp =  (Map<String,Object>) ingred.getValue();
                                                Log.d("db",temp.get("name").toString());
                                                NameArray[counter] = temp.get("name").toString();
                                                StringBuilder stringBuilder = new StringBuilder(100);
                                                Double nU = (Double) temp.get("nUnit");
                                                String f = Double.toString(nU);
                                                stringBuilder.append(f);
                                                stringBuilder.append(" ");
                                                stringBuilder.append(temp.get("unit").toString());
                                                AmountArray[counter] = stringBuilder.toString();
                                                counter = counter + 1;
                                            }
                                            counter = 0;
                                        }
                                    }
                                }
                                Log.d("db", document.getId() + " => " + document.getData());
                            }
                            Log.d("db","Begin change");
                            calorie_board = new CustomListAdapter_scoreboard(Ingredient_Display.this, NameArray, AmountArray, ImageArray);
                            listView.setAdapter(calorie_board);
                        } else {
                            Log.w("db", "Error getting documents.", task.getException());
                        }
                    }
                });


    }
}
