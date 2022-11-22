package com.example.mrerrandv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ViewTransaction extends AppCompatActivity {

    ImageView receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transaction);

        receipt = findViewById(R.id.receipt);

        SaveTransaction transaction = (SaveTransaction) getIntent().getSerializableExtra("transaction");

        Glide.with(ViewTransaction.this).load(transaction.getReceiptimg()).into(receipt);


    }
}