package com.example.mrerrandv2;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RiderOrderListVH extends RecyclerView.ViewHolder {
    public TextView item,qty;
    public CheckBox state;
    public RiderOrderListVH(@NonNull View itemView)
    {
        super(itemView);
        item = itemView.findViewById(R.id.item);
        qty = itemView.findViewById(R.id.qty);
        state = itemView.findViewById(R.id.ordercomp);
    }
}
