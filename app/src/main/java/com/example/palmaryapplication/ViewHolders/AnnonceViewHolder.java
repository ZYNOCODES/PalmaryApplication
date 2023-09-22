package com.example.palmaryapplication.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.R;

public class AnnonceViewHolder extends RecyclerView.ViewHolder {
    public ImageView AnnonceIMG;
    public TextView AnnonceTitle;
    public AnnonceViewHolder(@NonNull View itemView) {
        super(itemView);
        InisializationOfFealds();
    }
    private void InisializationOfFealds(){
        AnnonceIMG = itemView.findViewById(R.id.AnnonceIMG);
        AnnonceTitle = itemView.findViewById(R.id.AnnonceTitle);
    }
}
