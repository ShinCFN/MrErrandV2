package com.example.mrerrandv2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PendingListAdapter extends RecyclerView.Adapter<PendingListAdapter.MyViewHolder> {

    Context context;

    ArrayList<Rider> list;

    DBRiders dbRiders;


    public PendingListAdapter(Context context, ArrayList<Rider> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_admin_userlist, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        dbRiders = new DBRiders();

        Rider rider = list.get(position);
        int totalrates = rider.getTotalrates();
        int totalstars = rider.getTotalstars();

        if (totalstars == 0 && totalrates == 0) {
            holder.rating.setRating(0);
        } else {
            holder.rating.setRating(totalstars / totalrates);
        }



        holder.name.setText(rider.getFirstname() + " " + rider.getLastname());

        Picasso.get().load(rider.getProfileImage()).into(holder.profile);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AdminViewRiderActivity.class);
                intent.putExtra("rider", rider);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CircleImageView profile;
        View view;
        RatingBar rating;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            profile = itemView.findViewById(R.id.profile);
            rating = itemView.findViewById(R.id.rating);
            view = itemView;
        }
    }

}
