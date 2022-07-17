package com.example.mrerrandv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OrderActivity extends AppCompatActivity {

    Button pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        pay = findViewById(R.id.gotoPay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText orderlist = findViewById(R.id.OrderList);
                String orders = orderlist.getText().toString();

                if(orders.isEmpty()){
                    Toast.makeText(OrderActivity.this, "Order list is empty",Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(OrderActivity.this, PaymentActivity.class);
                    intent.putExtra("ORDER",orders);
                    startActivity(intent);
                }
            }
        });

    }
}