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
import com.google.protobuf.StringValue;


public class UserProfileFragment extends Fragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    TextView editName, editMobile, editStreet, editCity, editProvince, editZip, editEmail, totalstars, totalrates, totalrating;

    ShapeableImageView profilepic;

    private progressBar progressBar;

    DatabaseReference databaseReference;

    ValueEventListener updateListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        //Progress bar
        progressBar = new progressBar(getContext());
        progressBar.show();

        //Status bar
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(v.getContext(), R.color.finalLightGreen));
        View decor = getActivity().getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setNavigationBarColor(getContext().getResources().getColor(R.color.newGray));
            View view = getActivity().getWindow().getDecorView();
        }

        //Find Holders
        editName = v.findViewById(R.id.profileFull);
        editMobile = v.findViewById(R.id.profileNumber);
        editStreet = v.findViewById(R.id.profileStreet);
        editCity = v.findViewById(R.id.profileCity);
        editProvince = v.findViewById(R.id.profileProvince);
        editZip = v.findViewById(R.id.profileZip);
        editEmail = v.findViewById(R.id.profileEmail);
        profilepic = v.findViewById(R.id.profilePic);
        totalstars = v.findViewById(R.id.totalstars);
        totalrates = v.findViewById(R.id.totalrates);
        totalrating = v.findViewById(R.id.totalrating);

        //Get data
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());

        updateListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                if(getActivity() == null){
                    return;
                }

                //Set Rating
                if(snapshot.child("totalrates").exists() && snapshot.child("totalstars").exists()){
                    int stars, rates;
                    double total;

                    stars = Integer.parseInt(snapshot.child("totalstars").getValue().toString());
                    rates = Integer.parseInt(snapshot.child("totalrates").getValue().toString());

                    total = (double) stars/rates;


                    totalrates.setText("Total rates: " + rates);
                    totalstars.setText("Total stars: " + stars);
                    totalrating.setText(String.format("%.1f", total));
                }else{
                    totalrates.setText("Total Rates: 0");
                    totalstars.setText("Total Stars: 0");
                    totalrating.setText("0");
                }


                if (snapshot.child("profileImage").exists()) {
                    String image = snapshot.child("profileImage").getValue().toString();

                    progressBar.show();

                    Glide.with(UserProfileFragment.this).load(image).placeholder(R.drawable.blankuser).listener(new RequestListener<Drawable>() {
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
                }else{
                    progressBar.dismiss();
                }


                //Edit Profile

                LinearLayout editProfile = v.findViewById(R.id.profileEdit);

                editProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getActivity(), UserEditProfileActivity.class));
                        getActivity();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        databaseReference.addListenerForSingleValueEvent(updateListener);
    }

}