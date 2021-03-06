package com.example.mrerrandv2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RiderProfileFragment extends Fragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    TextView editName, editMobile, editEmail, editLicense, editPlate;

    ImageView profilepic,profLicense,profPlate,profOR;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_rider_profile, container, false);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Riders").child(auth.getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Find Holders

                editName = v.findViewById(R.id.profileName);
                editMobile = v.findViewById(R.id.profileNumber);
                editLicense = v.findViewById(R.id.profileLicense);
                editPlate = v.findViewById(R.id.profilePlate);
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

                profLicense = v.findViewById((R.id.rlicensePic));
                profPlate = v.findViewById((R.id.rplatePic));
                profOR = v.findViewById((R.id.rorcrPic));


                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Set profile image
                        if (snapshot.child("profileImage").exists()) {
                            String image = snapshot.child("profileImage").getValue().toString();

                            Picasso
                                    .get()
                                    .load(image)
                                    .into(profilepic);

                            progressDialog.dismiss();

                        } else {
                            progressDialog.dismiss();
                        }
                        //Set license image
                        if (snapshot.child("licensePic").exists()) {
                            String image = snapshot.child("licensePic").getValue().toString();

                            Picasso
                                    .get()
                                    .load(image)
                                    .into(profLicense);

                            progressDialog.dismiss();

                        } else {
                            progressDialog.dismiss();
                        }
                        //Set plate image
                        if (snapshot.child("platePic").exists()) {
                            String image = snapshot.child("platePic").getValue().toString();

                            Picasso
                                    .get()
                                    .load(image)
                                    .into(profPlate);

                            progressDialog.dismiss();

                        } else {
                            progressDialog.dismiss();
                        }
                        //Set orcr image
                        if (snapshot.child("orcrPic").exists()) {
                            String image = snapshot.child("orcrPic").getValue().toString();

                            Picasso
                                    .get()
                                    .load(image)
                                    .into(profOR);

                            progressDialog.dismiss();

                        } else {
                            progressDialog.dismiss();
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
                        startActivity(new Intent(getActivity(), EditProfileRiderActivity.class));
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