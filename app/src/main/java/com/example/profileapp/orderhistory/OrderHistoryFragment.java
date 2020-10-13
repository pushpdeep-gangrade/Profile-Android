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
import android.widget.EditText;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrderHistoryFragment extends Fragment {
    View view;
    RecyclerView orderRecyclerView;
    private NavController navController;
    List<Order> orderList = new ArrayList<>();
    private String mAuthorizationkey;
    private static final String AUTH_KEY = "authorizationkey";
    String getOrderHistoryUrl = MainActivity.url + "order/history";

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


        getOrderHistory();

        return view;
    }

    public void setOrderHistoryRecyclerView(){
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        orderRecyclerView.setLayoutManager(layoutManager);

        final OrderAdapter ad = new OrderAdapter(getContext(),
                android.R.layout.simple_list_item_1, orderList, navController, mAuthorizationkey);

        orderRecyclerView.setAdapter(ad);

    }

    public void getOrderHistory(){
        orderList.clear();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest getRequest = new StringRequest(Request.Method.GET, getOrderHistoryUrl,
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
                                JSONArray orders = root.getJSONArray("orders");

                                for(int i = 0; i < orders.length(); i++){
                                    JSONObject orderObj = orders.getJSONObject(i);

                                    Order nOrder = new Order();

                                    nOrder.orderId = String.valueOf(i+1);
                                    nOrder.orderDate = orderObj.getString("date");
                                    nOrder.items = new ArrayList<>();

                                    JSONArray itemsArr = orderObj.getJSONArray("items");

                                    for(int j = 0; j < itemsArr.length(); j++){
                                        JSONObject itemObj = itemsArr.getJSONObject(j);

                                        StoreItem nItem = new StoreItem();

                                        nItem.name = itemObj.getString("name");
                                        nItem.quantity = itemObj.getInt("quantity");
                                        nItem.photo = itemObj.getString("photo");
                                        nItem.region = itemObj.getString("region");
                                        nItem.discount = itemObj.getDouble("discount");
                                        nItem.price = itemObj.getDouble("price");

                                        nOrder.items.add(nItem);
                                    }

                                    Log.d("Order", nOrder.toString());

                                    orderList.add(nOrder);

                                }

                                setOrderHistoryRecyclerView();

                                Log.d("Order History", response);

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