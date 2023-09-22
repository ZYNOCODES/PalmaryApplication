package com.example.palmaryapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.Models.Cart;
import com.example.palmaryapplication.R;
import com.example.palmaryapplication.ViewHolders.MyOrderViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderViewHolder> {
    private Context context;
    private ArrayList<Cart> products;

    public MyOrderAdapter(Context context, ArrayList<Cart> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public MyOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_myorder_display,parent,false);
        return new MyOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderViewHolder holder, int position) {
        holder.TitleOutPut.setText(products.get(position).getProduct().getName());
        holder.PriceOutPut.setText(products.get(position).getProduct().getPrice());
        holder.QuantityOutPut.setText(products.get(position).getQuantity());
        holder.ModificationOutPut.setText(products.get(position).getModifications());
        Picasso.get().load(products.get(position).getProduct().getIMG()).into(holder.IMGOutPut);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
