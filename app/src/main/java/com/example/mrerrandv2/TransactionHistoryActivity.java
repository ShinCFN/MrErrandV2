package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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

public class TransactionHistoryActivity extends AppCompatActivity {

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

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.finalLightGreen));
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.newGray));
        }

        toolbarback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Recycler View
        dbTransaction = new DBTransaction(auth.getCurrentUser().getUid());
        transacRV.setLayoutManager(new WrapContentLinearLayoutManager(TransactionHistoryActivity.this));

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
                VHTransactionHistory vh = (VHTransactionHistory) viewHolder;
                SaveTransaction saveTransaction = (SaveTransaction) o;

                vh.date.setText(saveTransaction.getSimpleDate() + " - " + saveTransaction.getTime());
                vh.id.setText(saveTransaction.getKey());
                vh.ratingBar.setText(saveTransaction.getRating() + " Stars");

                DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference("Riders").child(saveTransaction.getRideruid());
                riderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Glide.with(TransactionHistoryActivity.this).load(snapshot.child("profileImage").getValue().toString()).into(vh.image);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                vh.holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //View order info
                        Intent intent = new Intent(TransactionHistoryActivity.this, ViewTransaction.class);
                        intent.putExtra("transaction", saveTransaction);
                        startActivity(intent);
                    }
                });


            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(TransactionHistoryActivity.this).inflate(R.layout.layout_transactionhistory, parent, false);
                return new VHTransactionHistory(view);
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