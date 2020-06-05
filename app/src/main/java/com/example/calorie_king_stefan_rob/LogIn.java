package com.example.calorie_king_stefan_rob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fatsecret.platform.model.CompactFood;
import com.fatsecret.platform.model.CompactRecipe;
import com.fatsecret.platform.model.Food;
import com.fatsecret.platform.model.Recipe;
import com.fatsecret.platform.services.FatsecretService;
import com.fatsecret.platform.services.Response;
import com.fatsecret.platform.services.android.ResponseListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.fatsecret.platform.services.android.Request;

import java.util.List;

public class LogIn extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView username;
    private TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();

        Log.d("db","begin grab");

        String key = "163485bd0ec542e9a48c564298abaf11";
        String secret = "ef51845af1854d2c9413ac42966c85aa";
        /*
        FatsecretService service = new FatsecretService(key, secret);
        String query = "pasta";
        com.fatsecret.platform.services.Response<CompactFood> response = service.searchFoods(query);
        if (response == null) {
            Log.i("db", "IS NULL :(");
        }
        else{
            Log.d("db","not null");
        }

         */

        Log.d("db","query now");
        String query = "pasta";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Listener listener = new Listener();

        Log.d("db","listener set");

        Request req = new Request(key, secret, listener);

        Log.d("db","req set");

        req.getFoods(requestQueue,query,2);

        Log.d("db","req made");
        Log.d("db","req made1");
        Log.d("db","req made2");
        Log.d("db","req made3");
        Log.d("db","req made4");



    }
    public void back_login(View view){
        Intent intent  = new Intent(LogIn.this, OpeningPage.class);
        startActivity(intent);
        finish();
    }
    public void toHome(View view){
        //need to implement checking log in, but in meantime just go straight to home screen
        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);
        if(username == null || password == null){
            return;
        }
        signIn(username.getText().toString(), password.getText().toString());
    }

    private void signIn(final String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Signing In", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            final String em = email;
                            editor.putString("email",email);
                            editor.apply();
                            Intent intent = new Intent(LogIn.this, HomeScreen.class);
                            startActivity(intent);
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Signing In", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
    class Listener implements ResponseListener {
        @Override
        public void onFoodListRespone(Response<CompactFood> response) {
            Log.d("db","food list response");
            //System.out.println("TOTAL FOOD ITEMS: " + response.getTotalResults());
            Log.d("db",Integer.toString(response.getTotalResults()));
            List<CompactFood> foods = response.getResults();
            //This list contains summary information about the food items

            //System.out.println("=========FOODS============");
            for (CompactFood food: foods) {
                System.out.println(food.getName());
                Log.d("db",food.getName());
            }
        }

        @Override
        public void onRecipeListRespone(Response<CompactRecipe> response) {
            Log.d("db","recipe list response");
            //System.out.println("TOTAL RECIPES: " + response.getTotalResults());

            List<CompactRecipe> recipes = response.getResults();
            //System.out.println("=========RECIPES==========");
            for (CompactRecipe recipe: recipes) {
                //System.out.println(recipe.getName());
            }
        }

        @Override
        public void onFoodResponse(Food food) {
            //System.out.println("FOOD NAME: " + food.getName());
            Log.d("db","response found");
        }

        @Override
        public void onRecipeResponse(Recipe recipe) {
            //System.out.println("RECIPE NAME: " + recipe.getName());
            Log.d("db","recipe response found");
        }
    }








}
