package com.example.palmaryapplication.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.R;
import com.google.android.material.card.MaterialCardView;

public class AdminAnnonceViewHolder extends RecyclerView.ViewHolder {
    public ImageView AnnonceIMG;
    public TextView AnnonceTitle;
    public MaterialCardView DeleteBTN;
    public AdminAnnonceViewHolder(@NonNull View itemView) {
        super(itemView);
        InisializationOfFealds();
    }
    private void InisializationOfFealds(){
        AnnonceIMG = itemView.findViewById(R.id.AnnonceIMG);
        AnnonceTitle = itemView.findViewById(R.id.AnnonceTitle);
        DeleteBTN = itemView.findViewById(R.id.DeleteBTN);
    }
}
