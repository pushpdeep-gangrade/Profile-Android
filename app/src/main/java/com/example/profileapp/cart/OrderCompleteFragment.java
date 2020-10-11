package com.example.profileapp.cart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.profileapp.MainActivity;
import com.example.profileapp.R;
import com.example.profileapp.models.Order;
import com.example.profileapp.models.StoreItem;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;


public class OrderCompleteFragment extends Fragment {
    View view;
    TextView orderNumber, orderDate, orderCompleteText, orderNumberText, orderDateText;
    Button returnButton;
    private NavController navController;
    Order order = new Order();

    public OrderCompleteFragment() {
        // Required empty public constructor
    }


    public static OrderCompleteFragment newInstance(String param1, String param2) {
        OrderCompleteFragment fragment = new OrderCompleteFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_order_complete, container, false);

        orderNumber = view.findViewById(R.id.orderComplete_orderNumber);
        orderCompleteText = view.findViewById(R.id.orderComplete_orderCompleteText);
        orderNumberText = view.findViewById(R.id.orderComplete_orderNumberText);
        orderDateText = view.findViewById(R.id.orderComplete_orderDateText);
        orderDate = view.findViewById(R.id.orderComplete_orderDate);
        returnButton = view.findViewById(R.id.orderComplete_returnButton);

        if(MainActivity.cartList.size() > 0){
            UUID orderUUID = UUID.randomUUID();

            order.orderId = String.valueOf(orderUUID);

            order.items = new ArrayList<>();

            for(int i = 0; i < MainActivity.cartList.size(); i++){
                order.items.add(MainActivity.cartList.get(i));
            }

            Date dt = new Date(System.currentTimeMillis());

            order.orderDate = dt;


            orderNumber.setText(order.orderId);
            orderDate.setText(String.valueOf(order.orderDate));

            MainActivity.orderList.add(order);
            MainActivity.cartList.clear();

        }
        else{
            orderCompleteText.setText("Cart is currently empty");
            orderNumber.setVisibility(View.GONE);
            orderDate.setVisibility(View.GONE);
            orderDateText.setVisibility(View.GONE);
            orderNumberText.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Checkout Failed", Toast.LENGTH_SHORT).show();
        }


        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_nav_order_complete_to_nav_store);
            }
        });

        return view;
    }
}