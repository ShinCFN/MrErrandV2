package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OfferWaitingActivityRider extends AppCompatActivity {

    TextView textOffer, texttitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_waiting);
        Order ord_open = (Order) getIntent().getSerializableExtra("ORD");

        textOffer = findViewById(R.id.waiting_offer);
        textOffer.setText(getIntent().getStringExtra("OFFER"));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String myid = firebaseUser.getUid();


        DatabaseReference currentOrder = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey()).child("Offers");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());

        currentOrder.child(myid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(!snapshot.exists()){
                   Toast.makeText(OfferWaitingActivityRider.this, "Offer was declined", Toast.LENGTH_LONG).show();
                   finish();
               }

               Log.e("TEST", ord_open.getKey());

               if(snapshot.exists()&& snapshot.child("state").getValue().toString().equals("accepted")){
                   databaseReference.child("isAccepted").setValue("true");
                   Intent intent = new Intent(OfferWaitingActivityRider.this, AcceptedOrderActivityRider.class);
                   intent.putExtra("KEY", getIntent().getStringExtra("ORD"));
                   intent.putExtra("RKEY", getIntent().getStringExtra("RIDERKEY"));
                   intent.putExtra("OPEN", ord_open);
                   startActivity(intent);
                   finish();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("isAccepted").getValue().toString().equals("true") && snapshot.child("Offers").child(getIntent().getStringExtra("RIDERKEY")).toString()!=auth.getCurrentUser().getUid()){
                    Log.e("WAT", "FACK");
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