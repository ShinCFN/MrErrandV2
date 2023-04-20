package com.example.mrerrandv2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import de.hdodenhof.circleimageview.CircleImageView;
import eightbitlab.com.blurview.BlurView;

public class AdminViewUserActivity extends AppCompatActivity {

    TextView editfull, editMobile, editStreet, editCity, editProvince, editZip, editEmail;
    ImageView profilepic, blurbg;
    progressBar progressBar;

    ConstraintLayout tHistBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user);

        progressBar = new progressBar(this);
        progressBar.show();

        User user = (User) getIntent().getSerializableExtra("user");

        editfull = findViewById(R.id.profileFull);
        editMobile = findViewById(R.id.profileNumber);
        editStreet = findViewById(R.id.profileStreet);
        editCity = findViewById(R.id.profileCity);
        editProvince = findViewById(R.id.profileProvince);
        editZip = findViewById(R.id.profileZip);
        editEmail = findViewById(R.id.profileEmail);
        profilepic = findViewById(R.id.profilePic);
        blurbg = findViewById(R.id.blurbg);
        tHistBtn = findViewById(R.id.tHistBtn);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getKey());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String firstname = snapshot.child("firstname").getValue().toString();
                String lastname = snapshot.child("lastname").getValue().toString();
                String mobilenumber = snapshot.child("mobile").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                String fname = firstname.toUpperCase() + " " + lastname.toUpperCase();
                editfull.setText(fname);
                editMobile.setText(mobilenumber);
                editEmail.setText(email);

                if (snapshot.child("street").exists()) {
                    String street = snapshot.child("street").getValue().toString();
                    editStreet.setText(street);
                } else {
                    editStreet.setTextColor(Color.RED);
                    editStreet.setText("--");
                }

                if (snapshot.child("city").exists()) {
                    String city = snapshot.child("city").getValue().toString();
                    editCity.setText(city);
                } else {
                    editCity.setTextColor(Color.RED);
                    editCity.setText("--");
                }

                if (snapshot.child("province").exists()) {
                    String province = snapshot.child("province").getValue().toString();
                    editProvince.setText(province);
                } else {
                    editProvince.setTextColor(Color.RED);
                    editProvince.setText("--");
                }

                if (snapshot.child("zip").exists()) {
                    String zipcode = snapshot.child("zip").getValue().toString();
                    editZip.setText(zipcode);
                } else {
                    editZip.setTextColor(Color.RED);
                    editZip.setText("--");
                }

                if (snapshot.child("profileImage").exists()) {
                    String image = snapshot.child("profileImage").getValue().toString();
                    Picasso
                            .get()
                            .load(image)
                            .into(profilepic);

                    Glide.with(AdminViewUserActivity.this).load(image).placeholder(R.drawable.blankuser).listener(new RequestListener<Drawable>() {
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



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tHistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminViewUserActivity.this, TransactionHistoryAdminActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}