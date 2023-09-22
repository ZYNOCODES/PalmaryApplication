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
import com.example.palmaryapplication.ViewHolders.AdminAnnonceViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminAnnonceAdapter extends RecyclerView.Adapter<AdminAnnonceViewHolder> {
    private Context context;
    private ArrayList<Product> annonces;
    private DatabaseReference RefProduct;
    private StorageReference ProductsImgref;

    public AdminAnnonceAdapter(Context context, ArrayList<Product> annonces) {
        this.context = context;
        this.annonces = annonces;
    }

    @NonNull
    @Override
    public AdminAnnonceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_annonce_admin,parent,false);
        return new AdminAnnonceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminAnnonceViewHolder holder, int position) {
        Picasso.get().load(annonces.get(position).getIMG()).into(holder.AnnonceIMG);
        holder.AnnonceTitle.setText(annonces.get(position).getName());
        RefProduct = FirebaseDatabase.getInstance(context.getString(R.string.DBURL))
                .getReference().child("Products");
        ProductsImgref = FirebaseStorage.getInstance().getReference().child("ProductImages");

        holder.DeleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete
                AlertDialog.Builder mydialog = new AlertDialog.Builder(context);
                mydialog.setTitle("Supprimer cette annonce");
                mydialog.setMessage("Voulez-vous vraiment supprimer cette annonce "
                        +annonces.get(position).getName()+" ?");
                mydialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // deleting the product
                        RefProduct.child(annonces.get(position).getID()).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            ProductsImgref.child(annonces.get(position).getImgRef()+".jpeg").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(context, "L'annonce a été supprimée avec succès", Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Toast.makeText(context, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }else{
                                            Toast.makeText(context, task.getException().toString() , Toast.LENGTH_SHORT).show();

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
                //annonce details
                Intent i = new Intent(context, AdminDisplayProductActivity.class);
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
