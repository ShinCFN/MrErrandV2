package com.example.mrerrandv2;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AdminViewRiderActivity extends AppCompatActivity {

    TextView editName, editMobile, editEmail, editLicense, editPlate, verify;

    ImageView profilepic,profLicense,profPlate,profOR, blurbg;

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

        editName = findViewById(R.id.profileFull);
        editMobile = findViewById(R.id.profileNumber);
        editLicense = findViewById(R.id.profileLicense);
        editPlate = findViewById(R.id.profilePlate);
        editEmail = findViewById(R.id.profileEmail);
        profilepic = findViewById(R.id.profilePic);
        blurbg = findViewById(R.id.blurbg);
        verify = findViewById(R.id.btnVerify);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Riders").child(rider.getKey());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Get Information

                String firstname = snapshot.child("firstname").getValue().toString();
                String lastname = snapshot.child("lastname").getValue().toString();
                String mobilenumber = snapshot.child("mobile").getValue().toString();
                String email = snapshot.child("email").getValue().toString();

                String name = firstname + " " + lastname;

                editName.setText(name);
                editMobile.setText(mobilenumber);
                editEmail.setText(email);

                profLicense = findViewById((R.id.rlicensePic));
                profPlate = findViewById((R.id.rplatePic));
                profOR = findViewById((R.id.rorcrPic));

                //Check if verified
                if (snapshot.child("verified").getValue().toString().equals("pending")){
                    verify.setVisibility(View.VISIBLE);
                }

                verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        riderRef.child(rider.getKey()).child("verified").setValue("true");
                        finish();
                    }
                });

                //Set profile image
                if (snapshot.child("profileImage").exists()) {
                    String image = snapshot.child("profileImage").getValue().toString();

                    Picasso
                            .get()
                            .load(image)
                            .into(profilepic);

                    Glide.with(AdminViewRiderActivity.this).load(image).placeholder(R.drawable.blankuser).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.dismiss();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.dismiss();
                            return false;
                        }
                    }).into(blurbg);

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