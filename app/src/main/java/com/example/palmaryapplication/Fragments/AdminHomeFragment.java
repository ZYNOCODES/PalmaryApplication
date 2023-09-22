package com.example.palmaryapplication.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.Adapters.AdminAnnonceAdapter;
import com.example.palmaryapplication.Adapters.AdminPostType1Adapter;
import com.example.palmaryapplication.Adapters.AdminPostType2Adapter;
import com.example.palmaryapplication.Models.Product;
import com.example.palmaryapplication.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminHomeFragment extends Fragment {
    private View view;
    private RecyclerView AnnonceRecyclerView, ProductRecyclerView, AllProductRecyclerView;
    private AdminPostType1Adapter adminpostType1Adapter;
    private AdminPostType2Adapter adminpostType2Adapter;
    private AdminAnnonceAdapter adminAnnonceAdapter;
    private DatabaseReference RefProduct;
    private MaterialCardView MaxonBTN,MomentBTN,KoolBTN,RegaloBTN;
    private TextView MaxonTitle,MomentTitle,KoolTitle,RegaloTitle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        //init
        InisializationOfFealds();

        //Recycler views
        LinearLayoutManager adminAnnoncemanager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        AnnonceRecyclerView.setLayoutManager(adminAnnoncemanager);
        LinearLayoutManager PostType1Manager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        ProductRecyclerView.setLayoutManager(PostType1Manager);
        LinearLayoutManager PostType2Manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        AllProductRecyclerView.setLayoutManager(PostType2Manager);

        //fetch data
        fetchDataFromDB();

        return view;
    }
    private void InisializationOfFealds(){
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
        RefProduct = FirebaseDatabase.getInstance(getContext().getString(R.string.DBURL))
                .getReference().child("Products");
    }
    private void fetchDataFromDB(){
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<Product> AnnonceProducts = new ArrayList<>();
        ArrayList<Product> RegaloProducts = new ArrayList<>();
        ArrayList<Product> KoolProducts = new ArrayList<>();
        ArrayList<Product> MomentProducts = new ArrayList<>();
        ArrayList<Product> MaxonProducts = new ArrayList<>();

        RefProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
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
                adminAnnonceAdapter = new AdminAnnonceAdapter(getActivity(), AnnonceProducts);
                AnnonceRecyclerView.setAdapter(adminAnnonceAdapter);

                //init category in burger button
                if (isAdded()) {
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

                                adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),RegaloProducts);
                                ProductRecyclerView.setAdapter(adminpostType1Adapter);
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

                                adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),KoolProducts);
                                ProductRecyclerView.setAdapter(adminpostType1Adapter);
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

                                adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),MomentProducts);
                                ProductRecyclerView.setAdapter(adminpostType1Adapter);
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

                                adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),MaxonProducts);
                                ProductRecyclerView.setAdapter(adminpostType1Adapter);
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

                        adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),RegaloProducts);
                        ProductRecyclerView.setAdapter(adminpostType1Adapter);
                    }else if (!KoolProducts.isEmpty()){
                        KoolBTN.setCardBackgroundColor(primaryColor);
                        KoolTitle.setTextColor(whiteColor);
                        RegaloBTN.setCardBackgroundColor(whiteColor);
                        RegaloTitle.setTextColor(primaryTextColor);
                        MomentBTN.setCardBackgroundColor(whiteColor);
                        MomentTitle.setTextColor(primaryTextColor);
                        MaxonBTN.setCardBackgroundColor(whiteColor);
                        MaxonTitle.setTextColor(primaryTextColor);

                        adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),KoolProducts);
                        ProductRecyclerView.setAdapter(adminpostType1Adapter);
                    }else if (!MomentProducts.isEmpty()){
                        MomentBTN.setCardBackgroundColor(primaryColor);
                        MomentTitle.setTextColor(whiteColor);
                        KoolBTN.setCardBackgroundColor(whiteColor);
                        KoolTitle.setTextColor(primaryTextColor);
                        RegaloBTN.setCardBackgroundColor(whiteColor);
                        RegaloTitle.setTextColor(primaryTextColor);
                        MaxonBTN.setCardBackgroundColor(whiteColor);
                        MaxonTitle.setTextColor(primaryTextColor);

                        adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),MomentProducts);
                        ProductRecyclerView.setAdapter(adminpostType1Adapter);
                    }else if (!MaxonProducts.isEmpty()){
                        MaxonBTN.setCardBackgroundColor(primaryColor);
                        MaxonTitle.setTextColor(whiteColor);
                        KoolBTN.setCardBackgroundColor(whiteColor);
                        KoolTitle.setTextColor(primaryTextColor);
                        MomentBTN.setCardBackgroundColor(whiteColor);
                        MomentTitle.setTextColor(primaryTextColor);
                        RegaloBTN.setCardBackgroundColor(whiteColor);
                        RegaloTitle.setTextColor(primaryTextColor);

                        adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),products);
                        ProductRecyclerView.setAdapter(adminpostType1Adapter);
                    }
                } else {
                    // Handle the case when the fragment is not attached to an activity
                    Log.e("DatabaseError", "fragment is not attached to an activity");
                }



                adminpostType2Adapter = new AdminPostType2Adapter(getActivity(), products);
                AllProductRecyclerView.setAdapter(adminpostType2Adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", "Operation canceled", error.toException());
                Toast.makeText(getActivity(), "Database operation canceled: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}