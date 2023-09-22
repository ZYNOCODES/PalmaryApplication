package com.example.palmaryapplication.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.Adapters.OrderAdapter;
import com.example.palmaryapplication.Interfaces.IconColorChangeListener;
import com.example.palmaryapplication.Models.Order;
import com.example.palmaryapplication.Models.User;
import com.example.palmaryapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyOrdersFragment extends Fragment {
    private View view;
    private OrderAdapter orderAdapter, ConfirmedorderAdapter;
    private RecyclerView MyOrdersRecyclerView, MyConfirmedOrdersRecyclerView;
    private ArrayList<Order> orders, ConfirmedOrders;
    private DatabaseReference RefOrder, Refuser;
    private FirebaseAuth Auth;
    private LinearLayout LocationContainer;
    private IconColorChangeListener iconColorChangeListener;

    private ImageView ProfileIMG;
    private TextView LocationAdress;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private  final  static int REQUEST_CODE=100;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        //init
        InisializationOfFealds();
        getLastLocation();
        //recycler view
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        MyOrdersRecyclerView.setLayoutManager(manager);
        LinearLayoutManager manager2 = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        MyConfirmedOrdersRecyclerView.setLayoutManager(manager2);

        //fetch data
        fetchDataFromDB();

        ProfileIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to profile
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                        .replace(R.id.MainFragmentContainer, new ProfilFragment());
                fragmentTransaction.commit();
                // Inside your method where you want to change the icon colors
                iconColorChangeListener.ChangeProfileIconColor();
            }
        });
        return view;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Check if the hosting Activity implements the interface
        if (context instanceof IconColorChangeListener) {
            iconColorChangeListener = (IconColorChangeListener) context;
        } else {
            throw new ClassCastException("Hosting Activity must implement IconColorChangeListener");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else {
                Toast.makeText(getActivity(), "La permission est requise", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void InisializationOfFealds(){
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getActivity());
        MyOrdersRecyclerView = view.findViewById(R.id.MyOrdersRecyclerView);
        MyConfirmedOrdersRecyclerView = view.findViewById(R.id.MyConfirmedOrdersRecyclerView);
        LocationAdress = view.findViewById(R.id.LocationAdress);
        LocationContainer = view.findViewById(R.id.LocationContainer);
        LocationContainer.setVisibility(View.GONE);
        ProfileIMG = view.findViewById(R.id.ProfileIMG);
        Auth = FirebaseAuth.getInstance();
        RefOrder = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Users")
                .child(Auth.getCurrentUser().getUid())
                .child("Orders");
        Refuser = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Users")
                .child(Auth.getCurrentUser().getUid());
    }
    private void fetchDataFromDB(){
        orders = new ArrayList<>();
        ConfirmedOrders = new ArrayList<>();
        RefOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orders.clear();
                ConfirmedOrders.clear();
                for (DataSnapshot oneSnapshot : snapshot.getChildren()){
                    Order order = oneSnapshot.getValue(Order.class);
                    if (order != null && order.getClientID().equals(Auth.getCurrentUser().getUid())
                            && !order.getConfirmation()) {
                        orders.add(order);
                    }
                    if (order != null && order.getClientID().equals(Auth.getCurrentUser().getUid())
                            && order.getConfirmation()) {
                        ConfirmedOrders.add(order);
                    }
                }
                orderAdapter = new OrderAdapter(getActivity(),orders);
                MyOrdersRecyclerView.setAdapter(orderAdapter);
                ConfirmedorderAdapter = new OrderAdapter(getActivity(),ConfirmedOrders);
                MyConfirmedOrdersRecyclerView.setAdapter(ConfirmedorderAdapter);
                // Create an ItemTouchHelper instance
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        ConfirmedorderAdapter.onItemSwiped(position);
                    }
                });

                // Attach the ItemTouchHelper to the RecyclerView
                itemTouchHelper.attachToRecyclerView(MyConfirmedOrdersRecyclerView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", "Operation canceled", error.toException());
                Toast.makeText(getActivity(), "Database operation canceled: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Refuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    if (user.getPhone() != null && user.getFullName() != null){
                        if (user.getIMG() != null){
                            Picasso.get().load(user.getIMG()).into(ProfileIMG);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", "Operation canceled", error.toException());
                Toast.makeText(getActivity(), "Database operation canceled: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location !=null){
                                Geocoder geocoder=new Geocoder(getActivity(), Locale.getDefault());
                                List<Address> addresses= null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    LocationAdress.setText(addresses.get(0).getAddressLine(0));
                                    //set locationcontainer visible
                                    LocationContainer.setVisibility(View.VISIBLE);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    });


        }
    }
}