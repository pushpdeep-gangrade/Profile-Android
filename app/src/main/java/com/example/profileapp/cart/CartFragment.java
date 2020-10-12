package com.example.profileapp.cart;

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

import com.example.profileapp.MainActivity;
import com.example.profileapp.R;
import com.example.profileapp.models.StoreItem;
import com.example.profileapp.store.StoreItemAdapter;

import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment {
    private ArrayList<StoreItem> storeItemArrayList = new ArrayList<>();
    List<StoreItem> cartList = MainActivity.cartList;
    RecyclerView cartItemRecyclerView;
    View view;
    private NavController navController;


    public CartFragment() {
        // Required empty public constructor
    }

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartItemRecyclerView = view.findViewById(R.id.cart_itemList);

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        setCartRecyclerView();

        return view;
    }

    public void setCartRecyclerView(){
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        cartItemRecyclerView.setLayoutManager(layoutManager);

        final CartItemAdapter ad = new CartItemAdapter(getContext(),
                android.R.layout.simple_list_item_1, cartList, navController, cartItemRecyclerView);

        cartItemRecyclerView.setAdapter(ad);


    }
}