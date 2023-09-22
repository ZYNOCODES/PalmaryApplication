package com.example.palmaryapplication.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.R;

public class CartViewHolder extends RecyclerView.ViewHolder {
    public ImageView IMGOutPut, DeleteBTN;
    public TextView TitleOutPut, PriceOutPut, QuantityOutPut, ModificationOutPut;
    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        InisializationOfFealds();
    }
    private void InisializationOfFealds(){
        IMGOutPut = itemView.findViewById(R.id.IMGOutPut);
        DeleteBTN = itemView.findViewById(R.id.DeleteBTN);
        TitleOutPut = itemView.findViewById(R.id.TitleOutPut);
        PriceOutPut = itemView.findViewById(R.id.PriceOutPut);
        QuantityOutPut = itemView.findViewById(R.id.QuantityOutPut);
        ModificationOutPut = itemView.findViewById(R.id.ModificationOutPut);
    }
}
