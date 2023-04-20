package com.example.mrerrandv2;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class RatingActivityTowardsRider extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    RatingBar ratingBar;

    DatabaseReference transacRef, orderRef;

    String receiptimg;

    int myRating = 0;
    SaveTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratingtorider);

        String riderkey = getIntent().getStringExtra("rider");
        String orderKey = getIntent().getStringExtra("order");

        ratingBar = findViewById(R.id.rating);

        transacRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid()).child("Transactions");
        orderRef = FirebaseDatabase.getInstance().getReference("Order").child(orderKey);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                int rating = (int) v;
                String message = null;

                myRating = (int) ratingBar.getRating();

                switch (rating) {

                    case 1:
                        message = "Sorry to hear that";
                        break;
                    case 2:
                        message = "Sorry to hear that";
                        break;
                    case 3:
                        message = "Good enough!";
                        break;
                    case 4:
                        message = "Great! Thank you!";
                        break;
                    case 5:
                        message = "Awesome!";
                        break;
                }

                Toasty.normal(RatingActivityTowardsRider.this, message, Toasty.LENGTH_LONG).show();

                DatabaseReference rateRef = FirebaseDatabase.getInstance().getReference("Riders").child(riderkey);

                rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child("totalstars").exists() && snapshot.child("totalrates").exists()) {
                            String totalstars = snapshot.child("totalstars").getValue().toString();
                            String totalrates = snapshot.child("totalrates").getValue().toString();

                            int newtotalstars = Integer.valueOf(totalstars) + myRating;
                            int newtotalrates = Integer.valueOf(totalrates) + 1;

                            rateRef.child("totalstars").setValue(newtotalstars).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    rateRef.child("totalrates").setValue(newtotalrates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");

                                            //Save to Transaction history
                                            databaseReference.child(getIntent().getStringExtra("order")).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    //Get date
                                                    String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());

                                                    //Get simple ate
                                                    String simpleDate = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date());

                                                    //Get time
                                                    String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

                                                    //Get timestamp
                                                    Long tsLong = System.currentTimeMillis()/1000;
                                                    String ts = tsLong.toString();

                                                    //Get receipt
                                                    receiptimg = snapshot.child("receipt").getValue().toString();

                                                    //If using text order
                                                    if (snapshot.child("ordertype").getValue().toString().equals("false")) {
                                                        transaction = new SaveTransaction("none", "false", currentDate, simpleDate, ts, currentTime, snapshot.child("rider").getValue().toString(), myRating, receiptimg, auth.getCurrentUser().getUid());
                                                        String transacKey = transacRef.push().getKey();
                                                        transacRef.child(transacKey).setValue(transaction).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                DatabaseReference orderlistRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid()).child("OrderList");
                                                                orderlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        transacRef.child(transacKey).child("OrderList").setValue(snapshot.getValue());
                                                                            transacRef.child(transacKey).child("receipt").setValue(receiptimg);
                                                                            //Save Chat
                                                                        orderRef.child("Chat").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                transacRef.child(transacKey).child("Chat").setValue(snapshot.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                        //Delete order
                                                                                        databaseReference.child(getIntent().getStringExtra("order")).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {
                                                                                                finish();
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                            }
                                                        });

                                                        // If using IMG order
                                                    } else {

                                                        transaction = new SaveTransaction(snapshot.child("orderlist").getValue().toString(), "true", currentDate, simpleDate, ts, currentTime, snapshot.child("rider").getValue().toString(), myRating, receiptimg, auth.getCurrentUser().getUid());
                                                        String transacKey = transacRef.push().getKey();
                                                        transacRef.child(transacKey).setValue(transaction);
                                                        //Save Chat
                                                        orderRef.child("Chat").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                transacRef.child(transacKey).child("Chat").setValue(snapshot.getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                        //Delete order
                                                                        databaseReference.child(getIntent().getStringExtra("order")).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                finish();
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });
                                }
                            });


                        } else {
                            int newrating = myRating;
                            int newtotalrates = 1;

                            rateRef.child("totalstars").setValue(newrating).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    rateRef.child("totalrates").setValue(newtotalrates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");

                                            databaseReference.child(getIntent().getStringExtra("order")).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    finish();
                                                }
                                            });
                                        }
                                    });
                                }
                            });

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
        finish();
    }

}