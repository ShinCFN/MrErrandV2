package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class AcceptedOrderActivityUser extends AppCompatActivity {

    CircleImageView profilePic;
    TextView profileName, profilePhone, profileView;
    RatingBar ratingBar;
    ImageView orderImage, receipt;
    RecyclerView orderlistrv;
    FirebaseRecyclerAdapter adapter;
    DBViewOrderList dbViewOrderList;
    LinearLayout receiptholder, orderdesc;
    CircleImageView chatvh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_order_user);

        orderImage = findViewById(R.id.orderimg);
        profilePic = findViewById(R.id.profilePic);
        profileName = findViewById(R.id.profileName);
        ratingBar = findViewById(R.id.ratingBar);
        profilePhone = findViewById(R.id.profilePhone);
        profileView = findViewById(R.id.profileView);
        orderlistrv = findViewById(R.id.userorderlistrv);
        orderdesc = findViewById(R.id.orderdesc);
        receiptholder = findViewById(R.id.receiptholder);
        receipt = findViewById(R.id.receipt);
        chatvh = findViewById(R.id.chat);

        //Set Profile
        DatabaseReference riderReference = FirebaseDatabase.getInstance().getReference("Riders").child(getIntent().getStringExtra("RIDERKEY"));
        riderReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.get().load(snapshot.child("profileImage").getValue().toString()).into(profilePic);
                profilePhone.setText(snapshot.child("mobile").getValue().toString());

                if (snapshot.child("totalstars").exists() && snapshot.child("totalrates").exists()) {
                    String stars = snapshot.child("totalstars").getValue().toString();
                    String rates = snapshot.child("totalrates").getValue().toString();

                    if (stars.equals("0") && rates.equals("0")) {
                        int ratenum = 0;
                        ratingBar.setRating(ratenum);
                    } else {
                        int ratenum = Integer.valueOf(stars) / Integer.valueOf(rates);
                        ratingBar.setRating(ratenum);
                    }
                }

                String name = snapshot.child("firstname").getValue().toString().toUpperCase() + " " + snapshot.child("lastname").getValue().toString().toUpperCase();
                profileName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //View profile
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open rider profile
                Toasty.info(AcceptedOrderActivityUser.this, "open rider profile", Toasty.LENGTH_SHORT).show();
            }
        });

        //Open chat
        chatvh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open chat activity
                Intent intent = new Intent(AcceptedOrderActivityUser.this, OrderChatActivity.class);
                intent.putExtra("type", "Users");
                intent.putExtra("ORDKEY", getIntent().getStringExtra("ORDKEY"));
                startActivity(intent);
            }
        });

        //Recycler View
        String UID = getIntent().getStringExtra("uid");
        dbViewOrderList = new DBViewOrderList(UID);
        orderlistrv.setLayoutManager(new WrapContentLinearLayoutManager(AcceptedOrderActivityUser.this));

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
                UserOrderListVH vh = (UserOrderListVH) viewHolder;
                OrderList list = (OrderList) o;

                vh.item.setText(list.getItem());
                vh.qty.setText(list.getQty());

                if (list.getState().equals("true")) {
                    vh.check.setVisibility(View.VISIBLE);
                    vh.xmark.setVisibility(View.GONE);
                } else if (list.getState().equals("false")) {
                    vh.check.setVisibility(View.GONE);
                    vh.xmark.setVisibility(View.GONE);
                } else if (list.getState().equals("none")) {
                    vh.xmark.setVisibility(View.VISIBLE);
                    vh.check.setVisibility(View.GONE);
                }
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(AcceptedOrderActivityUser.this).inflate(R.layout.layout_uservieworderlist, parent, false);
                return new UserOrderListVH(view);
            }

            @Override
            public void onDataChanged() {
            }
        };

        orderlistrv.setAdapter(adapter);
        adapter.startListening();

        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("Order");

        statusRef.child(getIntent().getStringExtra("ORDKEY")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("inDelivery")) {
                        orderdesc.setVisibility(View.GONE);
                        receiptholder.setVisibility(View.VISIBLE);
                        Picasso.get().load(snapshot.child("receipt").getValue().toString()).into(receipt);

                        receipt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent viewIMG = new Intent(AcceptedOrderActivityUser.this, ViewImageActivity.class);
                                viewIMG.putExtra("image", snapshot.child("receipt").getValue().toString());
                                startActivity(viewIMG);
                            }
                        });
                    }
                }

                if (snapshot.exists()) {
                    if (snapshot.child("status").getValue().toString().equals("complete")) {
                        Intent intent = new Intent(AcceptedOrderActivityUser.this, RatingActivityTowardsRider.class);
                        intent.putExtra("rider", getIntent().getStringExtra("RIDERKEY"));
                        intent.putExtra("order", getIntent().getStringExtra("ORDKEY"));
                        statusRef.removeEventListener(this);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Check Order Type
        if (getIntent().getStringExtra("type").equals("false")) {
            orderlistrv.setVisibility(View.VISIBLE);
        } else {
            orderImage.setVisibility(View.VISIBLE);
            DatabaseReference imgOrd = FirebaseDatabase.getInstance().getReference("Order").child(getIntent().getStringExtra("ORDKEY"));
            imgOrd.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Picasso.get().load(snapshot.child("orderlist").getValue().toString()).into(orderImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            orderImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent viewIMG = new Intent(AcceptedOrderActivityUser.this, ViewImageActivity.class);
                    viewIMG.putExtra("image", getIntent().getStringExtra("orderlist"));
                    startActivity(viewIMG);
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
        finish();
    }

    @Override
    public void onStop() {
        // Do your stuff here
        super.onStop();
        adapter.stopListening();
    }
}