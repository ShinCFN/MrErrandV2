package com.example.mrerrandv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileRiderActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Riders").child(auth.getCurrentUser().getUid());
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Riders").child(auth.getCurrentUser().getUid());

    private CircleImageView profpic;

    private ProgressDialog progressDialog;

    EditText editfirstname, editlastname, editmobilenum, editlicensenum, editplatenum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_rider);


        //Progress Dialog
        progressDialog = new ProgressDialog(EditProfileRiderActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

//        //Set Profile Pic
//        profpic = findViewById((R.id.editprofPic));
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if(snapshot.child("profileImage").exists()){
//                    String image = snapshot.child("profileImage").getValue().toString();
//
//                    Picasso
//                            .get()
//                            .load(image)
//                            .into(profpic);
//
//                    progressDialog.dismiss();
//
//                } else {
//                    progressDialog.dismiss();
//                }}
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Find Holders
                editfirstname = findViewById(R.id.editriderFirst);
                editlastname = findViewById(R.id.editriderLast);
                editmobilenum = findViewById(R.id.editriderMobile);

//        editLicense = findViewById(R.id.profileLicense);
//        editPlate = findViewById(R.id.profilePlate);

                //Get Information

                String firstname = snapshot.child("firstname").getValue().toString();
                String lastname = snapshot.child("lastname").getValue().toString();
                String mobilenumber = snapshot.child("mobilenum").getValue().toString();

                //Set Information

                editfirstname.setText(firstname);
                editlastname.setText(lastname);
                editmobilenum.setText(mobilenumber);


                //Set profile image
                if (snapshot.child("profileImage").exists()) {
                    String image = snapshot.child("profileImage").getValue().toString();

                    Picasso
                            .get()
                            .load(image)
                            .into(profpic);

                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button update = findViewById(R.id.btnSave);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Find Holders
                editfirstname = findViewById(R.id.editriderFirst);
                editlastname = findViewById(R.id.editriderLast);
                editmobilenum = findViewById(R.id.editriderMobile);
                editlicensenum = findViewById(R.id.editriderLicense);
                editplatenum = findViewById(R.id.editriderPlate);

                //Get Information

                String firstname = editfirstname.getText().toString();
                String lastname = editlastname.getText().toString();
                String mobilenumber = editmobilenum.getText().toString();
                String license = editlicensenum.getText().toString();
                String platenum = editplatenum.getText().toString();

                if(firstname.isEmpty()&&lastname.isEmpty()&&mobilenumber.isEmpty()&&license.isEmpty()&&platenum.isEmpty()){
                    Toast.makeText(EditProfileRiderActivity.this,"Please complete your information", Toast.LENGTH_LONG).show();
                }else{
                    //Set Information

                    databaseReference.child("firstname").setValue(firstname);
                    databaseReference.child("lastname").setValue(lastname);
                    databaseReference.child("mobile").setValue(mobilenumber);
                    databaseReference.child("license").setValue(license);
                    databaseReference.child("plate").setValue(platenum).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent = new Intent(EditProfileRiderActivity.this, RiderLandingPage.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                }
            }
        });
    }
}