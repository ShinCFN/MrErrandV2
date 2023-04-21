package com.example.mrerrandv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterPaymentOrderList extends RecyclerView.Adapter<AdapterPaymentOrderList.VH> {

    Context context;
    ArrayList<OrderList> list;

    public AdapterPaymentOrderList(Context context, ArrayList<OrderList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_paymentorderlist, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        OrderList orderList = list.get(position);

        holder.item.setText(orderList.getItem());
        holder.qty.setText(orderList.getQty());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{

        TextView item, qty;

        public VH(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item);
            qty = itemView.findViewById(R.id.qty);
        }
    }
}
