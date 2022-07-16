package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewOrderActivity extends AppCompatActivity {

    TextView order;
    EditText offerIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        Order ord_open = (Order) getIntent().getSerializableExtra("OPEN");

        order = findViewById(R.id.orderlistforoffer);

        order.setText(ord_open.getOrderlist());

        Button btnSend = findViewById(R.id.btnOffer);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference("Riders").child(firebaseUser.getUid());
        DBOffer dboff = new DBOffer(ord_open.getKey());

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Send offer to DB

                offerIn = (EditText) findViewById(R.id.editTextOfferInput);
                String offerText = offerIn.getText().toString();
                String textFirstName = firebaseUser.getDisplayName();
                String state = "false";

                UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Log.e("ID", firebaseUser.getUid());

                        String lastname = snapshot.child("lastname").getValue().toString();
                        String email = snapshot.child("email").getValue().toString();
                        String mnumber = snapshot.child("mobilenum").getValue().toString();
                        String platenum = snapshot.child("licensenum").getValue().toString();

                        if (!offerText.isEmpty()) {
                            AddOffer off = new AddOffer(textFirstName, offerText, state, lastname, email, mnumber, platenum);
                            dboff.add(off).addOnSuccessListener(suc -> {
                                databaseReference.child(ord_open.getKey()).child("Offers").orderByChild("ridername").equalTo(firebaseUser.getDisplayName()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                            String rkey = childSnapshot.getKey();
                                            Intent intent = new Intent(ViewOrderActivity.this, OfferWaitingActivityRider.class);
                                            intent.putExtra("OPEN", ord_open);
                                            intent.putExtra("OFFER", offerText);
                                            intent.putExtra("KEY", ord_open.getKey());
                                            intent.putExtra("RKEY", rkey);
                                            intent.putExtra("State", ord_open.isAccepted);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            });
                        } else {
                            Toast.makeText(ViewOrderActivity.this,"Offer is empty", Toast.LENGTH_LONG).show();
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
        startActivity(new Intent(ViewOrderActivity.this, RiderLandingPage.class));
        finish();
    }
}