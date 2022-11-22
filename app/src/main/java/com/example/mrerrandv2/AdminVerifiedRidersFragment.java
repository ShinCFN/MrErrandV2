package com.example.mrerrandv2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AdminVerifiedRidersFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<Rider> list;
    VerifiedListAdapter verifiedListAdapter;

    DatabaseReference ref;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_riderlist, container, false);

        //SwipeRefresh
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRiderList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //Recycler View
        ref = FirebaseDatabase.getInstance().getReference("Riders");
        recyclerView = v.findViewById(R.id.riderlistrv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        list = new ArrayList<>();
        verifiedListAdapter = new VerifiedListAdapter(getContext(), list);
        recyclerView.setAdapter(verifiedListAdapter);

        updateRiderList();

        return v;
    }

    public void updateRiderList() {

        list.clear();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Rider rider = dataSnapshot.getValue(Rider.class);

                    if(rider.getVerified().equals("true")){
                        rider.setKey(dataSnapshot.getKey());
                        list.add(rider);
                    }
                }

                verifiedListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}