package com.example.mrerrandv2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

public class OrderChatTransactionActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    RecyclerView chatrv;
    DBChatTransaction dbChatTransaction;
    FirebaseRecyclerAdapter adapter;
    ImageView sendbtn, toolbarback;
    EditText inputmsg;
    Chat chat;
    NestedScrollView nestedScrollView;
    String type, profimg;

    ConstraintLayout chatbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_chat);
        chatrv = findViewById(R.id.chatrv);
        sendbtn = findViewById(R.id.sendbtn);
        inputmsg = findViewById(R.id.inputmsg);
        toolbarback = findViewById(R.id.toolbarback);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        chatbox = findViewById(R.id.constraintLayout);

        //Hide text box
        chatbox.setVisibility(View.GONE);

        SaveTransaction DETAILS = (SaveTransaction) getIntent().getSerializableExtra("DETAILS");

        //Set type
        type = DETAILS.getOrdertype();

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

//        chatRef.getReference("Order").child(getIntent().getStringExtra("ORDERKEY")).child("Chat");

        //Recycler View
        dbChatTransaction = new DBChatTransaction(DETAILS.getUserID(), DETAILS.getKey());
        chatrv.setLayoutManager(new WrapContentLinearLayoutManager(OrderChatTransactionActivity.this));

        FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(dbChatTransaction.get(), new SnapshotParser<Chat>() {
                            @NonNull
                            @Override
                            public Chat parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Chat chat = snapshot.getValue(Chat.class);
                                chat.setKey(snapshot.getKey());
                                return chat;
                            }
                        }).build();

        adapter = new FirebaseRecyclerAdapter(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull Object o) {
                VHChat vh = (VHChat) viewHolder;
                Chat chat = (Chat) o;

                if (chat.getUid().equals(auth.getCurrentUser().getUid())) {
                    vh.holder.setVisibility(View.VISIBLE);
                    vh.name.setText(chat.getName());
                    vh.message.setText(chat.getMessage());
                    vh.time.setText(chat.getTime());
                } else {
                    vh.holderL.setVisibility(View.VISIBLE);

                    vh.nameL.setText(chat.getName());
                    vh.messageL.setText(chat.getMessage());
                    vh.timeL.setText(chat.getTime());
                }

                Glide.with(OrderChatTransactionActivity.this).load(chat.getProfImg()).into(vh.profile);
                Glide.with(OrderChatTransactionActivity.this).load(chat.getProfImg()).into(vh.profileL);
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(OrderChatTransactionActivity.this).inflate(R.layout.layout_chat, parent, false);
                return new VHChat(view);
            }

            @Override
            public void onDataChanged() {
            }
        };

        chatrv.setAdapter(adapter);
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
        adapter.stopListening();
        finish();
    }
}