package com.example.palmaryapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.palmaryapplication.AdminMainActivity;
import com.example.palmaryapplication.MainActivity;
import com.example.palmaryapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInFragment extends Fragment {
    private View view;
    private ImageView BackBTN;
    private TextView ToSignUpInterface;
    private EditText PasswordInPut, EmailInPut;
    private MaterialCardView LogInBTN;
    private FirebaseAuth Auth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_log_in, container, false);
        InisializationOfFealds();
        ButtonRedirection();
        LogInBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNotEmpty()){
                    isSaveInDB();
                }
            }
        });
        return view;
    }
    private void InisializationOfFealds(){
        BackBTN = view.findViewById(R.id.BackBTN);
        ToSignUpInterface = view.findViewById(R.id.ToSignUpInterface);
        LogInBTN = view.findViewById(R.id.LogInBTN);
        PasswordInPut = view.findViewById(R.id.PasswordInPut);
        EmailInPut = view.findViewById(R.id.EmailInPut);
        Auth = FirebaseAuth.getInstance();
    }
    private void ButtonRedirection(){
        BackBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        ToSignUpInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                        .replace(R.id.AuthentificationFragmentContainer, new SingupFragment())
                        .addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }
    private boolean isNotEmpty(){
        if (EmailInPut.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Entrer votre Email", Toast.LENGTH_SHORT).show();
            return false;
        }else if (PasswordInPut.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Entrer votre Mot de passe", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }
    private void isSaveInDB(){
        String my_email = EmailInPut.getText().toString();
        String pwd = PasswordInPut.getText().toString();

        Auth.signInWithEmailAndPassword(my_email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    if (my_email.equals("FratelloFood@admin.com") || my_email.equals("fratellofood@admin.com")){
                        Intent i = new Intent(getActivity(), AdminMainActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }else {
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }

                }else {
                    Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}