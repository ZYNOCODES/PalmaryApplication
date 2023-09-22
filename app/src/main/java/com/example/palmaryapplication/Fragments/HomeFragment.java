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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.Adapters.AnnonceAdapter;
import com.example.palmaryapplication.Adapters.PostType1Adapter;
import com.example.palmaryapplication.Adapters.PostType2Adapter;
import com.example.palmaryapplication.Interfaces.IconColorChangeListener;
import com.example.palmaryapplication.Models.Product;
import com.example.palmaryapplication.Models.User;
import com.example.palmaryapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
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

public class HomeFragment extends Fragment {
    private View view;
    private RecyclerView AnnonceRecyclerView, ProductRecyclerView, AllProductRecyclerView;
    private PostType1Adapter postType1Adapter;
    private PostType2Adapter postType2Adapter;
    private AnnonceAdapter annonceAdapter;
    private IconColorChangeListener iconColorChangeListener;
    private FirebaseAuth Auth;
    private DatabaseReference RefProduct, Refuser;
    private MaterialCardView MaxonBTN, MomentBTN, KoolBTN, RegaloBTN;
    private ImageView ProfileIMG;
    private LinearLayout LocationContainer;
    private TextView MaxonTitle,MomentTitle,KoolTitle,RegaloTitle,LocationAdress;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private  final  static int REQUEST_CODE=100;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //init
        InisializationOfFealds();
        getLastLocation();
        //Recyclerviews
        LinearLayoutManager Annoncemanager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        AnnonceRecyclerView.setLayoutManager(Annoncemanager);
        LinearLayoutManager PostType1Manager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        ProductRecyclerView.setLayoutManager(PostType1Manager);
        LinearLayoutManager PostType2Manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        AllProductRecyclerView.setLayoutManager(PostType2Manager);

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
        AnnonceRecyclerView = view.findViewById(R.id.AnnonceRecyclerView);
        ProductRecyclerView = view.findViewById(R.id.ProductRecyclerView);
        AllProductRecyclerView = view.findViewById(R.id.AllProductRecyclerView);
        MaxonBTN = view.findViewById(R.id.MaxonBTN);
        MomentBTN = view.findViewById(R.id.MomentBTN);
        KoolBTN = view.findViewById(R.id.KoolBTN);
        RegaloBTN = view.findViewById(R.id.RegaloBTN);
        MaxonTitle = view.findViewById(R.id.MaxonTitle);
        MomentTitle = view.findViewById(R.id.MomentTitle);
        KoolTitle = view.findViewById(R.id.KoolTitle);
        RegaloTitle = view.findViewById(R.id.RegaloTitle);
        LocationAdress = view.findViewById(R.id.LocationAdress);
        LocationContainer = view.findViewById(R.id.LocationContainer);
        LocationContainer.setVisibility(View.GONE);
        ProfileIMG = view.findViewById(R.id.ProfileIMG);
        Auth = FirebaseAuth.getInstance();
        RefProduct = FirebaseDatabase.getInstance(getContext().getString(R.string.DBURL))
                .getReference().child("Products");
        Refuser = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Users")
                .child(Auth.getCurrentUser().getUid());
    }
    private void fetchDataFromDB(){
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<Product> AnnonceProducts = new ArrayList<>();
        ArrayList<Product> RegaloProducts = new ArrayList<>();//1
        ArrayList<Product> KoolProducts = new ArrayList<>();//2
        ArrayList<Product> MomentProducts = new ArrayList<>();//3
        ArrayList<Product> MaxonProducts = new ArrayList<>();//4

        RefProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                AnnonceProducts.clear();
                RegaloProducts.clear();
                KoolProducts.clear();
                MomentProducts.clear();
                MaxonProducts.clear();

                for (DataSnapshot oneSnapshot : snapshot.getChildren()){
                    Product product = oneSnapshot.getValue(Product.class);
                    if (product != null && product.getAnnonce()) {
                        // If the condition is true, add the product to the filtered list
                        AnnonceProducts.add(product);
                    }
                    if (product != null && (product.getCategory().equals("Regalo") || product.getCategory().equals("regalo"))) {
                        // If the condition is true, add the product to the filtered list
                        RegaloProducts.add(product);
                    }
                    if (product != null && (product.getCategory().equals("Kool") || product.getCategory().equals("kool"))) {
                        // If the condition is true, add the product to the filtered list
                        KoolProducts.add(product);
                    }
                    if (product != null && (product.getCategory().equals("Moment") || product.getCategory().equals("moment"))) {
                        // If the condition is true, add the product to the filtered list
                        MomentProducts.add(product);
                    }
                    if (product != null && (product.getCategory().equals("Maxon") || product.getCategory().equals("maxon"))) {
                        // If the condition is true, add the product to the filtered list
                        MaxonProducts.add(product);
                    }

                    products.add(product);
                }

                annonceAdapter = new AnnonceAdapter(getActivity(), AnnonceProducts);
                AnnonceRecyclerView.setAdapter(annonceAdapter);

                if (isAdded()){
                    int primaryColor = ContextCompat.getColor(getActivity(), R.color.PrimaryColor);
                    int whiteColor = ContextCompat.getColor(getActivity(), R.color.white);
                    int primaryTextColor = ContextCompat.getColor(getActivity(), R.color.PrimaryTextColor);

                    if (RegaloProducts.isEmpty()){
                        RegaloBTN.setVisibility(View.GONE);
                    }else{
                        RegaloBTN.setVisibility(View.VISIBLE);
                        RegaloBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // show the content by category
                                RegaloBTN.setCardBackgroundColor(primaryColor);
                                RegaloTitle.setTextColor(whiteColor);
                                KoolBTN.setCardBackgroundColor(whiteColor);
                                KoolTitle.setTextColor(primaryTextColor);
                                MomentBTN.setCardBackgroundColor(whiteColor);
                                MomentTitle.setTextColor(primaryTextColor);
                                MaxonBTN.setCardBackgroundColor(whiteColor);
                                MaxonTitle.setTextColor(primaryTextColor);

                                postType1Adapter = new PostType1Adapter(getActivity(),RegaloProducts);
                                ProductRecyclerView.setAdapter(postType1Adapter);
                            }
                        });
                    }
                    if (KoolProducts.isEmpty()){
                        KoolBTN.setVisibility(View.GONE);
                    }else{
                        KoolBTN.setVisibility(View.VISIBLE);
                        KoolBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // show the content by category
                                KoolBTN.setCardBackgroundColor(primaryColor);
                                KoolTitle.setTextColor(whiteColor);
                                RegaloBTN.setCardBackgroundColor(whiteColor);
                                RegaloTitle.setTextColor(primaryTextColor);
                                MomentBTN.setCardBackgroundColor(whiteColor);
                                MomentTitle.setTextColor(primaryTextColor);
                                MaxonBTN.setCardBackgroundColor(whiteColor);
                                MaxonTitle.setTextColor(primaryTextColor);

                                postType1Adapter = new PostType1Adapter(getActivity(),KoolProducts);
                                ProductRecyclerView.setAdapter(postType1Adapter);
                            }
                        });
                    }
                    if (MomentProducts.isEmpty()){
                        MomentBTN.setVisibility(View.GONE);
                    }else{
                        MomentBTN.setVisibility(View.VISIBLE);
                        MomentBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // show the content by category
                                MomentBTN.setCardBackgroundColor(primaryColor);
                                MomentTitle.setTextColor(whiteColor);
                                KoolBTN.setCardBackgroundColor(whiteColor);
                                KoolTitle.setTextColor(primaryTextColor);
                                RegaloBTN.setCardBackgroundColor(whiteColor);
                                RegaloTitle.setTextColor(primaryTextColor);
                                MaxonBTN.setCardBackgroundColor(whiteColor);
                                MaxonTitle.setTextColor(primaryTextColor);

                                postType1Adapter = new PostType1Adapter(getActivity(),MomentProducts);
                                ProductRecyclerView.setAdapter(postType1Adapter);
                            }
                        });
                    }
                    if (MaxonProducts.isEmpty()){
                        MaxonBTN.setVisibility(View.GONE);
                    }else {
                        MaxonBTN.setVisibility(View.VISIBLE);
                        MaxonBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MaxonBTN.setCardBackgroundColor(primaryColor);
                                MaxonTitle.setTextColor(whiteColor);
                                KoolBTN.setCardBackgroundColor(whiteColor);
                                KoolTitle.setTextColor(primaryTextColor);
                                MomentBTN.setCardBackgroundColor(whiteColor);
                                MomentTitle.setTextColor(primaryTextColor);
                                RegaloBTN.setCardBackgroundColor(whiteColor);
                                RegaloTitle.setTextColor(primaryTextColor);

                                postType1Adapter = new PostType1Adapter(getActivity(),MaxonProducts);
                                ProductRecyclerView.setAdapter(postType1Adapter);
                            }
                        });
                    }
                    if (!RegaloProducts.isEmpty()){
                        RegaloBTN.setCardBackgroundColor(primaryColor);
                        RegaloTitle.setTextColor(whiteColor);
                        KoolBTN.setCardBackgroundColor(whiteColor);
                        KoolTitle.setTextColor(primaryTextColor);
                        MomentBTN.setCardBackgroundColor(whiteColor);
                        MomentTitle.setTextColor(primaryTextColor);
                        MaxonBTN.setCardBackgroundColor(whiteColor);
                        MaxonTitle.setTextColor(primaryTextColor);

                        postType1Adapter = new PostType1Adapter(getActivity(),RegaloProducts);
                        ProductRecyclerView.setAdapter(postType1Adapter);
                    }else if (!KoolProducts.isEmpty()){
                        KoolBTN.setCardBackgroundColor(primaryColor);
                        KoolTitle.setTextColor(whiteColor);
                        RegaloBTN.setCardBackgroundColor(whiteColor);
                        RegaloTitle.setTextColor(primaryTextColor);
                        MomentBTN.setCardBackgroundColor(whiteColor);
                        MomentTitle.setTextColor(primaryTextColor);
                        MaxonBTN.setCardBackgroundColor(whiteColor);
                        MaxonTitle.setTextColor(primaryTextColor);

                        postType1Adapter = new PostType1Adapter(getActivity(),KoolProducts);
                        ProductRecyclerView.setAdapter(postType1Adapter);
                    }else if (!MomentProducts.isEmpty()){
                        MomentBTN.setCardBackgroundColor(primaryColor);
                        MomentTitle.setTextColor(whiteColor);
                        KoolBTN.setCardBackgroundColor(whiteColor);
                        KoolTitle.setTextColor(primaryTextColor);
                        RegaloBTN.setCardBackgroundColor(whiteColor);
                        RegaloTitle.setTextColor(primaryTextColor);
                        MaxonBTN.setCardBackgroundColor(whiteColor);
                        MaxonTitle.setTextColor(primaryTextColor);

                        postType1Adapter = new PostType1Adapter(getActivity(),MomentProducts);
                        ProductRecyclerView.setAdapter(postType1Adapter);
                    }else if (!MaxonProducts.isEmpty()){
                        MaxonBTN.setCardBackgroundColor(primaryColor);
                        MaxonTitle.setTextColor(whiteColor);
                        KoolBTN.setCardBackgroundColor(whiteColor);
                        KoolTitle.setTextColor(primaryTextColor);
                        MomentBTN.setCardBackgroundColor(whiteColor);
                        MomentTitle.setTextColor(primaryTextColor);
                        RegaloBTN.setCardBackgroundColor(whiteColor);
                        RegaloTitle.setTextColor(primaryTextColor);

                        postType1Adapter = new PostType1Adapter(getActivity(),products);
                        ProductRecyclerView.setAdapter(postType1Adapter);
                    }
                }else {
                    // Handle the case when the fragment is not attached to an activity
                    Log.e("DatabaseError", "fragment is not attached to an activity");
                }
                postType2Adapter = new PostType2Adapter(getActivity(), products);
                AllProductRecyclerView.setAdapter(postType2Adapter);
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


        }else {
            askPermission();
        }
    }
    private void askPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }
}