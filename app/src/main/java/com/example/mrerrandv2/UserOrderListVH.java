package com.example.mrerrandv2;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserOrderListVH extends RecyclerView.ViewHolder {
    public TextView item,qty, price;
    public CheckBox check;
    public UserOrderListVH(@NonNull View itemView)
    {
        super(itemView);
        item = itemView.findViewById(R.id.item);
        qty = itemView.findViewById(R.id.qty);
        check = itemView.findViewById(R.id.ordercomp);
        price = itemView.findViewById(R.id.textPrice);
    }
}
