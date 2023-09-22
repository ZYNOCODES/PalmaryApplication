package com.example.palmaryapplication;

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
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.palmaryapplication.Models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
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

public class AdminDisplayProductActivity extends AppCompatActivity {
    private MaterialCardView UpdateBTN;
    private EditText AboutOutPut,TitleOutPut,PriceOutPut;
    private ImageView IMGOutPut,BackBTN;
    private CardView UpdateAboutBTN,UpdatePriceBTN,UpdateTitleBTN,UpdateIMGBTN;
    private Uri image_file;
    private String ProductID;
    private DatabaseReference RefProduct;
    private StorageReference ProductImgref;
    private Dialog dialog;
    private String ImageURL;
    private Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_display_product);

        //init
        InisializationOfFealds();
        ButtonsRediraction();
        openGalleryResult();
        dialog = new Dialog(AdminDisplayProductActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait1);
        dialog.setCanceledOnTouchOutside(false);

        //get product details
        RefProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                product = snapshot.getValue(Product.class);
                if (product != null){
                    AboutOutPut.setText(product.getDescription());
                    TitleOutPut.setText(product.getName());
                    PriceOutPut.setText(product.getPrice());
                    Picasso.get().load(product.getIMG()).into(IMGOutPut);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", "Operation canceled", error.toException());
                Toast.makeText(AdminDisplayProductActivity.this, "Database operation canceled: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        UpdateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNotEmpty()){
                    AlertDialog.Builder mydialog = new AlertDialog.Builder(AdminDisplayProductActivity.this);
                    mydialog.setTitle("Modifier cette publication "+product.getName());
                    mydialog.setMessage("Voulez-vous vraiment modifier cette publication: "
                            +product.getName()+" ?");
                    mydialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Update the product
                            dialog.show();
                            if (image_file != null) {
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
                                                                    Map<String, Object> updates = new HashMap<>();
                                                                    updates.put("name", TitleOutPut.getText().toString());
                                                                    updates.put("price", PriceOutPut.getText().toString());
                                                                    updates.put("description", AboutOutPut.getText().toString());
                                                                    updates.put("img", ImageURL);
                                                                    updates.put("imgRef", id);
                                                                    updateProductIntoDB(updates);
                                                                }
                                                            });
                                                }else {
                                                    dialog.dismiss();
                                                    Toast.makeText(AdminDisplayProductActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }else {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("name", TitleOutPut.getText().toString());
                                updates.put("price", PriceOutPut.getText().toString());
                                updates.put("description", AboutOutPut.getText().toString());
                                updateProductIntoDB(updates);
                            }
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

    }
    private void InisializationOfFealds(){
        UpdateBTN = findViewById(R.id.UpdateBTN);
        AboutOutPut = findViewById(R.id.AboutOutPut);
        TitleOutPut = findViewById(R.id.TitleOutPut);
        PriceOutPut = findViewById(R.id.PriceOutPut);
        IMGOutPut = findViewById(R.id.IMGOutPut);
        BackBTN = findViewById(R.id.BackBTN);
        UpdateAboutBTN = findViewById(R.id.UpdateAboutBTN);
        UpdatePriceBTN = findViewById(R.id.UpdatePriceBTN);
        UpdateTitleBTN = findViewById(R.id.UpdateTitleBTN);
        UpdateIMGBTN = findViewById(R.id.UpdateIMGBTN);
        ProductID = getIntent().getStringExtra("ProductID");
        RefProduct = FirebaseDatabase.getInstance(getString(R.string.DBURL))
                .getReference()
                .child("Products")
                .child(ProductID);
        ProductImgref = FirebaseStorage.getInstance().getReference()
                .child("ProductImages");
    }
    private void ButtonsRediraction(){
        BackBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        UpdateAboutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutOutPut.setText("");
            }
        });
        UpdateTitleBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TitleOutPut.setText("");
            }
        });
        UpdatePriceBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PriceOutPut.setText("");
            }
        });
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
                                IMGOutPut.setImageBitmap(resizedBitmap);
                            } else {
                                // Handle the case where resizing failed
                                Toast.makeText(AdminDisplayProductActivity.this,"Le redimensionnement a échoué.", Toast.LENGTH_SHORT).show();
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
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            // Calculate an appropriate inSampleSize value
            options.inSampleSize = calculateInSampleSize(options, desiredWidth, desiredHeight);

            inputStream.close();
            inputStream = getContentResolver().openInputStream(imageUri);

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
        if (TitleOutPut.getText().toString().isEmpty()){
            Toast.makeText(AdminDisplayProductActivity.this, "Entrez le nom du produit", Toast.LENGTH_SHORT).show();
            return false;
        }else if (PriceOutPut.getText().toString().isEmpty()){
            Toast.makeText(AdminDisplayProductActivity.this, "Entrez le prix du produit", Toast.LENGTH_SHORT).show();
            return false;
        }else if (AboutOutPut.getText().toString().isEmpty()){
            Toast.makeText(AdminDisplayProductActivity.this, "Entrez la description du produit", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }
    protected void updateProductIntoDB(Map<String, Object> productupdate){
        ProductImgref.child(product.getImgRef()+".jpeg").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    RefProduct.updateChildren(productupdate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        dialog.dismiss();
                                        Toast.makeText(AdminDisplayProductActivity.this, "Le produit a été modifié", Toast.LENGTH_SHORT).show();
                                    }else {
                                        dialog.dismiss();
                                        Toast.makeText(AdminDisplayProductActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(AdminDisplayProductActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}