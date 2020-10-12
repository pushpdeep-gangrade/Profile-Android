package com.example.profileapp.store;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.profileapp.MainActivity;
import com.example.profileapp.R;
import com.example.profileapp.login.LoginActivity;
import com.example.profileapp.models.StoreItem;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class StoreFragment extends Fragment {
    List<StoreItem> storeItemArrayList = new ArrayList<>();
    List<StoreItem> cartList = new ArrayList<>();
    RecyclerView storeItemRecyclerView;
    View view;
    private NavController navController;

    public StoreFragment() {
        // Required empty public constructor
    }


    public static StoreFragment newInstance(String param1, String param2) {
        StoreFragment fragment = new StoreFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_store, container, false);

        storeItemRecyclerView = view.findViewById(R.id.store_itemList);

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        readStoreItemJsonFile();


        return view;
    }


    public void readStoreItemJsonFile() {
        AssetManager assetManager = getContext().getAssets();
        storeItemArrayList.clear();

        try {
            InputStream is = assetManager.open("StoreItems.json");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getContext().getAssets().open("StoreItems.json"), "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            String response = stringBuilder.toString();

            Log.d("File Status", response.toString());

            JSONObject root = new JSONObject(response);
            JSONArray storeItems = root.getJSONArray("storeItems");

            for (int i = 0; i < storeItems.length(); i++) {
                JSONObject jsonStoreItemObject = (JSONObject) storeItems.get(i);
                StoreItem storeItem = new StoreItem();

                storeItem.name = jsonStoreItemObject.getString("name");
                storeItem.discount = jsonStoreItemObject.getDouble("discount");
                storeItem.region = jsonStoreItemObject.getString("region");
                storeItem.price = jsonStoreItemObject.getDouble("price");
                storeItem.photo = jsonStoreItemObject.getString("photo");
                storeItem.quantity = jsonStoreItemObject.getInt("quantity");

                storeItemArrayList.add(storeItem);

                Log.d("Store Item", storeItem.toString());
            }

            // use a linear layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            storeItemRecyclerView.setLayoutManager(layoutManager);

            final StoreItemAdapter ad = new StoreItemAdapter(getContext(),
                    android.R.layout.simple_list_item_1, storeItemArrayList, navController);

            storeItemRecyclerView.setAdapter(ad);

            //storeItemArrayList.clear();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


    }

}