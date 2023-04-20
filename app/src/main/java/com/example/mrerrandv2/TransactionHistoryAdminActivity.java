package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TransactionHistoryAdminActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    ImageView toolbarback;
    DBTransaction dbTransaction;
    RecyclerView transacRV;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        toolbarback = findViewById(R.id.toolbarback);
        transacRV = findViewById(R.id.transactionrv);

        User USER = (User) getIntent().getSerializableExtra("USER");


        //Toolbar
        TextView toolMain = findViewById(R.id.toolbarmain);
        TextView toolSub = findViewById(R.id.toolbarsub);
        toolMain.setText("");
        toolSub.setText("");

        toolbarback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Recycler View
        dbTransaction = new DBTransaction(USER.getKey());
        transacRV.setLayoutManager(new WrapContentLinearLayoutManager(TransactionHistoryAdminActivity.this));

        FirebaseRecyclerOptions<SaveTransaction> option =
                new FirebaseRecyclerOptions.Builder<SaveTransaction>()
                        .setQuery(dbTransaction.getInOrder(), new SnapshotParser<SaveTransaction>() {
                            @NonNull
                            @Override
                            public SaveTransaction parseSnapshot(@NonNull DataSnapshot snapshot) {
                                SaveTransaction saveTransaction = snapshot.getValue(SaveTransaction.class);
                                saveTransaction.setKey(snapshot.getKey());
                                return saveTransaction;
                            }
                        }).build();

        adapter = new FirebaseRecyclerAdapter(option) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull Object o) {
                TransactionHistoryVH vh = (TransactionHistoryVH) viewHolder;
                SaveTransaction saveTransaction = (SaveTransaction) o;

                vh.date.setText(saveTransaction.getSimpleDate() + " - " + saveTransaction.getTime());
                vh.id.setText(saveTransaction.getKey());
                vh.ratingBar.setRating(saveTransaction.getRating());

                DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference("Riders").child(saveTransaction.getRideruid());
                riderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Glide.with(TransactionHistoryAdminActivity.this).load(snapshot.child("profileImage").getValue().toString()).into(vh.image);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                vh.holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //View order info
                        Intent intent = new Intent(TransactionHistoryAdminActivity.this, ViewTransaction.class);
                        intent.putExtra("transaction", saveTransaction);
                        startActivity(intent);
                    }
                });


            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(TransactionHistoryAdminActivity.this).inflate(R.layout.layout_transactionhistory, parent, false);
                return new TransactionHistoryVH(view);
            }

            @Override
            public void onDataChanged() {
            }
        };

        transacRV.setOnTouchListener((view, motionEvent) -> true);
        transacRV.setAdapter(adapter);
        adapter.startListening();


    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}