package com.example.mrerrandv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewOfferActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    RecyclerView recyclerView;
    DBOffer dboff;
    FirebaseRecyclerAdapter adapter;
    ValueEventListener offerLis;
    ImageView toolbarback;
    LinearLayout search;
    DatabaseReference offerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_offers);
        offerRef = FirebaseDatabase.getInstance().getReference("Order").child(getIntent().getStringExtra("Key"));
        search = findViewById(R.id.search);

        recyclerView = findViewById(R.id.offersrv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(ViewOfferActivity.this, LinearLayoutManager.VERTICAL, false));

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ViewOfferActivity.this, R.color.finalBackground));

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

        String key = getIntent().getStringExtra("Key");
        dboff = new DBOffer(key);

        //Back Btn
        toolbarback = findViewById(R.id.toolbarback);
        toolbarback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Offer Check
        offerLis = offerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Offers").exists()){
                    //Hide search
                    search.setVisibility(View.GONE);
                }else{
                    search.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler View
        FirebaseRecyclerOptions<AddOffer> option =
                new FirebaseRecyclerOptions.Builder<AddOffer>()
                        .setQuery(dboff.get(), new SnapshotParser<AddOffer>() {
                            @NonNull
                            @Override
                            public AddOffer parseSnapshot(@NonNull DataSnapshot snapshot) {
                                AddOffer off = snapshot.getValue(AddOffer.class);
                                off.setKey(snapshot.getKey());
                                return off;
                            }
                        }).build();

        adapter = new FirebaseRecyclerAdapter(option) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull Object o) {
                OfferVH vh = (OfferVH) viewHolder;
                AddOffer off = (AddOffer) o;
                vh.orderName.setText(off.getRidername());
                vh.offer.setText(off.getOffer());
                vh.helmet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vh.helmet.reverseAnimationSpeed();
                        vh.helmet.playAnimation();

                        new AlertDialog.Builder(view.getContext())
                                .setMessage("Accept offer?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference("Order").child(key);
                                        orderReference.child("status").setValue("accepted");
                                        orderReference.child("Offers").child(off.getKey()).child("state").setValue("accepted").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                DatabaseReference refref = FirebaseDatabase.getInstance().getReference("Order").child(key);
                                                refref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        Intent intent = new Intent(ViewOfferActivity.this, AcceptedOrderActivityUser.class);
                                                        intent.putExtra("ORDER", off);
                                                        intent.putExtra("uid", off.getUid());
                                                        intent.putExtra("type", snapshot.child("ordertype").getValue().toString());
                                                        if (snapshot.child("orderlist").exists()) {
                                                            intent.putExtra("orderlist", snapshot.child("orderlist").getValue().toString());
                                                        }
                                                        intent.putExtra("ORDKEY", key);
                                                        intent.putExtra("RIDERKEY", off.getKey());
                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });

                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        vh.helmet.reverseAnimationSpeed();
                                        vh.helmet.playAnimation();
                                    }
                                }).show();
                    }
                });

                //Rating system
                vh.rating.setRating(off.getRating());

                //Auto next
                DatabaseReference refref = FirebaseDatabase.getInstance().getReference("Order").child(key);
                refref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("status").getValue().toString().equals("accepted")) {
                            Intent intent = new Intent(ViewOfferActivity.this, AcceptedOrderActivityUser.class);
                            intent.putExtra("ORDER", off);
                            intent.putExtra("uid", snapshot.child("uid").getValue().toString());
                            intent.putExtra("type", snapshot.child("ordertype").getValue().toString());
                            if (snapshot.child("orderlist").exists()) {
                                intent.putExtra("orderlist", snapshot.child("orderlist").getValue().toString());
                            }
                            intent.putExtra("ORDKEY", key);
                            intent.putExtra("RIDERKEY", off.getKey());
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                vh.order_options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(key).child("Offers");
                        databaseReference.child(off.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ViewOfferActivity.this, "Order declined", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ViewOfferActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(ViewOfferActivity.this).inflate(R.layout.layout_offers, parent, false);
                return new OfferVH(view);
            }

            @Override
            public void onDataChanged() {

            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        offerRef.removeEventListener(offerLis);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to cancel your order?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // PUT DELETE ORDER HERE

                        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Order").child(getIntent().getStringExtra("Key"));
                        firebaseDatabase.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                finish();
                            }
                        });
                    }
                }).setNegativeButton("No", null).show();
    }

}
