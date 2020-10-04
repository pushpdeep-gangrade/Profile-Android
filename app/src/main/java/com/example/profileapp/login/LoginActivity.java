package com.example.profileapp.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.profileapp.profile.ViewProfileFragment;
import com.example.profileapp.signup.SignUpActivity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

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
    TextView createAccount;
    String url = "http://104.248.113.55:8080/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        login = findViewById(R.id.login_loginButton);
        createAccount = findViewById(R.id.login_createAccountLink);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new GetPlacesAsync(LoginActivity.this, db, AddTripActivity.this).execute(url);
                String loginUrl = url + "login";

                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

                StringRequest postRequest = new StringRequest(Request.Method.POST, loginUrl,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                                if(response.equals("Invalid Credentials")){
                                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                                }
                                else{
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

                                        SharedPreferences prefs = getSharedPreferences("info", MODE_PRIVATE);
                                        prefs.edit().putString("user", gsonObject.toJson(user)).commit();

                                        Log.d("User", arr.get(0).toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //char[] userArrayConvert = response.toCharArray();

                                    //User user = userArrayConvert[0];
                                    //user.firstName = "";




                                    //SharedPreferences prefs = getSharedPreferences("info", MODE_PRIVATE);
                                    //prefs.edit().putString("user", gsonObject.toJson(user)).commit();

                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                    Intent gotoMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                    Bundle authBundle = new Bundle();
                                    Log.d("AuthKey", authkey);
                                    authBundle.putString(AUTH_KEY, authkey); //Your id
                                    gotoMainActivity.putExtras(authBundle); //Put your id to your next Intent
                                    startActivity(gotoMainActivity);
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
                    protected Map<String, String> getParams()
                    {
                        String emailText = email.getText().toString();
                        String passwordText = password.getText().toString();

                        //emailText = "pushpdeepg@gmail.com";
                        //passwordText = "pushpdeep";

                        Map<String, String>  params = new HashMap<String, String>();

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

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(gotoSignUp);
            }
        });

    }
}