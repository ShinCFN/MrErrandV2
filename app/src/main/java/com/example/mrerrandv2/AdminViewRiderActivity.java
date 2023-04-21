package com.example.mrerrandv2;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

    ImageView profilepic,profLicense,profPlate,profOR;

    progressBar progressBar;

    private Boolean pfpOk, licpOk, drvlOk, orcrOk;

    DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference("Riders");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_rider);

        //Bool
        pfpOk = false;
        licpOk = false;
        drvlOk = false;
        orcrOk = false;

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.finalBrown));
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.newGray));
        }


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

                //NEW GLIDE SETTINGS
                if (snapshot.child("profileImage").exists()) {
                    String image = snapshot.child("profileImage").getValue().toString();

                    Glide.with(AdminViewRiderActivity.this).load(image).placeholder(R.drawable.blankuser).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            pfpOk = true;
                            checkImages();
                            return false;
                        }
                    }).into(profilepic);

                } else {
                    pfpOk = true;
                    checkImages();
                }
                //Set license image
                if (snapshot.child("licensePic").exists()) {
                    String image = snapshot.child("licensePic").getValue().toString();

                    Glide.with(AdminViewRiderActivity.this).load(image).placeholder(R.color.white).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            drvlOk = true;
                            checkImages();
                            return false;
                        }
                    }).into(profLicense);

                } else {
                    drvlOk = true;
                    checkImages();
                }


                if (snapshot.child("license").exists()) {
                    String licenseNum = snapshot.child("license").getValue().toString();
                    String licenseFinal = licenseNum.replaceAll("\\w(?=\\w{4})", "•");

                    editLicense.setText(licenseFinal);
                }

                if (snapshot.child("plate").exists()) {
                    editPlate.setText(snapshot.child("plate").getValue().toString());
                }

                //Set plate image
                if (snapshot.child("platePic").exists()) {
                    String image = snapshot.child("platePic").getValue().toString();

                    Glide.with(AdminViewRiderActivity.this).load(image).placeholder(R.color.white).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            licpOk = true;
                            checkImages();
                            return false;
                        }
                    }).into(profPlate);

                } else {
                    licpOk = true;
                    checkImages();
                }

                //Set orcr image
                if (snapshot.child("orcrPic").exists()) {
                    String image = snapshot.child("orcrPic").getValue().toString();

                    Glide.with(AdminViewRiderActivity.this).load(image).placeholder(R.color.white).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            orcrOk = true;
                            checkImages();
                            return false;
                        }
                    }).into(profOR);

                } else {
                    orcrOk = true;
                    checkImages();
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

    private void checkImages(){
        if (pfpOk & licpOk & drvlOk & orcrOk) {
            progressBar.dismiss();
            pfpOk = false;
            licpOk = false;
            drvlOk = false;
            orcrOk = false;
        }
    }

}