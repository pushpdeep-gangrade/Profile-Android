package com.example.profileapp.store;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profileapp.R;

public class StoreItemViewHolder extends RecyclerView.ViewHolder {

    public View itemView;

    public StoreItemViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
    }
}
