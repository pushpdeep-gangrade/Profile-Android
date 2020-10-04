package com.example.profileapp.profile;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.profileapp.R;
import com.example.profileapp.models.User;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class ViewProfileFragment extends Fragment {
    View view;
    private NavHostFragment navHostFragment;
    private NavController navController;

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
                navController.navigate(R.id.action_nav_view_profile_to_nav_edit_profile);
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

        return view;
    }
}