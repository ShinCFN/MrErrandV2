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

import com.google.android.gms.tasks.OnSuccessListener;
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
        DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference("Riders").child(firebaseUser.getUid());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());
        DBOffer dboff = new DBOffer(ord_open.getKey());

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offerIn=findViewById(R.id.editTextOfferInput);

                String offerval = offerIn.getText().toString();

                if(offerval.isEmpty()){

                }else{

                    riderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String ridername = snapshot.child("firstname").getValue().toString();

                            databaseReference.child("Offers").child(firebaseUser.getUid()).child("ridername").setValue(ridername);
                            databaseReference.child("Offers").child(firebaseUser.getUid()).child("offer").setValue(offerval);
                            databaseReference.child("Offers").child(firebaseUser.getUid()).child("state").setValue("open").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Intent intent = new Intent(ViewOrderActivity.this, OfferWaitingActivityRider.class);
                                    intent.putExtra("ORD", ord_open);
                                    intent.putExtra("OFFER", offerval);
                                    intent.putExtra("RIDERID", firebaseUser.getUid());
                                    startActivity(intent);
                                    finish();
                                }
                            });

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