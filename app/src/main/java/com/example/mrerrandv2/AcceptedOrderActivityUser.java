package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AcceptedOrderActivityUser extends AppCompatActivity {

    TextView name, email, cnum, plate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_order_user);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(getIntent().getStringExtra("Key"));

        databaseReference.child("Offers").child(getIntent().getStringExtra("RKEY")).child("isAccepted").setValue("true");

        databaseReference.child("status").setValue("inProgress");
        databaseReference.child("isAccepted").setValue("Accepted");

        name = findViewById(R.id.acceptName);
        email = findViewById(R.id.acceptEmail);
        cnum = findViewById(R.id.acceptNum);
        plate = findViewById(R.id.acceptPlate);

        databaseReference.child("Offers").child(getIntent().getStringExtra("RKEY")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("ridername").getValue().toString());
                email.setText(snapshot.child("rideremail").getValue().toString());
                cnum.setText(snapshot.child("mobilenum").getValue().toString());
                plate.setText(snapshot.child("platenum").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue().toString();

                if(status.equals("inDelivery")){

                    Intent intent = new Intent(AcceptedOrderActivityUser.this, DeliveryActivityUser.class);
                    intent.putExtra("KEY", getIntent().getStringExtra("KEY"));
                    intent.putExtra("RKEY", getIntent().getStringExtra("RKEY"));
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