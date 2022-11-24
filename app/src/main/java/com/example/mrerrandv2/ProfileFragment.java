package com.example.mrerrandv2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderEffectBlur;
import eightbitlab.com.blurview.RenderScriptBlur;


public class ProfileFragment extends Fragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    TextView editName, editMobile, editStreet, editCity, editProvince, editZip, editEmail;

    ImageView profilepic, blurbg;

    BlurView blurview;

    private progressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        //Progress bar
        progressBar = new progressBar(getContext());

        //Status bar
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(v.getContext(), R.color.finalBackground));

        //Get data
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Find Holders
                editName = v.findViewById(R.id.profileFull);
                editMobile = v.findViewById(R.id.profileNumber);
                editStreet = v.findViewById(R.id.profileStreet);
                editCity = v.findViewById(R.id.profileCity);
                editProvince = v.findViewById(R.id.profileProvince);
                editZip = v.findViewById(R.id.profileZip);
                editEmail = v.findViewById(R.id.profileEmail);
                profilepic = v.findViewById(R.id.profilePic);
                blurbg = v.findViewById(R.id.blurbg);
                blurview = v.findViewById(R.id.blurView);

//                View decorView = getActivity().getWindow().getDecorView();
//                // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
//                ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
//
////                Set Blur
//                blurview.setupWith(rootView, new RenderScriptBlur(getActivity())).setBlurRadius(10);

                //Get Information
                String firstname = snapshot.child("firstname").getValue().toString();
                String lastname = snapshot.child("lastname").getValue().toString();
                String mobilenumber = snapshot.child("mobile").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                editName.setText(firstname + " " + lastname);

                //Set text
                editMobile.setText(mobilenumber);
                editEmail.setText(email);

                if (snapshot.child("street").exists()) {
                    String street = snapshot.child("street").getValue().toString();
                    editStreet.setText(street);
                } else {
                    editStreet.setText("--");
                }

                if (snapshot.child("city").exists()) {
                    String city = snapshot.child("city").getValue().toString();
                    editCity.setText(city);
                } else {
                    editCity.setText("--");
                }

                if (snapshot.child("province").exists()) {
                    String province = snapshot.child("province").getValue().toString();
                    editProvince.setText(province);
                } else {
                    editProvince.setText("--");
                }

                if (snapshot.child("zip").exists()) {
                    String zipcode = snapshot.child("zip").getValue().toString();
                    editZip.setText(zipcode);
                } else {
                    editZip.setText("--");
                }

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(getActivity() == null){
                            return;
                        }

                        if (snapshot.child("profileImage").exists()) {
                            String image = snapshot.child("profileImage").getValue().toString();

                            progressBar.show();

                            Glide.with(ProfileFragment.this).load(image).placeholder(R.drawable.blankuser).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    progressBar.dismiss();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    progressBar.dismiss();
                                    return false;
                                }
                            }).into(profilepic);

                            Glide.with(ProfileFragment.this).load(image).placeholder(R.drawable.blankuser).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    progressBar.dismiss();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    progressBar.dismiss();
                                    return false;
                                }
                            }).into(blurbg);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                //Edit Profile

                ConstraintLayout editProfile = v.findViewById(R.id.profileEdit);

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