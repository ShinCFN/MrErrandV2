package com.example.mrerrandv2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

import es.dmoral.toasty.Toasty;

public class SignInActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth authProfile;
    private static final String TAG = "Login Activity";
    private TextView SignUpButton;
    private TextView Ridersignup;
    private progressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editTextEmail = findViewById(R.id.editTextEmail) ;
        editTextPassword = findViewById(R.id.editTextPassword) ;
        progressBar = new progressBar(SignInActivity.this);

        authProfile = FirebaseAuth.getInstance();

        //Progress bar
        progressBar.dismiss();

        // Go to signup
        SignUpButton = (TextView)findViewById(R.id.gotosignup);
        Ridersignup = (TextView)findViewById(R.id.gotoridersignup);

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.finalBackground));

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setNavigationBarColor(getResources().getColor(R.color.finalBackground));
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }


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
        LinearLayout btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editTextEmail.getText().toString();
                String textPass = editTextPassword.getText().toString();

                if(TextUtils.isEmpty(textEmail)) {
                    Snackbar.make(view, "Please enter your email address", Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.snackbarBase)).setTextColor(getResources().getColor(R.color.finalLightGreen)).show();
                }else if(TextUtils.isEmpty(textPass)) {
                    Snackbar.make(view, "Please enter your password", Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.snackbarBase)).setTextColor(getResources().getColor(R.color.finalLightGreen)).show();
                }else {
                    progressBar.show();
                    loginUser(textEmail, textPass,view);
                }
            }
        });
    }


    //Sign in with email and pass
    private void loginUser(String textEmail, String textPass, View view) {

        authProfile.signInWithEmailAndPassword(textEmail,textPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //GET USER PERMS FROM DB
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    String userID = firebaseUser.getUid();
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("permissions");

                    referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ReadWriteUserDetailsUser readUserDetails = snapshot.getValue(ReadWriteUserDetailsUser.class);

                            if (readUserDetails != null) {

                                String usertype = readUserDetails.type;

                                if (usertype.equals("admin")) {
                                    startActivity(new Intent(SignInActivity.this, AdminMainActivity.class));
                                    finish();
                                }else if (usertype.equals("rider")) {
                                    startActivity(new Intent(SignInActivity.this, RiderLandingPage.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(SignInActivity.this, UserMainActivity.class));
                                    finish();
                                }
                            }
                            progressBar.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Snackbar.make(view, "Something went wrong", Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.snackbarBase)).setTextColor(getResources().getColor(R.color.finalLightGreen)).show();
                            progressBar.dismiss();
                        }
                    });
                }else{
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        Snackbar.make(view, "User does not exist or is no longer valid", Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.snackbarBase)).setTextColor(getResources().getColor(R.color.finalLightGreen)).show();
                    } catch (FirebaseAuthInvalidCredentialsException e ) {
                        Snackbar.make(view, "Invalid Credentials", Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.snackbarBase)).setTextColor(getResources().getColor(R.color.finalLightGreen)).show();
                    } catch (Exception e ){
                        Log.e(TAG, e.getMessage());
                        Snackbar.make(view, "Something went wrong", Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.snackbarBase)).setTextColor(getResources().getColor(R.color.finalLightGreen)).show();
                    }

                }
                progressBar.dismiss();
            }
        });
    }

}
