package com.example.mrerrandv2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RiderOrderListAdapter extends RecyclerView.Adapter<RiderOrderListAdapter.VH> {

    Context context;
    String uid;
    ArrayList<OrderList> list;

    public RiderOrderListAdapter(Context context, ArrayList<OrderList> list, String uid) {
        this.context = context;
        this.list = list;
        this.uid = uid;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_riderorderlist, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        OrderList orderList = list.get(position);
        holder.item.setText(orderList.getItem());
        holder.qty.setText(orderList.getQty());

        DatabaseReference checkboxRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("OrderList").child(orderList.getKey());

        holder.state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    new AlertDialog.Builder(holder.item.getContext())
                            .setMessage("Is this item available")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    checkboxRef.child("state").setValue("true");
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    checkboxRef.child("state").setValue("none");
                                }
                            }).show();
                }else{
                    checkboxRef.child("state").setValue("false");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{

        TextView item, qty;
        CheckBox state;

        public VH(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item);
            qty = itemView.findViewById(R.id.qty);
            state = itemView.findViewById(R.id.ordercomp);

        }
    }
}