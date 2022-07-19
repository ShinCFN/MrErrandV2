package com.example.mrerrandv2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter {

    private Context context;
    ArrayList<Order> list = new ArrayList<>();
    public OrderAdapter (Context ctx)
    {
        this.context = ctx;
    }
    public void setItems(ArrayList<Order> ord)
    {
        list.addAll(ord);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_orders,parent,false);
        return new OrderVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        OrderVH vh = (OrderVH) holder;
        Order ord = list.get(position);
        vh.orderName.setText(ord.getFirstname());
        vh.orderdesc.setText(ord.getOrderlist());


        Picasso.get().load(ord.getProfilePic()).into(vh.profilePic);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
