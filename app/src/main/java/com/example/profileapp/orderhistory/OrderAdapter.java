package com.example.profileapp.orderhistory;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profileapp.R;
import com.example.profileapp.models.Order;
import com.example.profileapp.models.StoreItem;
import com.example.profileapp.store.StoreItemViewHolder;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<StoreItemViewHolder> {
    List<Order> orderItemList = new ArrayList<>();
    Context context;
    NavController navController;
    String authToken;

    public OrderAdapter(@NonNull Context context, int resource, @NonNull List<Order> orderItemList,
        NavController navController, String authToken) {

        this.orderItemList = orderItemList;
        this.context = context;
        this.navController = navController;
        this.authToken = authToken;


    }

    @NotNull
    @Override
    public StoreItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d("In On Create View", "In On Create View");
            View v =  LayoutInflater.from(parent.getContext())
            .inflate(R.layout.order_item, parent, false);

            StoreItemViewHolder vh = new StoreItemViewHolder(v);
            return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull StoreItemViewHolder holder, int position) {
            final Order order = orderItemList.get(position);

            TextView orderNumber = holder.itemView.findViewById(R.id.orderItem_orderNumber);
            TextView orderDate = holder.itemView.findViewById(R.id.orderItem_orderDate);
            CardView cv = holder.itemView.findViewById(R.id.orderItem_cardView);

            assert order != null;
            orderNumber.setText(order.orderId);
            orderDate.setText(String.valueOf(order.orderDate));

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order", (Serializable) order);
                    bundle.putString("authorizationkey", authToken);
                    navController.navigate(R.id.action_nav_order_history_to_nav_previous_order, bundle);
                }
            });


    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

}