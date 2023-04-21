package com.example.mrerrandv2;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class VHTransaction extends RecyclerView.ViewHolder {
    public TextView info;
    public CircleImageView image;
    public ConstraintLayout holder;
    public VHTransaction(@NonNull View itemView)
    {
        super(itemView);
        info = itemView.findViewById(R.id.info);
        image = itemView.findViewById(R.id.image);
        holder= itemView.findViewById(R.id.holder);
    }
}
