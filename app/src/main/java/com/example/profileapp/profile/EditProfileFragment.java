package com.example.profileapp.profile;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.profileapp.models.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.example.profileapp.MainActivity.url;

public class EditProfileFragment extends Fragment {
    View view;
    private NavController navController;
    private final User user = new User();
    private Boolean allValid = false;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String AUTH_KEY = "authorizationkey";

    // TODO: Rename and change types of parameters
    private String mAuthorizationkey;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAuthorizationkey = getArguments().getString(AUTH_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        Log.d("mAuthorizationkey", mAuthorizationkey);

        SharedPreferences prefs = this.getActivity().getSharedPreferences("info", MODE_PRIVATE);

        Gson gson = new Gson();

        Button cancel = view.findViewById(R.id.cancel_edit_profile_button);
        Button save = view.findViewById(R.id.save_edit_profile_button);


        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();


        final EditText first = view.findViewById(R.id.editProfile_firstName);
        final EditText last = view.findViewById(R.id.editProfile_lastName);
        final EditText address = view.findViewById(R.id.editProfile_address);
        final EditText age = view.findViewById(R.id.editProfile_age);
        final EditText password = view.findViewById(R.id.editProfile_password);

        User currentUser = gson.fromJson(prefs.getString("user", null), User.class);
        String email;
        email = prefs.getString("email","");




        String profileUrl = MainActivity.url + "profile/" + email;

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest getRequest = new StringRequest(Request.Method.GET, profileUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        if(response.equals("UNAUTHORIZED")){
                            Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                        }
                        else{
                            try {
                                JSONArray arr = new JSONArray(response);
                                JSONObject userObject = new JSONObject(String.valueOf(arr.get(0)));

                                user.firstName = userObject.getString("fname");
                                user.lastName = userObject.getString("lname");
                                user.email = userObject.getString("emailId");
                                user.address = userObject.getString("address");
                                user.age = userObject.getInt("age");

                                first.setText(user.firstName);
                                last.setText(user.lastName);
                                address.setText(user.address);
                                age.setText(String.valueOf(user.age));

                                Log.d("User", arr.get(0).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                          //  Toast.makeText(getContext(), "Loaded User Profile", Toast.LENGTH_LONG).show();
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


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                allValid = true;

                if (first.getText() == null || first.getText().toString().equals("")) {
                    allValid = false;
                    first.setError("Enter your first name");
                }

                if (last.getText() == null || last.getText().toString().equals("")) {
                    allValid = false;
                    last.setError("Enter your last name");
                }

                if (address.getText() == null || address.getText().toString().equals("")) {
                    allValid = false;
                    address.setError("Enter your address");
                }

                if (age.getText() == null || age.getText().toString().equals("")) {
                    allValid = false;
                    age.setError("Enter your age");
                }

                if (password.getText() == null || password.getText().toString().equals("") || password.getText().toString().length() < 5) {
                    allValid = false;
                    password.setError("Enter your password to make changes");
                }

                if (allValid) {
                    String updateProfileUrl = url + "profile/" + user.email;

                    RequestQueue queue = Volley.newRequestQueue(getContext());

                    StringRequest postRequest = new StringRequest(Request.Method.POST, updateProfileUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // response
                                    Log.d("Response", response);

                                    if (response.equals("Email id not found in Record")) {
                                        Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();

                                        String firstNameText = first.getText().toString();
                                        String lastNameText = last.getText().toString();
                                        String emailText = user.email;
                                        String addressText = address.getText().toString();
                                        String ageText = age.getText().toString();


                                        User user = new User();

                                        user.firstName = firstNameText;
                                        user.lastName = lastNameText;
                                        user.email = emailText;
                                        user.address = addressText;
                                        user.age = Integer.parseInt(ageText);

                                        Gson gsonObject = new Gson();

                                        SharedPreferences prefs = getActivity().getSharedPreferences("info", MODE_PRIVATE);
                                        prefs.edit().putString("user", gsonObject.toJson(user)).commit();


                                        Bundle bundle = new Bundle();
                                        bundle.putString(AUTH_KEY, mAuthorizationkey);
                                        navController.navigate(R.id.action_nav_edit_profile_to_nav_view_profile, bundle);
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
                            String firstNameText = first.getText().toString();
                            String lastNameText = last.getText().toString();
                            String emailText = user.email;
                            String addressText = address.getText().toString();
                            String ageText = age.getText().toString();
                            String passwordText = password.getText().toString();

                            Map<String, String> params = new HashMap<>();

                            params.put("firstname", firstNameText);
                            params.put("lastname", lastNameText);
                            params.put("email", emailText);
                            params.put("address", addressText);
                            params.put("age", ageText);
                            params.put("password", passwordText);


                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<>();
                            params.put("authorizationkey", mAuthorizationkey);

                            return params;
                        }
                    };

                    queue.add(postRequest);
                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(AUTH_KEY, mAuthorizationkey);
                navController.navigate(R.id.action_nav_edit_profile_to_nav_view_profile, bundle);
            }
        });

        return view;
    }
}