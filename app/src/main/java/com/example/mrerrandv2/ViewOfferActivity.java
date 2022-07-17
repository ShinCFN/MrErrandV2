package com.example.mrerrandv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewOfferActivity extends AppCompatActivity
{

    FirebaseAuth auth = FirebaseAuth.getInstance();
    RecyclerView recyclerView;
    DBOffer dboff;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_offers);
        recyclerView = findViewById(R.id.offersrv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(ViewOfferActivity.this, LinearLayoutManager.VERTICAL, false));


        String key = getIntent().getStringExtra("Key");
        dboff = new DBOffer(key);

        FirebaseRecyclerOptions<AddOffer> option =
                new FirebaseRecyclerOptions.Builder<AddOffer>()
                        .setQuery(dboff.get(), new SnapshotParser<AddOffer>()
                        {
                            @NonNull
                            @Override
                            public AddOffer parseSnapshot(@NonNull DataSnapshot snapshot)
                            {
                                AddOffer off = snapshot.getValue(AddOffer.class);
                                off.setKey(snapshot.getKey());
                                return off;
                            }
                        }).build();

        adapter = new FirebaseRecyclerAdapter(option) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull Object o)
            {
                OfferVH vh = (OfferVH) viewHolder;
                AddOffer off = (AddOffer) o;
                vh.orderName.setText(off.getRidername());
                vh.offer.setText(off.getOffer());
                vh.order_options.setOnClickListener(v->
                {
                    PopupMenu popupMenu = new PopupMenu(ViewOfferActivity.this,vh.order_options);

                    popupMenu.inflate(R.menu.options_offer);
                    popupMenu.setOnMenuItemClickListener(item->
                    {
                        switch (item.getItemId())
                        {
                            case R.id.menu_open:
                                Intent intent = new Intent(ViewOfferActivity.this,AcceptedOrderActivityUser.class);
                                intent.putExtra("ACCEPTED",off);
                                intent.putExtra("Key", key);
                                intent.putExtra("RKEY", off.getKey());
                                startActivity(intent);
                                finish();
                                break;

                            case R.id.menu_remove:
                                DBOffer dboff = new DBOffer(key);
                                dboff.remove(off.getKey()).addOnSuccessListener(suc->
                                {
                                    Toast.makeText(ViewOfferActivity.this,"Order Removed",Toast.LENGTH_LONG).show();
                                }).addOnFailureListener(er->
                                {
                                    Toast.makeText(ViewOfferActivity.this, ""+er.getMessage(), Toast.LENGTH_LONG).show();
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
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(ViewOfferActivity.this).inflate(R.layout.layout_offers,parent,false);
                return new OfferVH(view);
            }

            @Override
            public void onDataChanged()
            {
                Toast.makeText(ViewOfferActivity.this,"DEBUG: Data Change", Toast.LENGTH_LONG).show();
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart()
    {
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

                        finish();

                        String key = getIntent().getStringExtra("Key");
                        DBOrder dbord = new DBOrder();
                        dbord.remove(key).addOnSuccessListener(suc->
                        {
                            Toast.makeText(ViewOfferActivity.this,"Order Cancelled",Toast.LENGTH_LONG).show();
                        }).addOnFailureListener(er->
                        {
                            Toast.makeText(ViewOfferActivity.this, ""+er.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                }).setNegativeButton("No", null).show();
    }

}
