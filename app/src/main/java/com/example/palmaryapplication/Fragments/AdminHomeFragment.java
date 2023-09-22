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
    private MaterialCardView DrinksBTN,SandwichBTN,PizzaBTN,BurgerBTN;
    private TextView DrinksTitle,SandwichTitle,PizzaTitle,BurgerTitle;
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
        DrinksBTN = view.findViewById(R.id.DrinksBTN);
        SandwichBTN = view.findViewById(R.id.SandwichBTN);
        PizzaBTN = view.findViewById(R.id.PizzaBTN);
        BurgerBTN = view.findViewById(R.id.BurgerBTN);
        DrinksTitle = view.findViewById(R.id.DrinksTitle);
        SandwichTitle = view.findViewById(R.id.SandwichTitle);
        PizzaTitle = view.findViewById(R.id.PizzaTitle);
        BurgerTitle = view.findViewById(R.id.BurgerTitle);
        RefProduct = FirebaseDatabase.getInstance(getContext().getString(R.string.DBURL))
                .getReference().child("Products");
    }
    private void fetchDataFromDB(){
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<Product> AnnonceProducts = new ArrayList<>();
        ArrayList<Product> BurgerProducts = new ArrayList<>();
        ArrayList<Product> PizzaProducts = new ArrayList<>();
        ArrayList<Product> SandwichProducts = new ArrayList<>();
        ArrayList<Product> DrinksProducts = new ArrayList<>();

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
                    if (product != null && (product.getCategory().equals("Burger") || product.getCategory().equals("burger"))) {
                        // If the condition is true, add the product to the filtered list
                        BurgerProducts.add(product);
                    }
                    if (product != null && (product.getCategory().equals("Pizza") || product.getCategory().equals("pizza"))) {
                        // If the condition is true, add the product to the filtered list
                        PizzaProducts.add(product);
                    }
                    if (product != null && (product.getCategory().equals("Sandwich") || product.getCategory().equals("sandwich"))) {
                        // If the condition is true, add the product to the filtered list
                        SandwichProducts.add(product);
                    }
                    if (product != null && (product.getCategory().equals("Drink") || product.getCategory().equals("drink"))) {
                        // If the condition is true, add the product to the filtered list
                        DrinksProducts.add(product);
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
                    if (BurgerProducts.isEmpty()){
                        BurgerBTN.setVisibility(View.GONE);
                    }else{
                        BurgerBTN.setVisibility(View.VISIBLE);
                        BurgerBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // show the content by category
                                BurgerBTN.setCardBackgroundColor(primaryColor);
                                BurgerTitle.setTextColor(whiteColor);
                                PizzaBTN.setCardBackgroundColor(whiteColor);
                                PizzaTitle.setTextColor(primaryTextColor);
                                SandwichBTN.setCardBackgroundColor(whiteColor);
                                SandwichTitle.setTextColor(primaryTextColor);
                                DrinksBTN.setCardBackgroundColor(whiteColor);
                                DrinksTitle.setTextColor(primaryTextColor);

                                adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),BurgerProducts);
                                ProductRecyclerView.setAdapter(adminpostType1Adapter);
                            }
                        });
                    }
                    if (PizzaProducts.isEmpty()){
                        PizzaBTN.setVisibility(View.GONE);
                    }else{
                        PizzaBTN.setVisibility(View.VISIBLE);
                        PizzaBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // show the content by category
                                PizzaBTN.setCardBackgroundColor(primaryColor);
                                PizzaTitle.setTextColor(whiteColor);
                                BurgerBTN.setCardBackgroundColor(whiteColor);
                                BurgerTitle.setTextColor(primaryTextColor);
                                SandwichBTN.setCardBackgroundColor(whiteColor);
                                SandwichTitle.setTextColor(primaryTextColor);
                                DrinksBTN.setCardBackgroundColor(whiteColor);
                                DrinksTitle.setTextColor(primaryTextColor);

                                adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),PizzaProducts);
                                ProductRecyclerView.setAdapter(adminpostType1Adapter);
                            }
                        });
                    }
                    if (SandwichProducts.isEmpty()){
                        SandwichBTN.setVisibility(View.GONE);
                    }else{
                        SandwichBTN.setVisibility(View.VISIBLE);
                        SandwichBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // show the content by category
                                SandwichBTN.setCardBackgroundColor(primaryColor);
                                SandwichTitle.setTextColor(whiteColor);
                                PizzaBTN.setCardBackgroundColor(whiteColor);
                                PizzaTitle.setTextColor(primaryTextColor);
                                BurgerBTN.setCardBackgroundColor(whiteColor);
                                BurgerTitle.setTextColor(primaryTextColor);
                                DrinksBTN.setCardBackgroundColor(whiteColor);
                                DrinksTitle.setTextColor(primaryTextColor);

                                adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),SandwichProducts);
                                ProductRecyclerView.setAdapter(adminpostType1Adapter);
                            }
                        });
                    }
                    if (DrinksProducts.isEmpty()){
                        DrinksBTN.setVisibility(View.GONE);
                    }else {
                        DrinksBTN.setVisibility(View.VISIBLE);
                        DrinksBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DrinksBTN.setCardBackgroundColor(primaryColor);
                                DrinksTitle.setTextColor(whiteColor);
                                PizzaBTN.setCardBackgroundColor(whiteColor);
                                PizzaTitle.setTextColor(primaryTextColor);
                                SandwichBTN.setCardBackgroundColor(whiteColor);
                                SandwichTitle.setTextColor(primaryTextColor);
                                BurgerBTN.setCardBackgroundColor(whiteColor);
                                BurgerTitle.setTextColor(primaryTextColor);

                                adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),DrinksProducts);
                                ProductRecyclerView.setAdapter(adminpostType1Adapter);
                            }
                        });
                    }
                    if (!BurgerProducts.isEmpty()){
                        BurgerBTN.setCardBackgroundColor(primaryColor);
                        BurgerTitle.setTextColor(whiteColor);
                        PizzaBTN.setCardBackgroundColor(whiteColor);
                        PizzaTitle.setTextColor(primaryTextColor);
                        SandwichBTN.setCardBackgroundColor(whiteColor);
                        SandwichTitle.setTextColor(primaryTextColor);
                        DrinksBTN.setCardBackgroundColor(whiteColor);
                        DrinksTitle.setTextColor(primaryTextColor);

                        adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),BurgerProducts);
                        ProductRecyclerView.setAdapter(adminpostType1Adapter);
                    }else if (!PizzaProducts.isEmpty()){
                        PizzaBTN.setCardBackgroundColor(primaryColor);
                        PizzaTitle.setTextColor(whiteColor);
                        BurgerBTN.setCardBackgroundColor(whiteColor);
                        BurgerTitle.setTextColor(primaryTextColor);
                        SandwichBTN.setCardBackgroundColor(whiteColor);
                        SandwichTitle.setTextColor(primaryTextColor);
                        DrinksBTN.setCardBackgroundColor(whiteColor);
                        DrinksTitle.setTextColor(primaryTextColor);

                        adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),PizzaProducts);
                        ProductRecyclerView.setAdapter(adminpostType1Adapter);
                    }else if (!SandwichProducts.isEmpty()){
                        SandwichBTN.setCardBackgroundColor(primaryColor);
                        SandwichTitle.setTextColor(whiteColor);
                        PizzaBTN.setCardBackgroundColor(whiteColor);
                        PizzaTitle.setTextColor(primaryTextColor);
                        BurgerBTN.setCardBackgroundColor(whiteColor);
                        BurgerTitle.setTextColor(primaryTextColor);
                        DrinksBTN.setCardBackgroundColor(whiteColor);
                        DrinksTitle.setTextColor(primaryTextColor);

                        adminpostType1Adapter = new AdminPostType1Adapter(getActivity(),SandwichProducts);
                        ProductRecyclerView.setAdapter(adminpostType1Adapter);
                    }else if (!DrinksProducts.isEmpty()){
                        DrinksBTN.setCardBackgroundColor(primaryColor);
                        DrinksTitle.setTextColor(whiteColor);
                        PizzaBTN.setCardBackgroundColor(whiteColor);
                        PizzaTitle.setTextColor(primaryTextColor);
                        SandwichBTN.setCardBackgroundColor(whiteColor);
                        SandwichTitle.setTextColor(primaryTextColor);
                        BurgerBTN.setCardBackgroundColor(whiteColor);
                        BurgerTitle.setTextColor(primaryTextColor);

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