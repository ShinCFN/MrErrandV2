package com.example.mrerrandv2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mrerrandv2.databinding.ActivityMainBinding;


public class UserMainActivity extends AppCompatActivity {

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);
        setContentView(binding.getRoot());
        replaceFragment(new UserFragmentHome());


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.home:
                    if(item.getItemId() != binding.bottomNavigationView.getSelectedItemId()){
                        replaceFragment(new UserFragmentHome());
                    }
                    break;
                case R.id.profile:
                    if(item.getItemId() != binding.bottomNavigationView.getSelectedItemId()){
                        replaceFragment(new UserProfileFragment());
                    }
                    break;
                case R.id.settings:
                    if(item.getItemId() != binding.bottomNavigationView.getSelectedItemId()){
                        replaceFragment(new SettingsFragment());
                    }
                    break;
            }
            return true;
        });

    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Main_layout, fragment);
        fragmentTransaction.commit();


    }

}
