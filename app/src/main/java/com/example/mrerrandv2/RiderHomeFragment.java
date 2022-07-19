package com.example.mrerrandv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import pl.droidsonroids.gif.GifImageButton;
import pl.droidsonroids.gif.GifImageView;


public class RiderHomeFragment extends Fragment {

    DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("Riders");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    RecyclerView recyclerView;
    DBOrder dbord;
    FirebaseRecyclerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_rider_home, container, false);

        //Set user name from database ( DELETE LATER)

//        newRef.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                String name = snapshot.child("firstname").getValue().toString();
//
//                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                        .setDisplayName(name).build();
//
//                user.updateProfile(profileUpdates);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        //Recycler View

        recyclerView = v.findViewById(R.id.ordersrv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        dbord = new DBOrder();

        FirebaseRecyclerOptions<Order> option =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(dbord.get(), new SnapshotParser<Order>() {
                            @NonNull
                            @Override
                            public Order parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Order ord = snapshot.getValue(Order.class);
                                ord.setKey(snapshot.getKey());
                                return ord;
                            }
                        }).build();

        adapter = new FirebaseRecyclerAdapter(option) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull Object o) {
                OrderVH vh = (OrderVH) viewHolder;
                Order ord = (Order) o;
                vh.orderName.setText(ord.getFirstname());
                vh.orderdesc.setText(ord.getOrderlist());
                Picasso.get().load(ord.getProfilePic()).into(vh.profilePic);

                vh.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getContext(), ViewOrderActivity.class);
                        intent.putExtra("OPEN", ord);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_orders, parent, false);
                return new OrderVH(view);
            }

            @Override
            public void onDataChanged() {
                Toast.makeText(getContext(), "DEBUG: Data Change", Toast.LENGTH_LONG).show();
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

        return v;
}
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.stopListening();
    }
}