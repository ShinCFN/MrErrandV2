package com.example.mrerrandv2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class AdminViewUserActivity extends AppCompatActivity {

    TextView editfull, editMobile, editStreet, editCity, editProvince, editZip, editEmail;
    ImageView profilepic;
    progressBar progressBar;

    LinearLayout tHistBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user);

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getWindow().getContext(), R.color.finalLightGreen));
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.newGray));
        }

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
        tHistBtn = findViewById(R.id.tHist);

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
                    Glide.with(AdminViewUserActivity.this).load(snapshot.child("profileImage").getValue().toString()).placeholder(R.drawable.blankuser).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.dismiss();
                            return false;
                        }
                    }).into(profilepic);
                } else {
                    progressBar.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tHistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminViewUserActivity.this, AdminTransactionHistoryActivity.class);
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