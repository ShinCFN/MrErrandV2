package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class RiderDeliveryActivity extends AppCompatActivity {

    TextView name, mobile, street, city, province;

    String userid = null;

    ConstraintLayout complete;

    RatingBar ratingBar;

    String key = null;

    public String uid;

    Double lat, lng;

    CircleImageView chatvh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_rider);

        Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");
        key = ord_open.getKey();

        //Toolbar
        TextView toolMain = findViewById(R.id.toolbarmain);
        TextView toolSub = findViewById(R.id.toolbarsub);
        toolMain.setText("");
        toolSub.setText("");

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.finalBrown));

        //Set Profile
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setRating(ord_open.getRating());

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setNavigationBarColor(getResources().getColor(R.color.finalBackground));
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        //Open chat
        chatvh = findViewById(R.id.chatbtn);
        chatvh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open chat activity
                Intent intent = new Intent(RiderDeliveryActivity.this, OrderChatActivity.class);
                intent.putExtra("type", "Riders");
                intent.putExtra("ORDKEY", ord_open.getKey());
                startActivity(intent);
            }
        });

        //Map
        Fragment fragment = new RiderDeliveryMapsFragment();
        Bundle args = new Bundle();

        DatabaseReference latlngRef = FirebaseDatabase.getInstance().getReference("Users").child(ord_open.getUID());
        latlngRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lat = (Double) snapshot.child("coords").child("latitude").getValue();
                lng = (Double) snapshot.child("coords").child("longitude").getValue();

                args.putDouble("lat", lat);
                args.putDouble("lng", lng);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        fragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map_frame_layout, fragment)
                .commit();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");

        databaseReference.child(ord_open.getKey()).child("uid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String uid = snapshot.getValue().toString();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

                userid = uid;

                userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        name = findViewById(R.id.profileName);
                        mobile = findViewById(R.id.profileNumber);
                        street = findViewById(R.id.profileStreet);
                        city = findViewById(R.id.profileCity);
                        province = findViewById(R.id.profileProvince);

                        String username = snapshot.child("firstname").getValue().toString()+" "+snapshot.child("lastname").getValue().toString();

                        name.setText(username);
                        mobile.setText(snapshot.child("mobile").getValue().toString());
                        street.setText(snapshot.child("street").getValue().toString());
                        city.setText(snapshot.child("city").getValue().toString());
                        province.setText(snapshot.child("province").getValue().toString());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        complete = findViewById(R.id.btnComplt);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order").child(key);
                orderRef.child("status").setValue("complete");
                Intent intent = new Intent(RiderDeliveryActivity.this, RiderRatingActivity.class);
                intent.putExtra("user", userid);

                startActivity(intent);
                finish();
            }
        });

    }
}