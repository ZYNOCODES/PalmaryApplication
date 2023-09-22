package com.example.palmaryapplication.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
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

import com.example.palmaryapplication.Adapters.CartAdapter;
import com.example.palmaryapplication.Interfaces.IconColorChangeListener;
import com.example.palmaryapplication.Models.Cart;
import com.example.palmaryapplication.Models.Localisation;
import com.example.palmaryapplication.Models.Order;
import com.example.palmaryapplication.Models.User;
import com.example.palmaryapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment {
    private View view;
    private Animation vibrate;
    private MaterialCardView DeleveryNotes, PlaceMYOrderBTN;
    private CheckBox DeleveryCheckBox;
    private DatabaseReference RefCart, RefOrder, Refuser, RefOrderConfirmation;
    private FirebaseAuth Auth;
    private CartAdapter cartAdapter;
    private RecyclerView CartRecyclerView;
    private LinearLayout AddToCartBTN;
    private TextView DeliveryCartPrice, TotleCartPrice, TotleItemsPrice, LocationOutPut, ClientPhoneNumber, ClientFullName;
    private ImageView CopyBTN;
    private LinearLayout LocationContainer;
    private ArrayList<Cart> products;
    private IconColorChangeListener iconColorChangeListener;
    private Dialog dialog;
    private EditText DeleveryNotesInPut;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Localisation UserLocation;
    private  final  static int REQUEST_CODE=100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);

        //init
        InisializationOfFealds();
        ButtonRedirection();
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait1);
        dialog.setCanceledOnTouchOutside(false);

        //recycler view
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        CartRecyclerView.setLayoutManager(manager);

        //fetch data
        fetchDataFromDB();

        return view;
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

    private void InisializationOfFealds(){
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getActivity());
        DeleveryNotes = view.findViewById(R.id.DeleveryNotes);
        DeleveryCheckBox = view.findViewById(R.id.DeleveryCheckBox);
        CartRecyclerView = view.findViewById(R.id.CartRecyclerView);
        AddToCartBTN = view.findViewById(R.id.AddToCartBTN);
        PlaceMYOrderBTN = view.findViewById(R.id.PlaceMYOrderBTN);
        DeliveryCartPrice = view.findViewById(R.id.DeliveryCartPrice);
        TotleCartPrice = view.findViewById(R.id.TotleCartPrice);
        TotleItemsPrice = view.findViewById(R.id.TotleItemsPrice);
        LocationOutPut = view.findViewById(R.id.LocationOutPut);
        DeleveryNotesInPut = view.findViewById(R.id.DeleveryNotesInPut);
        ClientPhoneNumber = view.findViewById(R.id.ClientPhoneNumber);
        ClientFullName = view.findViewById(R.id.ClientFullName);
        CopyBTN = view.findViewById(R.id.CopyBTN);
        myClipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        LocationContainer = view.findViewById(R.id.LocationContainer);
        LocationContainer.setVisibility(View.GONE);
        vibrate = AnimationUtils.loadAnimation(getActivity(), R.anim.vibrate);
        Auth = FirebaseAuth.getInstance();
        RefCart = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Users")
                .child(Auth.getCurrentUser().getUid())
                .child("Cart");
        RefOrder = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Orders");
        Refuser = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Users")
                .child(Auth.getCurrentUser().getUid());
        RefOrderConfirmation = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Users")
                .child(Auth.getCurrentUser().getUid())
                .child("Orders");
    }
    private void ButtonRedirection(){
        DeleveryCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    if (DeleveryCheckBox.isChecked()){
                        getLastLocation();
                    }else {
                        LocationContainer.setVisibility(View.GONE);
                    }
                }else {
                    DeleveryCheckBox.setChecked(false);
                    getLastLocation();
                }

            }
        });
        AddToCartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                        .replace(R.id.MainFragmentContainer, new HomeFragment());
                fragmentTransaction.commit();
                // Inside your method where you want to change the icon colors
                iconColorChangeListener.changeIconColors();
            }
        });
        PlaceMYOrderBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (products.size() > 0){
                    RefOrder = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                            .getReference()
                            .child("Orders");
                    RefOrder = RefOrder.push();
                    String idd = RefOrder.getKey();
                    if (!DeleveryCheckBox.isChecked()){
                        Order order = new Order(idd,"a domicile",String.valueOf(TotleCartPrice.getText()),Auth.getCurrentUser().getUid(),false,products);
                        saveOrderIntoDB(order);
                    }else {
                        if (UserLocation != null){
                            Order order = new Order(idd,"Livraison",UserLocation,String.valueOf(DeleveryNotesInPut.getText()),
                                    String.valueOf(TotleCartPrice.getText()),Auth.getCurrentUser().getUid(),false,products);
                            saveOrderIntoDB(order);
                        }else {
                            Toast.makeText(getActivity(), "Localisation est requis", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        CopyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!LocationOutPut.getText().toString().isEmpty()){
                    myClip = ClipData.newPlainText("text", LocationOutPut.getText().toString());
                    myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(getActivity(), "Location Copied",Toast.LENGTH_SHORT).show();
                }
                CopyBTN.startAnimation(vibrate);
            }
        });
    }
    private void fetchDataFromDB(){
        products = new ArrayList<>();
        RefCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                final int[] totalItem = {0};
                int DeliveryPrice = 200;
                for (DataSnapshot oneSnapshot : snapshot.getChildren()){
                    Cart product = oneSnapshot.getValue(Cart.class);
                    if (product != null) {
                        totalItem[0] = totalItem[0] + Integer.parseInt(product.getProduct().getPrice())*Integer.parseInt(product.getQuantity());
                    }
                    products.add(product);
                }
                TotleItemsPrice.setText(String.valueOf(totalItem[0]));
                DeliveryCartPrice.setText(String.valueOf(DeliveryPrice));
                TotleCartPrice.setText(String.valueOf(totalItem[0]+DeliveryPrice));
                cartAdapter = new CartAdapter(getActivity(), products);
                CartRecyclerView.setAdapter(cartAdapter);
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
                        ClientPhoneNumber.setText(String.valueOf(user.getPhone()));
                        ClientFullName.setText(String.valueOf(user.getFullName()));
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
    protected void saveOrderIntoDB(Order order){
        RefOrder.setValue(order)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            RefOrderConfirmation = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                                    .getReference()
                                    .child("Users")
                                    .child(Auth.getCurrentUser().getUid())
                                    .child("Orders");
                            RefOrderConfirmation.child(order.getID()).setValue(order)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                dialog.dismiss();
                                                //clear cart
                                                RefCart.removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(getActivity(), "La commande a été ajoutée", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }else {
                                                dialog.dismiss();
                                                Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }else {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
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
                                    UserLocation = new Localisation(addresses.get(0).getAddressLine(0),addresses.get(0).getLocality(),
                                            String.valueOf(addresses.get(0).getLatitude()),String.valueOf(addresses.get(0).getLongitude()));
                                    LocationOutPut.setText(addresses.get(0).getAddressLine(0));
                                    //set locationcontainer visible
                                    DeleveryCheckBox.setChecked(true);
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
        DeleveryCheckBox.startAnimation(vibrate);
    }
}