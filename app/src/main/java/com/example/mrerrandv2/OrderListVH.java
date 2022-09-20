package com.example.mrerrandv2;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class OrderListVH extends RecyclerView.ViewHolder {
    public TextView item,qty, num;
    public ImageView delItem;
    public ConstraintLayout holder;
    public OrderListVH(@NonNull View itemView)
    {
        super(itemView);
        item = itemView.findViewById(R.id.item);
        qty = itemView.findViewById(R.id.qty);
        delItem = itemView.findViewById(R.id.delbutton);
        holder = itemView.findViewById(R.id.holder);
    }
}
