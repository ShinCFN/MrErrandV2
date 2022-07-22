package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class AcceptedOrderActivityUser extends AppCompatActivity {

    CircleImageView profile;
    TextView name, email, cnum, plate;
    ImageView platepic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_order_user);
        profile = findViewById(R.id.profilePic);
        name = findViewById(R.id.riderName);
        email = findViewById(R.id.riderEmail);
        cnum = findViewById(R.id.riderMobile);
        plate = findViewById(R.id.riderPlate);
        platepic = findViewById(R.id.rplatePic);

        DatabaseReference riderReference = FirebaseDatabase.getInstance().getReference("Riders").child(getIntent().getStringExtra("RIDERKEY"));

        riderReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ridername = snapshot.child("firstname").getValue().toString()+" "+snapshot.child("lastname").getValue().toString();

                name.setText(ridername);
                email.setText(snapshot.child("email").getValue().toString());
                cnum.setText(snapshot.child("mobilenum").getValue().toString());
                plate.setText(snapshot.child("plate").getValue().toString());

                Picasso.get().load(snapshot.child("profileImage").getValue().toString()).into(profile);
                Picasso.get().load(snapshot.child("platePic").getValue().toString()).into(platepic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("Order");

        statusRef.child(getIntent().getStringExtra("ORDKEY")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("status").getValue().toString().equals("inDelivery")){
                    TextView state = findViewById(R.id.status);
                    state.setText("YOUR RIDER IS ON THE WAY");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}