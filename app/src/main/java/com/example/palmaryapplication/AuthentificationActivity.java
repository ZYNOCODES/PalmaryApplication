package com.example.palmaryapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.palmaryapplication.Fragments.RegistrationFragment;

public class AuthentificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.AuthentificationFragmentContainer,new RegistrationFragment()).commit();
    }
}