package com.example.profileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.profileapp.login.LoginActivity;
import com.example.profileapp.profile.EditProfileFragment;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {

    private static final String AUTH_KEY = "authorizationkey";
    private String mAuthorizationkey;
    public static final String url = "http://104.248.113.55:8080/v1/user/";
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPref = getSharedPreferences("info", Context.MODE_PRIVATE);
        if(sharedPref!=null){
            mAuthorizationkey = sharedPref.getString("authKey","");
        }
//        if (getIntent().getStringExtra(AUTH_KEY) != null) {
//            mAuthorizationkey = getIntent().getStringExtra(AUTH_KEY);
//        }

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        Bundle authBundle = new Bundle();
        authBundle.putString(AUTH_KEY, mAuthorizationkey);
        navController.setGraph(navController.getGraph(), authBundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.logout) {
            SharedPreferences settings = getSharedPreferences("info", Context.MODE_PRIVATE);
            settings.edit().clear().commit();
/*            Log.d("nav",getClass().toString() + " Called from: " + getParentActivityIntent());
            if (getClass().toString().equals("EditProfileFragment")){
                navController.navigate(R.id.action_nav_edit_profile_to_nav_logout);
            }
            else { */
                //navController.navigate(R.id.action_nav_view_profile_to_nav_logout2);
            Intent goBackToLogin = new Intent(this, LoginActivity.class);
            startActivity(goBackToLogin);
            finish();
 //           }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}