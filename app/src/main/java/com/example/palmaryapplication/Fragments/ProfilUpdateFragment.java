package com.example.palmaryapplication.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.palmaryapplication.Models.User;
import com.example.palmaryapplication.R;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfilUpdateFragment extends Fragment {
    private View view;
    private ImageView ProfileIMGOutPut, BackBTN;
    private EditText ClientFullName, ClientPhoneNumber;
    private TextView ClientEmail;
    private CardView UpdateIMGBTN, DeleteIMGBTN;
    private MaterialCardView UpdateProfilBTN;
    private DatabaseReference Refuser;
    private FirebaseAuth Auth;
    private Uri image_file;
    private Dialog dialog;
    private StorageReference UsersImgref;
    private String ImageURL;
    private User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profil_update, container, false);

        //init
        InisializationOfFealds();
        openGalleryResult();
        fetchDataFromDB();
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait1);
        dialog.setCanceledOnTouchOutside(false);

        BackBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        //update profile
        UpdateProfilBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNotEmpty()){
                    dialog.show();
                    if (image_file != null) {
                        String id = GenerateID();
                        UsersImgref.child(id+".jpeg").putFile(image_file)
                                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()){
                                            UsersImgref.child(id+".jpeg").getDownloadUrl()
                                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            ImageURL = uri.toString();
                                                            Map<String, Object> updates = new HashMap<>();
                                                            updates.put("phone", ClientPhoneNumber.getText().toString());
                                                            updates.put("fullName", ClientFullName.getText().toString());
                                                            updates.put("img", ImageURL);
                                                            updates.put("imgRef",id);
                                                            if (user.getImgRef() != null){
                                                                UsersImgref.child(user.getImgRef()+".jpeg").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            updateImageIntoDB(updates);
                                                                        }else {
                                                                            Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }else {
                                                                updateImageIntoDB(updates);
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
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("phone", ClientPhoneNumber.getText().toString());
                        updates.put("fullName", ClientFullName.getText().toString());
                        updateImageIntoDB(updates);
                    }
                }
            }
        });

        //delete profile image
        DeleteIMGBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null && user.getIMG() != null){
                    AlertDialog.Builder mydialog = new AlertDialog.Builder(getContext());
                    mydialog.setTitle("Supprimer votre photo de profile");
                    mydialog.setMessage("Voulez-vous vraiment supprimer votre photo de profile ?");
                    mydialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // deleting the profil IMG
                            dialog.show();
                            DeleteImagefromDB();
                            ProfileIMGOutPut.setImageDrawable(getActivity().getDrawable(R.drawable.profilavatar));
                        }
                    });
                    mydialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    mydialog.show();
                }
            }
        });
        return view;
    }
    private void InisializationOfFealds(){
        UpdateProfilBTN = view.findViewById(R.id.UpdateProfilBTN);
        ClientEmail = view.findViewById(R.id.ClientEmail);
        ClientPhoneNumber = view.findViewById(R.id.ClientPhoneNumber);
        ClientFullName = view.findViewById(R.id.ClientFullName);
        ProfileIMGOutPut = view.findViewById(R.id.ProfileIMGOutPut);
        UpdateIMGBTN = view.findViewById(R.id.UpdateIMGBTN);
        DeleteIMGBTN = view.findViewById(R.id.DeleteIMGBTN);
        BackBTN = view.findViewById(R.id.BackBTN);
        Auth = FirebaseAuth.getInstance();
        Refuser = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Users")
                .child(Auth.getCurrentUser().getUid());
        UsersImgref = FirebaseStorage.getInstance().getReference()
                .child("UsersImages");
    }
    private void openGalleryResult(){
        ActivityResultLauncher<Intent> openGalleryResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            image_file = data.getData();
                            float desiredWidthDp = 150; // Desired width in dp
                            float desiredHeightDp = 100; // Desired height in dp

                            // Convert dp to pixels based on the device's screen density
                            float density = getResources().getDisplayMetrics().density;
                            int desiredWidthPx = (int) (desiredWidthDp * density);
                            int desiredHeightPx = (int) (desiredHeightDp * density);

                            // Resize or downscale the image before setting it to the ImageView
                            Bitmap resizedBitmap = resizeImage(image_file, desiredWidthPx, desiredHeightPx);

                            if (resizedBitmap != null) {
                                // Set the resized bitmap to the ImageView
                                ProfileIMGOutPut.setImageBitmap(resizedBitmap);
                            } else {
                                // Handle the case where resizing failed
                                Toast.makeText(getActivity(),"Le redimensionnement a échoué.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        UpdateIMGBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryResult.launch(OpenGalery());
            }
        });
    }
    private Bitmap resizeImage(Uri imageUri, int desiredWidth, int desiredHeight) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            // Calculate an appropriate inSampleSize value
            options.inSampleSize = calculateInSampleSize(options, desiredWidth, desiredHeight);

            inputStream.close();
            inputStream = requireContext().getContentResolver().openInputStream(imageUri);

            // Decode the bitmap with the calculated inSampleSize
            options.inJustDecodeBounds = false;
            Bitmap resizedBitmap = BitmapFactory.decodeStream(inputStream, null, options);

            inputStream.close();

            return resizedBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private int calculateInSampleSize(BitmapFactory.Options options, int desiredWidth, int desiredHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (height > desiredHeight || width > desiredWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= desiredHeight && (halfWidth / inSampleSize) >= desiredWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    private Intent OpenGalery(){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        return i;
    }
    private String GenerateID(){
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyyMMdd");
        String saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH : MM : ss a");
        String SaveCurrentTime = currentTime.format(calForDate.getTime());
        return saveCurrentDate+SaveCurrentTime;
    }
    private boolean isNotEmpty(){
        if (ClientFullName.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Entrez votre nom", Toast.LENGTH_SHORT).show();
            return false;
        }else if (ClientPhoneNumber.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Entrez votre numero de telephone", Toast.LENGTH_SHORT).show();
            return false;
        }else if (ClientEmail.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Entrez votre email", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }
    protected void updateImageIntoDB(Map<String, Object> product){
        Refuser.updateChildren(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "profile a été modifié", Toast.LENGTH_SHORT).show();
                        }else {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void fetchDataFromDB(){
        Refuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user != null) {
                    if (user.getPhone() != null && user.getFullName() != null){
                        ClientPhoneNumber.setText(user.getPhone().toString());
                        ClientFullName.setText(user.getFullName().toString());
                        ClientEmail.setText(user.getEmail().toString());
                        if (user.getIMG() != null){
                            Picasso.get().load(user.getIMG()).into(ProfileIMGOutPut);
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
    protected void DeleteImagefromDB(){
        UsersImgref.child(user.getImgRef()+".jpeg").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Refuser.child("img").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Refuser.child("imgRef").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            dialog.dismiss();
                                            Toast.makeText(getActivity(), "Photo de profile a ete supprimée", Toast.LENGTH_SHORT).show();
                                            fetchDataFromDB();
                                        }else {
                                            Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}