package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;


public class AdminUserListFragment extends Fragment {

    RecyclerView recyclerView;
    DBUsers dbusers;
    FirebaseRecyclerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_admin_userlist, container, false);

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

        //Recycler View

        recyclerView = v.findViewById(R.id.userlistrv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        dbusers = new DBUsers();

        FirebaseRecyclerOptions<User> option =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(dbusers.get(), new SnapshotParser<User>() {
                            @NonNull
                            @Override
                            public User parseSnapshot(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                user.setKey(snapshot.getKey());
                                return user;
                            }
                        }).build();

        adapter = new FirebaseRecyclerAdapter(option) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull Object o) {
                UserVH vh = (UserVH) viewHolder;
                User user = (User) o;

                int totalrates = user.getTotalrates();
                int totalstars = user.getTotalstars();

                if (totalstars==0 && totalrates==0){
                    vh.rating.setRating(0);
                }else{
                    vh.rating.setRating(totalstars/totalrates);
                }

                vh.name.setText(user.getFirstname()+" "+user.getLastname());

                Picasso.get().load(user.getProfileImage()).into(vh.profile);

                vh.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), AdminViewUserActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_admin_userlist, parent, false);
                return new UserVH(view);
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