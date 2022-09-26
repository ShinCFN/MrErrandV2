package com.example.mrerrandv2;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mrerrandv2.databinding.ActivityRiderMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RiderLandingPage extends AppCompatActivity {

    ActivityRiderMainBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Riders");
    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");

    ValueEventListener orderRefListener;

    RiderHomeFragment golist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityRiderMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_rider_main);
        setContentView(binding.getRoot());
//        replaceFragment(new RiderHomeFragment());
        golist = new RiderHomeFragment();

        databaseReference.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("licensePic").exists() && snapshot.child("platePic").exists() && snapshot.child("orcrPic").exists()) {

                    orderRefListener = orderRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                replaceFragment(new RiderHomeFragment());
                                orderRef.removeEventListener(this);
                            } else {
                                replaceFragment(new EmptyFragment());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    replaceFragment(new RiderErrorFragment());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.home:

                    databaseReference.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child("licensePic").exists() && snapshot.child("platePic").exists() && snapshot.child("orcrPic").exists()) {
                                orderRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            replaceFragment(new RiderHomeFragment());
                                            orderRef.removeEventListener(this);
                                        } else {
                                            replaceFragment(new EmptyFragment());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                replaceFragment(new RiderErrorFragment());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    break;
                case R.id.profile:
                    replaceFragment(new RiderProfileFragment());
                    break;
                case R.id.settings:
                    replaceFragment(new SettingsFragment());
                    break;
            }
            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Main_layout, fragment);
        fragmentTransaction.commit();


    }

    @Override
    public void onStop() {
        super.onStop();
        if (orderRef != null && orderRefListener != null) {
            orderRef.removeEventListener(orderRefListener);
        }
    }

    public void listener() {
        if (orderRefListener != null) {
            orderRef.addValueEventListener(orderRefListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (orderRef != null && orderRefListener != null) {
            orderRef.removeEventListener(orderRefListener);
        }
    }

}
