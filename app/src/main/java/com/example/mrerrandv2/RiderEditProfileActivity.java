package com.example.mrerrandv2;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class RiderEditProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Riders").child(auth.getCurrentUser().getUid());
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Riders").child(auth.getCurrentUser().getUid());

    private CircleImageView profpic;

    private progressBar progressBar;

    private ImageView profLicense, profPlate, profOR, rorcrPic, toolbarback;

    private TextView profOrBtn;

    private Boolean pfpOk, licpOk, drvlOk, orcrOk;

    EditText editfirstname, editlastname, editmobilenum, editlicensenum, editplatenum, editLicense, editPlate;

    private final int PICK_PROFILE_CODE = 12;
    private final int PICK_LICENSE_CODE = 13;
    private final int PICK_PLATE_CODE = 14;
    private final int PICK_OR_CODE = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_rider);

        //Bool
        pfpOk = false;
        licpOk = false;
        drvlOk = false;
        orcrOk = false;

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getWindow().getContext(), R.color.finalBrown));
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.newGray));
        }


        //Toolbar
        toolbarback = findViewById(R.id.toolbarback);
        toolbarback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Prog bar
        progressBar = new progressBar(this);
        progressBar.show();

        profLicense = findViewById((R.id.editriderLicensePic));
        profPlate = findViewById((R.id.editriderPlatePic));
        profOR = findViewById((R.id.rorcrPic));
        profOrBtn = findViewById(R.id.btnUploadOR);

        //Find Holders
        editfirstname = findViewById(R.id.editriderFirst);
        editlastname = findViewById(R.id.editriderLast);
        editmobilenum = findViewById(R.id.editriderMobile);
        editLicense = findViewById(R.id.editriderLicense);
        editPlate = findViewById(R.id.editriderPlate);
        rorcrPic = findViewById(R.id.rorcrPic);
        profpic = findViewById(R.id.editprofPic);

        editPlate.addTextChangedListener(new TextWatcher() {
            int prevL = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                prevL = editPlate.getText().toString().length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();

                if ((prevL <= length) && length == 3) {
                    String data = editPlate.getText().toString();

                    editPlate.setText(data + "-");
                    editPlate.setSelection(length + 1);
                }
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                //Get Information

                String firstname = snapshot.child("firstname").getValue().toString();
                String lastname = snapshot.child("lastname").getValue().toString();
                String mobilenumber = snapshot.child("mobile").getValue().toString();

                //Set Information

                editfirstname.setText(firstname);
                editlastname.setText(lastname);
                editmobilenum.setText(mobilenumber);

                if (snapshot.child("license").exists()) {
                    String license = snapshot.child("license").getValue().toString();
                    editLicense.setText(license);
                } else {

                }

                if (snapshot.child("plate").exists()) {
                    String plate = snapshot.child("plate").getValue().toString();
                    editPlate.setText(plate);
                } else {

                }

                //Pfp
                if (snapshot.child("profileImage").exists()) {
                    String image = snapshot.child("profileImage").getValue().toString();
                    Glide.with(RiderEditProfileActivity.this).load(image).placeholder(R.drawable.blankuser).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            pfpOk = true;
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            pfpOk = true;
                            return false;
                        }
                    }).into(profpic);

                } else {
                    pfpOk = true;
                }

                //Set license image
                if (snapshot.child("licensePic").exists()) {
                    String image = snapshot.child("licensePic").getValue().toString();

                    Glide.with(RiderEditProfileActivity.this).load(image).placeholder(R.color.white).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            drvlOk = true;
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            drvlOk = true;
                            return false;
                        }
                    }).into(profLicense);
                } else {
                    drvlOk = true;
                }

                //Set plate image
                if (snapshot.child("platePic").exists()) {
                    String image = snapshot.child("platePic").getValue().toString();

                    Glide.with(RiderEditProfileActivity.this).load(image).placeholder(R.color.white).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            licpOk = true;
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            licpOk = true;
                            return false;
                        }
                    }).into(profPlate);
                } else {
                    licpOk = true;
                }

                //Set orcr image
                if (snapshot.child("orcrPic").exists()) {
                    String image = snapshot.child("orcrPic").getValue().toString();

                    Glide.with(RiderEditProfileActivity.this).load(image).placeholder(R.color.white).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            orcrOk = true;
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            orcrOk = true;
                            return false;
                        }
                    }).into(profOR);
                } else {
                    orcrOk = true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Image status check
        if (pfpOk = licpOk = drvlOk = orcrOk = true) {

            progressBar.dismiss();
            pfpOk = false;
            licpOk = false;
            drvlOk = false;
            orcrOk = false;
        }

        TextView update = findViewById(R.id.btnSave);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.show();

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

                if (firstname.isEmpty() || lastname.isEmpty() || mobilenumber.isEmpty() || license.isEmpty() || platenum.isEmpty() || license.isEmpty() || platenum.isEmpty()) {
                    progressBar.dismiss();
                    Toast.makeText(RiderEditProfileActivity.this, "Please complete your information", Toast.LENGTH_LONG).show();
                } else {
                    //Set verification
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child("verified").getValue().equals("false")) {
                                databaseReference.child("verified").setValue("pending");

                                //Set Information
                                databaseReference.child("firstname").setValue(firstname);
                                databaseReference.child("lastname").setValue(lastname);
                                databaseReference.child("mobile").setValue(mobilenumber);
                                databaseReference.child("license").setValue(license);
                                databaseReference.child("plate").setValue(platenum).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toasty.success(RiderEditProfileActivity.this, "Success", Toasty.LENGTH_SHORT).show();
                                        progressBar.dismiss();
                                        finish();
                                    }
                                });

                                //Update Firebase Display Name

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(firstname).build();

                                auth.getCurrentUser().updateProfile(profileUpdates);

                            }else{
                                //Set Information
                                databaseReference.child("firstname").setValue(firstname);
                                databaseReference.child("lastname").setValue(lastname);
                                databaseReference.child("mobile").setValue(mobilenumber);
                                databaseReference.child("license").setValue(license);
                                databaseReference.child("plate").setValue(platenum).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toasty.success(RiderEditProfileActivity.this, "Success", Toasty.LENGTH_SHORT).show();
                                        progressBar.dismiss();
                                        finish();
                                    }
                                });

                                //Update Firebase Display Name

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(firstname).build();

                                auth.getCurrentUser().updateProfile(profileUpdates);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });

        //Crop Profile
        profpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropProfile();
            }
        });

        //Crop License
        profLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropLicense();
            }
        });

        //Crop Plate
        profPlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropPlate();
            }
        });

        //Crop OR
        profOrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropOR();
            }
        });

        rorcrPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropOR();
            }
        });


    }

    private void cropProfile() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Image"),PICK_PROFILE_CODE);
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

    private void cropLicense() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Image"),PICK_LICENSE_CODE);
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

    //Crop Plate
    private void cropPlate() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Image"),PICK_PLATE_CODE);
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

    //Crop OR/CR
    private void cropOR() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Image"),PICK_OR_CODE);
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
    //Pic handler

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PROFILE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                resizeProfile(data.getData());
            }
        }
        if (requestCode == PICK_LICENSE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                resizeLicense(data.getData());
            }
        }
        if (requestCode == PICK_PLATE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                resizePlate(data.getData());
            }
        }
        if (requestCode == PICK_OR_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                resizeOR(data.getData());
            }
        }

    }

    //Resize Profile
    private final ActivityResultLauncher<CropImageContractOptions> cropProfile =
            registerForActivityResult(new CropImageContract(), this::onCropProfileResult);

    public void resizeProfile(Uri uri) {
        CropImageContractOptions options = new CropImageContractOptions(uri, new CropImageOptions())
                .setMultiTouchEnabled(true)
                .setAspectRatio(1, 1)
                .setOutputCompressQuality(50)
                .setActivityTitle("")
                .setActivityMenuIconColor(0)
                .setNoOutputImage(false);

        cropProfile.launch(options);
    }

    public void onCropProfileResult(@NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {

            uploadProfile(result.getUriContent());

            profpic.setImageURI(result.getUriContent());

        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {

        } else {
            Toast.makeText(RiderEditProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    //Resize License
    private final ActivityResultLauncher<CropImageContractOptions> cropLicense =
            registerForActivityResult(new CropImageContract(), this::onCropLicenseResult);

    public void resizeLicense(Uri uri) {
        CropImageContractOptions options = new CropImageContractOptions(uri, new CropImageOptions())
                .setMultiTouchEnabled(true)
                .setAspectRatio(154, 100)
                .setOutputCompressQuality(50)
                .setActivityTitle("")
                .setActivityMenuIconColor(0)
                .setNoOutputImage(false);

        cropLicense.launch(options);
    }

    public void onCropLicenseResult(@NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {

            uploadLicense(result.getUriContent());

            profLicense.setImageURI(result.getUriContent());

        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {

        } else {
            Toast.makeText(RiderEditProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    //Resize Plate
    private final ActivityResultLauncher<CropImageContractOptions> cropPlate =
            registerForActivityResult(new CropImageContract(), this::onCropPlateResult);

    public void resizePlate(Uri uri) {
        CropImageContractOptions options = new CropImageContractOptions(uri, new CropImageOptions())
                .setMultiTouchEnabled(true)
                .setAspectRatio(25, 10)
                .setOutputCompressQuality(50)
                .setActivityTitle("")
                .setActivityMenuIconColor(0)
                .setNoOutputImage(false);

        cropPlate.launch(options);
    }

    public void onCropPlateResult(@NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {

            uploadPlate(result.getUriContent());

            profPlate.setImageURI(result.getUriContent());

        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {

        } else {
            Toast.makeText(RiderEditProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    //Resize OR/CR
    private final ActivityResultLauncher<CropImageContractOptions> cropOR =
            registerForActivityResult(new CropImageContract(), this::onCropORResult);

    public void resizeOR(Uri uri) {
        CropImageContractOptions options = new CropImageContractOptions(uri, new CropImageOptions())
                .setMultiTouchEnabled(true)
                .setOutputCompressQuality(50)
                .setActivityTitle("")
                .setActivityMenuIconColor(0)
                .setNoOutputImage(false);

        cropOR.launch(options);
    }

    public void onCropORResult(@NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {

            uploadOR(result.getUriContent());

            profOR.setImageURI(result.getUriContent());

        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {

        } else {
            Toast.makeText(RiderEditProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    //Uploading Profile
    private void uploadProfile(Uri uriContent) {

        progressBar.show();

        storageReference.child("profileImage").putFile(uriContent).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.dismiss();
                storageReference.child("profileImage").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        databaseReference.child("profileImage").setValue(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.dismiss();
            }
        });

    }

    //Uploading License
    private void uploadLicense(Uri uriContent) {

        progressBar.show();

        storageReference.child("License").putFile(uriContent).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.dismiss();
                storageReference.child("License").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        databaseReference.child("licensePic").setValue(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.dismiss();
            }
        });

    }

    //Uploading Plate
    private void uploadPlate(Uri uriContent) {

        progressBar.show();

        storageReference.child("Plate").putFile(uriContent).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.dismiss();
                storageReference.child("Plate").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        databaseReference.child("platePic").setValue(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.dismiss();
            }
        });

    }

    //Uploading ORCR
    private void uploadOR(Uri uriContent) {

        progressBar.show();

        storageReference.child("orcr").putFile(uriContent).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.dismiss();
                storageReference.child("orcr").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        databaseReference.child("orcrPic").setValue(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}