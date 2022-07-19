package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeliveryActivityRider extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_rider);

//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(getIntent().getStringExtra("KEY"));
//
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String firstname = snapshot.child("firstname").getValue().toString();
//                String lastname = snapshot.child("lastname").getValue().toString();
//                String email = snapshot.child("useremail").getValue().toString();
//                String address = snapshot.child("address").getValue().toString();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


    }
}