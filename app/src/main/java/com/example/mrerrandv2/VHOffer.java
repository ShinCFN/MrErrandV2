package com.example.mrerrandv2;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

public class VHOffer extends RecyclerView.ViewHolder {
    public TextView orderName,offer;
    public ImageView order_options;
    public LottieAnimationView helmet;
    public RatingBar rating;
    public VHOffer(@NonNull View itemView)
    {
        super(itemView);
        helmet = itemView.findViewById(R.id.helmetanim);
        orderName = itemView.findViewById(R.id.orderName);
        offer = itemView.findViewById(R.id.orderdesc);
        order_options = itemView.findViewById(R.id.order_options);
        rating = itemView.findViewById(R.id.rating);
    }
}
