package com.example.mrerrandv2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class EditProfileActivity extends AppCompatActivity{

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users").child(auth.getCurrentUser().getUid()).child("profileImage");

    private CircleImageView profpic;
    private ProgressDialog progressDialog;
    private final int PICK_IMAGE_CODE = 12;
    private ImageView toolbarback;

    private progressBar progressBar;

    private TextView maplayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        //Toolbar
        TextView toolMain = findViewById(R.id.toolbarmain);
        TextView toolSub = findViewById(R.id.toolbarsub);
        toolMain.setText("");
        toolSub.setText("");

        progressBar = new progressBar(this);
        profpic = findViewById((R.id.editprofPic));
        toolbarback = findViewById(R.id.toolbarback);
        maplayout = findViewById(R.id.btnChoose);

        //Map
        Fragment fragment = new EditProfileUserMapsFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map_frame_layout, fragment)
                .commit();

        maplayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, GoogleMapsActivity.class);
                startActivity(intent);
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("profileImage").exists()) {

                    progressBar.show();
                    String image = snapshot.child("profileImage").getValue().toString();

                    Glide.with(EditProfileActivity.this).load(image).placeholder(R.drawable.blankuser).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.dismiss();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.dismiss();
                            return false;
                        }
                    }).into(profpic);

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


        progressDialog = new ProgressDialog(this);

        TextView updateBTN = findViewById(R.id.btnSave);

        //Toolbar
        toolbarback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Update Button
        updateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.show();
                TextView editFirst, editLast, editMobile, editStreet, editCity, editProvince, editZip;

                //Get ID
                editFirst = findViewById(R.id.editproftFirst);
                editLast = findViewById(R.id.editproftLast);
                editMobile = findViewById(R.id.editprofMobile);
                editStreet = findViewById(R.id.editprofStreet);
                editCity = findViewById(R.id.editprofCity);
                editProvince = findViewById(R.id.editprofProvince);
                editZip = findViewById(R.id.editprofZip);

                //Get Values
                String firstname = editFirst.getText().toString();
                String lastname = editLast.getText().toString();
                String mobile = editMobile.getText().toString();
                String street = editStreet.getText().toString();
                String city = editCity.getText().toString();
                String province = editProvince.getText().toString();
                String zip = editZip.getText().toString();

                //Push to DB

                if (!firstname.isEmpty() && !lastname.isEmpty() && !mobile.isEmpty() && !street.isEmpty() && !city.isEmpty() && !province.isEmpty() && !zip.isEmpty()) {

                    databaseReference.child("firstname").setValue(firstname);
                    databaseReference.child("lastname").setValue(lastname);
                    databaseReference.child("mobile").setValue(mobile);
                    databaseReference.child("street").setValue(street);
                    databaseReference.child("city").setValue(city);
                    databaseReference.child("province").setValue(province);
                    databaseReference.child("zip").setValue(zip).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toasty.success(EditProfileActivity.this, "Success", Toasty.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                    //Update Firebase Display Name

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(firstname).build();

                    auth.getCurrentUser().updateProfile(profileUpdates);

                } else {
                    Toast.makeText(EditProfileActivity.this, "Please complete your information", Toast.LENGTH_LONG).show();
                }
                progressBar.dismiss();
            }
        });


        profpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkP();
            }
        });

        //Set values on input boxes

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TextView editFirst, editLast, editMobile, editStreet, editCity, editProvince, editZip;

                //Get ID
                editFirst = findViewById(R.id.editproftFirst);
                editLast = findViewById(R.id.editproftLast);
                editMobile = findViewById(R.id.editprofMobile);
                editStreet = findViewById(R.id.editprofStreet);
                editCity = findViewById(R.id.editprofCity);
                editProvince = findViewById(R.id.editprofProvince);
                editZip = findViewById(R.id.editprofZip);


                //Get Values
                String firstname = snapshot.child("firstname").getValue().toString();
                String lastname = snapshot.child("lastname").getValue().toString();
                String mobile = snapshot.child("mobile").getValue().toString();

                //Check if address is given

                if (snapshot.child("street").exists()) {
                    String street = snapshot.child("street").getValue().toString();
                    editStreet.setText(street);
                }

                if (snapshot.child("city").exists()) {
                    String city = snapshot.child("city").getValue().toString();
                    editCity.setText(city);
                }

                if (snapshot.child("province").exists()) {
                    String province = snapshot.child("province").getValue().toString();
                    editProvince.setText(province);
                }

                if (snapshot.child("zip").exists()) {
                    String zipcode = snapshot.child("zip").getValue().toString();
                    editZip.setText(zipcode);
                }


                //Push
                editFirst.setText(firstname);
                editLast.setText(lastname);
                editMobile.setText(mobile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkP() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Image"),PICK_IMAGE_CODE);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                resizeImage(data.getData());
            }
        }

    }

    private final ActivityResultLauncher<CropImageContractOptions> cropImage =
            registerForActivityResult(new CropImageContract(), this::onCropImageResult);

    public void resizeImage(Uri uri) {
        CropImageContractOptions options = new CropImageContractOptions(uri, new CropImageOptions())
                .setMultiTouchEnabled(true)
                .setAspectRatio(1, 1)
                .setOutputCompressQuality(50)
                .setActivityTitle("")
                .setActivityMenuIconColor(0)
                .setNoOutputImage(false);

        cropImage.launch(options);
    }

    public void onCropImageResult(@NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {

            uploadImage(result.getUriContent());

            profpic.setImageURI(result.getUriContent());

        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {

        } else {
            Toast.makeText(EditProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadImage(Uri uriContent) {

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        storageReference.putFile(uriContent).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        databaseReference.child("profileImage").setValue(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.cancel();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}