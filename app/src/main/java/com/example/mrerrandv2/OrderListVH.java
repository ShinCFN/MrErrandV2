package com.example.mrerrandv2;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderListVH extends RecyclerView.ViewHolder {
    public TextView item,qty;
    public ImageView delItem;
    public OrderListVH(@NonNull View itemView)
    {
        super(itemView);
        item = itemView.findViewById(R.id.item);
        qty = itemView.findViewById(R.id.qty);
        delItem = itemView.findViewById(R.id.delbutton);
    }
}
