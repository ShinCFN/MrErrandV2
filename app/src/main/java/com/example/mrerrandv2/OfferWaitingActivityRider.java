package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class OfferWaitingActivityRider extends AppCompatActivity {

    TextView textOffer, texttitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_waiting);
        Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");

        textOffer = findViewById(R.id.waiting_offer);
        textOffer.setText(getIntent().getStringExtra("OFFER"));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String myid = firebaseUser.getUid();


        //                    databaseReference.child("isAccepted").setValue("true");
//                    Intent intent = new Intent(OfferWaitingActivityRider.this, AcceptedOrderActivityRider.class);
//                    intent.putExtra("KEY", getIntent().getStringExtra("ORD"));
//                    intent.putExtra("RKEY", getIntent().getStringExtra("RIDERKEY"));
//                    intent.putExtra("OPEN", ord_open);
//                    startActivity(intent);
//                    finish();


        DatabaseReference order = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());


        //Order cancellation listener
        databaseReference.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                } else {
                    Toasty.warning(OfferWaitingActivityRider.this, "Order was cancelled", Toasty.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Offer listener
        order.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("Offers").child(firebaseUser.getUid()).exists()) {
                    if (snapshot.child("status").getValue().toString().equals("accepted")) {
                        deleteOthers();
                    }


                } else if (!snapshot.child("Offers").child(firebaseUser.getUid()).exists() && snapshot.exists()){
                    Toasty.error(OfferWaitingActivityRider.this, "Offer was declined", Toasty.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        databaseReference.orderByChild("isAccepted").equalTo("true").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                texttitle = findViewById(R.id.offertitle);
//                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
//                    String orderkey = childSnapshot.getKey();
//                    databaseReference.child(orderkey).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            String username = snapshot.child("ridername").getValue().toString();
//                            String state = snapshot.child("isAccepted").getValue().toString();
//                            String currentUser = firebaseUser.getDisplayName();
//                            Log.e("CHANGE", username);
//
//                            if (username.equals(currentUser) && state.equals("true")) {
//                                Intent intent = new Intent(OfferWaitingActivityRider.this, AcceptedOrderActivityRider.class);
//                                intent.putExtra("KEY", getIntent().getStringExtra("KEY"));
//                                intent.putExtra("RKEY", getIntent().getStringExtra("RKEY"));
//                                intent.putExtra("OPEN", ord_open);
//                                startActivity(intent);
//                                finish();
//                            } else {
//                                Toast.makeText(OfferWaitingActivityRider.this, "Order was placed for another rider", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(OfferWaitingActivityRider.this, RiderLandingPage.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
    }

    private void deleteOthers() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());
        databaseReference.child("Offers").orderByChild("state").equalTo("accepted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    postSnapshot.getKey();
                    String chosenrider = postSnapshot.getKey();

                    if (firebaseUser.getUid().equals(chosenrider)) {
                        Toasty.success(OfferWaitingActivityRider.this, "Offer accepted", Toasty.LENGTH_LONG).show();
                        Intent intent = new Intent(OfferWaitingActivityRider.this, AcceptedOrderActivityRider.class);
                        intent.putExtra("RKEY", getIntent().getStringExtra("RIDERKEY"));
                        intent.putExtra("ORDER", ord_open);
                        startActivity(intent);
                        finish();
                    } else {
                        Toasty.success(OfferWaitingActivityRider.this, "Offer was placed for another rider", Toasty.LENGTH_LONG).show();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        Order ord_open = (Order) getIntent().getSerializableExtra("ORD");
        DBOffer dboff = new DBOffer(ord_open.getKey());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        Log.e("Delete Offer Key", ord_open.getKey());
        DatabaseReference mycurrentoffer = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey()).child("Offers");
        mycurrentoffer.child(firebaseUser.getUid()).removeValue();
        finish();
    }
}