package com.example.palmaryapplication;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.palmaryapplication.Models.Cart;
import com.example.palmaryapplication.Models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DisplayProductActivity extends AppCompatActivity {
    private CardView PlusBTN,LessBTN;
    private MaterialCardView AddToCartBTN;
    private EditText ModificationInPut,QuantityOutPut;
    private TextView AboutOutPut,TitleOutPut,PriceOutPut;
    private ImageView IMGOutPut,BackBTN;
    private String ProductID;
    private DatabaseReference RefProduct,RefCart;
    private FirebaseAuth Auth;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_product);

        //init
        InisializationOfFealds();
        ButtonsRediraction();
        dialog = new Dialog(DisplayProductActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait1);
        dialog.setCanceledOnTouchOutside(false);

        //get product details
        RefProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                AboutOutPut.setText(product.getDescription());
                TitleOutPut.setText(product.getName());
                PriceOutPut.setText(product.getPrice());
                Picasso.get().load(product.getIMG()).into(IMGOutPut);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", "Operation canceled", error.toException());
                Toast.makeText(DisplayProductActivity.this, "Database operation canceled: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        AddToCartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                RefProduct.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product product = snapshot.getValue(Product.class);
                        RefCart = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                                .getReference()
                                .child("Users")
                                .child(Auth.getCurrentUser().getUid())
                                .child("Cart");
                        RefCart = RefCart.push();
                        String idd = RefCart.getKey();
                        Cart cart = new Cart(idd,product,QuantityOutPut.getText().toString());
                        RefCart.setValue(cart)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            dialog.dismiss();
                                            Toast.makeText(DisplayProductActivity.this, "La commande a été ajoutée au panier", Toast.LENGTH_SHORT).show();
                                        }else {
                                            dialog.dismiss();
                                            Toast.makeText(DisplayProductActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DatabaseError", "Operation canceled", error.toException());
                        Toast.makeText(DisplayProductActivity.this, "Database operation canceled: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void InisializationOfFealds(){
        AddToCartBTN = findViewById(R.id.AddToCartBTN);
        PlusBTN = findViewById(R.id.PlusBTN);
        LessBTN = findViewById(R.id.LessBTN);
        QuantityOutPut = findViewById(R.id.QuantityOutPut);

        AboutOutPut = findViewById(R.id.AboutOutPut);
        TitleOutPut = findViewById(R.id.TitleOutPut);
        PriceOutPut = findViewById(R.id.PriceOutPut);
        IMGOutPut = findViewById(R.id.IMGOutPut);
        BackBTN = findViewById(R.id.BackBTN);
        ProductID = getIntent().getStringExtra("ProductID");
        Auth = FirebaseAuth.getInstance();
        RefProduct = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Products")
                .child(ProductID);
        RefCart = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Users")
                .child(Auth.getCurrentUser().getUid())
                .child("Cart");
    }
    private void ButtonsRediraction(){
        PlusBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(Integer.parseInt(QuantityOutPut.getText().toString()) < 0)) {
                    int result = Integer.parseInt(QuantityOutPut.getText().toString()) + 1;
                    QuantityOutPut.setText(String.valueOf(result));
                }else {
                    QuantityOutPut.setText("0");
                }
            }
        });
        LessBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(Integer.parseInt(QuantityOutPut.getText().toString()) <= 0)) {
                    int result = Integer.parseInt(QuantityOutPut.getText().toString()) - 1;
                    QuantityOutPut.setText(String.valueOf(result));
                }else {
                    QuantityOutPut.setText("0");
                }
            }
        });
        BackBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}