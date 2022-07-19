package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
        name = findViewById(R.id.acceptName);
        email = findViewById(R.id.acceptEmail);
        cnum = findViewById(R.id.acceptNum);
        plate = findViewById(R.id.acceptPlate);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(getIntent().getStringExtra("ORDKEY"));
        DatabaseReference riderReference = FirebaseDatabase.getInstance().getReference("Riders").child(getIntent().getStringExtra("RIDERKEY"));

        Log.e("ORDER KEY AFTER", getIntent().getStringExtra("ORDKEY"));
        Log.e("RIDER KEY AFTER", getIntent().getStringExtra("RIDERKEY"));

        riderReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ridername = snapshot.child("firstname").getValue().toString()+" "+snapshot.child("lastname").getValue().toString();

                name.setText(ridername);
                email.setText(snapshot.child("email").getValue().toString());
                cnum.setText(snapshot.child("mobilenum").getValue().toString());
                plate.setText(snapshot.child("plate").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//
//
//
//
//
//
//        databaseReference.child("state").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String status = snapshot.getValue().toString();
//
//                if(status.equals("inDelivery")){
//
//                    Intent intent = new Intent(AcceptedOrderActivityUser.this, DeliveryActivityUser.class);
//                    intent.putExtra("KEY", getIntent().getStringExtra("KEY"));
//                    intent.putExtra("RKEY", getIntent().getStringExtra("RKEY"));
//                    startActivity(intent);
//                    finish();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}