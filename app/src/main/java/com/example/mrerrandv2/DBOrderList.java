package com.example.mrerrandv2;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DBOrderList {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference;
    public DBOrderList(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("Users").child(auth.getCurrentUser().getUid()).child(OrderList.class.getSimpleName());
    }
    public Task<Void> add(OrderList ord)
    {
        return databaseReference.push().setValue(ord);
    }

    public Task<Void> setState(String position ,String state)
    {
        return databaseReference.child(position).setValue(state);
    }
    public Query get(String key)
    {
        if (key == null)
        {
           return databaseReference.orderByKey().limitToFirst(8);
        }
        return databaseReference.orderByKey().startAfter(key).limitToFirst(8);
    }

    public Task<Void> remove(String key)
    {
        return databaseReference.child(key).removeValue();
    }

    public Query get()
    {

        return databaseReference;

    }

}
