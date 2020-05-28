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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = prefs.getString("username","guest1");
        username_show.setText("Username: " + username);

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
        TextView username_update  =findViewById(R.id.editText);
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
            Toast.makeText(Settings.this, "Not a Valid Calorie Goal",Toast.LENGTH_SHORT).show();
        }
    }
    public void back_to_home(View view){
        Intent intent = new Intent(Settings.this, HomeScreen.class);
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
