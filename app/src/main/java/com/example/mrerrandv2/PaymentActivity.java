package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PaymentActivity extends AppCompatActivity {

    LinearLayout COD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        TextView textview = findViewById(R.id.orderlistforpay);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String orderlist = getIntent().getStringExtra("ORDER");
        FirebaseUser firebaseUser = auth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        DBOrder dbord = new DBOrder();

        textview.setText(orderlist);
        String textFirstName = firebaseUser.getDisplayName();

        String state = "false";
        String status = "null";
        Boolean type = getIntent().getExtras().getBoolean("type");

        COD = findViewById(R.id.COD);
        COD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get user details
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String lastname = snapshot.child("lastname").getValue().toString();
                        String profilePic = snapshot.child("profileImage").getValue().toString();
                        String uid = firebaseUser.getUid();

                        if (type){
                            String textOrderList = getIntent().getStringExtra("imgorder");
                            //Send order to DB
                            Order ord = new Order(textFirstName, textOrderList, state, lastname,profilePic, status, uid, type.toString());
                            dbord.add(ord).addOnSuccessListener(suc -> {
                                databaseReference.orderByChild("firstname").equalTo(firebaseUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                            String key = childSnapshot.getKey();
                                            Log.e("Key", key);
                                            Toast.makeText(PaymentActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(PaymentActivity.this, ViewOfferActivity.class);
                                            intent.putExtra("Key", key);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            });
                        } else if (!type){
                            String textOrderList = orderlist;
                            //Send order to DB
                            Order ord = new Order(textFirstName, textOrderList, state, lastname,profilePic, status, uid, type.toString());
                            dbord.add(ord).addOnSuccessListener(suc -> {
                                databaseReference.orderByChild("firstname").equalTo(firebaseUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                            String key = childSnapshot.getKey();
                                            Log.e("Key", key);
                                            Toast.makeText(PaymentActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(PaymentActivity.this, ViewOfferActivity.class);
                                            intent.putExtra("Key", key);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }
}