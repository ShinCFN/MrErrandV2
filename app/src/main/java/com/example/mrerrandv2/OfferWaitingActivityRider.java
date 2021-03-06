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

import java.io.Serializable;

import es.dmoral.toasty.Toasty;

public class OfferWaitingActivityRider extends AppCompatActivity {

    TextView textOffer, texttitle;

    Boolean accepted = false;

    String key = null;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();
    Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_waiting);



       key = ord_open.getKey();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());
        DatabaseReference order = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());

        textOffer = findViewById(R.id.waiting_offer);
        textOffer.setText(getIntent().getStringExtra("OFFER"));

        //Order cancellation listener
        databaseReference.child("status").addValueEventListener(statusListener);

        //Offer listener
        order.addValueEventListener(offerListener);



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

                    if (firebaseUser.getUid().equals(chosenrider) && !accepted) {
                        accepted = true;
                        Toasty.success(OfferWaitingActivityRider.this, "Offer accepted", Toasty.LENGTH_LONG).show();
                        Intent intent = new Intent(OfferWaitingActivityRider.this, AcceptedOrderActivityRider.class);
                        intent.putExtra("RKEY", getIntent().getStringExtra("RIDERKEY"));
                        intent.putExtra("ORDER", ord_open);
                        startActivity(intent);
                        finish();
                    } else if((!firebaseUser.getUid().equals(chosenrider) && !accepted)) {
                        Toasty.info(OfferWaitingActivityRider.this, "Offer was placed for another rider", Toasty.LENGTH_LONG).show();
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
        Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");
        DBOffer dboff = new DBOffer(ord_open.getKey());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        Log.e("Delete Offer Key", ord_open.getKey());
        DatabaseReference mycurrentoffer = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey()).child("Offers");
        mycurrentoffer.child(firebaseUser.getUid()).removeValue();
        finish();
    }

    private ValueEventListener offerListener = new ValueEventListener() {
        DatabaseReference order = FirebaseDatabase.getInstance().getReference("Order").child(key);
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.child("Offers").child(firebaseUser.getUid()).exists()) {
                if (snapshot.child("status").getValue().toString().equals("accepted")) {
                    deleteOthers();
                }


            } else if (!snapshot.child("Offers").child(firebaseUser.getUid()).exists() && snapshot.exists()){
                Toasty.error(OfferWaitingActivityRider.this, "Offer was declined", Toasty.LENGTH_SHORT).show();
                order.removeEventListener(offerListener);
                finish();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private ValueEventListener statusListener = new ValueEventListener() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(key);
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {

            } else {
                Toasty.warning(OfferWaitingActivityRider.this, "Order was cancelled", Toasty.LENGTH_SHORT).show();
                databaseReference.removeEventListener(statusListener);
                finish();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
}