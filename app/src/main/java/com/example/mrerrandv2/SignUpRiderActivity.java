package com.example.mrerrandv2;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpRiderActivity extends AppCompatActivity {


    private EditText firstname, lastname, emailIn, numIN, passIn, passInC;
    private static final String TAG = "SignUpActivity";
    private ImageView rLicense, rPlate;
    private progressBar progressBar;

    private final int PICK_LICENSE_CODE = 12;
    private final int PICK_PLATE_CODE = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ridersignup);

        progressBar = new progressBar(this);

        // Status Bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().setStatusBarColor(Color.TRANSPARENT);


        Toast.makeText(SignUpRiderActivity.this, "You can register now", Toast.LENGTH_SHORT).show();

        //Get id from layout

        firstname = findViewById(R.id.editFirstName);
        lastname = findViewById(R.id.editLastName);
        emailIn = findViewById(R.id.editTextEmail);
        numIN = findViewById(R.id.editTextMobile);
        passIn = findViewById(R.id.editTextPassword);
        passInC = findViewById(R.id.editTextConfirmPassword);


        // Signup Button
        TextView btnSignup = findViewById(R.id.btnRegister);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textFirstName = firstname.getText().toString();
                String textLastName = lastname.getText().toString();
                String textEmail = emailIn.getText().toString();
                String textNum = numIN.getText().toString();
                String textPass = passIn.getText().toString();
                String textPassC = passInC.getText().toString();
                String textType = "rider";

                //Validate Mobile Num
                String mobileRegex = "[0][9][0-9]{9}"; // Starting Number
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textNum);


                if (TextUtils.isEmpty(textFirstName)) {
                    Toast.makeText(SignUpRiderActivity.this, "Please enter your first name", Toast.LENGTH_LONG).show();
                    firstname.setError("First name is required");
                    firstname.requestFocus();
                } else if (TextUtils.isEmpty(textLastName)) {
                    Toast.makeText(SignUpRiderActivity.this, "Please enter your last name", Toast.LENGTH_LONG).show();
                    lastname.setError("Last name is required");
                    lastname.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(SignUpRiderActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    emailIn.setError("Email is required");
                    emailIn.requestFocus();
                } else if (TextUtils.isEmpty(textNum)) {
                    Toast.makeText(SignUpRiderActivity.this, "Please enter your mobile number", Toast.LENGTH_LONG).show();
                    numIN.setError("Mobile Number is required");
                    numIN.requestFocus();
                } else if (TextUtils.isEmpty(textPass)) {
                    Toast.makeText(SignUpRiderActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    passIn.setError("Password is required");
                    passIn.requestFocus();
                } else if (TextUtils.isEmpty(textPassC)) {
                    Toast.makeText(SignUpRiderActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    passInC.setError("Password is required");
                    passInC.requestFocus();
                } else if (!textPass.equals(textPassC)) {
                    Toast.makeText(SignUpRiderActivity.this, "Your password does not match", Toast.LENGTH_LONG).show();
                    passIn.setError("Password does not match");
                    passIn.requestFocus();
                } else if (textNum.length() != 11) {
                    Toast.makeText(SignUpRiderActivity.this, "Your mobile number does not contain 11 digits", Toast.LENGTH_LONG).show();
                    numIN.setError("Mobile number invalid");
                    numIN.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(SignUpRiderActivity.this, "Please number is invalid", Toast.LENGTH_LONG).show();
                    numIN.setError("Mobile number invalid");
                    numIN.requestFocus();
                } else {
                    progressBar.show();
                    registerUser(textFirstName, textLastName, textEmail, textNum, textType, textPass);
                }
            }
        });
    }


    //Register user button: Send to DB

    private void registerUser(String textFirstName, String textLastName, String textEmail, String textNum, String textType, String textPass) {
        FirebaseAuth auth = FirebaseAuth.getInstance();


        //Create user profile
        auth.createUserWithEmailAndPassword(textEmail, textPass).addOnCompleteListener(SignUpRiderActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //Change user name

                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFirstName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //ReadWriteUserDetails
                    ReadWriteUserDetailsRider writeUserDetails = new ReadWriteUserDetailsRider(textFirstName, textLastName, textEmail, textNum, textType);

                    //Extract Users from db

                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Riders");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                //Send Email Verification [WIP]
                                Toast.makeText(SignUpRiderActivity.this, "User registered sucessfully", Toast.LENGTH_LONG).show();

                                //Open User Profile after success

                                Intent intent = new Intent(SignUpRiderActivity.this, RiderLandingPage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                //Send Permission Level

                                DatabaseReference permlevels = FirebaseDatabase.getInstance().getReference("permissions");
                                permlevels.child(firebaseUser.getUid()).setValue(writeUserDetails);

                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SignUpRiderActivity.this, "User registration failed", Toast.LENGTH_LONG).show();

                            }
                            //Hide progressbar
                            progressBar.dismiss();
                        }
                    });


                } else {
                    progressBar.dismiss();
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        passIn.setError("Your password is too weak!");
                        passIn.requestFocus();

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        emailIn.setError("Your email is invalid or already in use!");
                        emailIn.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        emailIn.setError("User is already registered with this email!");
                        emailIn.requestFocus();
                    } catch (Exception e) {
                        Toast.makeText(SignUpRiderActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
