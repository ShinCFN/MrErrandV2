package com.example.mrerrandv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ViewTransaction extends AppCompatActivity {

    ImageView receipt, toolbarback;

    TextView viewConvo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transaction);

        receipt = findViewById(R.id.receipt);
        toolbarback= findViewById(R.id.toolbarback);
        viewConvo = findViewById(R.id.btnChat);

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




        SaveTransaction transaction = (SaveTransaction) getIntent().getSerializableExtra("transaction");

        Glide.with(ViewTransaction.this).load(transaction.getReceiptimg()).into(receipt);


        viewConvo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewTransaction.this, OrderChatTransactionActivity.class);
                intent.putExtra("DETAILS" ,transaction);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}