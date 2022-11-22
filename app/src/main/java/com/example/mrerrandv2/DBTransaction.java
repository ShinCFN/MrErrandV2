package com.example.mrerrandv2;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DBTransaction {

    private DatabaseReference databaseReference;
    public DBTransaction(String authid){

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("Users").child(authid).child("Transactions");
    }
    public Task<Void> add(SaveTransaction saveTransaction)
    {
        return databaseReference.push().setValue(saveTransaction);
    }

    public Task<Void> remove(String key)
    {
        return databaseReference.child(key).removeValue();
    }

    public Query get()
    {

        return databaseReference;

    }

    public Query getFive()
    {

        return databaseReference.orderByChild("timestamp").limitToFirst(5);

    }

    public Query getInOrder()
    {



        return databaseReference.orderByChild("timestamp");

    }

}
