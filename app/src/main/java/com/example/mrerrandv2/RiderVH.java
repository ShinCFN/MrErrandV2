package com.example.mrerrandv2;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RiderVH extends RecyclerView.ViewHolder {
    public TextView name;
    public CircleImageView profile;
    public View view;
    public RatingBar rating;

    public RiderVH(@NonNull View itemView)
    {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        profile = itemView.findViewById(R.id.profile);
        rating = itemView.findViewById(R.id.rating);
        view = itemView;
    }
}
