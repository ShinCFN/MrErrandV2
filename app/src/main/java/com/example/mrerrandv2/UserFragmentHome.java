package com.example.mrerrandv2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;


public class UserFragmentHome extends Fragment {

    ConstraintLayout Orderbtn;
    progressBar progressBar;
    ImageView info;
    static int i = 0;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ConstraintLayout activeorder;
    DatabaseReference activeOrderRef;
    String key;
    TextView orderdesc, history;
    RecyclerView transactionrv;
    DBTransaction dbTransaction;
    FirebaseRecyclerAdapter adapter;
    Boolean status = false;

    CircleImageView profilePic;
    TextView welcome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        activeorder = v.findViewById(R.id.activeorder);
        Orderbtn = v.findViewById(R.id.OrderNowButton);
        orderdesc = v.findViewById(R.id.orderdesc);
        history = v.findViewById(R.id.history);
        welcome = v.findViewById(R.id.helloTxt);
        profilePic = v.findViewById(R.id.profilePic);

        //Status bar
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(v.getContext(), R.color.newDark));
        View decor = getActivity().getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setNavigationBarColor(getContext().getResources().getColor(R.color.finalBackground));
            View view = getActivity().getWindow().getDecorView();
        }

        //Prog bar
        progressBar = new progressBar(getContext());

        //SetWelcome
        progressBar.show();
        welcome.setText("Hello, " + auth.getCurrentUser().getDisplayName());
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("profileImage").exists()){
                    Glide.with(UserFragmentHome.this).load(snapshot.child("profileImage").getValue().toString()).placeholder(R.drawable.blankuser).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.dismiss();
                            return false;
                        }
                    }).into(profilePic);
                } else{
                    progressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Transaction History
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        transactionrv = v.findViewById(R.id.transactionrv);
        transactionrv.setLayoutManager(layoutManager);
        dbTransaction = new DBTransaction(auth.getCurrentUser().getUid());

        FirebaseRecyclerOptions<SaveTransaction> option =
                new FirebaseRecyclerOptions.Builder<SaveTransaction>()
                        .setQuery(dbTransaction.getFive(), new SnapshotParser<SaveTransaction>() {
                            @NonNull
                            @Override
                            public SaveTransaction parseSnapshot(@NonNull DataSnapshot snapshot) {
                                SaveTransaction saveTransaction = snapshot.getValue(SaveTransaction.class);
                                saveTransaction.setKey(snapshot.getKey());
                                return saveTransaction;
                            }
                        }).build();

        adapter = new FirebaseRecyclerAdapter(option) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull Object o) {

                VHTransaction vh = (VHTransaction) viewHolder;
                SaveTransaction saveTransaction = (SaveTransaction) o;

                //Get order type
                String ordertype = saveTransaction.getOrdertype();

                DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference("Riders").child(saveTransaction.getRideruid());
                riderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Glide.with(getContext()).load(snapshot.child("profileImage").getValue().toString()).into(vh.image);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                vh.info.setText(saveTransaction.getSimpleDate());

                vh.holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //View transaction

                        Intent intent = new Intent(getActivity(), ViewTransaction.class);
                        intent.putExtra("transaction", saveTransaction);
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_transactions, parent, false);
                return new VHTransaction(view);
            }

            @Override
            public void onDataChanged() {
            }
        };

        //Disable scroll
        transactionrv.setOnTouchListener((view, motionEvent) -> true);
        transactionrv.setAdapter(adapter);
        adapter.startListening();

        //Open transaction history
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start activity
                Intent intent = new Intent(getContext(), TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkActiveOrder();
        transactionrv.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void Order() {
        progressBar.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("city").exists() &&
                        snapshot.child("email").exists() &&
                        snapshot.child("firstname").exists() &&
                        snapshot.child("lastname").exists() &&
                        snapshot.child("mobile").exists() &&
                        snapshot.child("profileImage").exists() &&
                        snapshot.child("province").exists() &&
                        snapshot.child("street").exists() &&
                        snapshot.child("zip").exists()) {

                    Intent intent = new Intent(getActivity(), UserOrderActivity.class);
                    startActivity(intent);

                } else {
                    Toasty.error(getContext(), "Please complete your information", Toasty.LENGTH_LONG).show();
                }
                progressBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //Active order
    public void checkActiveOrder() {

        DatabaseReference queryFirebase;

        queryFirebase = FirebaseDatabase.getInstance().getReference("Order");

        queryFirebase.orderByChild("uid").equalTo(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Orderbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (status) {
                            Toasty.warning(getActivity(), "You already have an active order", Toasty.LENGTH_SHORT).show();
                        } else {
                            Order();
                        }

                    }
                });

                if (!snapshot.exists()) {
                    status = false;
                    activeorder.setVisibility(View.GONE);
                    return;
                } else {
                    //Has an active order
                    status = true;

                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                        key = childSnapshot.getKey();
                        orderdesc.setText("#" + key);
                        activeorder.setVisibility(View.VISIBLE);
                        activeorder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (childSnapshot.child("status").getValue().toString().equals("null")) {
                                    Intent intent = new Intent(getActivity(), ViewOfferActivity.class);
                                    intent.putExtra("Key", key);
                                    startActivity(intent);
                                }

                                if (childSnapshot.child("status").getValue().toString().equals("accepted") || childSnapshot.child("status").getValue().toString().equals("inDelivery")) {
                                    Intent intent = new Intent(getActivity(), UserOrderAcceptedActivity.class);
                                    intent.putExtra("ORDKEY", key);
                                    intent.putExtra("RIDERKEY", childSnapshot.child("rider").getValue().toString());
                                    intent.putExtra("uid", childSnapshot.child("uid").getValue().toString());
                                    intent.putExtra("type", childSnapshot.child("ordertype").getValue().toString());

                                    startActivity(intent);
                                }

                                if (childSnapshot.child("status").getValue().toString().equals("complete")) {
                                    Intent intent = new Intent(getActivity(), UserRatingActivity.class);
                                    intent.putExtra("order", key);
                                    intent.putExtra("rider", childSnapshot.child("rider").getValue().toString());
                                    startActivity(intent);
                                }
                            }
                        });


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}