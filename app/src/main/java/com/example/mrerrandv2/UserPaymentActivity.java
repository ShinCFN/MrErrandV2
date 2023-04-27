package com.example.mrerrandv2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class UserPaymentActivity extends AppCompatActivity {

    ConstraintLayout COD, textorderlayout, imgorderlayout;
    RecyclerView orderlistrv;
    ArrayList<OrderList> list;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    AdapterPaymentOrderList adapter;
    private long lastClickTime = 0;
    progressBar progressBar;
    Boolean ordertype;
    ImageView orderImageView;

    TextView toolMain;

    TextView receiptdate, receiptname, purchasenum, textDesiredStore;
    ImageView toolbarback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String orderlist = getIntent().getStringExtra("ORDER");
        String desiredStore = getIntent().getStringExtra("store");
        FirebaseUser firebaseUser = auth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid()).child("OrderList");

        progressBar = new progressBar(this);
        list = new ArrayList<>();
        adapter = new AdapterPaymentOrderList(this, list);
        orderlistrv = findViewById(R.id.orderlistrv);
        orderlistrv.setHasFixedSize(true);
        orderlistrv.setLayoutManager(new LinearLayoutManager(this));
        orderlistrv.setAdapter(adapter);

        toolbarback = findViewById(R.id.toolbarback);
        receiptdate = findViewById(R.id.receiptdate);
        receiptname = findViewById(R.id.receiptname);
        purchasenum = findViewById(R.id.purchasenum);
        textorderlayout = findViewById(R.id.textorderlayout);
        imgorderlayout = findViewById(R.id.imgorderlayout);
        orderImageView = findViewById(R.id.orderImageView);
        textDesiredStore = findViewById(R.id.textDesiredStore);
        toolMain = findViewById(R.id.ordertype);

        DBOrder dbord = new DBOrder();

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.finalLightGreen));
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.newGray));
        }

        //Set Order Type
        ordertype = getIntent().getExtras().getBoolean("type");

        //Prog bar
        progressBar.show();

        toolbarback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(ordertype){
            imgorderlayout.setVisibility(View.VISIBLE);
            String img = getIntent().getExtras().getString("imgorder");
//            Picasso.get().load(img).into(orderImageView);

            Glide.with(this).load(img).placeholder(R.color.white).listener(new RequestListener<Drawable>() {
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
            }).into(orderImageView);

            toolMain.setText("Image order");

        } else {

            toolMain.setText("Order list");

            textorderlayout.setVisibility(View.VISIBLE);
            progressBar.dismiss();
        }

        String textFirstName = firebaseUser.getDisplayName();

        String state = "false";
        String status = "null";
        Boolean type = getIntent().getExtras().getBoolean("type");

        //Toolbar
        toolbarback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Order List Recycler View
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    OrderList orderList = dataSnapshot.getValue(OrderList.class);
                    list.add(orderList);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //Set Receipt Info
        if (!ordertype) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
            format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            String time = format.format(calendar.getTime());
            receiptdate.setText(time);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long num = snapshot.getChildrenCount();

                    if (Integer.valueOf((int) num).equals(1)) {
                        purchasenum.setText("(total " + num + " item)");
                    } else {
                        purchasenum.setText("(total " + num + " items)");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference userDetails = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
            userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String username = snapshot.child("firstname").getValue() + " " + snapshot.child("lastname").getValue();
                    receiptname.setText(username);
                    textDesiredStore.setText("Store: " + desiredStore);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


        DatabaseReference proceed = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
        //Proceed
        COD = findViewById(R.id.payBtn);
        COD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.show();
                //Get user details
                proceed.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                            return;
                        }

                        lastClickTime = SystemClock.elapsedRealtime();

                        String lastname = snapshot.child("lastname").getValue().toString();
                        String profilePic = snapshot.child("profileImage").getValue().toString();
                        String uid = firebaseUser.getUid();

                        String totalstars = snapshot.child("totalstars").getValue().toString();
                        String totalrates = snapshot.child("totalrates").getValue().toString();


                        if (type && totalstars.equals("0") && totalrates.equals("0")) {
                            int rating = 0;
                            String textOrderList = getIntent().getStringExtra("imgorder");
                            //Send order to DB
                            Order ord = new Order(textFirstName, textOrderList, state, lastname, profilePic, status, uid, type.toString(), rating, desiredStore);
                            dbord.add(ord).addOnSuccessListener(suc -> {
                                databaseReference.orderByChild("firstname").equalTo(firebaseUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                            String key = childSnapshot.getKey();
                                            Toast.makeText(UserPaymentActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(UserPaymentActivity.this, ViewOfferActivity.class);
                                            intent.putExtra("Key", key);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            });
                        } else if (type && !totalstars.equals("0") && !totalrates.equals("0")) {

                            int rating = Integer.valueOf(totalstars) / Integer.valueOf(totalrates);

                            String textOrderList = getIntent().getStringExtra("imgorder");
                            //Send order to DB
                            Order ord = new Order(textFirstName, textOrderList, state, lastname, profilePic, status, uid, type.toString(), rating, desiredStore);
                            dbord.add(ord).addOnSuccessListener(suc -> {
                                databaseReference.orderByChild("firstname").equalTo(firebaseUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                            String key = childSnapshot.getKey();
                                            Toast.makeText(UserPaymentActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(UserPaymentActivity.this, ViewOfferActivity.class);
                                            intent.putExtra("Key", key);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            });

                        } else if (!type && totalstars.equals("0") && totalrates.equals("0")) {

                            int rating = 0;

                            String textOrderList = orderlist;
                            //Send order to DB
                            Order ord = new Order(textFirstName, textOrderList, state, lastname, profilePic, status, uid, type.toString(), rating, desiredStore);
                            dbord.add(ord).addOnSuccessListener(suc -> {
                                databaseReference.orderByChild("firstname").equalTo(firebaseUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                            String key = childSnapshot.getKey();

                                            Toast.makeText(UserPaymentActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(UserPaymentActivity.this, ViewOfferActivity.class);
                                            intent.putExtra("Key", key);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            });
                        } else if (!type && !totalstars.equals("0") && !totalrates.equals("0")) {

                            int rating = Integer.valueOf(totalstars) / Integer.valueOf(totalrates);

                            String textOrderList = orderlist;
                            //Send order to DB
                            Order ord = new Order(textFirstName, textOrderList, state, lastname, profilePic, status, uid, type.toString(), rating, desiredStore);
                            dbord.add(ord).addOnSuccessListener(suc -> {
                                databaseReference.orderByChild("firstname").equalTo(firebaseUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                            String key = childSnapshot.getKey();

                                            Toast.makeText(UserPaymentActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(UserPaymentActivity.this, ViewOfferActivity.class);
                                            intent.putExtra("Key", key);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            });

                        }
                        progressBar.dismiss();
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
    }
}