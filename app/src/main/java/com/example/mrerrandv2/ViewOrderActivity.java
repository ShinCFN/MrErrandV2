package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewOrderActivity extends AppCompatActivity {

    TextView order;
    EditText offerIn;
    ImageView imgorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        Order ord_open = (Order) getIntent().getSerializableExtra("OPEN");

        order = findViewById(R.id.orderlistforoffer);
        order.setText(ord_open.getOrderlist());
        Button btnSend = findViewById(R.id.btnOffer);
        offerIn = findViewById(R.id.editTextOfferInput);
        imgorder = findViewById(R.id.imgorder);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference("Riders").child(firebaseUser.getUid());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());
        DBOffer dboff = new DBOffer(ord_open.getKey());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("ordertype").getValue().toString().equals("true")){
                    imgorder.setVisibility(View.VISIBLE);
                    Picasso.get().load(snapshot.child("orderlist").getValue().toString()).into(imgorder);
                }else{
                    order.setVisibility(View.VISIBLE);
                    order.setText(snapshot.child("orderlist").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String offerval = offerIn.getText().toString();

                if (offerval.isEmpty()) {

                } else {

                    riderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String ridername = snapshot.child("firstname").getValue().toString();
                            String state = "open";

                            AddOffer addOffer = new AddOffer(ridername, offerval, state);

                            databaseReference.child("Offers").child(firebaseUser.getUid()).setValue(addOffer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Intent intent = new Intent(ViewOrderActivity.this, OfferWaitingActivityRider.class);
                                    intent.putExtra("ORDER", ord_open);
                                    intent.putExtra("OFFER", offerval);
                                    intent.putExtra("RIDERID", firebaseUser.getUid());
                                    startActivity(intent);
                                    finish();
                                }
                            });
//                            databaseReference.child("Offers").child(firebaseUser.getUid()).child("ridername").setValue(ridername).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    databaseReference.child("Offers").child(firebaseUser.getUid()).child("offer").setValue(offerval).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            databaseReference.child("Offers").child(firebaseUser.getUid()).child("state").setValue("open").addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void unused) {
//
//                                                }
//                                            });
//                                        }
//                                    });
//                                }
//                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}