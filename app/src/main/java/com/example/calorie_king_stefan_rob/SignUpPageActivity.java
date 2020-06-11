package com.example.calorie_king_stefan_rob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class SignUpPageActivity extends AppCompatActivity{

   private static Button sign_up_button;
   private static Button back_button;
   private static EditText username;
   private static EditText password;
   private FirebaseAuth mAuth;

   // added by rob
   private FirebaseFirestore db;


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
      Intent intent  = new Intent(SignUpPageActivity.this, OpeningPageActivity.class);
      startActivity(intent);
      finish();
   }

   public void final_signup(View view){
      username = (EditText)findViewById(R.id.username);
      password = (EditText)findViewById(R.id.password);
      //Intent intent = new Intent(SignUp_Page.this, HomeScreen.class);
      createAccount(username.getText().toString(), password.getText().toString());

   }

   private void createAccount(final String email, String password) {
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
                       SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                       SharedPreferences.Editor editor = prefs.edit();
                       final String em = email;
                       editor.putString("email",email);

                        // added by rob
                       db = FirebaseFirestore.getInstance();
                        HashMap<String, Long> nIngredients = new HashMap<>();
                        nIngredients.put("nIngredients", new Long(0));
                        db.collection(user.getEmail())
                                .document("previouslyUsedIngredients")
                                .set(nIngredients, SetOptions.merge());


                       Intent intent = new Intent(SignUpPageActivity.this, HomeScreenActivity.class);
                       startActivity(intent);
                       finish();
                       //updateUI(user);
                    } else {
                       // If sign in fails, display a message to the user.
                       Log.w("Creating Account", "createUserWithEmail:failure", task.getException());
                       Toast.makeText(SignUpPageActivity.this, "Authentication failed.",
                               Toast.LENGTH_SHORT).show();
                    }
                 }
              });
   }
}
