package com.example.palmaryapplication.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palmaryapplication.R;
import com.google.android.material.card.MaterialCardView;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    public TextView OrderID;
    public TextView OrderPrice;
    public MaterialCardView OrderCancelBTN;
    public MaterialCardView OrderConfirmedBTN;
    public TextView OrderType;
    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        InisializationOfFealds();
    }
    private void InisializationOfFealds(){
        OrderID = itemView.findViewById(R.id.OrderID);
        OrderPrice = itemView.findViewById(R.id.OrderPrice);
        OrderConfirmedBTN = itemView.findViewById(R.id.OrderConfirmedBTN);
        OrderCancelBTN = itemView.findViewById(R.id.OrderCancelBTN);
        OrderType = itemView.findViewById(R.id.OrderType);
    }
}
