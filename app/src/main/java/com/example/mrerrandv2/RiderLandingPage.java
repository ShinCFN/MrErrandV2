package com.example.mrerrandv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RiderLandingPage extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    DBOrder dbord;
    String key = null;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riderlanding);
        recyclerView = findViewById(R.id.ordersrv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(RiderLandingPage.this, LinearLayoutManager.VERTICAL, false));
        dbord = new DBOrder();

        FirebaseRecyclerOptions<Order> option =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(dbord.get(), new SnapshotParser<Order>() {
                            @NonNull
                            @Override
                            public Order parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Order ord = snapshot.getValue(Order.class);
                                ord.setKey(snapshot.getKey());
                                return ord;
                            }
                        }).build();

        adapter = new FirebaseRecyclerAdapter(option) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull Object o) {
                OrderVH vh = (OrderVH) viewHolder;
                Order ord = (Order) o;
                vh.orderName.setText(ord.getFirstname());
                vh.orderdesc.setText(ord.getOrderlist());

                vh.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(RiderLandingPage.this, ViewOrderActivity.class);
                        intent.putExtra("OPEN", ord);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(RiderLandingPage.this).inflate(R.layout.layout_orders, parent, false);
                return new OrderVH(view);
            }

            @Override
            public void onDataChanged() {
                Toast.makeText(RiderLandingPage.this, "DEBUG: Data Change", Toast.LENGTH_LONG).show();
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
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        auth.signOut();
                        startActivity(new Intent(RiderLandingPage.this, SignInActivity.class));
                        finish();
                    }
                }).setNegativeButton("No", null).show();


    }


}
