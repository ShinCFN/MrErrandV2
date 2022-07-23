package com.example.mrerrandv2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class OrderActivity extends AppCompatActivity {

    Button pay;
    ImageView imgorder;
    private final int PICK_IMAGE_CODE = 17;
    ProgressDialog progressDialog;
    Boolean isImageorder = false;
    FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        pay = findViewById(R.id.gotoPay);
        progressDialog = new ProgressDialog(OrderActivity.this);
        imgorder = findViewById(R.id.imgorder);

        //Set image order
        imgorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropOrder();
            }
        });

        //Next Button
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
                    intent.putExtra("state", isImageorder.toString());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

        //Crop
        private void cropOrder() {
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, PICK_IMAGE_CODE);
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

            if(requestCode==PICK_IMAGE_CODE && resultCode==RESULT_OK){
                if(data!=null){
                    resizeOrder(data.getData());
                }
            }

        }

        //Resize Profile
        private final ActivityResultLauncher<CropImageContractOptions> cropReceipt =
                registerForActivityResult(new CropImageContract(), this::onCropOrderResult);

        public void resizeOrder(Uri uri) {
            CropImageContractOptions options= new CropImageContractOptions(uri, new CropImageOptions())
                    .setMultiTouchEnabled(true)
                    .setOutputCompressQuality(80)
                    .setActivityTitle("")
                    .setActivityMenuIconColor(0)
                    .setNoOutputImage(false);

            cropReceipt.launch(options);
        }

        public void onCropOrderResult(@NonNull CropImageView.CropResult result) {
            if (result.isSuccessful()) {

                uploadReceipt(result.getUriContent());

                imgorder.setImageURI(result.getUriContent());

            } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {

            } else {
                Toast.makeText(OrderActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        }

        //Uploading
        private void uploadReceipt(Uri uriContent) {

            Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");

            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Orders");

            progressDialog.setCancelable(false);
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            storageReference.child(auth.getCurrentUser().getUid()).putFile(uriContent).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    isImageorder = true;
                    progressDialog.dismiss();
                    storageReference.child(auth.getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Intent intent = new Intent(OrderActivity.this, PaymentActivity.class);
                            intent.putExtra("imgorder", uri.toString());
                            intent.putExtra("type", isImageorder);
                            startActivity(intent);
                            finish();
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
        new AlertDialog.Builder(this)
                .setMessage("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setNegativeButton("No", null).show();
    }
}