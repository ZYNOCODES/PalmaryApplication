package com.example.palmaryapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.Adapters.MyOrderAdapter;
import com.example.palmaryapplication.Models.Order;
import com.example.palmaryapplication.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AdminConfirmeOrderActivity extends AppCompatActivity {
    private DatabaseReference RefOrder, RefOrderConfirmation, Refuser, RefOrderConfirmationClient, RefUserOrder;
    private MyOrderAdapter myOrderAdapter;
    private RecyclerView CartRecyclerView;
    private TextView DeliveryCartPrice, TotleCartPrice, TotleItemsPrice, LocationOutPut, DeleveryNotesOutPut, ClientPhoneNumber, ClientFullName;
    private String OrderID;
    private ImageView CancelBTN;
    private LinearLayout DeleveryCard;
    private MaterialCardView AnnulationBTN, ConfirmationBTN;
    private Dialog dialog;
    private Order order;
    private FirebaseAuth Auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_confirme_order);

        //init
        InisializationOfFealds();
        ButtonsRediraction();
        dialog = new Dialog(AdminConfirmeOrderActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait1);
        dialog.setCanceledOnTouchOutside(false);

        //recycler view
        LinearLayoutManager manager = new LinearLayoutManager(AdminConfirmeOrderActivity.this,LinearLayoutManager.VERTICAL,false);
        CartRecyclerView.setLayoutManager(manager);

        //fetch data
        fetchDataFromDB();
    }
    private void InisializationOfFealds(){
        CartRecyclerView = findViewById(R.id.CartRecyclerView);
        CancelBTN = findViewById(R.id.CancelBTN);
        DeliveryCartPrice = findViewById(R.id.DeliveryCartPrice);
        TotleCartPrice = findViewById(R.id.TotleCartPrice);
        TotleItemsPrice = findViewById(R.id.TotleItemsPrice);
        LocationOutPut = findViewById(R.id.LocationOutPut);
        DeleveryNotesOutPut = findViewById(R.id.DeleveryNotesOutPut);
        DeleveryCard = findViewById(R.id.DeleveryCard);
        ConfirmationBTN = findViewById(R.id.ConfirmationBTN);
        AnnulationBTN = findViewById(R.id.AnnulationBTN);
        ClientPhoneNumber = findViewById(R.id.ClientPhoneNumber);
        ClientFullName = findViewById(R.id.ClientFullName);
        OrderID = getIntent().getStringExtra("OrderID");
        Auth = FirebaseAuth.getInstance();
        RefOrder = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Orders")
                .child(OrderID);
        RefOrderConfirmation = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Users")
                .child(Auth.getCurrentUser().getUid())
                .child("Orders");

    }
    private void ButtonsRediraction(){
        CancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ConfirmationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mydialog = new AlertDialog.Builder(AdminConfirmeOrderActivity.this);
                mydialog.setTitle("Confirmée cette commande "+order.getID());
                mydialog.setMessage("Voulez-vous vraiment confirmée cette commande: "
                        +order.getID()+" ?");
                mydialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Update the product
                        dialog.show();
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("confirmation", true);
                        updateOrderIntoDB(updates);
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
        AnnulationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mydialog = new AlertDialog.Builder(AdminConfirmeOrderActivity.this);
                mydialog.setTitle("Annuler cette commande");
                mydialog.setMessage("Voulez-vous vraiment annuler cette commande: "
                        +order.getID()+" ?");
                mydialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RefUserOrder = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                                .getReference()
                                .child("Users")
                                .child(order.getClientID())
                                .child("Orders")
                                .child(OrderID);
                        // deleting the product
                        if (RefOrder != null){
                            RefOrder.removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                RefUserOrder.removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    onBackPressed();
                                                                    Toast.makeText(AdminConfirmeOrderActivity.this, "Order deleted", Toast.LENGTH_SHORT).show();
                                                                }else{
                                                                    Toast.makeText(AdminConfirmeOrderActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }else{
                                                Toast.makeText(AdminConfirmeOrderActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else {
                            Toast.makeText(AdminConfirmeOrderActivity.this, "la commande est annulée par le client", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }

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
    private void fetchDataFromDB(){
        if (RefOrder != null){
            RefOrder.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    order = snapshot.getValue(Order.class);
                    final int[] totalItem = {0};
                    if (order != null) {
                        TotleCartPrice.setText(String.valueOf(order.getPrice()));
                        for (int i = 0 ; i < order.getProducts().size() ; i++){
                            totalItem[0] = totalItem[0] + Integer.parseInt(order.getProducts().get(i).getProduct().getPrice()) * Integer.parseInt(order.getProducts().get(i).getQuantity());
                        }
                        TotleItemsPrice.setText(String.valueOf(totalItem[0]));
                        DeliveryCartPrice.setText(String.valueOf(Integer.parseInt(order.getPrice()) - totalItem[0]));
                        if (order.getType().equals("a domicile")){
                            DeleveryCard.setVisibility(View.GONE);
                        }else {
                            DeleveryCard.setVisibility(View.VISIBLE);
                            LocationOutPut.setText(String.valueOf(order.getLocation().getAddress()));
                            DeleveryNotesOutPut.setText(String.valueOf(order.getLocationNotes()));
                        }
                        myOrderAdapter = new MyOrderAdapter(AdminConfirmeOrderActivity.this,order.getProducts());
                        CartRecyclerView.setAdapter(myOrderAdapter);
                        Refuser = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                                .getReference()
                                .child("Users")
                                .child(order.getClientID());
                        Refuser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                if (user != null) {
                                    if (user.getPhone() != null && user.getFullName() != null){
                                        ClientPhoneNumber.setText(String.valueOf(user.getPhone()));
                                        ClientFullName.setText(String.valueOf(user.getFullName()));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("DatabaseError", "Operation canceled", error.toException());
                                Toast.makeText(AdminConfirmeOrderActivity.this, "Database operation canceled: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("DatabaseError", "Operation canceled", error.toException());
                    Toast.makeText(AdminConfirmeOrderActivity.this, "Database operation canceled: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(AdminConfirmeOrderActivity.this, "la commande est annulée par le client", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

    }
    protected void updateOrderIntoDB(Map<String, Object> product){
        if (RefOrder != null){
            RefOrder.updateChildren(product)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                RefOrderConfirmation = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                                        .getReference()
                                        .child("Users")
                                        .child(Auth.getCurrentUser().getUid())
                                        .child("Orders");
                                RefOrderConfirmationClient = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                                        .getReference()
                                        .child("Users")
                                        .child(order.getClientID())
                                        .child("Orders");
                                RefOrderConfirmation.child(order.getID()).setValue(order)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    RefOrderConfirmationClient.child(order.getID()).setValue(order)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        dialog.dismiss();
                                                                        onBackPressed();
                                                                        Toast.makeText(AdminConfirmeOrderActivity.this, "La commande a été confirmé avec succès", Toast.LENGTH_SHORT).show();
                                                                    }else {
                                                                        dialog.dismiss();
                                                                        Toast.makeText(AdminConfirmeOrderActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }else {
                                                    dialog.dismiss();
                                                    Toast.makeText(AdminConfirmeOrderActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }else {
                                dialog.dismiss();
                                Toast.makeText(AdminConfirmeOrderActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            Toast.makeText(AdminConfirmeOrderActivity.this, "la commande est annulée par le client", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

    }
}