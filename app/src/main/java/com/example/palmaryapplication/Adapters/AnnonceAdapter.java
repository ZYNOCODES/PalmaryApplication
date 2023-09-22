package com.example.palmaryapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.DisplayProductActivity;
import com.example.palmaryapplication.Models.Product;
import com.example.palmaryapplication.R;
import com.example.palmaryapplication.ViewHolders.AnnonceViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AnnonceAdapter extends RecyclerView.Adapter<AnnonceViewHolder> {
    private Context context;
    private ArrayList<Product> annonces;

    public AnnonceAdapter(Context context, ArrayList<Product> annonces) {
        this.context = context;
        this.annonces = annonces;
    }

    @NonNull
    @Override
    public AnnonceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_annonce,parent,false);
        return new AnnonceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnonceViewHolder holder, int position) {
        Picasso.get().load(annonces.get(position).getIMG()).into(holder.AnnonceIMG);
        holder.AnnonceTitle.setText(annonces.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //annonce details
                Intent i = new Intent(context, DisplayProductActivity.class);
                i.putExtra("ProductID",annonces.get(position).getID());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return annonces.size();
    }
}
