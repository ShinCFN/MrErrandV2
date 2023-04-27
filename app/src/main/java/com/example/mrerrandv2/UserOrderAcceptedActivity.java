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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

public class UserOrderAcceptedActivity extends AppCompatActivity {

    ConstraintLayout profileView, orderdesc;
    TextView profileName, deliveryFee;
    RatingBar ratingBar;
    ImageView orderImage, receipt;
    RecyclerView orderlistrv;
    FirebaseRecyclerAdapter adapter;
    DBViewOrderList dbViewOrderList;
    LinearLayout receiptholder;
    CircleImageView chatvh;
    ImageView toolbarback;

    progressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_order_user);

        orderImage = findViewById(R.id.orderimg);
        profileName = findViewById(R.id.profileName);
        ratingBar = findViewById(R.id.ratingBar);
        profileView = findViewById(R.id.profileView);
        orderlistrv = findViewById(R.id.userorderlistrv);
        orderdesc = findViewById(R.id.orderlabel);
        receiptholder = findViewById(R.id.receiptholder);
        receipt = findViewById(R.id.receipt);
        chatvh = findViewById(R.id.chat);
        toolbarback = findViewById(R.id.toolbarback);
        deliveryFee = findViewById(R.id.deliveryFee);
        progressBar = new progressBar(UserOrderAcceptedActivity.this);

        //Toolbar
        TextView toolMain = findViewById(R.id.toolbarmain);
        TextView toolSub = findViewById(R.id.toolbarsub);
        toolMain.setText("");
        toolSub.setText("");
        toolbarback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.finalDarkGreen));

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setNavigationBarColor(getResources().getColor(R.color.finalBackground));
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        //Set Profile
        DatabaseReference riderReference = FirebaseDatabase.getInstance().getReference("Riders").child(getIntent().getStringExtra("RIDERKEY"));
        riderReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

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
                Intent intent = new Intent(UserOrderAcceptedActivity.this, PopupViewProfileRider.class);
                intent.putExtra("details", getIntent().getStringExtra("RIDERKEY"));
                startActivity(intent);
            }
        });

        //Open chat
        chatvh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open chat activity
                Intent intent = new Intent(UserOrderAcceptedActivity.this, OrderChatActivity.class);
                intent.putExtra("type", "Users");
                intent.putExtra("ORDKEY", getIntent().getStringExtra("ORDKEY"));
                startActivity(intent);
            }
        });

        //Recycler View
        String UID = getIntent().getStringExtra("uid");
        dbViewOrderList = new DBViewOrderList(UID);
        orderlistrv.setLayoutManager(new WrapContentLinearLayoutManager(UserOrderAcceptedActivity.this));

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
                VHUserOrderList vh = (VHUserOrderList) viewHolder;
                OrderList list = (OrderList) o;

                vh.item.setText(list.getItem());
                vh.qty.setText(list.getQty());

                vh.check.setEnabled(false);
                if (list.getState().equals("true")) {
                    vh.check.setButtonDrawable(R.drawable.custom_checkbox_green);
                    vh.check.setChecked(true);
                } else if (list.getState().equals("false")) {
                    vh.check.setChecked(false);
                } else if (list.getState().equals("none")) {
                    vh.check.setButtonDrawable(R.drawable.custom_checkbox_red);
                    vh.check.setChecked(true);
                }

                if (list.getPrice() == (null)) {
                    vh.price.setText("-");
                } else if (list.getPrice().equals(0)) {
                    vh.price.setText("N/A");
                } else {
                    vh.price.setText("₱ " + list.getPrice());

                }

            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(UserOrderAcceptedActivity.this).inflate(R.layout.layout_uservieworderlist, parent, false);
                return new VHUserOrderList(view);
            }

            @Override
            public void onDataChanged() {
            }
        }

        ;

        orderlistrv.setAdapter(adapter);
        adapter.startListening();

        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("Order");

        statusRef.child(getIntent().getStringExtra("ORDKEY")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.child("status").getValue().toString().equals("inDelivery")) {
                                progressBar.show();
                                receiptholder.setVisibility(View.VISIBLE);

                                Glide.with(UserOrderAcceptedActivity.this).load(snapshot.child("receipt").getValue().toString()).listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        progressBar.dismiss();
                                        return false;
                                    }
                                }).into(receipt);

                                receipt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent viewIMG = new Intent(UserOrderAcceptedActivity.this, ViewImageActivity.class);
                                        viewIMG.putExtra("image", snapshot.child("receipt").getValue().toString());
                                        startActivity(viewIMG);
                                    }
                                });
                            }
                        }

                        if (snapshot.exists()) {
                            if (snapshot.child("status").getValue().toString().equals("complete")) {
                                Intent intent = new Intent(UserOrderAcceptedActivity.this, UserRatingActivity.class);
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
            orderdesc.setVisibility(View.GONE);
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
                    DatabaseReference listRef = FirebaseDatabase.getInstance().getReference("Order").child(getIntent().getStringExtra("ORDKEY"));
                    listRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Intent viewIMG = new Intent(UserOrderAcceptedActivity.this, ViewImageActivity.class);
                            viewIMG.putExtra("image", snapshot.child("orderlist").getValue().toString());
                            startActivity(viewIMG);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

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

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();
        //Set fee
        DatabaseReference priceRef = FirebaseDatabase.getInstance().getReference("Order").child(getIntent().getStringExtra("ORDKEY"));
        priceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                deliveryFee.setText("₱ " + snapshot.child("price").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}