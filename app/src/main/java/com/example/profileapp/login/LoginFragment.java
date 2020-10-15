package com.example.profileapp.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.profileapp.R;

public class LoginFragment extends Fragment {

    private NavController navController;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        settings.edit().clear().commit();

        Intent goBackToLogin = new Intent(getActivity(), LoginActivity.class);
        getActivity().startActivity(goBackToLogin);
        getActivity().finish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;

        /*View view = inflater.inflate(R.layout.activity_login, container, false);
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        //TEMP CODE!!!!!!!!!!!!!!!!!!!
        view.findViewById(R.id.login_loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_nav_logout_to_nav_view_profile2);
            }
        });

        view.findViewById(R.id.login_createAccountLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_nav_login_to_nav_signup);
            }
        });*/

        return view;
    }
}