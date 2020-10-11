package com.example.profileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.example.profileapp.cart.CartFragment;
import com.example.profileapp.login.LoginActivity;
import com.example.profileapp.models.Order;
import com.example.profileapp.models.StoreItem;
import com.example.profileapp.profile.EditProfileFragment;
import com.example.profileapp.store.StoreFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String AUTH_KEY = "authorizationkey";
    private String mAuthorizationkey;
    public static final String url = "http://104.248.113.55:8080/v1/user/";
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    public static List<StoreItem> cartList = new ArrayList<>();
    public static List<Order> orderList = new ArrayList<>();

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

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .setDrawerLayout(drawerLayout).build();

        NavigationUI.setupActionBarWithNavController(this,navController, appBarConfiguration);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

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
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.logout) {
          /*  SharedPreferences settings = getSharedPreferences("info", Context.MODE_PRIVATE);
            settings.edit().clear().commit();*/

/*            Log.d("nav",getClass().toString() + " Called from: " + getParentActivityIntent());
            if (getClass().toString().equals("EditProfileFragment")){
                navController.navigate(R.id.action_nav_edit_profile_to_nav_logout);
            }
            else { */
                //navController.navigate(R.id.action_nav_view_profile_to_nav_logout2);

 //           }
           /* Intent goBackToLogin = new Intent(this, LoginActivity.class);
            startActivity(goBackToLogin);
            finish();*/


            navController.navigate(R.id.nav_cart);

            return true;
        }
        else if (item.getItemId() == R.id.checkout) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Check Out");

            builder.setMessage("Would you like to check out?")
            .setPositiveButton("Check Out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    navController.navigate(R.id.nav_order_complete);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            final AlertDialog dialog = builder.create();

            builder.setCancelable(false);

            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}