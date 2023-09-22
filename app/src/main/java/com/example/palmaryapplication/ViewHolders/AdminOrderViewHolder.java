package com.example.palmaryapplication.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.R;
import com.google.android.material.card.MaterialCardView;

public class AdminOrderViewHolder extends RecyclerView.ViewHolder {
    public TextView OrderID;
    public TextView OrderPrice;
    public TextView OrderType;
    public MaterialCardView OrderVoirBTN;
    public AdminOrderViewHolder(@NonNull View itemView) {
        super(itemView);
        InisializationOfFealds();
    }
    private void InisializationOfFealds(){
        OrderID = itemView.findViewById(R.id.OrderID);
        OrderPrice = itemView.findViewById(R.id.OrderPrice);
        OrderVoirBTN = itemView.findViewById(R.id.OrderVoirBTN);
        OrderType = itemView.findViewById(R.id.OrderType);
    }
}
