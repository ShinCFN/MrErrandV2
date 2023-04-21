package com.example.mrerrandv2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RiderProfileFragment extends Fragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    TextView profileFull, editMobile, editEmail, editLicense, editPlate;

    ShapeableImageView profilepic, profLicense, profPlate, profOR;

    private progressBar progressBar;

    private Boolean pfpOk, licpOk, drvlOk, orcrOk;

    DatabaseReference databaseReference;

    ValueEventListener updateListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //Progress bar
        progressBar = new progressBar(getContext());
        progressBar.show();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_rider_profile, container, false);


        //Bool
        pfpOk = false;
        licpOk = false;
        drvlOk = false;
        orcrOk = false;

        //Status bar
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(v.getContext(), R.color.finalBrown));
        View decor = getActivity().getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setNavigationBarColor(getContext().getResources().getColor(R.color.newGray));
            View view = getActivity().getWindow().getDecorView();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Riders").child(auth.getCurrentUser().getUid());

        updateListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!RiderProfileFragment.this.isVisible()) {
                    return;
                }

                //Find Holders

                profileFull = v.findViewById(R.id.profileFull);
                editMobile = v.findViewById(R.id.profileNumber);
                editLicense = v.findViewById(R.id.profileLicense);
                editPlate = v.findViewById(R.id.profilePlate);
                editEmail = v.findViewById(R.id.profileEmail);
                profilepic = v.findViewById(R.id.profilePic);


                //Get Information

                String firstname = snapshot.child("firstname").getValue().toString();
                String lastname = snapshot.child("lastname").getValue().toString();
                String mobilenumber = snapshot.child("mobile").getValue().toString();
                String email = snapshot.child("email").getValue().toString();

                //Set text

                profileFull.setText(firstname + " " + lastname);
                editMobile.setText(mobilenumber);
                editEmail.setText(email);

                profLicense = v.findViewById((R.id.rlicensePic));
                profPlate = v.findViewById((R.id.rplatePic));
                profOR = v.findViewById((R.id.rorcrPic));

                //Set profile image
                if (snapshot.child("profileImage").exists()) {
                    String image = snapshot.child("profileImage").getValue().toString();

                    Glide.with(getContext()).load(image).placeholder(R.drawable.blankuser).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            pfpOk = true;
                            checkImages();
                            return false;
                        }
                    }).into(profilepic);

                } else {
                    pfpOk = true;
                    checkImages();
                }
                //Set license image
                if (snapshot.child("licensePic").exists()) {
                    String image = snapshot.child("licensePic").getValue().toString();

                    Glide.with(RiderProfileFragment.this).load(image).placeholder(R.color.white).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            drvlOk = true;
                            checkImages();
                            return false;
                        }
                    }).into(profLicense);

                } else {
                    drvlOk = true;
                    checkImages();
                }


                if (snapshot.child("license").exists()) {
                    String licenseNum = snapshot.child("license").getValue().toString();
                    String licenseFinal = licenseNum.replaceAll("\\w(?=\\w{4})", "•");

                    editLicense.setText(licenseFinal);
                }

                if (snapshot.child("plate").exists()) {
                    editPlate.setText(snapshot.child("plate").getValue().toString());
                }

                //Set plate image
                if (snapshot.child("platePic").exists()) {
                    String image = snapshot.child("platePic").getValue().toString();

                    Glide.with(getContext()).load(image).placeholder(R.color.white).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            licpOk = true;
                            checkImages();
                            return false;
                        }
                    }).into(profPlate);

                } else {
                    licpOk = true;
                    checkImages();
                }

                //Set orcr image
                if (snapshot.child("orcrPic").exists()) {
                    String image = snapshot.child("orcrPic").getValue().toString();

                    Glide.with(getContext()).load(image).placeholder(R.color.white).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            orcrOk = true;
                            checkImages();
                            return false;
                        }
                    }).into(profOR);

                } else {
                    orcrOk = true;
                    checkImages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        //Edit Profile
        LinearLayout editProfile = v.findViewById(R.id.profileEdit);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), RiderEditProfileActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        progressBar.show();
        databaseReference.addListenerForSingleValueEvent(updateListener);

    }

    //Image status check
    private void checkImages(){
        if (pfpOk & licpOk & drvlOk & orcrOk) {
            progressBar.dismiss();
            pfpOk = false;
            licpOk = false;
            drvlOk = false;
            orcrOk = false;
        }
    }
}