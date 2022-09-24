package com.example.mrerrandv2;

import android.content.Intent;
import android.media.Rating;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.protobuf.Value;

import es.dmoral.toasty.Toasty;


public class HomeFragment extends Fragment {

    ConstraintLayout Orderbtn;
    progressBar progressBar;
    ImageView info;
    static int i = 0;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ConstraintLayout activeorder;
    ValueEventListener activeOrdListener;
    DatabaseReference activeOrderRef;
    String key;
    TextView orderdesc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        activeorder = v.findViewById(R.id.activeorder);
        Orderbtn = v.findViewById(R.id.OrderNowButton);
        orderdesc = v.findViewById(R.id.orderdesc);

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

        //Active order
        activeOrderRef = FirebaseDatabase.getInstance().getReference("Order");
        activeOrdListener = activeOrderRef.orderByChild("uid").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Boolean status;

                if (snapshot.getValue() == null) {
                    // The child doesn't exist
                    activeorder.setVisibility(View.GONE);
                    status = false;
                } else {
                    status = true;
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                        key = childSnapshot.getKey();
                        orderdesc.setText("#" + key);
                        activeorder.setVisibility(View.VISIBLE);
                        activeorder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (childSnapshot.child("status").getValue().toString().equals("null")) {
                                    Intent intent = new Intent(getActivity(), ViewOfferActivity.class);
                                    intent.putExtra("Key", key);
                                    startActivity(intent);
                                }

                                if (childSnapshot.child("status").getValue().toString().equals("accepted") || childSnapshot.child("status").getValue().toString().equals("inDelivery")) {
                                    Intent intent = new Intent(getActivity(), AcceptedOrderActivityUser.class);
                                    intent.putExtra("ORDKEY", key);
                                    intent.putExtra("RIDERKEY", childSnapshot.child("rider").getValue().toString());
                                    intent.putExtra("uid", childSnapshot.child("uid").getValue().toString());
                                    intent.putExtra("type", childSnapshot.child("ordertype").getValue().toString());

                                    startActivity(intent);
                                }

                                if (childSnapshot.child("status").getValue().toString().equals("complete")) {
                                    Intent intent = new Intent(getActivity(), RatingActivityTowardsRider.class);
                                    intent.putExtra("order", key);
                                    intent.putExtra("rider", childSnapshot.child("rider").getValue().toString());
                                    startActivity(intent);
                                }


                            }
                        });

                    }
                }

                Orderbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (status) {
                            Toasty.warning(getActivity(), "You already have an active order", Toasty.LENGTH_SHORT).show();
                        } else {
                            Order();
                        }

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        activeOrderRef.removeEventListener(activeOrdListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        activeOrderRef.addValueEventListener(activeOrdListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        activeOrderRef.removeEventListener(activeOrdListener);
    }

    public void Order() {
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
}