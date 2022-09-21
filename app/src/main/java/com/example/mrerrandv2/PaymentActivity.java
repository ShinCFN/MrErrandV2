package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class PaymentActivity extends AppCompatActivity {

    ConstraintLayout COD;
    RecyclerView orderlistrv;
    ArrayList<OrderList> list;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    PaymentOrderListAdapter adapter;
    private long lastClickTime = 0;
    progressBar progressBar;

    TextView receiptdate, receiptname, purchasenum;
    ImageView toolbarback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String orderlist = getIntent().getStringExtra("ORDER");
        FirebaseUser firebaseUser = auth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid()).child("OrderList");

        progressBar = new progressBar(this);
        list = new ArrayList<>();
        adapter = new PaymentOrderListAdapter(this, list);
        orderlistrv = findViewById(R.id.orderlistrv);
        orderlistrv.setHasFixedSize(true);
        orderlistrv.setLayoutManager(new LinearLayoutManager(this));
        orderlistrv.setAdapter(adapter);

        toolbarback = findViewById(R.id.toolbarback);
        receiptdate = findViewById(R.id.receiptdate);
        receiptname = findViewById(R.id.receiptname);
        purchasenum = findViewById(R.id.purchasenum);

        DBOrder dbord = new DBOrder();

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(PaymentActivity.this, R.color.finalBackground));

        //Toolbar
        TextView toolMain = findViewById(R.id.toolbarmain);
        TextView toolSub = findViewById(R.id.toolbarsub);
        toolMain.setText("");
        toolSub.setText("");

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
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        String time = format.format(calendar.getTime());
        receiptdate.setText(time);

        receiptname.setText(auth.getCurrentUser().getDisplayName());

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

                        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
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
                            Order ord = new Order(textFirstName, textOrderList, state, lastname, profilePic, status, uid, type.toString(), rating);
                            dbord.add(ord).addOnSuccessListener(suc -> {
                                databaseReference.orderByChild("firstname").equalTo(firebaseUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                            String key = childSnapshot.getKey();
                                            Toast.makeText(PaymentActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(PaymentActivity.this, ViewOfferActivity.class);
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
                            Order ord = new Order(textFirstName, textOrderList, state, lastname, profilePic, status, uid, type.toString(), rating);
                            dbord.add(ord).addOnSuccessListener(suc -> {
                                databaseReference.orderByChild("firstname").equalTo(firebaseUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                            String key = childSnapshot.getKey();
                                            Toast.makeText(PaymentActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(PaymentActivity.this, ViewOfferActivity.class);
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
                            Order ord = new Order(textFirstName, textOrderList, state, lastname, profilePic, status, uid, type.toString(), rating);
                            dbord.add(ord).addOnSuccessListener(suc -> {
                                databaseReference.orderByChild("firstname").equalTo(firebaseUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                            String key = childSnapshot.getKey();
                                            Log.e("Key", key);
                                            Toast.makeText(PaymentActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(PaymentActivity.this, ViewOfferActivity.class);
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
                            Order ord = new Order(textFirstName, textOrderList, state, lastname, profilePic, status, uid, type.toString(), rating);
                            dbord.add(ord).addOnSuccessListener(suc -> {
                                databaseReference.orderByChild("firstname").equalTo(firebaseUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                            String key = childSnapshot.getKey();
                                            Log.e("Key", key);
                                            Toast.makeText(PaymentActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(PaymentActivity.this, ViewOfferActivity.class);
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