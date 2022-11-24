package com.example.mrerrandv2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class RiderOrderListAdapter extends RecyclerView.Adapter<RiderOrderListAdapter.VH> {

    Context context;
    String uid;
    ArrayList<OrderList> list;

    String itemPrice;

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

        //Check current checkbox state
        if(orderList.getState().equals("true")){
            holder.state.setButtonDrawable(R.drawable.custom_checkbox_green);
            holder.state.setChecked(true);
        }else if(orderList.getState().equals("none")){
            holder.state.setButtonDrawable(R.drawable.custom_checkbox_red);
            holder.state.setChecked(true);
        }

        holder.price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemPrice = holder.price.getText().toString();
            }
        });



        DatabaseReference checkboxRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("OrderList").child(orderList.getKey());

        holder.state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    new AlertDialog.Builder(holder.item.getContext())
                            .setMessage("Is this item available")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(holder.price.getText().toString().isEmpty()){
                                        Toasty.error(compoundButton.getContext(), "Input item price", Toasty.LENGTH_SHORT).show();
                                        holder.state.setChecked(false);
                                    }else{
                                        holder.state.setButtonDrawable(R.drawable.custom_checkbox_green);
                                        checkboxRef.child("state").setValue("true");
                                        holder.price.setEnabled(false);
                                        checkboxRef.child("price").setValue(itemPrice);
                                        holder.price.setText("₱ " + itemPrice);
                                    }
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    holder.state.setButtonDrawable(R.drawable.custom_checkbox_red);
                                    checkboxRef.child("state").setValue("none");
                                    holder.price.setEnabled(false);
                                    checkboxRef.child("price").setValue("0");
                                    holder.price.setText("₱ 0");
                                }
                            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    checkboxRef.child("state").setValue("false");
                                    holder.price.setEnabled(true);
                                    checkboxRef.child("price").removeValue();
                                    holder.price.setText("");
                                    holder.state.setChecked(false);
                                }
                            }).show();

                } else {
                    holder.price.setText("");
                    checkboxRef.child("state").setValue("false");
                    holder.price.setEnabled(true);
                    checkboxRef.child("price").removeValue();
                    holder.state.setChecked(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder {

        TextView item, qty;
        CheckBox state;
        EditText price;

        public VH(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item);
            qty = itemView.findViewById(R.id.qty);
            state = itemView.findViewById(R.id.ordercomp);
            price = itemView.findViewById(R.id.editPrice);

        }
    }
}
