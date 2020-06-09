package com.example.calorie_king_stefan_rob;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;


public class OpeningPage extends AppCompatActivity {
    private static Button login;
    private static Button signup;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_page);
        LocalDate date = LocalDate.now();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        if (! prefs.getString("current date",null).equals(date.toString())){
            Log.d("db","hey ur here");
            editor.putInt("current number",000);
            editor.putString("current date", date.toString());
            editor.apply();
        }



    }
    public void signUp(View view){
        //Click the sign up button, for users that have never made an account
        Log.i("SignUp", "Creating Intent");
        Intent intent  = new Intent(OpeningPage.this, SignUp_Page.class);
        Log.i("SignUp","Intent Created, Moving to new Sign Up Page");
        startActivity(intent);
        Log.i("SignUp","Activity Started, Finishing Now");
        finish();
    }
    public void logIn(View view){

        Intent intent = new Intent(OpeningPage.this, LogIn.class);
        startActivity(intent);
        finish();
    }
}
