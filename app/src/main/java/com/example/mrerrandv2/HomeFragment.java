package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.droidsonroids.gif.GifImageButton;
import pl.droidsonroids.gif.GifImageView;


public class HomeFragment extends Fragment {

    LinearLayout Orderbtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        Orderbtn = v.findViewById(R.id.OrderNowButton);
        Orderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("city").exists() &&
                                snapshot.child("email").exists() &&
                                snapshot.child("firstname").exists() &&
                                snapshot.child("lastname").exists() &&
                                snapshot.child("mobile").exists() &&
                                snapshot.child("profileImage").exists() &&
                                snapshot.child("province").exists() &&
                                snapshot.child("street").exists() &&
                                snapshot.child("zip").exists()) {

                            Intent intent = new Intent(getActivity(), OrderActivity.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(getContext(),"Please complete your information", Toast.LENGTH_LONG).show();
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        return v;
    }
}