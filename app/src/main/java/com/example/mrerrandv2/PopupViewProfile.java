package com.example.mrerrandv2;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PopupViewProfile extends AppCompatActivity {

    TextView profileName, profileNumber, profileAdd;
    ImageView profilePic;

    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_view_profile);

        profileName = findViewById(R.id.profileName);
        profileNumber = findViewById(R.id.profileNumber);
        profileAdd = findViewById(R.id.profileAdd);
        profilePic = findViewById(R.id.profilePic);

        String UserID = getIntent().getStringExtra("details");

        userRef.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("firstname").getValue() + " " + snapshot.child("lastname").getValue();
                profileName.setText(name);
                profileNumber.setText(snapshot.child("mobile").getValue().toString());

                String addressFull = snapshot.child("street").getValue() + ", " + snapshot.child("province").getValue() + ", " + snapshot.child("city").getValue();
                profileAdd.setText(addressFull);

                Picasso.get().load(snapshot.child("profileImage").getValue().toString()).into(profilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        CardView popup = findViewById(R.id.popup_view_profile);
        getSupportActionBar().hide();

        ViewGroup.LayoutParams layoutParams = popup.getLayoutParams();
        int width  = layoutParams.width;

        getWindow().setLayout(width, ListPopupWindow.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;

        params.x = 0;
        params.y = 0;
        getWindow().setAttributes(params);

        ViewGroup root = (ViewGroup) getWindow().getDecorView().getRootView();
        applyDim(root);
    }

    private static void applyDim(ViewGroup parent) {
        Drawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dim.setAlpha(200);

        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.add(dim);
    }
}
