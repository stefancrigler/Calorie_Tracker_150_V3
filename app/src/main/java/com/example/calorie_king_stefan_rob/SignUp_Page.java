package com.example.calorie_king_stefan_rob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp_Page extends AppCompatActivity{

    private static Button sign_up_button;
    private static Button back_button;
    private static EditText username;
    private static EditText password;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    public void back(View view){
        Intent intent  = new Intent(SignUp_Page.this, OpeningPage.class);
        startActivity(intent);
        finish();
    }

    public void final_signup(View view){
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        //Intent intent = new Intent(SignUp_Page.this, HomeScreen.class);
        createAccount(username.getText().toString(), password.getText().toString());

    }

    private void createAccount(String email, String password) {
        Log.d("Creating Account", "createAccount:" + email);
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Creating Acccount", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();



                            Intent intent = new Intent(SignUp_Page.this, HomeScreen.class);
                            startActivity(intent);
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Creating Account", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp_Page.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }







}
