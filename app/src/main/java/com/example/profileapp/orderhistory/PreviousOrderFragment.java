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
import android.widget.TextView;

import com.example.profileapp.R;
import com.example.profileapp.cart.CartItemAdapter;
import com.example.profileapp.models.Order;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class PreviousOrderFragment extends Fragment {
    View view;
    RecyclerView previousItemRecyclerView;
    TextView orderDate, total;
    private NavController navController;
    Order order;
    private static final String AUTH_KEY = "authorizationkey";

    public PreviousOrderFragment() {
        // Required empty public constructor
    }


    public static PreviousOrderFragment newInstance(String param1, String param2) {
        PreviousOrderFragment fragment = new PreviousOrderFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable("order");
            Log.d("Order", order.toString());

            String mAuthorizationkey = getArguments().getString(AUTH_KEY);
            if(mAuthorizationkey != null){
                Log.d("previous auth", mAuthorizationkey);

            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_previous_order, container, false);

        previousItemRecyclerView = view.findViewById(R.id.previous_itemList);

        orderDate = view.findViewById(R.id.previous_orderDate);
        total = view.findViewById(R.id.previous_total);

        orderDate.setText(String.valueOf(order.orderDate));

        double calcTotal = 0;

        for(int i = 0; i < order.items.size(); i++){
            double price = order.items.get(i).price;
            double discount = order.items.get(i).discount;

            double priceAfterDiscount = (price - (price*discount))*order.items.get(i).quantity;

            calcTotal+=priceAfterDiscount;
        }

        NumberFormat formatter = new DecimalFormat("#0.00");
        String formatted = formatter.format(calcTotal);

        total.setText(String.format("$%s", formatted));


        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        setPreviousRecyclerView();

        return view;
    }

    public void setPreviousRecyclerView(){
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        previousItemRecyclerView.setLayoutManager(layoutManager);

        final PreviousOrderAdapter ad = new PreviousOrderAdapter(getContext(),
                android.R.layout.simple_list_item_1, order.items, navController);

        previousItemRecyclerView.setAdapter(ad);


    }
}