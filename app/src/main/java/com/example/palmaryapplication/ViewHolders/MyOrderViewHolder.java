package com.example.palmaryapplication.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.R;

public class MyOrderViewHolder extends RecyclerView.ViewHolder {
    public ImageView IMGOutPut;
    public TextView TitleOutPut, PriceOutPut, QuantityOutPut, ModificationOutPut;
    public MyOrderViewHolder(@NonNull View itemView) {
        super(itemView);
        InisializationOfFealds();
    }
    private void InisializationOfFealds(){
        IMGOutPut = itemView.findViewById(R.id.IMGOutPut);
        TitleOutPut = itemView.findViewById(R.id.TitleOutPut);
        PriceOutPut = itemView.findViewById(R.id.PriceOutPut);
        QuantityOutPut = itemView.findViewById(R.id.QuantityOutPut);
        ModificationOutPut = itemView.findViewById(R.id.ModificationOutPut);
    }
}
