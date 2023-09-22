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
import com.example.palmaryapplication.ViewHolders.PostType2ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostType2Adapter extends RecyclerView.Adapter<PostType2ViewHolder> {
    private Context context;
    private ArrayList<Product> Posts;

    public PostType2Adapter(Context context, ArrayList<Product> posts) {
        this.context = context;
        Posts = posts;
    }

    @NonNull
    @Override
    public PostType2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_posttype2,parent,false);
        return new PostType2ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostType2ViewHolder holder, int position) {
        Picasso.get().load(Posts.get(position).getIMG()).into(holder.PostIMG);
        holder.PostTitle.setText(Posts.get(position).getName());
        holder.PostIngredients.setText(Posts.get(position).getIngredients());
        holder.PostPrice.setText(Posts.get(position).getPrice());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //redirect to post details
                Intent i = new Intent(context, DisplayProductActivity.class);
                i.putExtra("ProductID",Posts.get(position).getID());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Posts.size();
    }
}
