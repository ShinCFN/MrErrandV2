package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import pl.droidsonroids.gif.GifImageButton;
import pl.droidsonroids.gif.GifImageView;


public class HomeFragment extends Fragment {

    LinearLayout Orderbtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        Orderbtn = v.findViewById(R.id.OrderNowButton);
        Orderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }
}