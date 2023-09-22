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

import com.example.palmaryapplication.DisplayOrderActivity;
import com.example.palmaryapplication.Interfaces.ItemSwipeCallback;
import com.example.palmaryapplication.Models.Order;
import com.example.palmaryapplication.R;
import com.example.palmaryapplication.ViewHolders.OrderViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> implements ItemSwipeCallback {
        private Context context;
        private ArrayList<Order> orders;
        private DatabaseReference RefOrder, RefConfirmedOrder, RefUserOrder;
        private FirebaseAuth Auth;
        public OrderAdapter(Context context, ArrayList<Order> orders) {
                this.context = context;
                this.orders = orders;
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.card_order,parent,false);
                return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
                RefOrder = FirebaseDatabase.getInstance(context.getString(R.string.DBURL))
                        .getReference()
                        .child("Orders");
                RefUserOrder = FirebaseDatabase.getInstance(context.getString(R.string.DBURL))
                        .getReference()
                        .child("Users")
                        .child(orders.get(position).getClientID())
                        .child("Orders");
                if (orders.get(position).getConfirmation()){
                        holder.OrderCancelBTN.setVisibility(View.GONE);
                        holder.OrderConfirmedBTN.setVisibility(View.VISIBLE);

                }else {
                        holder.OrderConfirmedBTN.setVisibility(View.GONE);
                        holder.OrderCancelBTN.setVisibility(View.VISIBLE);
                        holder.OrderCancelBTN.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                        //cancel this order
                                        AlertDialog.Builder mydialog = new AlertDialog.Builder(context);
                                        mydialog.setTitle("Supprimer cette commande ");
                                        mydialog.setMessage("Voulez-vous vraiment supprimer cette commande "
                                                +orders.get(position).getID()+" ?");
                                        mydialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                        // deleting the order
                                                        RefOrder.child(orders.get(position).getID()).removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful()){
                                                                                        RefUserOrder.child(orders.get(position).getID()).removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if(task.isSuccessful()){
                                                                                                                        Toast.makeText(context, "La commande a été supprimée avec succès", Toast.LENGTH_SHORT).show();
                                                                                                                }else{
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
                }

                holder.OrderID.setText(orders.get(position).getID());
                holder.OrderPrice.setText(orders.get(position).getPrice());
                holder.OrderType.setText(orders.get(position).getType());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                //redirect to order details
                                Intent i = new Intent(context, DisplayOrderActivity.class);
                                i.putExtra("OrderID",orders.get(position).getID());
                                context.startActivity(i);
                        }
                });
        }

        @Override
        public int getItemCount() {
                return orders.size();
        }

        @Override
        public void onItemSwiped(int position) {
                Auth = FirebaseAuth.getInstance();
                RefConfirmedOrder = FirebaseDatabase.getInstance(context.getString(R.string.DBURL))
                        .getReference()
                        .child("Users")
                        .child(Auth.getCurrentUser().getUid())
                        .child("Orders");
                //delete order
                AlertDialog.Builder mydialog = new AlertDialog.Builder(context);
                mydialog.setTitle("Commande est complète");
                mydialog.setMessage("Voulez-vous vraiment supprimer cette commande "
                        +orders.get(position).getID()+" ?");
                mydialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                                // deleting the product
                                RefConfirmedOrder.child(orders.get(position).getID()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                                Toast.makeText(context, "La commande a été supprimée avec succès", Toast.LENGTH_SHORT).show();
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
                                notifyDataSetChanged();
                        }
                });
                mydialog.show();
        }
}
