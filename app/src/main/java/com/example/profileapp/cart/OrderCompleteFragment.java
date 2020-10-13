package com.example.profileapp.cart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.profileapp.MainActivity;
import com.example.profileapp.R;
import com.example.profileapp.models.Order;
import com.example.profileapp.models.StoreItem;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class OrderCompleteFragment extends Fragment {
    View view;
    TextView orderNumber, orderDate, orderCompleteText, orderNumberText, orderDateText;
    Button returnButton;
    private NavController navController;
    Order order = new Order();
    private String mAuthorizationkey;
    private static final String AUTH_KEY = "authorizationkey";
    List<StoreItem> cartList = new ArrayList<>();
    String getCartUrl = MainActivity.url + "cart";
    String orderCompleteUrl = MainActivity.url + "order/complete";


    public static OrderCompleteFragment newInstance(String param1, String param2) {
        OrderCompleteFragment fragment = new OrderCompleteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAuthorizationkey = getArguments().getString(AUTH_KEY);
            Log.d("complete auth", mAuthorizationkey);
        }

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

        loadCart();

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle authBundle = new Bundle();
                authBundle.putString(AUTH_KEY, mAuthorizationkey);
                navController.navigate(R.id.action_nav_order_complete_to_nav_store, authBundle);
            }
        });

        return view;
    }

    public void completeOrder(){
        if(cartList.size() > 0){
            /*UUID orderUUID = UUID.randomUUID();

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
            MainActivity.cartList.clear();*/

            Date dt = new Date(System.currentTimeMillis());

            orderNumberText.setVisibility(View.GONE);
            orderNumber.setVisibility(View.GONE);

            order.orderDate = String.valueOf(dt);
            orderNumber.setText(order.orderId);
            orderDate.setText(String.valueOf(order.orderDate));

            addToOrderHistory();

        }
        else{
            orderCompleteText.setText("Cart is currently empty");
            orderNumber.setVisibility(View.GONE);
            orderDate.setVisibility(View.GONE);
            orderDateText.setVisibility(View.GONE);
            orderNumberText.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Checkout Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void addToOrderHistory(){
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest getRequest = new StringRequest(Request.Method.POST, orderCompleteUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        if(response.equals("UNAUTHORIZED")){
                            Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
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
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("authorizationkey", mAuthorizationkey);

                return params;
            }
        };

        queue.add(getRequest);
    }

    public void loadCart(){
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest getRequest = new StringRequest(Request.Method.GET, getCartUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        if(response.equals("UNAUTHORIZED")){
                            Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                        }
                        else{
                            Gson gsonObject = new Gson();

                            try {
                                JSONObject root = new JSONObject(response);
                                JSONArray itemsArray = root.getJSONArray("items");

                                for(int i = 0; i < itemsArray.length(); i++){
                                    JSONObject itemObj = itemsArray.getJSONObject(i);

                                    StoreItem nItem = new StoreItem();

                                    nItem.name = itemObj.getString("name");
                                    nItem.quantity = itemObj.getInt("quantity");
                                    nItem.photo = itemObj.getString("photo");
                                    nItem.region = itemObj.getString("region");
                                    nItem.discount = itemObj.getDouble("discount");
                                    nItem.price = itemObj.getDouble("price");

                                    cartList.add(nItem);

                                }

                                completeOrder();

                                Log.d("Cart", response);
                            } catch (JSONException e) {
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
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("authorizationkey", mAuthorizationkey);

                return params;
            }
        };

        queue.add(getRequest);
    }
}