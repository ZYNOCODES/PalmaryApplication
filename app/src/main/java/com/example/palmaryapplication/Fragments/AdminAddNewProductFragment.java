package com.example.palmaryapplication.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.palmaryapplication.Models.Product;
import com.example.palmaryapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AdminAddNewProductFragment extends Fragment {
    private View view;
    private ImageView AddIMGInPut;
    private EditText NameInPut, PriceInPut, IngredientsInPut, CategoryInPut, AboutInPut;
    private CheckBox AnnonceCheckBox;
    private MaterialCardView AddNewProductBTN;
    private Uri image_file;
    private String ImageURL;
    private StorageReference ProductImgref;
    private DatabaseReference RefProduct;
    private Dialog dialog;
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SimpleCropImg";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_add_new_product, container, false);

        //init
        InisializationOfFealds();
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait1);
        dialog.setCanceledOnTouchOutside(false);
        //crop IMG
        AddIMGInPut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"),CODE_IMG_GALLERY);
            }
        });
        //add new product
        AddNewProductBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNotEmpty()){
                    dialog.show();
                    String id = GenerateID();
                    ProductImgref.child(id+".jpeg").putFile(image_file)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()){
                                        ProductImgref.child(id+".jpeg").getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        ImageURL = uri.toString();
                                                        RefProduct = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                                                                .getReference().child("Products");
                                                        RefProduct = RefProduct.push();
                                                        String idd = RefProduct.getKey();
                                                        if (AnnonceCheckBox.isChecked()){
                                                            Product product = new Product(idd,NameInPut.getText().toString(),PriceInPut.getText().toString(),
                                                                    IngredientsInPut.getText().toString(),CategoryInPut.getText().toString(),AboutInPut.getText().toString(),ImageURL,id,true);
                                                            saveProductIntoDB(product);
                                                        }else {
                                                            Product product = new Product(idd,NameInPut.getText().toString(),PriceInPut.getText().toString(),
                                                                    IngredientsInPut.getText().toString(),CategoryInPut.getText().toString(),AboutInPut.getText().toString(),ImageURL,id,false);
                                                            saveProductIntoDB(product);
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
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_IMG_GALLERY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    startCrop(imageUri);
                }
            }
        }else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK){
            if (data != null) {
                image_file = UCrop.getOutput(data);
                if (image_file != null){
                    AddIMGInPut.setImageURI(image_file);
                }
            }

        }
    }

    private void InisializationOfFealds(){
        AddIMGInPut = view.findViewById(R.id.AddIMGInPut);
        NameInPut = view.findViewById(R.id.NameInPut);
        PriceInPut = view.findViewById(R.id.PriceInPut);
        IngredientsInPut = view.findViewById(R.id.IngredientsInPut);
        CategoryInPut = view.findViewById(R.id.CategoryInPut);
        AboutInPut = view.findViewById(R.id.AboutInPut);
        AnnonceCheckBox = view.findViewById(R.id.AnnonceCheckBox);
        AddNewProductBTN = view.findViewById(R.id.AddNewProductBTN);
        RefProduct = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference().child("Products");
        ProductImgref = FirebaseStorage.getInstance().getReference()
                .child("ProductImages");
    }
    private void startCrop(@NonNull Uri uri){
        String destinationFileName = ".jpeg";
        Uri destinationUri = Uri.fromFile(new File(requireActivity().getCacheDir(), destinationFileName));

        UCrop uCrop = UCrop.of(uri, destinationUri);
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(500, 500);
        uCrop.withOptions(getCropOptions());
        uCrop.start(requireContext(), AdminAddNewProductFragment.this);
    }
    private UCrop.Options getCropOptions(){
        UCrop.Options options = new UCrop.Options();
        //Quality
        options.setCompressionQuality(70);
        //format
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        //UI
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        //colors
        options.setStatusBarColor(getResources().getColor(R.color.PrimaryColor));
        options.setToolbarColor(getResources().getColor(R.color.SecondColor));

        return options;
    }
    private boolean isNotEmpty(){
        if (NameInPut.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Entrez le nom du produit", Toast.LENGTH_SHORT).show();
            return false;
        }else if (PriceInPut.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Entrez le prix du produit", Toast.LENGTH_SHORT).show();
            return false;
        }else if (IngredientsInPut.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Entrez les ingredients du produit", Toast.LENGTH_SHORT).show();
            return false;
        }else if (CategoryInPut.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Entrez la catégorie de produit", Toast.LENGTH_SHORT).show();
            return false;
        }else if (AboutInPut.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Entrez la description du produit", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }
    private String GenerateID(){
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyyMMdd");
        String saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH : MM : ss a");
        String SaveCurrentTime = currentTime.format(calForDate.getTime());
        return saveCurrentDate+SaveCurrentTime;
    }
    protected void saveProductIntoDB(Product product){
        RefProduct.setValue(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Le produit a été ajoutée", Toast.LENGTH_SHORT).show();
                        }else {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}