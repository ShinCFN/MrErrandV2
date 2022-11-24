package com.example.mrerrandv2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class ViewOrderActivity extends AppCompatActivity {


    EditText offerIn;
    ImageView imgorder, toolbarback;
    TextView textqty, textid, textidcenter, textDesiredStore;
    View offerbtn, textdivide;

    RecyclerView orderlistrv;
    DBViewOrderList dbViewOrderList;
    FirebaseRecyclerAdapter adapter;
    ConstraintLayout wholeorderrv, wholeimgorder;
    progressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        Order ord_open = (Order) getIntent().getSerializableExtra("OPEN");
        offerIn = findViewById(R.id.editTextOfferInput);
        wholeimgorder = findViewById(R.id.wholeimgorder);
        imgorder = findViewById(R.id.imgorder);
        offerbtn = findViewById(R.id.offerbtn);
        orderlistrv = findViewById(R.id.textorderrv);
        wholeorderrv = findViewById(R.id.wholeorderrv);
        textdivide = findViewById(R.id.textdivide);
        textid = findViewById(R.id.textid);
        textqty = findViewById(R.id.textqty);
        toolbarback = findViewById(R.id.toolbarback);
        textidcenter = findViewById(R.id.textidcenter);
        textDesiredStore = findViewById(R.id.textDesiredStore);

        dbViewOrderList = new DBViewOrderList(ord_open.getUID());
        progressBar = new progressBar(ViewOrderActivity.this);

        progressBar.show();

        //Toolbar
        TextView toolMain = findViewById(R.id.toolbarmain);
        TextView toolSub = findViewById(R.id.toolbarsub);
        toolMain.setText("Order list");
        toolSub.setText("List of items request by the client");

        toolbarback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference("Riders").child(firebaseUser.getUid());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Set desired store
                textDesiredStore.setText(snapshot.child("desiredStore").getValue().toString());

                if (snapshot.child("ordertype").getValue().toString().equals("true")) {
                    wholeimgorder.setVisibility(View.VISIBLE);
                    textdivide.setVisibility(View.GONE);
                    textid.setVisibility(View.GONE);
                    textqty.setVisibility(View.GONE);
                    textidcenter.setVisibility(View.VISIBLE);

                    Glide.with(ViewOrderActivity.this).load(snapshot.child("orderlist").getValue().toString()).placeholder(R.color.white).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.dismiss();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.dismiss();
                            return false;
                        }
                    }).into(imgorder);


                    imgorder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent viewIMG = new Intent(ViewOrderActivity.this, ViewImageActivity.class);
                            viewIMG.putExtra("image", snapshot.child("orderlist").getValue().toString());
                            startActivity(viewIMG);
                        }
                    });
                } else {
                    wholeorderrv.setVisibility(View.VISIBLE);
                    progressBar.dismiss();

                    //Recycler View
                    orderlistrv.setLayoutManager(new WrapContentLinearLayoutManager(ViewOrderActivity.this));

                    FirebaseRecyclerOptions<OrderList> options =
                            new FirebaseRecyclerOptions.Builder<OrderList>()
                                    .setQuery(dbViewOrderList.get(), new SnapshotParser<OrderList>() {
                                        @NonNull
                                        @Override
                                        public OrderList parseSnapshot(@NonNull DataSnapshot snapshot) {
                                            OrderList orl = snapshot.getValue(OrderList.class);
                                            orl.setKey(snapshot.getKey());
                                            return orl;
                                        }
                                    }).build();

                    adapter = new FirebaseRecyclerAdapter(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull Object o) {
                            OrderListVH vh = (OrderListVH) viewHolder;
                            OrderList list = (OrderList) o;

                            vh.item.setText(list.getItem());
                            vh.qty.setText(list.getQty());

                        }

                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(ViewOrderActivity.this).inflate(R.layout.layout_vieworderlist, parent, false);
                            return new OrderListVH(view);
                        }

                        @Override
                        public void onDataChanged() {
                        }
                    };

                    orderlistrv.setAdapter(adapter);
                    adapter.startListening();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        offerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ordRef = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());
                ordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String offerval = offerIn.getText().toString();
                            if (offerval.isEmpty()) {
                                Toasty.error(ViewOrderActivity.this, "Enter amount", Toasty.LENGTH_SHORT).show();
                            } else {

                                riderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        String ridername = snapshot.child("firstname").getValue().toString();
                                        String state = "open";

                                        String totalstars = snapshot.child("totalstars").getValue().toString();
                                        String totalrates = snapshot.child("totalrates").getValue().toString();

                                        if (totalstars.equals("0") && totalrates.equals("0")) {
                                            int rating = 0;

                                            AddOffer addOffer = new AddOffer(ridername, offerval, state, rating);

                                            databaseReference.child("Offers").child(firebaseUser.getUid()).setValue(addOffer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Intent intent = new Intent(ViewOrderActivity.this, OfferWaitingActivityRider.class);
                                                    intent.putExtra("ORDER", ord_open);
                                                    intent.putExtra("OFFER", offerval);
                                                    intent.putExtra("RIDERID", firebaseUser.getUid());
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        } else {
                                            int rating = Integer.valueOf(totalstars) / Integer.valueOf(totalrates);

                                            AddOffer addOffer = new AddOffer(ridername, offerval, state, rating);

                                            databaseReference.child("Offers").child(firebaseUser.getUid()).setValue(addOffer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Intent intent = new Intent(ViewOrderActivity.this, OfferWaitingActivityRider.class);
                                                    intent.putExtra("ORDER", ord_open);
                                                    intent.putExtra("OFFER", offerval);
                                                    intent.putExtra("RIDERID", firebaseUser.getUid());
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        } else {
                            Toasty.warning(ViewOrderActivity.this, "Order was cancelled", Toasty.LENGTH_LONG).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Order ord_open = (Order) getIntent().getSerializableExtra("OPEN");
        if (ord_open.getOrdertype().equals("false")) {
            adapter.stopListening();
        }
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        Order ord_open = (Order) getIntent().getSerializableExtra("OPEN");
        if (ord_open.getOrdertype().equals("false")) {
            adapter.stopListening();
        }
    }
}