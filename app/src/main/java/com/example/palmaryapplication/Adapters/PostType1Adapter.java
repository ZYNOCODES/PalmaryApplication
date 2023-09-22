package com.example.palmaryapplication.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.DisplayProductActivity;
import com.example.palmaryapplication.Models.Cart;
import com.example.palmaryapplication.Models.Product;
import com.example.palmaryapplication.R;
import com.example.palmaryapplication.ViewHolders.PostType1ViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostType1Adapter extends RecyclerView.Adapter<PostType1ViewHolder> {
    private Context context;
    private ArrayList<Product> Posts;
    private DatabaseReference RefCart;
    private FirebaseAuth Auth;
    public PostType1Adapter(Context context, ArrayList<Product> posts) {
        this.context = context;
        Posts = posts;
    }

    @NonNull
    @Override
    public PostType1ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_posttype1,parent,false);
        return new PostType1ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostType1ViewHolder holder, int position) {
        Picasso.get().load(Posts.get(position).getIMG()).into(holder.PostIMG);
        holder.PostTitle.setText(Posts.get(position).getName());
        holder.PostIngredients.setText(Posts.get(position).getIngredients());
        holder.PostPrice.setText(Posts.get(position).getPrice());

        Auth = FirebaseAuth.getInstance();
        RefCart = FirebaseDatabase.getInstance(context.getString(R.string.DBURL))
                .getReference()
                .child("Users")
                .child(Auth.getCurrentUser().getUid())
                .child("Cart");
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait1);
        dialog.setCanceledOnTouchOutside(false);
        holder.AddToCartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add to cart
                RefCart = FirebaseDatabase.getInstance(context.getString(R.string.DBURL))
                        .getReference()
                        .child("Users")
                        .child(Auth.getCurrentUser().getUid())
                        .child("Cart");
                RefCart = RefCart.push();
                String idd = RefCart.getKey();
                Cart cart = new Cart(idd,Posts.get(position),"1","");
                RefCart.setValue(cart)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    dialog.dismiss();
                                    Toast.makeText(context, "La commande a été ajoutée au panier", Toast.LENGTH_SHORT).show();
                                }else {
                                    dialog.dismiss();
                                    Toast.makeText(context, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
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
