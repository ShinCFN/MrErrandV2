package com.example.mrerrandv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;


public class SettingsFragment extends Fragment {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    TextView btnLogout;
    Switch btnTheme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        btnLogout = v.findViewById(R.id.btnLogout);
        btnTheme = v.findViewById(R.id.darkswitch);

        //Theme Changer
        SharedPreferences appSettingPrefs = v.getContext().getSharedPreferences("AppSettingPrefs", 0);
        Boolean isNightModeOn = appSettingPrefs.getBoolean("NightMode", false);

        SharedPreferences.Editor sharedPrefEdit = appSettingPrefs.edit();

        if (isNightModeOn) {
            btnTheme.setChecked(true);
        }else{
            btnTheme.setChecked(false);
        }

        btnTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sharedPrefEdit.putBoolean("NightMode", true).apply();
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sharedPrefEdit.putBoolean("NightMode", false).apply();
                }
                Toasty.normal(getContext(), "Restart required", Toasty.LENGTH_SHORT).show();
            }
        });

//        btnTheme.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isNightModeOn) {
//                    sharedPrefEdit.putBoolean("NightMode", false).apply();
//                }else{
//                    sharedPrefEdit.putBoolean("NightMode", true).apply();
//                }
//                Toasty.normal(getContext(), "Restart required", Toasty.LENGTH_SHORT).show();
//            }
//        });


        //Logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(getContext())
                        .setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                auth.signOut();
                                Toast.makeText(getActivity(), "Signed out", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), SignInActivity.class));
                                getActivity().finish();
                            }
                        }).setNegativeButton("No", null).show();


            }
        });


        return v;
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Main_layout, fragment);
        fragmentTransaction.commit();


    }
}