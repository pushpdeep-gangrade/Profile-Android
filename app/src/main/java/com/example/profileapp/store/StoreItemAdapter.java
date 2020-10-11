package com.example.profileapp.store;

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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StoreItemAdapter extends RecyclerView.Adapter<StoreItemViewHolder> {
    List<StoreItem> storeItemList = new ArrayList<>();
    Context context;
    NavController navController;

    public StoreItemAdapter(@NonNull Context context, int resource, @NonNull List<StoreItem> storeItems,
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
        itemPrice.setText("$" + String.valueOf(storeItem.price));
        //itemQuantity.setText("0");

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addToCart.getText().toString().equals("Add to Cart")){
                    storeItem.quantity = Integer.parseInt((itemQuantity.getText().toString()));
                    MainActivity.cartList.add(storeItem);
                    addToCart.setText("Remove from Cart");
                    Log.d("cart List", MainActivity.cartList.toString());
                }
                else{
                    MainActivity.cartList.remove(storeItem);
                    addToCart.setText("Add to Cart");
                    Log.d("cart List", MainActivity.cartList.toString());
                }
            }
        });

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

        //ImageView iv_coverPhoto_trips = convertView.findViewById(R.id.iv_coverPhoto_trips);
        //Log.d(TAG, "trip url: " + trip.url);
        //Picasso.get().load(trip.url).into(iv_coverPhoto_trips);

    }

    @Override
    public int getItemCount() {
        return storeItemList.size();
    }
}
