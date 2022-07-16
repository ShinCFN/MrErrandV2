package com.example.mrerrandv2;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfferVH extends RecyclerView.ViewHolder {
    public TextView orderName,offer, order_options;
    public OfferVH(@NonNull View itemView)
    {
        super(itemView);
        orderName = itemView.findViewById(R.id.orderName);
        offer = itemView.findViewById(R.id.orderdesc);
        order_options = itemView.findViewById(R.id.order_options);
    }
}
