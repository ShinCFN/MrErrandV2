package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "Login Activity";
    private String type;
    private TextView SignUpButton;
    private TextView Ridersignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

//        getSupportActionBar("Login");

        editTextEmail = findViewById(R.id.editTextEmail) ;
        editTextPassword = findViewById(R.id.editTextPassword) ;
        progressBar = findViewById(R.id.progressBar1);

        authProfile = FirebaseAuth.getInstance();


        // Go to signup
        SignUpButton = (TextView)findViewById(R.id.gotosignup);
        Ridersignup = (TextView)findViewById(R.id.gotoridersignup);


        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        // Go to rider signup
        Ridersignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpRiderActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //Login
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editTextEmail.getText().toString();
                String textPass = editTextPassword.getText().toString();

                progressBar.setVisibility(View.VISIBLE);

                if(TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(SignInActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    editTextEmail.setError("Enter Email Address");
                    editTextEmail.requestFocus();
                }else if(TextUtils.isEmpty(textPass)) {
                    Toast.makeText(SignInActivity.this, "Please your password", Toast.LENGTH_LONG).show();
                    editTextPassword.setError("Enter Password");
                    editTextPassword.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPass);
                }
            }
        });
    }


    //Sign in with email and pass
    private void loginUser(String textEmail, String textPass) {

        authProfile.signInWithEmailAndPassword(textEmail,textPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SignInActivity.this, "Already logged in!", Toast.LENGTH_LONG).show();
                    //GET USER PERMS FROM DB
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    String userID = firebaseUser.getUid();
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("permissions");

                    referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                            if (readUserDetails != null) {

                                String usertype = readUserDetails.type;

                                if (usertype.equals("admin")) {
                                    startActivity(new Intent(SignInActivity.this, AdminActivity.class));
                                    finish();
                                }else if (usertype.equals("rider")) {
                                    startActivity(new Intent(SignInActivity.this, RiderLandingPage.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SignInActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });
                }else{
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        editTextEmail.setError("User does not exist or is no longer valid");
                        editTextEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e ) {
                        editTextEmail.setError("Invalid credentials");
                        editTextEmail.requestFocus();
                    } catch (Exception e ){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(SignInActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
