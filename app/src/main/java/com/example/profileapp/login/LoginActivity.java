package com.example.profileapp.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.profileapp.MainActivity;
import com.example.profileapp.R;
import com.example.profileapp.models.User;
import com.example.profileapp.profile.GetProfile;
import com.example.profileapp.signup.SignUpActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String AUTH_KEY = "authorizationkey";
    private String authkey;
    Button login;
    EditText email, password;
    Button createAccount;
    final String url = "http://104.248.113.55:8088/v1/user/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getSharedPreferences("info", Context.MODE_PRIVATE);
        String a = sharedPref.getString("email", "");
        String b = sharedPref.getString("authKey", "");
        Log.d("demo", "hello" + a + "   " + b);
        verifyToken(a, b);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        login = findViewById(R.id.login_loginButton);
        createAccount = findViewById(R.id.login_createAccountLink);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("demo", "login");
                //new GetPlacesAsync(LoginActivity.this, db, AddTripActivity.this).execute(url);
                String loginUrl = url + "login";

                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

                StringRequest postRequest = new StringRequest(Request.Method.POST, loginUrl,
                        new Response.Listener<String>() {
                            @SuppressLint("ApplySharedPref")
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                                if (response.equals("Invalid Credentials")) {
                                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                                } else {
                                    Gson gsonObject = new Gson();
                                    try {
                                        JSONArray arr = new JSONArray(response);
                                        JSONObject userObject = new JSONObject(String.valueOf(arr.get(0)));
                                        User user = new User();
                                        user.firstName = userObject.getString("fname");
                                        user.lastName = userObject.getString("lname");
                                        user.email = userObject.getString("emailId");
                                        user.address = userObject.getString("address");
                                        user.age = userObject.getInt("age");

//                                        SharedPreferences prefs = getSharedPreferences("info", MODE_PRIVATE);
//                                        prefs.edit().putString("user", gsonObject.toJson(user)).commit();

                                        SharedPreferences prefs = getSharedPreferences("info", MODE_PRIVATE);
                                        prefs.edit().putString("email", user.email).commit();
                                        prefs.edit().putString("authKey", authkey).commit();

                                        Log.d("Login", response);



                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    //  Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                    Intent gotoMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                    Bundle authBundle = new Bundle();
                                    //   Log.d("AuthKey", authkey);
                                 //   Log.d("auth", authkey);
                                    authBundle.putString(AUTH_KEY, authkey); //Your id

                                    SharedPreferences prefs = getSharedPreferences("info", MODE_PRIVATE);
                                    //prefs.edit().putString("email", user.email).commit();
                                    prefs.edit().putString("authKey", authkey).commit();

                                    gotoMainActivity.putExtras(authBundle); //Put your id to your next Intent
                                    startActivity(gotoMainActivity);
                                    finish();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                NetworkResponse response = error.networkResponse;
                                String errorMsg = "";
                                if (response != null && response.data != null) {
                                    String errorString = new String(response.data);
                                    Log.i("log error", errorString);
                                }
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        String emailText = email.getText().toString();
                        String passwordText = password.getText().toString();

                        Map<String, String> params = new HashMap<>();

                        params.put("email", emailText);
                        params.put("password", passwordText);

                        return params;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        Log.d("TEST", "Headers size:" + response.headers.size());
                        authkey = response.headers.get("AuthorizationKey");
                        return super.parseNetworkResponse(response);
                    }

                    @Override
                    protected VolleyError parseNetworkError(VolleyError volleyError) {
                        if (volleyError.networkResponse != null && volleyError.networkResponse.headers != null) {
                            Log.d("TEST", "Headers size:" + volleyError.networkResponse.headers.size());
                            authkey = null;
                        }
                        return super.parseNetworkError(volleyError);
                    }
                };
                queue.add(postRequest);

            }
        });

        /*login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(gotoMainActivity);
                finish();
            }
        });*/

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(gotoSignUp);
            }
        });
    }

    public void verifyToken(String email, final String token) {
        GetProfile obj = new GetProfile(getApplicationContext(), token);
        obj.execute(url + "profile/me");
    }
}
