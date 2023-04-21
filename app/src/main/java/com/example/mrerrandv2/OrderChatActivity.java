package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderChatActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    RecyclerView chatrv;
    DBChat dbChat;
    FirebaseRecyclerAdapter adapter;
    ImageView sendbtn, toolbarback;
    EditText inputmsg;
    Chat chat;
    NestedScrollView nestedScrollView;
    String type, profimg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_chat);
        chatrv = findViewById(R.id.chatrv);
        sendbtn = findViewById(R.id.sendbtn);
        inputmsg = findViewById(R.id.inputmsg);
        toolbarback = findViewById(R.id.toolbarback);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        //Set type
        type = getIntent().getStringExtra("type");

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
        String UID = getIntent().getStringExtra("ORDKEY");
        dbChat = new DBChat(UID);
        chatrv.setLayoutManager(new WrapContentLinearLayoutManager(OrderChatActivity.this));

        FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(dbChat.get(), new SnapshotParser<Chat>() {
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
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
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

                Glide.with(OrderChatActivity.this).load(chat.getProfImg()).into(vh.profile);
                Glide.with(OrderChatActivity.this).load(chat.getProfImg()).into(vh.profileL);
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(OrderChatActivity.this).inflate(R.layout.layout_chat, parent, false);
                return new VHChat(view);
            }

            @Override
            public void onDataChanged() {
            }
        };

        chatrv.setAdapter(adapter);

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = inputmsg.getText().toString();
                String UID = getIntent().getStringExtra("ORDKEY");
                dbChat = new DBChat(UID);

                //return if empty
                if (inputmsg.getText().toString().isEmpty()) {
                    return;
                }

                //Get timestamp
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();

                //Get time
                String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(getIntent().getStringExtra("type")).child(auth.getCurrentUser().getUid()).child("profileImage");
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        profimg = snapshot.getValue().toString();
                        chat = new Chat(auth.getCurrentUser().getDisplayName(), msg, ts, currentTime, "false", auth.getCurrentUser().getUid(), type, profimg);
                        dbChat.add(chat);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                inputmsg.setText("");
            }
        });


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