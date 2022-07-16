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


        vh.order_options.setOnClickListener(v->
        {
            PopupMenu popupMenu = new PopupMenu(context,vh.order_options);

            popupMenu.inflate(R.menu.options_order);
            popupMenu.setOnMenuItemClickListener(item->
            {
                switch (item.getItemId())
                {
                    case R.id.menu_open:
                        Intent intent = new Intent(context,ViewOrderActivity.class);
                        intent.putExtra("OPEN",ord);
                        context.startActivity(intent);
                        break;

                    case R.id.menu_remove:
                        DBOrder dbord = new DBOrder();
                        dbord.remove(ord.getKey()).addOnSuccessListener(suc->
                        {
                            Toast.makeText(context,"Order Removed",Toast.LENGTH_LONG).show();
                            notifyItemRemoved(position);
                        }).addOnFailureListener(er->
                        {
                            Toast.makeText(context, ""+er.getMessage(), Toast.LENGTH_LONG).show();
                        });
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
