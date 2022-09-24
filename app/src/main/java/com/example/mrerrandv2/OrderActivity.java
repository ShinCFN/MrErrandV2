package com.example.mrerrandv2;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
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

import es.dmoral.toasty.Toasty;

public class OrderActivity extends AppCompatActivity {

    View pay, imgorder, addbutton;
    private final int PICK_IMAGE_CODE = 17;
    Boolean isImageorder = false;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private progressBar progressBar;
    EditText addnewitem, addnewqty;
    DBOrderList dbOrderList;
    ImageView toolbarback;
    ConstraintLayout wholeorderrv;

    RecyclerView orderlistrv;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        pay = findViewById(R.id.btnNext);

        dbOrderList = new DBOrderList();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        wholeorderrv = findViewById(R.id.wholeorderrv);
        progressBar = new progressBar(this);
        imgorder = findViewById(R.id.imgorder);
        orderlistrv = findViewById(R.id.orderarray);
        addbutton = findViewById(R.id.addbutton);
        addnewitem = findViewById(R.id.additem);
        addnewqty = findViewById(R.id.addqty);
        toolbarback = findViewById(R.id.toolbarback);

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.finalBackground));

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.finalDarkGray));
            View view = getWindow().getDecorView();
        }

        //Toolbar
        TextView toolMain = findViewById(R.id.toolbarmain);
        TextView toolSub = findViewById(R.id.toolbarsub);
        toolMain.setText("");
        toolSub.setText("");

        toolbarback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //RV visibility
        DatabaseReference orderlistRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid());
        orderlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("OrderList").exists()){
                    wholeorderrv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler View
        orderlistrv.setLayoutManager(new WrapContentLinearLayoutManager(this));
        FirebaseRecyclerOptions<OrderList> options =
                new FirebaseRecyclerOptions.Builder<OrderList>()
                        .setQuery(dbOrderList.get(), new SnapshotParser<OrderList>() {
                            @NonNull
                            @Override
                            public OrderList parseSnapshot(@NonNull DataSnapshot snapshot) {
                                OrderList orl = snapshot.getValue(OrderList.class);
                                orl.setKey(snapshot.getKey());
                                return orl;
                            }
                        }).build();

        adapter = new FirebaseRecyclerAdapter(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull Object o) {
                OrderListVH vh = (OrderListVH) viewHolder;
                OrderList list = (OrderList) o;
                vh.item.setText(list.getItem());
                vh.qty.setText(list.getQty());

                vh.delItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference databaseReference;
                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid())
                                .child("OrderList");
                        databaseReference.child(list.getKey()).removeValue();
                    }
                });

//                //Set color
//                if (position % 2 == 0){
//                    vh.holder.setBackgroundColor(getResources().getColor(R.color.Gray));
//                } else {
//                    vh.holder.setBackgroundColor(getResources().getColor(R.color.white));
//                }
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(OrderActivity.this).inflate(R.layout.layout_orderlist, parent, false);
                return new OrderListVH(view);
            }

            @Override
            public void onDataChanged() {
            }
        };
        orderlistrv.setAdapter(adapter);


        //Add item
        String state = "false";
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.show();
                if (!addnewitem.getText().toString().isEmpty() && !addnewqty.getText().toString().isEmpty()) {
                    OrderList orderList = new OrderList(addnewitem.getText().toString(), addnewqty.getText().toString(), state);
                    dbOrderList.add(orderList).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            addnewitem.setText("");
                            addnewqty.setText("");
                            wholeorderrv.setVisibility(View.VISIBLE);
                        }
                    });

                } else {
                    if (addnewitem.getText().toString().isEmpty()) {
                        Toasty.error(OrderActivity.this, "Enter item", Toasty.LENGTH_LONG).show();
                    } else {
                        Toasty.error(OrderActivity.this, "Enter quantity", Toasty.LENGTH_LONG).show();
                    }
                }
                progressBar.dismiss();
                addnewitem.requestFocus();
            }
        });


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
                progressBar.show();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("OrderList").exists()) {
                            isImageorder = false;
                            Intent intent = new Intent(OrderActivity.this, PaymentActivity.class);
                            intent.putExtra("type", isImageorder);
                            adapter.stopListening();
                            startActivity(intent);
                            finish();

                        } else {
                            Toasty.error(OrderActivity.this, "Order list is empty", Toasty.LENGTH_LONG).show();
                        }
                        progressBar.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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

        if (requestCode == PICK_IMAGE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                resizeOrder(data.getData());
            }
        }

    }

    //Resize Profile
    private final ActivityResultLauncher<CropImageContractOptions> cropReceipt =
            registerForActivityResult(new CropImageContract(), this::onCropOrderResult);

    public void resizeOrder(Uri uri) {
        CropImageContractOptions options = new CropImageContractOptions(uri, new CropImageOptions())
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

        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {

        } else {
            Toasty.error(OrderActivity.this, "Failed", Toasty.LENGTH_LONG).show();
        }
    }

    //Uploading
    private void uploadReceipt(Uri uriContent) {

        Order ord_open = (Order) getIntent().getSerializableExtra("ORDER");

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Orders");

        progressBar.show();

        storageReference.child(auth.getCurrentUser().getUid()).putFile(uriContent).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                isImageorder = true;
                progressBar.dismiss();
                storageReference.child(auth.getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent intent = new Intent(OrderActivity.this, PaymentActivity.class);
                        intent.putExtra("imgorder", uri.toString());
                        intent.putExtra("type", isImageorder);
                        adapter.stopListening();
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.cancel();
            }
        });
    }

    @Override
    public void onBackPressed() {
        adapter.stopListening();
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}