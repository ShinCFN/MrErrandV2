package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
        Order ord_open = (Order) getIntent().getSerializableExtra("OPEN");

        textOffer = findViewById(R.id.waiting_offer);
        textOffer.setText(getIntent().getStringExtra("OFFER"));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Order");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(getIntent().getStringExtra("KEY")).child("Offers");

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.child(getIntent().getStringExtra("KEY")).exists()) {
                    Toast.makeText(OfferWaitingActivityRider.this, "Order was cancelled", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OfferWaitingActivityRider.this, RiderLandingPage.class);
                    startActivity(intent);
                    finish();
                }

                if(!snapshot.child(getIntent().getStringExtra("KEY")).child("Offers").child(getIntent().getStringExtra("RKEY")).exists()){
                    Toast.makeText(OfferWaitingActivityRider.this, "Offer was declined", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OfferWaitingActivityRider.this, RiderLandingPage.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.orderByChild("isAccepted").equalTo("true").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                texttitle = findViewById(R.id.offertitle);
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String orderkey = childSnapshot.getKey();
                    databaseReference.child(orderkey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String username = snapshot.child("ridername").getValue().toString();
                            String state = snapshot.child("isAccepted").getValue().toString();
                            String currentUser = firebaseUser.getDisplayName();
                            Log.e("CHANGE", username);

                            if (username.equals(currentUser) && state.equals("true")) {
                                Intent intent = new Intent(OfferWaitingActivityRider.this, AcceptedOrderActivityRider.class);
                                intent.putExtra("KEY", getIntent().getStringExtra("KEY"));
                                intent.putExtra("RKEY", getIntent().getStringExtra("RKEY"));
                                intent.putExtra("OPEN", ord_open);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(OfferWaitingActivityRider.this, "Order was placed for another rider", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(OfferWaitingActivityRider.this, RiderLandingPage.class);
                                startActivity(intent);
                                finish();
                            }
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

    @Override
    public void onBackPressed() {
        String key = getIntent().getStringExtra("KEY");
        String riderkey = getIntent().getStringExtra("RKEY");
        super.onBackPressed();
        DBOffer dboff = new DBOffer(key);

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Order");

        Log.e("Delete Offer Key", riderkey);

        dboff.remove(riderkey).addOnSuccessListener(suc ->
        {
            Toast.makeText(OfferWaitingActivityRider.this, "Offer Canceled", Toast.LENGTH_LONG).show();
        }).addOnFailureListener(er ->
        {
            Toast.makeText(OfferWaitingActivityRider.this, "" + er.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}