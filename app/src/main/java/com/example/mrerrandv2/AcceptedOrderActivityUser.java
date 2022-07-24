package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    ImageView platepic, receipt;
    Boolean activityDone = false;

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
        receipt = findViewById(R.id.receipt);

        DatabaseReference riderReference = FirebaseDatabase.getInstance().getReference("Riders").child(getIntent().getStringExtra("RIDERKEY"));

        riderReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ridername = snapshot.child("firstname").getValue().toString() + " " + snapshot.child("lastname").getValue().toString();

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
                if (!activityDone) {
                    if (snapshot.child("status").getValue().toString().equals("inDelivery")) {
                        TextView state = findViewById(R.id.status);
                        state.setText("YOUR RIDER IS ON THE WAY\n\n PLEASE PREPARE THE AMOUNT SPECIFIED");
                        receipt.setVisibility(View.VISIBLE);
                        Picasso.get().load(snapshot.child("receipt").getValue().toString()).into(receipt);

                        receipt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent viewIMG = new Intent(AcceptedOrderActivityUser.this, ViewImageActivity.class);
                                viewIMG.putExtra("image", snapshot.child("profilePic").getValue().toString());
                                startActivity(viewIMG);
                            }
                        });
                    }
                }

                if (!activityDone) {
                    if (snapshot.child("status").getValue().toString().equals("complete")) {
                        activityDone = true;
                        Intent intent = new Intent(AcceptedOrderActivityUser.this, RatingActivity.class);
                        intent.putExtra("rider", getIntent().getStringExtra("RIDERKEY"));
                        intent.putExtra("order", getIntent().getStringExtra("ORDKEY"));
                        startActivity(intent);
                        finish();
                    }
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

    @Override
    public void onStop () {
        // Do your stuff here
        super.onStop();
    }
}