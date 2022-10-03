package com.example.mrerrandv2;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DBChat {
    private DatabaseReference databaseReference;
    public DBChat(String orderid){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("Order").child(orderid).child("Chat");
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
