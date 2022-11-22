package com.example.mrerrandv2;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class SplashscreenActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;

    private View mainView;
    private boolean status;
    RelativeLayout nonet;
    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        nonet = findViewById(R.id   .nonet);
        status = false;

        mainView = getWindow().getDecorView();
        mainView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


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

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;


        //Check internet connection
        if(connected){

            DatabaseReference serverstatus = FirebaseDatabase.getInstance().getReference("Main");
            serverstatus.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Check server status
                    if(snapshot.child("status").getValue().toString().equals("OK")){

                        if (authProfile.getCurrentUser() != null) {
                            //GET USER PERMS FROM DB
                            FirebaseUser firebaseUser = authProfile.getCurrentUser();
                            String userID = firebaseUser.getUid();

                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("permissions");
                            referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        String usertype = snapshot.child("type").getValue().toString();

                                        if (usertype.equals("admin")) {
                                            startActivity(new Intent(SplashscreenActivity.this, AdminMainActivity.class));
                                        } else if (usertype.equals("rider")) {
                                            startActivity(new Intent(SplashscreenActivity.this, RiderLandingPage.class));
                                        } else {
                                            startActivity(new Intent(SplashscreenActivity.this, MainActivity.class));
                                        }
                                        finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(SplashscreenActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                    throw error.toException();
                                }

                            });
                        } else {
                            startActivity(new Intent(SplashscreenActivity.this, SignInActivity.class));
                            finish();
                        }

                    }else{
                        Toasty.error(SplashscreenActivity.this, "Server offline! Try again later", Toasty.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }else{
            nonet.setVisibility(View.VISIBLE);
//            Toasty.error(SplashscreenActivity.this, "You are not connected to the internet", Toasty.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                   finish();
                }
            }, 3000);
        }
    }
}