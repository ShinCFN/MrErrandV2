package com.example.mrerrandv2;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
        View v = inflater.inflate(R.layout.fragment_admin_riderlist, container, false);

        //Status bar
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(v.getContext(), R.color.newYellow));
        View decor = getActivity().getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setNavigationBarColor(getContext().getResources().getColor(R.color.black));
        }

        //Toolbar
        TextView toolMain = v.findViewById(R.id.riderStatus);
        toolMain.setText("Verified Riders");

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