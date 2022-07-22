package com.example.mrerrandv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AcceptedOrderActivityRider extends AppCompatActivity {

    TextView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_order_rider);
        Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());

        list = findViewById(R.id.orderlist);
        list.setText(ord_open.getOrderlist());

        Button next = findViewById(R.id.btnAccToOTW);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("status").setValue("inDelivery").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(AcceptedOrderActivityRider.this, DeliveryActivityRider.class);
                        intent.putExtra("ORDER", ord_open);
                        intent.putExtra("RKEY", getIntent().getStringExtra("RKEY"));
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });


    }
}