package com.example.mrerrandv2;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionHistoryVH extends RecyclerView.ViewHolder {
    public TextView date, id;
    public CircleImageView image;
    public ConstraintLayout holder;
    public RatingBar ratingBar;
    public TransactionHistoryVH(@NonNull View itemView)
    {
        super(itemView);
        date = itemView.findViewById(R.id.date);
        id = itemView.findViewById(R.id.id);
        image = itemView.findViewById(R.id.image);
        holder= itemView.findViewById(R.id.holder);
        ratingBar= itemView.findViewById(R.id.ratingBar);
    }
}
