package com.example.mrerrandv2;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderVH extends RecyclerView.ViewHolder {
    public TextView orderName,orderdesc;
    public ImageView profilePic;
    public View view;
    public OrderVH(@NonNull View itemView)
    {
        super(itemView);
        orderName = itemView.findViewById(R.id.orderName);
        orderdesc = itemView.findViewById(R.id.orderdesc);
        profilePic = itemView.findViewById(R.id.profile);
        view = itemView;
    }
}
