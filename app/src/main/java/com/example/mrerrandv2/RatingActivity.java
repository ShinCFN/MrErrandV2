package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class RatingActivity extends AppCompatActivity {

    RatingBar ratingBar;

    int myRating = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        String riderkey = getIntent().getStringExtra("rider");

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

                Toasty.info(RatingActivity.this, message, Toasty.LENGTH_LONG).show();

                DatabaseReference rateRef = FirebaseDatabase.getInstance().getReference("Riders").child(riderkey).child("Rating");

                rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            String totalstars = snapshot.child("totalstars").getValue().toString();
                            String totalrates = snapshot.child("totalrates").getValue().toString();

                            int newtotalstars = Integer.valueOf(totalstars) + myRating;
                            int newtotalrates = Integer.valueOf(totalrates) + 1;

                            Rating data = new Rating(newtotalstars, newtotalrates);

                            rateRef.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");

                                    databaseReference.child(getIntent().getStringExtra("order")).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            finish();
                                        }
                                    });
                                }
                            });
                        } else {
                            int newrating = myRating;
                            int newtotalrates = 1;
                            Rating data = new Rating(newrating, newtotalrates);

                            rateRef.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");
                                    databaseReference.child(getIntent().getStringExtra("order")).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Intent intent = new Intent(RatingActivity.this, MainActivity.class);
                                            startActivity(intent);
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