package com.example.profileapp.transactionhistory;

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
import com.example.profileapp.models.Transaction;
import com.example.profileapp.orderhistory.OrderAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TransactionHistoryFragment extends Fragment {
    List<Transaction> transactionList = new ArrayList<>();
    private String mAuthorizationkey;
    private static final String AUTH_KEY = "authorizationkey";
    String getTransactionUrl = MainActivity.url + "transaction/history";
    TextView transactionId, amount, cardNumber;
    RecyclerView transactionRecyclerView;
    private NavController navController;

    View view;

    public TransactionHistoryFragment() {
        // Required empty public constructor
    }

    public static TransactionHistoryFragment newInstance(String param1, String param2) {
        TransactionHistoryFragment fragment = new TransactionHistoryFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAuthorizationkey = getArguments().getString(AUTH_KEY);
            if(mAuthorizationkey != null){
                Log.d("cart auth", mAuthorizationkey);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        transactionRecyclerView = view.findViewById(R.id.transactionH_recyclerView);

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        getTransactions();
        return view;
    }

    public void setTransactionHistoryRecyclerView(){
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        transactionRecyclerView.setLayoutManager(layoutManager);


        final TransactionHistoryAdapter ad = new TransactionHistoryAdapter(getContext(),
                android.R.layout.simple_list_item_1, transactionList, navController, mAuthorizationkey);

        transactionRecyclerView.setAdapter(ad);

    }

    public void getTransactions(){

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest getRequest = new StringRequest(Request.Method.GET, getTransactionUrl,
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
                            try {
                                JSONObject root = new JSONObject(response);
                                JSONArray transactions = root.getJSONArray("transactions");

                                Transaction transaction = new Transaction();

                                for(int i = 0; i < transactions.length(); i++){
                                    JSONObject transactionObj = transactions.getJSONObject(i);
                                    transaction.transactionId = transactionObj.getString("tId");
                                    transaction.amount = transactionObj.getDouble("tAmount");
                                    transaction.cardType = transactionObj.getString("tCardType");
                                    transaction.maskedNumber = transactionObj.getString("tMaskedNumber");
                                    transaction.status = transactionObj.getString("tStatus");
                                    transaction.type = transactionObj.getString("tType");

                                    transactionList.add(transaction);
                                }

                                setTransactionHistoryRecyclerView();

                                Log.d("Transactions", response.toString());
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
}