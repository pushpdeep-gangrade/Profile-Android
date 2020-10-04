package com.example.profileapp.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    EditText firstName, lastName, email, address, password, reEnterPassword, age;
    Button signUp, cancel;
    String url = "http://104.248.113.55:8080/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstName = findViewById(R.id.signup_firstName);
        lastName = findViewById(R.id.signup_lastName);
        email = findViewById(R.id.signup_email);
        address = findViewById(R.id.signup_address);
        password = findViewById(R.id.signup_password);
        reEnterPassword = findViewById(R.id.signup_reEnterPassword);
        age = findViewById(R.id.signup_age);
        signUp = findViewById(R.id.signup_signupButton);
        cancel = findViewById(R.id.signup_cancelButton);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String signupUrl = url + "signup";

                RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);

                StringRequest postRequest = new StringRequest(Request.Method.POST, signupUrl,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                                if(response.equals("Email id already registered")){
                                    Toast.makeText(SignUpActivity.this, response, Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(SignUpActivity.this, response, Toast.LENGTH_LONG).show();
                                    finish();
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
                        String firstNameText = firstName.getText().toString();
                        String lastNameText = lastName.getText().toString();
                        String emailText = email.getText().toString();
                        String addressText = address.getText().toString();
                        String ageText = age.getText().toString();
                        String passwordText = password.getText().toString();
                        String reEnterPasswordText = reEnterPassword.getText().toString();

                        Map<String, String>  params = new HashMap<String, String>();

                        boolean allValid = true;

                        if (firstNameText.equals("")) {
                            firstName.setError("Please enter your first name");
                            allValid = false;
                        }

                        if (lastNameText.equals("")) {
                            lastName.setError("Please enter your last name");
                            allValid = false;
                        }

                        if (emailText.equals("")) {
                            email.setError("Please enter your email");
                            allValid = false;
                        }

                        if (addressText.equals("")) {
                            address.setError("Please enter your city");
                            allValid = false;
                        }

                        if (ageText.equals("")) {
                            age.setError("Please enter your age");
                            allValid = false;
                        }

                        if (!passwordText.equals(reEnterPasswordText) || passwordText.equals("")) {
                            if (!passwordText.equals(reEnterPasswordText)) {
                                password.setError("Passwords do not match");
                            } else {
                                password.setError("Please enter a password");
                            }
                            allValid = false;
                        }

                        if(allValid){
                            params.put("firstname", firstNameText);
                            params.put("lastname", lastNameText);
                            params.put("email", emailText);
                            params.put("address", addressText);
                            params.put("age", ageText);
                            params.put("password", passwordText);
                        }

                        return params;
                    }
                };

                queue.add(postRequest);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}