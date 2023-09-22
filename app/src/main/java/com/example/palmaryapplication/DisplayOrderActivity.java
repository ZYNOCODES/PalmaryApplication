package com.example.palmaryapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DisplayOrderActivity extends AppCompatActivity {
    private DatabaseReference RefOrder, Refuser;
    private MyOrderAdapter myOrderAdapter;
    private RecyclerView CartRecyclerView;
    private TextView DeliveryCartPrice, TotleCartPrice, TotleItemsPrice, LocationOutPut, DeleveryNotesOutPut, ClientPhoneNumber, ClientFullName;
    private String OrderID;
    private ImageView CancelBTN;
    private LinearLayout DeleveryCard;
    private Order order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_order);

        //init
        InisializationOfFealds();
        ButtonsRediraction();

        //recycler view
        LinearLayoutManager manager = new LinearLayoutManager(DisplayOrderActivity.this,LinearLayoutManager.VERTICAL,false);
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
        ClientPhoneNumber = findViewById(R.id.ClientPhoneNumber);
        ClientFullName = findViewById(R.id.ClientFullName);
        OrderID = getIntent().getStringExtra("OrderID");
        RefOrder = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Orders")
                .child(OrderID);
    }
    private void ButtonsRediraction(){
        CancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void fetchDataFromDB(){
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
                    myOrderAdapter = new MyOrderAdapter(DisplayOrderActivity.this,order.getProducts());
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
                                    ClientPhoneNumber.setText(user.getPhone().toString());
                                    ClientFullName.setText(user.getFullName().toString());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("DatabaseError", "Operation canceled", error.toException());
                            Toast.makeText(DisplayOrderActivity.this, "Database operation canceled: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", "Operation canceled", error.toException());
                Toast.makeText(DisplayOrderActivity.this, "Database operation canceled: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}