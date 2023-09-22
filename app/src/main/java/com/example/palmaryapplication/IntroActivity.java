package com.example.palmaryapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class IntroActivity extends AppCompatActivity {
    FirebaseAuth Auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Auth = FirebaseAuth.getInstance();
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1000);
                }catch (Exception e){

                }finally {
                    if (Auth.getCurrentUser() != null){
                        if (Auth.getCurrentUser().getEmail().equals("FratelloFood@admin.com") || Auth.getCurrentUser().getEmail().equals("fratellofood@admin.com")){
                            Intent i = new Intent(IntroActivity.this,AdminMainActivity.class);
                            startActivity(i);
                            finish();
                        }else {
                            Intent i = new Intent(IntroActivity.this,MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }else {
                        Intent i = new Intent(IntroActivity.this,AuthentificationActivity.class);
                        startActivity(i);
                        finish();
                    }

                }
            }
        };
        thread.start();
    }

}