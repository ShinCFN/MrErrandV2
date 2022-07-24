package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashscreenActivity extends AppCompatActivity {


    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        authProfile = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                start();
            }
        }, 1700);
    }

    private void start() {
//        authProfile.signOut();


        if (authProfile.getCurrentUser() != null) {
            //GET USER PERMS FROM DB
            FirebaseUser firebaseUser = authProfile.getCurrentUser();
            String userID = firebaseUser.getUid();
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("permissions");

            referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                    if (readUserDetails != null) {

                        String usertype = readUserDetails.type;

                        if (usertype.equals("admin")) {
                            startActivity(new Intent(SplashscreenActivity.this, AdminMainActivity.class));
                            finish();
                        } else if (usertype.equals("rider")) {
                            startActivity(new Intent(SplashscreenActivity.this, RiderLandingPage.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashscreenActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SplashscreenActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }

            });
        } else {
            startActivity(new Intent(SplashscreenActivity.this, SignInActivity.class));
            finish();
        }
    }
}