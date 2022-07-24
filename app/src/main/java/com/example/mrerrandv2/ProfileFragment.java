package com.example.mrerrandv2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    TextView editName, editMobile, editStreet, editCity, editProvince, editZip, editEmail;

    ImageView profilepic;

    private TextView dimmer;
    private LinearLayout progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        //Progress bar
        progressBar = v.findViewById(R.id.progressbar);
        dimmer = v.findViewById(R.id.dimmer);

        progressBar.setVisibility(View.VISIBLE);
        dimmer.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        //Get data
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Find Holders

                editName = v.findViewById(R.id.profileName);
                editMobile = v.findViewById(R.id.profileNumber);
                editStreet = v.findViewById(R.id.profileStreet);
                editCity = v.findViewById(R.id.profileCity);
                editProvince = v.findViewById(R.id.profileProvince);
                editZip = v.findViewById(R.id.profileZip);
                editEmail = v.findViewById(R.id.profileEmail);
                profilepic = v.findViewById(R.id.profilePic);


                //Get Information

                String firstname = snapshot.child("firstname").getValue().toString();
                String lastname = snapshot.child("lastname").getValue().toString();
                String mobilenumber = snapshot.child("mobilenum").getValue().toString();
                String email = snapshot.child("email").getValue().toString();

                //Set text

                String name = firstname + " " + lastname;

                editName.setText(name);
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

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child("profileImage").exists()) {
                            String image = snapshot.child("profileImage").getValue().toString();
                            Picasso
                                    .get()
                                    .load(image)
                                    .into(profilepic);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    dimmer.setVisibility(View.GONE);
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                }
                            }, 1000);
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    dimmer.setVisibility(View.GONE);
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                //Edit Profile

                Button editProfile = v.findViewById(R.id.profileEdit);

                editProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getActivity(), EditProfileActivity.class));
                        getActivity();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return v;
    }
}