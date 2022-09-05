package com.example.mrerrandv2;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

public class AdminViewUserActivity extends AppCompatActivity {

    TextView editFirst, editLast, editMobile, editStreet, editCity, editProvince, editZip, editEmail;
    CircleImageView profilepic;
    progressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user);

        progressBar = new progressBar(this);
        progressBar.show();

        User user = (User) getIntent().getSerializableExtra("user");

        editFirst = findViewById(R.id.profileFirst);
        editLast = findViewById(R.id.profileLast);
        editMobile = findViewById(R.id.profileNumber);
        editStreet = findViewById(R.id.profileStreet);
        editCity = findViewById(R.id.profileCity);
        editProvince = findViewById(R.id.profileProvince);
        editZip = findViewById(R.id.profileZip);
        editEmail = findViewById(R.id.profileEmail);
        profilepic = findViewById(R.id.profilePic);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getKey());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String firstname = snapshot.child("firstname").getValue().toString();
                String lastname = snapshot.child("lastname").getValue().toString();
                String mobilenumber = snapshot.child("mobilenum").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                editFirst.setText(firstname.toUpperCase());
                editLast.setText(lastname.toUpperCase());
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