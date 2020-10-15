package com.example.profileapp.transactionhistory;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profileapp.R;
import com.example.profileapp.models.Order;
import com.example.profileapp.models.Transaction;
import com.example.profileapp.store.StoreItemViewHolder;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<StoreItemViewHolder> {
    List<Transaction> transactionArrayList = new ArrayList<>();
    Context context;
    NavController navController;
    String authToken;

    public TransactionHistoryAdapter(@NonNull Context context, int resource, @NonNull List<Transaction> orderItemList,
                        NavController navController, String authToken) {

        this.transactionArrayList = orderItemList;
        this.context = context;
        this.navController = navController;
        this.authToken = authToken;


    }

    @NotNull
    @Override
    public StoreItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("In On Create View", "In On Create View");
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);

        StoreItemViewHolder vh = new StoreItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull StoreItemViewHolder holder, int position) {
        final Transaction transaction = transactionArrayList.get(position);

        TextView id = holder.itemView.findViewById(R.id.transactionItem_id);
        TextView card = holder.itemView.findViewById(R.id.transactionItem_card);
        TextView amount = holder.itemView.findViewById(R.id.transactionItem_amount);
        CardView cv = holder.itemView.findViewById(R.id.transactionItem_cardView);

        assert transaction != null;
        id.setText(transaction.transactionId);
        card.setText(transaction.maskedNumber);
        amount.setText(String.valueOf(transaction.amount));

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("transaction", (Serializable) transaction);
                bundle.putString("authorizationkey", authToken);
                navController.navigate(R.id.action_nav_transaction_history_to_nav_transaction_details, bundle);
            }
        });


    }

    @Override
    public int getItemCount() {
        return transactionArrayList.size();
    }

}