package com.example.palmaryapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.Adapters.OrderAdapter;
import com.example.palmaryapplication.AuthentificationActivity;
import com.example.palmaryapplication.Models.Order;
import com.example.palmaryapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminAllOrdersFragment extends Fragment {
    private View view;
    private ArrayList<Order> orders;
    private OrderAdapter orderAdapter;
    private RecyclerView MyOrdersRecyclerView;
    private DatabaseReference RefOrder;
    private FirebaseAuth Auth;
    private ImageView LogOutBTN;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_orders, container, false);

        //init
        InisializationOfFealds();

        //recycler view
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        MyOrdersRecyclerView.setLayoutManager(manager);

        //fetch data
        fetchDataFromDB();

        //Logout
        LogOutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.signOut();
                Intent i = new Intent(getActivity(), AuthentificationActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });
        return view;
    }
    private void InisializationOfFealds(){
        MyOrdersRecyclerView = view.findViewById(R.id.MyOrdersRecyclerView);
        LogOutBTN = view.findViewById(R.id.LogOutBTN);
        Auth = FirebaseAuth.getInstance();
        RefOrder = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Orders");
    }
    private void fetchDataFromDB(){
        orders = new ArrayList<>();
        RefOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orders.clear();
                for (DataSnapshot oneSnapshot : snapshot.getChildren()){
                    Order order = oneSnapshot.getValue(Order.class);
                    if (order != null && order.getConfirmation()) {
                        orders.add(order);
                    }
                }
                orderAdapter = new OrderAdapter(getActivity(),orders);
                MyOrdersRecyclerView.setAdapter(orderAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", "Operation canceled", error.toException());
                Toast.makeText(getActivity(), "Database operation canceled: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}