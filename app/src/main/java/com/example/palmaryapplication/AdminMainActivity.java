package com.example.palmaryapplication;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.palmaryapplication.Fragments.AdminAddNewProductFragment;
import com.example.palmaryapplication.Fragments.AdminAllOrdersFragment;
import com.example.palmaryapplication.Fragments.AdminHomeFragment;
import com.example.palmaryapplication.Fragments.AdminOrdersFragment;

public class AdminMainActivity extends AppCompatActivity {
    private ImageView Ic_Home,Ic_AllOrders,Ic_NewOrders,Ic_NewProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        InisializationOfFealds();
        IconsOnClickListener();
    }
    private void InisializationOfFealds(){
        Ic_Home = findViewById(R.id.Ic_Home);
        Ic_AllOrders = findViewById(R.id.Ic_AllOrders);
        Ic_NewOrders = findViewById(R.id.Ic_NewOrders);
        Ic_NewProduct = findViewById(R.id.Ic_NewProduct);
    }
    private  void IconsOnClickListener(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.AdminMainFragmentContainer,new AdminHomeFragment()).commit();
        Ic_Home.setColorFilter(getColor(R.color.PrimaryColor), PorterDuff.Mode.SRC_IN);

        Ic_Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.AdminMainFragmentContainer,new AdminHomeFragment())
                        .commit();
                Ic_Home.setColorFilter(getColor(R.color.PrimaryColor), PorterDuff.Mode.SRC_IN);
                Ic_AllOrders.setColorFilter(getColor(R.color.Gray), PorterDuff.Mode.SRC_IN);
                Ic_NewOrders.setColorFilter(getColor(R.color.Gray),PorterDuff.Mode.SRC_IN);
                Ic_NewProduct.setColorFilter(getColor(R.color.Gray), PorterDuff.Mode.SRC_IN);
            }
        });
        Ic_NewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.AdminMainFragmentContainer,new AdminOrdersFragment())
                        .commit();
                Ic_Home.setColorFilter(getColor(R.color.Gray), PorterDuff.Mode.SRC_IN);
                Ic_AllOrders.setColorFilter(getColor(R.color.Gray), PorterDuff.Mode.SRC_IN);
                Ic_NewOrders.setColorFilter(getColor(R.color.PrimaryColor),PorterDuff.Mode.SRC_IN);
                Ic_NewProduct.setColorFilter(getColor(R.color.Gray), PorterDuff.Mode.SRC_IN);
            }
        });
        Ic_NewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.AdminMainFragmentContainer,new AdminAddNewProductFragment())
                        .commit();
                Ic_Home.setColorFilter(getColor(R.color.Gray), PorterDuff.Mode.SRC_IN);
                Ic_AllOrders.setColorFilter(getColor(R.color.Gray), PorterDuff.Mode.SRC_IN);
                Ic_NewOrders.setColorFilter(getColor(R.color.Gray),PorterDuff.Mode.SRC_IN);
                Ic_NewProduct.setColorFilter(getColor(R.color.PrimaryColor), PorterDuff.Mode.SRC_IN);
            }
        });
        Ic_AllOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.AdminMainFragmentContainer,new AdminAllOrdersFragment())
                        .commit();
                Ic_Home.setColorFilter(getColor(R.color.Gray), PorterDuff.Mode.SRC_IN);
                Ic_AllOrders.setColorFilter(getColor(R.color.PrimaryColor), PorterDuff.Mode.SRC_IN);
                Ic_NewOrders.setColorFilter(getColor(R.color.Gray),PorterDuff.Mode.SRC_IN);
                Ic_NewProduct.setColorFilter(getColor(R.color.Gray), PorterDuff.Mode.SRC_IN);
            }
        });
    }
}