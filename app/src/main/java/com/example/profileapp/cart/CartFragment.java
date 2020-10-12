package com.example.profileapp.cart;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.cardinalcommerce.shared.userinterfaces.ProgressDialog;
import com.example.profileapp.MainActivity;
import com.example.profileapp.R;
import com.example.profileapp.models.StoreItem;
import com.example.profileapp.store.StoreItemAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class CartFragment extends Fragment {
    private ArrayList<StoreItem> storeItemArrayList = new ArrayList<>();
    List<StoreItem> cartList = MainActivity.cartList;
    RecyclerView cartItemRecyclerView;
    View view;
    Button pay;
    Button checkout;
    TextView textviewPayment;
    private NavController navController;
    private String mAuthorizationkey;
    ProgressBar mProgressBar;
    private static final String AUTH_KEY = "authorizationkey";


    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            storeItemArrayList = (ArrayList<StoreItem>) getArguments().getSerializable("cartList");
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

        textviewPayment.setVisibility(View.INVISIBLE);
        pay.setVisibility(View.INVISIBLE);

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        setCartRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getclientToken();
            }
        });

    }

    public void setCartRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        cartItemRecyclerView.setLayoutManager(layoutManager);

        final CartItemAdapter ad = new CartItemAdapter(getContext(),
                android.R.layout.simple_list_item_1, cartList, navController, cartItemRecyclerView);

        cartItemRecyclerView.setAdapter(ad);
    }

    public void getclientToken(){
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get( "http://104.248.113.55:8080/v1/payment/client_token/", new TextHttpResponseHandler() {
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
                .clientToken(braintreeClientToken);
        startActivityForResult(dropInRequest.getIntent(view.getContext()), 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("demo","activity result");
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                final DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                textviewPayment.setText(result.getPaymentMethodType() + " " + result.getPaymentMethodNonce().getDescription());
                textviewPayment.setVisibility(View.VISIBLE);
                checkout.setVisibility(View.INVISIBLE);
                pay.setVisibility(View.VISIBLE);
                pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        sendNonce(result.getPaymentMethodNonce().getNonce());
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

    public void sendNonce(String nonce){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("payment_method_nonce", nonce);
        client.post("http://104.248.113.55:8080/v1/payment/checkout", params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Payment succesful", Toast.LENGTH_SHORT).show();
                        Log.d("demo","sent");
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
}