package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    Button complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_rider);

        Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");

        Log.e("test", ord_open.getKey());

        databaseReference.child(ord_open.getKey()).child("uid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String uid = snapshot.getValue().toString();

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

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
                        mobile.setText(snapshot.child("mobilenum").getValue().toString());
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
                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());
                orderRef.child("status").setValue("complete");
                finish();
            }
        });

    }
}