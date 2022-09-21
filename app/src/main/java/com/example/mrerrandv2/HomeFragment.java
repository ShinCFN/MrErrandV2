package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;


public class HomeFragment extends Fragment {

    ConstraintLayout Orderbtn;
    progressBar progressBar;
    ImageView info;
    static int i = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        //Status bar
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(v.getContext(), R.color.finalBackground));

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setNavigationBarColor(getContext().getResources().getColor(R.color.finalDarkGray));
            View view = getActivity().getWindow().getDecorView();
        }

        //Toolbar
        info = v.findViewById(R.id.toolbarright);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() == null) {
                    return;
                }
                i++;
                if (i == 5) {
                    i = 0;
                    Toasty.info(getActivity(), "This app is WIP", Toasty.LENGTH_LONG).show();
                }
            }
        });


        //Prog bar
        progressBar = new progressBar(getContext());
        Orderbtn = v.findViewById(R.id.OrderNowButton);
        Orderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.show();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("city").exists() &&
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
                            Toasty.error(getContext(), "Please complete your information", Toasty.LENGTH_LONG).show();
                        }
                        progressBar.dismiss();
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