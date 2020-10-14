package com.example.profileapp.cart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.cardinalcommerce.shared.userinterfaces.ProgressDialog;
import com.example.profileapp.MainActivity;
import com.example.profileapp.R;
import com.example.profileapp.models.StoreItem;
import com.example.profileapp.models.User;
import com.example.profileapp.store.StoreItemAdapter;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


@SuppressWarnings("ALL")
public class CartFragment extends Fragment {
    List<StoreItem> cartList = new ArrayList<>();
    RecyclerView cartItemRecyclerView;
    View view;
    Button pay;
    Button checkout, clearCart;
    TextView textviewPayment, currentTotalText;
    private NavController navController;
    private String mAuthorizationkey;
    ProgressBar mProgressBar;
    private static final String AUTH_KEY = "authorizationkey";
    String getCartUrl = MainActivity.url + "cart";
    User user = new User();
    double currentTotal = 0;
    String formatted;


    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<StoreItem> storeItemArrayList = (ArrayList<StoreItem>) getArguments().getSerializable("cartList");
            Log.d("Store Items cart", storeItemArrayList.toString());

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
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        cartItemRecyclerView = view.findViewById(R.id.cart_itemList);
        pay = view.findViewById(R.id.button_pay);
        checkout = view.findViewById(R.id.button_checkout);
        textviewPayment = view.findViewById(R.id.textview_payment);
        currentTotalText = view.findViewById(R.id.cart_currentTotal);
        clearCart = view.findViewById(R.id.cart_clearCart);

        textviewPayment.setVisibility(View.INVISIBLE);
        pay.setVisibility(View.INVISIBLE);



        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        getUser();
        loadCart();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cartList.size() > 0){
                    getclientToken();
                }
                else{
                    Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_LONG).show();
                }
            }
        });
        
        clearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCart();
            }
        });

    }

    public void setCartRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        cartItemRecyclerView.setLayoutManager(layoutManager);

        final CartItemAdapter ad = new CartItemAdapter(getContext(),
                android.R.layout.simple_list_item_1, cartList, navController, cartItemRecyclerView,
                mAuthorizationkey, currentTotalText);

        cartItemRecyclerView.setAdapter(ad);
    }

    public void loadCart(){
        cartList.clear();

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

                                getCurrentTotal();
                                Log.d("Total", formatted);

                                setCartRecyclerView();


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

    public void clearCart(){
        cartList.clear();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest getRequest = new StringRequest(Request.Method.DELETE, getCartUrl,
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
                                Log.d("Clear Cart", response);

                                Toast.makeText(getContext(), "Cart Cleared", Toast.LENGTH_LONG).show();

                                setCartRecyclerView();

                                getCurrentTotal();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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

    public void getclientToken(){
        final AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("authorizationkey", mAuthorizationkey);
        client.get( "http://104.248.113.55:8088/v1/payment/client_token/", new TextHttpResponseHandler() {
            private String clientToken;

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("demo",responseString);
                Log.d("demo","get token failed");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String clientToken) {
                showDropIn(clientToken);
                Log.d("demo",clientToken);
            }
        });
    }

    public void showDropIn(String braintreeClientToken){
        DropInRequest dropInRequest = new DropInRequest()
                .vaultManager(true)
                .collectDeviceData(true)
                .clientToken(braintreeClientToken);
        startActivityForResult(dropInRequest.getIntent(view.getContext()), 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("demo","activity result");
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                final DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                final String deviceData = result.getDeviceData();
                textviewPayment.setText(result.getPaymentMethodType() + " " + result.getPaymentMethodNonce().getDescription());
                textviewPayment.setVisibility(View.VISIBLE);
                checkout.setVisibility(View.INVISIBLE);
                pay.setVisibility(View.VISIBLE);
                pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        sendNonce(result.getPaymentMethodNonce().getNonce(),deviceData);
                    }
                });

            } else if (resultCode == RESULT_CANCELED) {
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("demo", error.toString());
            }
        }
    }

    public void sendNonce(String nonce,String deviceDataFromClient){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("payment_method_nonce", nonce);

        client.addHeader("authorizationkey", mAuthorizationkey);

        params.put("email", user.email);
        params.put("firstname", user.firstName);
        params.put("lastname", user.lastName);
        params.put("deviceDataFromClient", deviceDataFromClient);

        getCurrentTotal();
        Log.d("Total", formatted);
        params.put("amount", formatted);

        client.post("http://104.248.113.55:8088/v1/payment/checkout", params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Payment successful", Toast.LENGTH_SHORT).show();
                        try {
                            String str = new String(responseBody, "UTF-8");
                            Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Log.d("demo","sent");

                        Bundle authBundle = new Bundle();
                        authBundle.putString(AUTH_KEY, mAuthorizationkey);
                        navController.navigate(R.id.nav_order_complete, authBundle);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Payment failed", Toast.LENGTH_SHORT).show();
                        Log.d("demo","failed" );
                    }
                    // Your implementation here
                }
        );
    }

    @SuppressLint("SetTextI18n")
    public void getCurrentTotal(){
        currentTotal = 0;

        for(int i = 0; i < cartList.size(); i++){
            double price = cartList.get(i).price;
            double discount = cartList.get(i).discount;

            double priceAfterDiscount = (price - (price*discount))*cartList.get(i).quantity;

            currentTotal+=priceAfterDiscount;
        }

        NumberFormat formatter = new DecimalFormat("#0.00");
        formatted = formatter.format(currentTotal);
        currentTotalText.setText("Current Total: " + formatted);
    }

    public void getUser(){
        String profileUrl = MainActivity.url + "profile/me";

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest getRequest = new StringRequest(Request.Method.GET, profileUrl,
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
                                JSONObject userObject = new JSONObject(response);

                                user.firstName = userObject.getString("fname");
                                user.lastName = userObject.getString("lname");
                                user.email = userObject.getString("emailId");
                                user.address = userObject.getString("address");
                                user.age = userObject.getInt("age");

                                Log.d("User", userObject.toString());
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