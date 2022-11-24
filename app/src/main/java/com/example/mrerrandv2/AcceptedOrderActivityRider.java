package com.example.mrerrandv2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class AcceptedOrderActivityRider extends AppCompatActivity {

    ImageView receipt, order;

    private final int PICK_RECEIPT_CODE = 16;

    ProgressDialog progressDialog;
    RecyclerView orderlistrv;
    DBViewOrderList dbViewOrderList;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    RiderOrderListAdapter adapter;
    ArrayList<OrderList> list;
    String key;
    ConstraintLayout uploadbtn, profileView, orderlabel;
    TextView NextBTN, profileName, deliveryFee;
    LinearLayout receiptholder;
    ImageView orderImage;
    boolean receiptStatus = false;
    CircleImageView chatvh;
    RatingBar ratingBar;
    Boolean ordertype = false;
    DatabaseReference orderRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_order_rider);
        progressDialog = new ProgressDialog(AcceptedOrderActivityRider.this);
        Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");
        order = findViewById(R.id.orderimg);

        dbViewOrderList = new DBViewOrderList(ord_open.getUID());

        orderRef = FirebaseDatabase.getInstance().getReference("Order").child(ord_open.getKey());

        orderlistrv = findViewById(R.id.riderorderlistrv);
        orderlistrv.setHasFixedSize(true);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(ord_open.getUID()).child("OrderList");
        orderlistrv.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new RiderOrderListAdapter(this, list, ord_open.getUID());
        orderlistrv.setAdapter(adapter);
        dbViewOrderList = new DBViewOrderList(ord_open.getUID());
        uploadbtn = findViewById(R.id.btnUpload);
        receipt = findViewById(R.id.receipt);
        NextBTN = findViewById(R.id.btnNext);
        receiptholder = findViewById(R.id.receiptholder);
        orderImage = findViewById(R.id.orderimg);
        profileName = findViewById(R.id.profileName);
        ratingBar = findViewById(R.id.ratingBar);
        profileView = findViewById(R.id.profileView);
        chatvh = findViewById(R.id.chatbtn);
        orderlabel = findViewById(R.id.orderlabel);
        deliveryFee = findViewById(R.id.deliveryFee);

        //Toolbar
        TextView toolMain = findViewById(R.id.toolbarmain);
        TextView toolSub = findViewById(R.id.toolbarsub);
        toolMain.setText("");
        toolSub.setText("");

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.finalBrown));

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setNavigationBarColor(getResources().getColor(R.color.finalBackground));
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        //Open chat
        chatvh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open chat activity
                Intent intent = new Intent(AcceptedOrderActivityRider.this, OrderChatActivity.class);
                intent.putExtra("type", "Riders");
                intent.putExtra("ORDKEY", ord_open.getKey());
                startActivity(intent);
            }
        });

        //Recycler View
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    OrderList orderList = dataSnapshot.getValue(OrderList.class);
                    list.add(orderList);
                    key = dataSnapshot.getKey();
                    orderList.setKey(key);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Disable RV scrolling
        orderlistrv.setNestedScrollingEnabled(false);

        //Set Profile
        ratingBar.setRating(ord_open.getRating());

        String name = ord_open.getFirstname().toUpperCase() + " " + ord_open.getLastname().toUpperCase();
        profileName.setText(name);

        //View profile
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AcceptedOrderActivityRider.this, PopupViewProfile.class);
                intent.putExtra("details", ord_open.getUID());
                startActivity(intent);
            }
        });

        //Check Order Type
        if (ord_open.getOrdertype().equals("false")){
            orderlistrv.setVisibility(View.VISIBLE);
            orderlabel.setVisibility(View.VISIBLE);
            ordertype = false;
        }else{
            ordertype = true;
            uploadbtn.setVisibility(View.VISIBLE);
            orderImage.setVisibility(View.VISIBLE);
            Picasso.get().load(ord_open.getOrderlist()).into(orderImage);

            orderImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent viewIMG = new Intent(AcceptedOrderActivityRider.this, ViewImageActivity.class);
                    viewIMG.putExtra("image", ord_open.getOrderlist());
                    startActivity(viewIMG);
                }
            });
        }

        NextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("receipt").exists() && receiptStatus) {
                            DatabaseReference CBCheck = FirebaseDatabase.getInstance().getReference("Users").child(ord_open.getUID()).child("OrderList");
                            CBCheck.orderByChild("state").equalTo("false").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        Toasty.error(AcceptedOrderActivityRider.this, "Complete the order list", Toasty.LENGTH_SHORT).show();
                                    }else{
                                        orderRef.child("status").setValue("inDelivery").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Intent intent = new Intent(AcceptedOrderActivityRider.this, DeliveryActivityRider.class);
                                                intent.putExtra("ORDER", ord_open);
                                                intent.putExtra("RKEY", getIntent().getStringExtra("RKEY"));
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

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

        //Receipt Button
        DatabaseReference CBCheck = FirebaseDatabase.getInstance().getReference("Users").child(ord_open.getUID()).child("OrderList");
        CBCheck.orderByChild("state").equalTo("false").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(ordertype){
                    return;
                }
                if (snapshot.exists()){
                    uploadbtn.setVisibility(View.GONE);
                }else{
                    uploadbtn.setVisibility(View.VISIBLE);
                    CBCheck.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropReceipt();
            }
        });

    }

    //Cropper
    private void cropReceipt() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Image"),PICK_RECEIPT_CODE);
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

        if (requestCode == PICK_RECEIPT_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                resizeReceipt(data.getData());
            }
        }

    }

    //Resize Profile
    private final ActivityResultLauncher<CropImageContractOptions> cropReceipt =
            registerForActivityResult(new CropImageContract(), this::onCropReceiptResult);

    public void resizeReceipt(Uri uri) {
        CropImageContractOptions options = new CropImageContractOptions(uri, new CropImageOptions())
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
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users").child(ord_open.getUID()).child("Orders").child(ord_open.getKey());

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
                        NextBTN.setVisibility(View.VISIBLE);
                        receiptholder.setVisibility(View.VISIBLE);
                        databaseReference.child("receipt").setValue(uri.toString());
                        receiptStatus = true;
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

    }

    @Override
    public void onStop() {
        // Do your stuff here
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set fee
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                deliveryFee.setText("₱ " + snapshot.child("price").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
