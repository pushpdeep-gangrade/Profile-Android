package com.example.profileapp.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.profileapp.MainActivity;
import com.example.profileapp.R;
import com.example.profileapp.login.LoginActivity;
import com.example.profileapp.models.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ViewProfileFragment extends Fragment {
    View view;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private User user = new User();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String AUTH_KEY = "authorizationkey";

    // TODO: Rename and change types of parameters
    private String mAuthorizationkey;

    public ViewProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ViewProfileFragment newInstance(String authorizationkey) {
        ViewProfileFragment fragment = new ViewProfileFragment();
        Bundle args = new Bundle();
        args.putString(AUTH_KEY, authorizationkey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAuthorizationkey = getArguments().getString(AUTH_KEY);
            Log.d("ViewProfileFragment", mAuthorizationkey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_profile, container, false);

        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        view.findViewById(R.id.viewProfile_editProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(AUTH_KEY, mAuthorizationkey);
                navController.navigate(R.id.action_nav_view_profile_to_nav_edit_profile, bundle);
            }
        });

        SharedPreferences prefs = this.getActivity().getSharedPreferences("info", MODE_PRIVATE);

        Gson gson = new Gson();
        User user = gson.fromJson(prefs.getString("user", null), User.class);

        TextView first = view.findViewById(R.id.viewProfile_firstNameText);
        TextView last = view.findViewById(R.id.viewProfile_lastNameText);
        TextView email = view.findViewById(R.id.viewProfile_emailText);
        TextView address = view.findViewById(R.id.viewProfile_addressText);
        TextView age = view.findViewById(R.id.viewProfile_ageText);

        first.setText(user.firstName);
        last.setText(user.lastName);
        email.setText(user.email);
        address.setText(user.address);
        age.setText(String.valueOf(user.age));

        /*
        SharedPreferences prefs = this.getActivity().getSharedPreferences("info", MODE_PRIVATE);

        Gson gson = new Gson();
        User currentUser = gson.fromJson(prefs.getString("user", null), User.class);

        String profileUrl = MainActivity.url + "profile/" + currentUser.email;

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
                            Gson gsonObject = new Gson();

                            try {
                                JSONArray arr = new JSONArray(response);
                                JSONObject userObject = new JSONObject(String.valueOf(arr.get(0)));

                                user.firstName = userObject.getString("fname");
                                user.lastName = userObject.getString("lname");
                                user.email = userObject.getString("emailId");
                                user.address = userObject.getString("address");
                                user.age = userObject.getInt("age");

                                TextView first = view.findViewById(R.id.viewProfile_firstNameText);
                                TextView last = view.findViewById(R.id.viewProfile_lastNameText);
                                TextView email = view.findViewById(R.id.viewProfile_emailText);
                                TextView address = view.findViewById(R.id.viewProfile_addressText);
                                TextView age = view.findViewById(R.id.viewProfile_ageText);

                                first.setText(user.firstName);
                                last.setText(user.lastName);
                                email.setText(user.email);
                                address.setText(user.address);
                                age.setText(String.valueOf(user.age));

                                Log.d("User", arr.get(0).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(getContext(), "Loaded User Profile", Toast.LENGTH_LONG).show();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("authorizationkey", mAuthorizationkey);

                return params;
            }
        };

        queue.add(getRequest);
        */

        return view;
    }
}