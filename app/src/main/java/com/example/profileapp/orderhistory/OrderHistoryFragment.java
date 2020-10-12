package com.example.profileapp.orderhistory;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.profileapp.MainActivity;
import com.example.profileapp.R;
import com.example.profileapp.models.Order;

import java.util.ArrayList;
import java.util.List;


public class OrderHistoryFragment extends Fragment {
    View view;
    RecyclerView orderRecyclerView;
    private NavController navController;
    List<Order> orderList = MainActivity.orderList;
    private String mAuthorizationkey;
    private static final String AUTH_KEY = "authorizationkey";

    public OrderHistoryFragment() {
        // Required empty public constructor
    }


    public static OrderHistoryFragment newInstance(String param1, String param2) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAuthorizationkey = getArguments().getString(AUTH_KEY);
            Log.d("history auth", mAuthorizationkey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_order_history, container, false);

        orderRecyclerView = view.findViewById(R.id.orderHistory_orderList);

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        setCartRecyclerView();

        return view;
    }

    public void setCartRecyclerView(){
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        orderRecyclerView.setLayoutManager(layoutManager);

        final OrderAdapter ad = new OrderAdapter(getContext(),
                android.R.layout.simple_list_item_1, orderList, navController, mAuthorizationkey);

        orderRecyclerView.setAdapter(ad);

    }
}