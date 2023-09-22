package com.example.palmaryapplication.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.AdminDisplayProductActivity;
import com.example.palmaryapplication.Models.Product;
import com.example.palmaryapplication.R;
import com.example.palmaryapplication.ViewHolders.AdminPostType2ViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminPostType2Adapter extends RecyclerView.Adapter<AdminPostType2ViewHolder> {
    private Context context;
    private ArrayList<Product> Posts;
    private DatabaseReference RefProduct;
    private StorageReference ProductsImgref;
    public AdminPostType2Adapter(Context context, ArrayList<Product> posts) {
        this.context = context;
        Posts = posts;
    }

    @NonNull
    @Override
    public AdminPostType2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_posttype2_admin,parent,false);
        return new AdminPostType2ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPostType2ViewHolder holder, int position) {
        Picasso.get().load(Posts.get(position).getIMG()).into(holder.PostIMG);
        holder.PostTitle.setText(Posts.get(position).getName());
        holder.PostIngredients.setText(Posts.get(position).getIngredients());
        holder.PostPrice.setText(Posts.get(position).getPrice());
        RefProduct = FirebaseDatabase.getInstance(context.getString(R.string.DBURL))
                .getReference().child("Products");
        ProductsImgref = FirebaseStorage.getInstance().getReference().child("ProductImages");

        holder.DeleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete the product from the database
                AlertDialog.Builder mydialog = new AlertDialog.Builder(context);
                mydialog.setTitle("Supprimer cette publication");
                mydialog.setMessage("Voulez-vous vraiment supprimer cette publication "
                        +Posts.get(position).getName()+" ?");
                mydialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // deleting the product
                        String IMGREF = Posts.get(position).getImgRef()+".jpeg";
                        RefProduct.child(Posts.get(position).getID()).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            ProductsImgref.child(IMGREF).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(context, "La publication a été supprimée avec succès", Toast.LENGTH_SHORT).show();

                                                    }else {
                                                        Toast.makeText(context, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }else{
                                            Toast.makeText(context, task.getException().toString(), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }
                });
                mydialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                mydialog.show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //redirect to post details
                Intent i = new Intent(context, AdminDisplayProductActivity.class);
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
