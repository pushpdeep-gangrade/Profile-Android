package com.example.profileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.profileapp.cart.CartFragment;
import com.example.profileapp.login.LoginActivity;
import com.example.profileapp.models.Order;
import com.example.profileapp.models.StoreItem;
import com.example.profileapp.models.User;
import com.example.profileapp.profile.EditProfileFragment;
import com.example.profileapp.profile.ViewProfileFragment;
import com.example.profileapp.store.StoreFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String AUTH_KEY = "authorizationkey";
    private String mAuthorizationkey;
    public static final String url = "http://104.248.113.55:8088/v1/user/";
    private NavController navController;
    public static List<StoreItem> cartList = new ArrayList<>();
    public static List<Order> orderList = new ArrayList<>();
    private TextView userFullname, userEmail;
    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences("info", Context.MODE_PRIVATE);
        if(sharedPref!=null){
            mAuthorizationkey = sharedPref.getString("authKey","");
            Log.d("mauth", mAuthorizationkey);
            getUser();
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

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .setDrawerLayout(drawerLayout).build();

        NavigationUI.setupActionBarWithNavController(this,navController, appBarConfiguration);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setNavigationItemSelectedListener(this);

        userFullname = navView.getHeaderView(0).findViewById(R.id.nav_header_user_name);
        userEmail = navView.getHeaderView(0).findViewById(R.id.nav_header_nav_user_email);

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


            Bundle authBundle = new Bundle();
            authBundle.putString(AUTH_KEY, mAuthorizationkey);
            authBundle.putSerializable("cartList", (Serializable) cartList);
            Log.d("User", user.toString());
            navController.navigate(R.id.nav_cart, authBundle);

            return true;
        }
//        else if (item.getItemId() == R.id.checkout) {
//
//            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Check Out");
//
//            builder.setMessage("Would you like to check out?")
//            .setPositiveButton("Check Out", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    Bundle authBundle = new Bundle();
//                    authBundle.putString(AUTH_KEY, mAuthorizationkey);
//                    navController.navigate(R.id.nav_order_complete, authBundle);
//                }
//            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//
//                }
//            });
//
//            final AlertDialog dialog = builder.create();
//
//            builder.setCancelable(false);
//
//            dialog.show();
//
//            return true;
//        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        Bundle authBundle = new Bundle();
        authBundle.putString(AUTH_KEY, mAuthorizationkey);
        authBundle.putSerializable("cartList", (Serializable) cartList);

        if(item.getItemId() == R.id.nav_view_profile){
            navController.navigate(R.id.nav_view_profile, authBundle);

            drawer.closeDrawer(GravityCompat.START);
            Log.d("Profile", "Clicked Profile");
        }
        else if(item.getItemId() == R.id.nav_order_history){
            navController.navigate(R.id.nav_order_history, authBundle);

            drawer.closeDrawer(GravityCompat.START);
            Log.d("Order History", "Clicked Order History");
        }
        else if(item.getItemId() == R.id.nav_store){
            navController.navigate(R.id.nav_store, authBundle);

            drawer.closeDrawer(GravityCompat.START);
            Log.d("Store", "Clicked Store");
        }
        else if(item.getItemId() == R.id.nav_cart){
            navController.navigate(R.id.nav_cart, authBundle);

            drawer.closeDrawer(GravityCompat.START);
            Log.d("Cart", "Clicked Cart");
        }
        else if(item.getItemId() == R.id.nav_transaction_history){
            navController.navigate(R.id.nav_transaction_history, authBundle);

            drawer.closeDrawer(GravityCompat.START);
            Log.d("Cart", "Clicked Transaction History");
        }
        else if(item.getItemId() == R.id.nav_logout){
            SharedPreferences settings = getSharedPreferences("info", Context.MODE_PRIVATE);
            settings.edit().clear().commit();

            Intent goBackToLogin = new Intent(this, LoginActivity.class);
            startActivity(goBackToLogin);
            finish();

            drawer.closeDrawer(GravityCompat.START);
            Log.d("Logout", "Clicked Logout");
        }

        return true;
    }

    public void getUser(){
        String profileUrl = MainActivity.url + "profile/me";

        RequestQueue queue = Volley.newRequestQueue(this);

        final StringRequest getRequest = new StringRequest(Request.Method.GET, profileUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        if(response.equals("UNAUTHORIZED")){
                            Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                        }
                        else{
                            Gson gsonObject = new Gson();

                            try {
                                JSONObject userObject = new JSONObject(response);

                                user.firstName = userObject.getString("fname");
                                user.lastName = userObject.getString("lname");
                                user.email = userObject.getString("emailId");
                                user.address = userObject.getString("address");
                                user.age = userObject.getInt("age");

                                userEmail.setText(user.email);
                                userFullname.setText(String.format("%s %s", user.firstName, user.lastName));

                                Log.d("User", userObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //   Toast.makeText(getContext(), "Loaded User Profile", Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("authorizationkey", mAuthorizationkey);

                return params;
            }
        };

        queue.add(getRequest);
    }
}