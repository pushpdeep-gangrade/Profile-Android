package com.example.profileapp.cart;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.profileapp.MainActivity;
import com.example.profileapp.R;
import com.example.profileapp.models.StoreItem;
import com.example.profileapp.store.StoreItemAdapter;
import com.example.profileapp.store.StoreItemViewHolder;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartItemAdapter extends RecyclerView.Adapter<StoreItemViewHolder> {
    List<StoreItem> storeItemList = new ArrayList<>();
    Context context;
    NavController navController;
    RecyclerView rv;
    String mAuthorizationkey;
    String postCartUrl = MainActivity.url + "cart";

    public CartItemAdapter(@NonNull Context context, int resource, @NonNull List<StoreItem> storeItems,
                            NavController navController, RecyclerView rv, String mAuthorizationkey) {

        this.storeItemList = storeItems;
        this.context = context;
        this.navController = navController;
        this.rv = rv;
        this.mAuthorizationkey = mAuthorizationkey;

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
        itemPrice.setText(String.valueOf("$" + storeItem.price));
        itemQuantity.setText(String.valueOf(storeItem.quantity));
        itemQuantity.setEnabled(false);
        addToCart.setText("Remove from Cart");

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQuantity = storeItem.quantity - (storeItem.quantity * 2);
                storeItem.quantity = newQuantity;
                storeItemList.remove(storeItem);
                removeItemFromCart(storeItem);
                Log.d("cart List", storeItemList.toString());

                final CartItemAdapter ad = new CartItemAdapter(context,
                        android.R.layout.simple_list_item_1, storeItemList, navController, rv, mAuthorizationkey);

                rv.setAdapter(ad);

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


    }

    @Override
    public int getItemCount() {
        return storeItemList.size();
    }

    public void removeItemFromCart(final StoreItem item){
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest getRequest = new StringRequest(Request.Method.POST, postCartUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        if(response.equals("UNAUTHORIZED")){
                            Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                        }
                        else{
                            Gson gsonObject = new Gson();

                            try {
                                //JSONObject root = new JSONObject(response);
                                //JSONArray itemsArray = root.getJSONArray("items");

                                Log.d("Remove Item Response", response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //   Toast.makeText(getContext(), "Loaded User Profile", Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<>();


                params.put("name", item.name);
                params.put("discount", String.valueOf(item.discount));
                params.put("photo", item.photo);
                params.put("price", String.valueOf(item.price));
                params.put("quantity", String.valueOf(item.quantity));
                params.put("region", item.region);


                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("authorizationkey", mAuthorizationkey);

                return params;
            }
        };

        queue.add(getRequest);
    }

}
