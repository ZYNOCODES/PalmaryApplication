package com.example.palmaryapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.Models.Cart;
import com.example.palmaryapplication.R;
import com.example.palmaryapplication.ViewHolders.CartViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{
    private Context context;
    private ArrayList<Cart> products;
    private DatabaseReference RefCart;
    private FirebaseAuth Auth;
    public CartAdapter(Context context, ArrayList<Cart> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_myorder,parent,false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.TitleOutPut.setText(products.get(position).getProduct().getName());
        holder.PriceOutPut.setText(products.get(position).getProduct().getPrice());
        holder.QuantityOutPut.setText(products.get(position).getQuantity());
        holder.ModificationOutPut.setText(products.get(position).getModifications());
        Picasso.get().load(products.get(position).getProduct().getIMG()).into(holder.IMGOutPut);
        Auth = FirebaseAuth.getInstance();
        RefCart = FirebaseDatabase.getInstance(context.getString(R.string.DBURL))
                .getReference()
                .child("Users")
                .child(Auth.getCurrentUser().getUid())
                .child("Cart");
        holder.DeleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete from cart
                RefCart.child(products.get(position).getID()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(context, "La comande a été supprimée avec succès du panier", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(context, "Erreur, vérifiez votre connexion internet.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
