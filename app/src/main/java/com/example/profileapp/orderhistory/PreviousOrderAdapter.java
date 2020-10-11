package com.example.profileapp.orderhistory;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profileapp.MainActivity;
import com.example.profileapp.R;
import com.example.profileapp.models.StoreItem;
import com.example.profileapp.store.StoreItemAdapter;
import com.example.profileapp.store.StoreItemViewHolder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class PreviousOrderAdapter extends RecyclerView.Adapter<StoreItemViewHolder> {
    List<StoreItem> storeItemList = new ArrayList<>();
    Context context;
    NavController navController;

    public PreviousOrderAdapter(@NonNull Context context, int resource, @NonNull List<StoreItem> storeItems,
                           NavController navController) {

        this.storeItemList = storeItems;
        this.context = context;
        this.navController = navController;

        Log.d("In Constructor", "In Constructor");

    }

    @NotNull
    @Override
    public StoreItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("In On Create View", "In On Create View");
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_item, parent, false);

        StoreItemViewHolder vh = new StoreItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull StoreItemViewHolder holder, int position) {
        final StoreItem storeItem = storeItemList.get(position);

        ImageView itemImage = holder.itemView.findViewById(R.id.storeItem_itemImage);
        TextView itemName = holder.itemView.findViewById(R.id.storeItem_itemName);
        TextView itemPrice = holder.itemView.findViewById(R.id.storeItem_itemPrice);
        final EditText itemQuantity = holder.itemView.findViewById(R.id.storeItem_itemQuantity);
        final Button addToCart = holder.itemView.findViewById(R.id.storeItem_addCartButton);

        assert storeItem != null;
        itemName.setText(storeItem.name);

        NumberFormat formatter = new DecimalFormat("#0.00");
        String formatted = formatter.format(storeItem.price);
        itemPrice.setText(String.format("$%s", formatted));

        itemQuantity.setText(String.valueOf(storeItem.quantity));
        itemQuantity.setEnabled(false);
        addToCart.setVisibility(View.GONE);

        try
        {
            // get input stream
            InputStream ims = context.getAssets().open("itemImages/" + storeItem.photo);
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            itemImage.setImageDrawable(d);
            ims.close();
        }
        catch(IOException ex)
        {
            return;
        }


    }

    @Override
    public int getItemCount() {
        return storeItemList.size();
    }
}
