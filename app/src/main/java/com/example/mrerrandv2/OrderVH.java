package com.example.mrerrandv2;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderVH extends RecyclerView.ViewHolder {
    public TextView orderName,orderdesc, order_options;
    public View view;
    public OrderVH(@NonNull View itemView)
    {
        super(itemView);
        orderName = itemView.findViewById(R.id.orderName);
        orderdesc = itemView.findViewById(R.id.orderdesc);
        order_options = itemView.findViewById(R.id.order_options);
        view = itemView;
    }
}
