package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeliveryActivityRider extends AppCompatActivity {

    TextView name, mobile, street, city, province, zip, email;
    CircleImageView profile;

    String userid = null;

    Button complete;

    String key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_rider);

        Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");
        key = ord_open.getKey();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");

        databaseReference.child(ord_open.getKey()).child("uid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String uid = snapshot.getValue().toString();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

                userid = uid;

                userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        profile = findViewById(R.id.profilePic);
                        name = findViewById(R.id.profileName);
                        mobile = findViewById(R.id.profileNumber);
                        street = findViewById(R.id.profileStreet);
                        city = findViewById(R.id.profileCity);
                        province = findViewById(R.id.profileProvince);
                        zip = findViewById(R.id.profileZip);
                        email = findViewById(R.id.profileEmail);

                        String username = snapshot.child("firstname").getValue().toString()+" "+snapshot.child("lastname").getValue().toString();

                        Picasso.get().load(snapshot.child("profileImage").getValue().toString()).into(profile);
                        name.setText(username);
                        mobile.setText(snapshot.child("mobile").getValue().toString());
                        street.setText(snapshot.child("street").getValue().toString());
                        city.setText(snapshot.child("city").getValue().toString());
                        province.setText(snapshot.child("province").getValue().toString());
                        zip.setText(snapshot.child("zip").getValue().toString());
                        email.setText(snapshot.child("email").getValue().toString());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        complete = findViewById(R.id.btnComplete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order").child(key);
                orderRef.child("status").setValue("complete");
                Intent intent = new Intent(DeliveryActivityRider.this, RatingActivityTowardsUser.class);
                intent.putExtra("user", userid);

                startActivity(intent);
                finish();
            }
        });

    }
}