package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;


public class RidersFragment extends Fragment {

    RecyclerView recyclerView;
    DBRiders dbRiders;
    FirebaseRecyclerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_riders, container, false);

        //Recycler View

        recyclerView = v.findViewById(R.id.riderlistrv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        dbRiders = new DBRiders();

        FirebaseRecyclerOptions<Rider> option =
                new FirebaseRecyclerOptions.Builder<Rider>()
                        .setQuery(dbRiders.get(), new SnapshotParser<Rider>() {
                            @NonNull
                            @Override
                            public Rider parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Rider rider = snapshot.getValue(Rider.class);
                                rider.setKey(snapshot.getKey());
                                return rider;
                            }
                        }).build();

        adapter = new FirebaseRecyclerAdapter(option) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull Object o) {
                RiderVH vh = (RiderVH) viewHolder;
                Rider rider = (Rider) o;

                int totalrates = rider.getTotalrates();
                int totalstars = rider.getTotalstars();

                if (totalstars==0 && totalrates==0){
                    vh.rating.setRating(0);
                }else{
                    vh.rating.setRating(totalstars/totalrates);
                }

                vh.name.setText(rider.getFirstname() + " " + rider.getLastname());

                Picasso.get().load(rider.getProfileImage()).into(vh.profile);

                vh.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), AdminViewRiderActivity.class);
                        intent.putExtra("rider", rider);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_admin, parent, false);
                return new RiderVH(view);
            }

            @Override
            public void onDataChanged() {
            }
        };

        recyclerView.setAdapter(adapter);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}