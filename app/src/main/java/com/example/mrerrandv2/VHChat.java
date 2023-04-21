package com.example.mrerrandv2;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class VHChat extends RecyclerView.ViewHolder {
    public TextView message, messageL;
    public TextView name, nameL;
    public TextView time, timeL;
    public CircleImageView profile, profileL;

    ConstraintLayout holder, holderL;

    public VHChat(@NonNull View itemView)
    {
        super(itemView);

        holder = itemView.findViewById(R.id.holderright);
        holderL = itemView.findViewById(R.id.holderleft);

        name = itemView.findViewById(R.id.name);
        message = itemView.findViewById(R.id.message);
        profile = itemView.findViewById(R.id.profile);
        time = itemView.findViewById(R.id.time);

        nameL = itemView.findViewById(R.id.nameL);
        messageL = itemView.findViewById(R.id.messageL);
        profileL = itemView.findViewById(R.id.profileL);
        timeL = itemView.findViewById(R.id.timeL);

    }
}
