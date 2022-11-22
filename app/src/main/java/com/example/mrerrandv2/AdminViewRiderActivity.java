package com.example.mrerrandv2;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AdminViewRiderActivity extends AppCompatActivity {

    TextView editName, editMobile, editEmail, editLicense, editPlate, verify;

    ImageView profilepic,profLicense,profPlate,profOR;

    progressBar progressBar;

    DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference("Riders");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_rider);


        Rider rider = (Rider) getIntent().getSerializableExtra("rider");


        progressBar = new progressBar(this);
        progressBar.show();

        //Find Holders

        editName = findViewById(R.id.profileName);
        editMobile = findViewById(R.id.profileNumber);
        editLicense = findViewById(R.id.profileLicense);
        editPlate = findViewById(R.id.profilePlate);
        editEmail = findViewById(R.id.profileEmail);
        profilepic = findViewById(R.id.profilePic);
        verify = findViewById(R.id.btnVerify);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                riderRef.child(rider.getKey()).child("verified").setValue("true");
                finish();
            }
        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Riders").child(rider.getKey());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Get Information

                String firstname = snapshot.child("firstname").getValue().toString();
                String lastname = snapshot.child("lastname").getValue().toString();
                String mobilenumber = snapshot.child("mobilenum").getValue().toString();
                String email = snapshot.child("email").getValue().toString();

                String name = firstname + " " + lastname;

                editName.setText(name);
                editMobile.setText(mobilenumber);
                editEmail.setText(email);

                profLicense = findViewById((R.id.rlicensePic));
                profPlate = findViewById((R.id.rplatePic));
                profOR = findViewById((R.id.rorcrPic));

                //Set profile image
                if (snapshot.child("profileImage").exists()) {
                    String image = snapshot.child("profileImage").getValue().toString();

                    Picasso
                            .get()
                            .load(image)
                            .into(profilepic);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.dismiss();
                        }
                    }, 1000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.dismiss();
                        }
                    }, 1000);
                }
                //Set license image
                if (snapshot.child("licensePic").exists()) {
                    String image = snapshot.child("licensePic").getValue().toString();

                    Picasso
                            .get()
                            .load(image)
                            .into(profLicense);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.dismiss();
                        }
                    }, 1000);

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.dismiss();
                        }
                    }, 1000);
                }
                //Set plate image
                if (snapshot.child("platePic").exists()) {
                    String image = snapshot.child("platePic").getValue().toString();

                    Picasso
                            .get()
                            .load(image)
                            .into(profPlate);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.dismiss();
                        }
                    }, 1000);

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.dismiss();
                        }
                    }, 1000);
                }
                //Set orcr image
                if (snapshot.child("orcrPic").exists()) {
                    String image = snapshot.child("orcrPic").getValue().toString();

                    Picasso
                            .get()
                            .load(image)
                            .into(profOR);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.dismiss();
                        }
                    }, 1000);

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.dismiss();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}