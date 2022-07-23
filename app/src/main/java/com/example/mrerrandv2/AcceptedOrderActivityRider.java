package com.example.mrerrandv2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

import es.dmoral.toasty.Toasty;

public class AcceptedOrderActivityRider extends AppCompatActivity {

    TextView list;
    ImageView receipt, order;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private final int PICK_RECEIPT_CODE = 16;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_order_rider);
        progressDialog = new ProgressDialog(AcceptedOrderActivityRider.this);
        Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");
        list = findViewById(R.id.orderlist);
        order = findViewById(R.id.imgorder);



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("ordertype").getValue().toString().equals("true")){
                    order.setVisibility(View.VISIBLE);
                    Picasso.get().load(ord_open.getOrderlist()).into(order);

                    order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent viewIMG = new Intent(AcceptedOrderActivityRider.this, ViewImageActivity.class);
                            viewIMG.putExtra("image", ord_open.getOrderlist());
                            startActivity(viewIMG);
                        }
                    });

                }else{
                    list.setVisibility(View.VISIBLE);
                    list.setText(ord_open.getOrderlist());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button next = findViewById(R.id.btnAccToOTW);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("receipt").exists()){
                            databaseReference.child("status").setValue("inDelivery").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Intent intent = new Intent(AcceptedOrderActivityRider.this, DeliveryActivityRider.class);
                                    intent.putExtra("ORDER", ord_open);
                                    intent.putExtra("RKEY", getIntent().getStringExtra("RKEY"));
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            Toasty.error(AcceptedOrderActivityRider.this, "Upload receipt", Toasty.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        receipt = findViewById(R.id.receipt);
        receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cropReceipt();


            }
        });


    }

    //Crop
    private void cropReceipt() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, PICK_RECEIPT_CODE);
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

    //Handler
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_RECEIPT_CODE && resultCode==RESULT_OK){
            if(data!=null){
                resizeReceipt(data.getData());
            }
        }

    }

    //Resize Profile
    private final ActivityResultLauncher<CropImageContractOptions> cropReceipt =
            registerForActivityResult(new CropImageContract(), this::onCropReceiptResult);

    public void resizeReceipt(Uri uri) {
        CropImageContractOptions options= new CropImageContractOptions(uri, new CropImageOptions())
                .setMultiTouchEnabled(true)
                .setOutputCompressQuality(80)
                .setActivityTitle("")
                .setActivityMenuIconColor(0)
                .setNoOutputImage(false);

        cropReceipt.launch(options);
    }

    public void onCropReceiptResult(@NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {

            uploadReceipt(result.getUriContent());

            receipt.setImageURI(result.getUriContent());

        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {

        } else {
            Toast.makeText(AcceptedOrderActivityRider.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    //Uploading
    private void uploadReceipt(Uri uriContent) {

        Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Receipt").child(ord_open.getKey());

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        storageReference.child("receipt").putFile(uriContent).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                storageReference.child("receipt").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        databaseReference.child("receipt").setValue(uri.toString());
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
}
