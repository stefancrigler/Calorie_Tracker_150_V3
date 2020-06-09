package com.example.calorie_king_stefan_rob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class My_Groups_Page extends AppCompatActivity {

    private static Button to_leaderboards;
    private static Button back_to_home;
    private static Button group_update;
    private TextView my_group;
    private TextView group_edit;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__groups__page);
        my_group = (TextView) findViewById(R.id.textView5);
        SharedPreferences savedGroup = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String group_name = savedGroup.getString("group_name",null);
        if(group_name == null){
            my_group.setText("Create or Join a Group by typing in the name below!");
        }
        else{
            my_group.setText("My Group: " + group_name);
        }
    }

    //return to home screen
    public void back_to_home(View view){
        Intent intent = new Intent(My_Groups_Page.this, HomeScreen.class);
        startActivity(intent);
        finish();
    }
    //see leaderboard
    public void to_leader(View view){
        SharedPreferences savedGroup = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String group_name = savedGroup.getString("group_name",null);
        if(group_name == null) {
            Toast.makeText(My_Groups_Page.this, "Create or Join a Group to See Leaderboards!", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(My_Groups_Page.this, CalorieGoalBoard.class);
            startActivity(intent);
            finish();
        }
    }

    //update group


    public void update_group(View view){
        group_edit = (TextView) findViewById(R.id.new_group);
        final String groupname = group_edit.getText().toString();
        db.collection("calorie_goals")
                .whereEqualTo("group", groupname)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                count = count + 1;
                            }
                            if(count < 6){
                                Toast.makeText(My_Groups_Page.this, "Joined Group: " + groupname, Toast.LENGTH_SHORT).show();
                                my_group.setText("My Group: " + groupname);
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                final String email = prefs.getString("email",null);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("group_name",groupname);
                                editor.apply();
                                String username = prefs.getString("username",null);
                                Map<String, String> cg = new HashMap<>();
                                cg.put("group",groupname);
                                db.collection("calorie_goals").document(email)
                                        .set(cg, SetOptions.merge())
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
                            else{
                                Toast.makeText(My_Groups_Page.this, "Sorry, this group is full", Toast.LENGTH_SHORT).show();
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                final String email = prefs.getString("email",null);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("group_name",groupname);
                                editor.apply();
                                String username = prefs.getString("username",null);
                                Map<String, String> cg = new HashMap<>();
                                cg.put("group",groupname);
                                db.collection("calorie_goals").document(email)
                                        .set(cg, SetOptions.merge())
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
                        } else {
                            Log.d("db", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


}
