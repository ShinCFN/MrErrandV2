package com.example.mrerrandv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    ImageView toolbarback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_offers);
        recyclerView = findViewById(R.id.offersrv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(ViewOfferActivity.this, LinearLayoutManager.VERTICAL, false));

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ViewOfferActivity.this, R.color.finalBG));

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


                //Options
                vh.order_options.setOnClickListener(v ->
                {
                    PopupMenu popupMenu = new PopupMenu(ViewOfferActivity.this, vh.order_options);

                    popupMenu.inflate(R.menu.options_offer);
                    popupMenu.setOnMenuItemClickListener(item ->
                    {
                        switch (item.getItemId()) {
                            case R.id.menu_open:

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
                                break;

                            case R.id.menu_decline:

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
                                break;
                        }
                        return false;
                    });
                    popupMenu.show();
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
