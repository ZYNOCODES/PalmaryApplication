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
import com.example.palmaryapplication.Models.User;
import com.example.palmaryapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SingupFragment extends Fragment {
    private View view;
    private ImageView BackBTN;
    private TextView ToSignInInterface;
    private EditText FullNameInPut,PhoneInPut,EmailInPut,PassordInPut;
    private MaterialCardView SignUpBTN;
    private FirebaseAuth Auth;
    private DatabaseReference RefUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_singup, container, false);
        InisializationOfFealds();
        ButtonRedirection();
        SignUpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNotEmpty()){
                    createAnAccount();
                }
            }
        });
        return view;
    }
    private void InisializationOfFealds(){
        BackBTN = view.findViewById(R.id.BackBTN);
        ToSignInInterface = view.findViewById(R.id.ToSignInInterface);
        FullNameInPut = view.findViewById(R.id.FullNameInPut);
        PhoneInPut = view.findViewById(R.id.PhoneInPut);
        EmailInPut = view.findViewById(R.id.EmailInPut);
        PassordInPut = view.findViewById(R.id.PassordInPut);
        SignUpBTN = view.findViewById(R.id.SignUpBTN);

        Auth = FirebaseAuth.getInstance();
        RefUser = FirebaseDatabase.getInstance(getString(R.string.DBURL)).getReference().child("Users");
    }
    private void ButtonRedirection(){
        BackBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        ToSignInInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                        .replace(R.id.AuthentificationFragmentContainer, new LogInFragment())
                        .addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
    private boolean isNotEmpty(){
        if (EmailInPut.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Entrer votre Email", Toast.LENGTH_SHORT).show();
            return false;
        }else if (PassordInPut.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Entrer votre Mot de pass", Toast.LENGTH_SHORT).show();
            return false;
        }else if (FullNameInPut.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Entrer votre Nom complet", Toast.LENGTH_SHORT).show();
            return false;
        }else if (PhoneInPut.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Entrer votre Numero de telephone", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }
    private void createAnAccount(){
        String my_email = EmailInPut.getText().toString();
        String pwd = PassordInPut.getText().toString();

        Auth.createUserWithEmailAndPassword(my_email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    User user = new User(EmailInPut.getText().toString(),FullNameInPut.getText().toString(),PhoneInPut.getText().toString());
                    saveUserInDB(user);
                }else {
                    Toast.makeText(getActivity(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void saveUserInDB(User user){
        RefUser.child(Auth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (user.getEmail().equals("Palmary@admin.com") || user.getEmail().equals("palmary@admin.com")){
                        Intent i = new Intent(getActivity(), AdminMainActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }else {
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }
                }else {
                    Toast.makeText(getActivity(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}