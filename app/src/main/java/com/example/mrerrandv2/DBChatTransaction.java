package com.example.mrerrandv2;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DBChatTransaction {
    private DatabaseReference databaseReference;
    public DBChatTransaction(String userid, String transacID){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("Users").child(userid).child("Transactions").child(transacID).child("Chat");
    }
    public Task<Void> add(Chat chat)
    {
        return databaseReference.push().setValue(chat);
    }

    public Task<Void> setState(String position ,String state)
    {
        return databaseReference.child(position).setValue(state);
    }

    public Task<Void> remove(String key)
    {
        return databaseReference.child(key).removeValue();
    }

    public Query get()
    {

        return databaseReference;

    }

    public Query getPos(String pos)
    {

        return databaseReference.child(pos);

    }

    public Query getInOrder()
    {

        return databaseReference.orderByChild("timestamp");

    }

}
