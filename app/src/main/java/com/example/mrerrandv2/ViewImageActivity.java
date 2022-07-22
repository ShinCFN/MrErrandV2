package com.example.mrerrandv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {

    TouchImageView holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        String image = getIntent().getStringExtra("image");
        holder = findViewById(R.id.image);

        Picasso.get().load(image).into(holder);


    }

    @Override
    public void onBackPressed() {
        finish();
    }
}