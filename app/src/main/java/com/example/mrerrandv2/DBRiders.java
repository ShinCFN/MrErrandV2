package com.example.mrerrandv2;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DBRiders {

    private DatabaseReference databaseReference;
    public DBRiders(){

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("Riders");
    }
    public Task<Void> add(DBRiders users)
    {
        return databaseReference.push().setValue(users);
    }

    public Query get(String key)
    {
        if (key == null)
        {
           return databaseReference.orderByKey().limitToFirst(8);
        }
        return databaseReference.orderByKey().startAfter(key).limitToFirst(8);
    }

    public Task<Void> remove(String keyr)
    {
        return databaseReference.child(keyr).removeValue();
    }

    public Query get()
    {

        return databaseReference;

    }

    public Query getPending()
    {
        Query query = databaseReference.orderByChild("verified").equalTo("pending");
        return query;

    }
}
