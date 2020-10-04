package com.example.profileapp.profile;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.profileapp.R;
import com.example.profileapp.models.User;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class EditProfileFragment extends Fragment {
    View view;
    private NavHostFragment navHostFragment;
    private NavController navController;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String AUTH_KEY = "authorizationkey";

    // TODO: Rename and change types of parameters
    private String mAuthorizationkey;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String authorizationkey) {
        EditProfileFragment fragment = new EditProfileFragment();
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
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        SharedPreferences prefs = this.getActivity().getSharedPreferences("info", MODE_PRIVATE);

        Gson gson = new Gson();
        User user = gson.fromJson(prefs.getString("user", null), User.class);

        EditText first = view.findViewById(R.id.editProfile_firstName);
        EditText last = view.findViewById(R.id.editProfile_lastName);
        EditText email = view.findViewById(R.id.editProfile_email);
        EditText address = view.findViewById(R.id.editProfile_address);
        EditText age = view.findViewById(R.id.editProfile_age);
        Button cancel = view.findViewById(R.id.cancel_edit_profile_button);
        Button save = view.findViewById(R.id.save_edit_profile_button);

        first.setText(user.firstName);
        last.setText(user.lastName);
        email.setText(user.email);
        address.setText(user.address);
        age.setText(String.valueOf(user.age));

        navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_nav_edit_profile_to_nav_view_profile);
            }
        });

        return view;
    }
}