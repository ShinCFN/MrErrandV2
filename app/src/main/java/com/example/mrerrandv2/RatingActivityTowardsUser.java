package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class RatingActivityTowardsUser extends AppCompatActivity {

    RatingBar ratingBar;

    int myRating = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratingtouser);

        String userkey = getIntent().getStringExtra("user");

        ratingBar = findViewById(R.id.rating);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                int rating = (int) v;
                String message = null;

                myRating = (int) ratingBar.getRating();

                switch (rating) {

                    case 1:
                        message = "Sorry to hear that";
                        break;
                    case 2:
                        message = "Sorry to hear that";
                        break;
                    case 3:
                        message = "Good enough!";
                        break;
                    case 4:
                        message = "Great! Thank you!";
                        break;
                    case 5:
                        message = "Awesome!";
                        break;
                }

                Toasty.normal(RatingActivityTowardsUser.this, message, Toasty.LENGTH_LONG).show();

                DatabaseReference rateRef = FirebaseDatabase.getInstance().getReference("Users").child(userkey);

                rateRef.child("OrderList").removeValue();

                rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child("totalstars").exists() && snapshot.child("totalrates").exists()) {
                            String totalstars = snapshot.child("totalstars").getValue().toString();
                            String totalrates = snapshot.child("totalrates").getValue().toString();

                            int newtotalstars = Integer.valueOf(totalstars) + myRating;
                            int newtotalrates = Integer.valueOf(totalrates) + 1;

                            rateRef.child("totalstars").setValue(newtotalstars).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    rateRef.child("totalrates").setValue(newtotalrates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Intent intent = new Intent(RatingActivityTowardsUser.this, RiderLandingPage.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            });


                        } else {
                            int newrating = myRating;
                            int newtotalrates = 1;

                            rateRef.child("totalstars").setValue(newrating).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    rateRef.child("totalrates").setValue(newtotalrates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            finish();
                                        }
                                    });
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}