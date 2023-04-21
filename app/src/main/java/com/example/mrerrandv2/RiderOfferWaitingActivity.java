package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class RiderOfferWaitingActivity extends AppCompatActivity {

    TextView textOffer;
    ImageView toolbarback;
    Boolean accepted = false;

    String key = null;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_waiting);

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.finalBackground));

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.finalDarkGray));
            View view = getWindow().getDecorView();
        }

        //Toolbar
        TextView toolMain = findViewById(R.id.toolbarmain);
        TextView toolSub = findViewById(R.id.toolbarsub);
        toolMain.setText("");
        toolSub.setText("");
        toolbarback = findViewById(R.id.toolbarback);

        toolbarback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");

        DatabaseReference order = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());

        textOffer = findViewById(R.id.waiting_offer);
        textOffer.setText(getIntent().getStringExtra("OFFER"));

        //Offer listener
        order.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                } else {
                    Toasty.warning(RiderOfferWaitingActivity.this, "Order was cancelled", Toasty.LENGTH_SHORT).show();
                    order.removeEventListener(this);
                    finish();
                }


                if (snapshot.child("Offers").child(firebaseUser.getUid()).exists()) {
                    if (snapshot.child("status").getValue().toString().equals("accepted")) {
                        order.removeEventListener(this);
                        DatabaseReference order = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());
                        order.child("rider").setValue(auth.getCurrentUser().getUid());
                        deleteOthers();
                    }


                } else if (!snapshot.child("Offers").child(firebaseUser.getUid()).exists() && snapshot.exists()){
                    Toasty.error(RiderOfferWaitingActivity.this, "Offer was declined", Toasty.LENGTH_SHORT).show();
                    order.removeEventListener(this);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



    }

    private void deleteOthers() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());
        databaseReference.child("Offers").orderByChild("state").equalTo("accepted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    postSnapshot.getKey();
                    String chosenrider = postSnapshot.getKey();

                    if (firebaseUser.getUid().equals(chosenrider) && !accepted) {
                        accepted = true;
                        Toasty.success(RiderOfferWaitingActivity.this, "Offer accepted", Toasty.LENGTH_LONG).show();
                        Intent intent = new Intent(RiderOfferWaitingActivity.this, RiderOrderAcceptedActivity.class);
                        intent.putExtra("RKEY", getIntent().getStringExtra("RIDERKEY"));
                        intent.putExtra("ORDER", ord_open);
                        startActivity(intent);
                        databaseReference.removeEventListener(this);
                        finish();
                    } else if((!firebaseUser.getUid().equals(chosenrider) && !accepted)) {
                        Toasty.info(RiderOfferWaitingActivity.this, "Order was placed for another rider", Toasty.LENGTH_LONG).show();
                        databaseReference.removeEventListener(this);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");
        DBOffer dboff = new DBOffer(ord_open.getKey());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        DatabaseReference mycurrentoffer = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey()).child("Offers");
        mycurrentoffer.child(firebaseUser.getUid()).removeValue();
        finish();
    }
}