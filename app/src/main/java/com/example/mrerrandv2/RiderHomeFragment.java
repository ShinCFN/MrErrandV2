package com.example.mrerrandv2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

import es.dmoral.toasty.Toasty;


public class RiderHomeFragment extends Fragment {

    DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("Riders");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    RecyclerView recyclerView;
    DBOrder dbord;
    FirebaseRecyclerAdapter adapter;
    RiderLandingPage landingPage;
    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_rider_home, container, false);

        //Listener
        landingPage = new RiderLandingPage();

        //Status bar
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(v.getContext(), R.color.finalBackground));

        //Recycler View
        recyclerView = v.findViewById(R.id.ordersrv);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext()));
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

                if (ord.getOrdertype().equals("true")){
                    vh.orderdesc.setText("Open to view order list");
                }else if (ord.getStatus().equals("accepted") || ord.getStatus().equals("inDelivery") || ord.getStatus().equals("complete")){
                    vh.orderdesc.setText("Accepted");
                    vh.orderdesc.setTextColor(Color.GREEN);
                }else{
                    vh.orderdesc.setText(ord.getOrderlist());
                }
                vh.rating.setRating(ord.getRating());

                Picasso.get().load(ord.getProfilePic()).into(vh.profilePic);

                vh.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (ord.getStatus().equals("accepted") || ord.getStatus().equals("inDelivery") || ord.getStatus().equals("complete")){
                            Toasty.warning(getContext(), "Order was placed for another rider", Toasty.LENGTH_LONG).show();
                        }else{
                            Intent intent = new Intent(getContext(), ViewOrderActivity.class);
                            intent.putExtra("OPEN", ord);
                            startActivity(intent);
                        }

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
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

        //Order check
        orderRef.orderByChild("rider").equalTo(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Order ord = childSnapshot.getValue(Order.class);
                    ord.setKey(childSnapshot.getKey());
                    if(snapshot.exists()){

                        Log.e("te", ord.getFirstname());
                        Intent intent = new Intent(getActivity(), AcceptedOrderActivityRider.class);
                        intent.putExtra("RKEY", childSnapshot.child("rider").getValue().toString());
                        intent.putExtra("ORDER", ord);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return v;
}
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
        landingPage.listener();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}