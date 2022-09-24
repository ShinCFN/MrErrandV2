package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
    ImageView imgorder;

    RecyclerView orderlistrv;
    DBViewOrderList dbViewOrderList;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        Order ord_open = (Order) getIntent().getSerializableExtra("OPEN");
        Button btnSend = findViewById(R.id.btnOffer);
        offerIn = findViewById(R.id.editTextOfferInput);
        imgorder = findViewById(R.id.imgorder);
        orderlistrv = findViewById(R.id.textorderrv);
        dbViewOrderList = new DBViewOrderList(ord_open.getUID());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference("Riders").child(firebaseUser.getUid());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("ordertype").getValue().toString().equals("true")) {
                    imgorder.setVisibility(View.VISIBLE);
                    Picasso.get().load(snapshot.child("orderlist").getValue().toString()).into(imgorder);
                    imgorder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent viewIMG = new Intent(ViewOrderActivity.this, ViewImageActivity.class);
                            viewIMG.putExtra("image", snapshot.child("orderlist").getValue().toString());
                            startActivity(viewIMG);
                        }
                    });
                } else {
                    orderlistrv.setVisibility(View.VISIBLE);


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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference ordRef = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getUID());
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