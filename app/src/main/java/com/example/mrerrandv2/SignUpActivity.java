package com.example.mrerrandv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity {


    private TextInputEditText firstname, lastname, emailIn, numIN, passIn, passInC;
    private static final String TAG = "SignUpActivity";
    CardView etcardOne, etcardTwo, etcardThree, etcardFour, etcardFive, etcardSix;
    private boolean isAtleast6 = false, hasupper = false, haslower = false, hasnum = false, nothis = false, nospaces = false;
    boolean passisgo;
    private progressBar progressBar;
    private ImageView toolbarback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressBar = new progressBar(this);
        toolbarback = findViewById(R.id.toolbarback);

        //Status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.finalLightGreen));
        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        //Nav Bar
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.newGray));
        }

        toolbarback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        firstname = findViewById(R.id.editFirstName);
        lastname = findViewById(R.id.editLastName);
        emailIn = findViewById(R.id.editTextEmail);
        numIN = findViewById(R.id.editTextMobile);
        passIn = findViewById(R.id.editTextPassword);
        passInC = findViewById(R.id.editTextConfirmPassword);

        etcardOne = findViewById(R.id.cardOne);
        etcardTwo = findViewById(R.id.cardTwo);
        etcardThree = findViewById(R.id.cardThree);
        etcardFour = findViewById(R.id.cardFour);
        etcardFive = findViewById(R.id.cardFive);
        etcardSix = findViewById(R.id.cardSix);


        TextView btnSignup = findViewById(R.id.btnRegister);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //
                String textFirstName = firstname.getText().toString();
                String textLastName = lastname.getText().toString();
                String textEmail = emailIn.getText().toString();
                String textNum = numIN.getText().toString();
                String textPass = passIn.getText().toString();
                String textPassC = passInC.getText().toString();
                String textType = "user";

                //Validate Mobile Num
                String mobileRegex = "[0][9][0-9]{9}"; // Starting Number
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textNum);


                if (TextUtils.isEmpty(textFirstName)) {
                    Snackbar.make(view, "Please enter your first name", Snackbar.LENGTH_LONG).show();
                    firstname.requestFocus();
                } else if (TextUtils.isEmpty(textLastName)) {
                    Snackbar.make(view, "Please enter your last name", Snackbar.LENGTH_LONG).show();
                    lastname.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Snackbar.make(view, "Please enter your email", Snackbar.LENGTH_LONG).show();
                    emailIn.requestFocus();
                } else if (TextUtils.isEmpty(textNum)) {
                    Snackbar.make(view, "Please enter your mobile number", Snackbar.LENGTH_LONG).show();
                    numIN.requestFocus();
                } else if (TextUtils.isEmpty(textPass)) {
                    Snackbar.make(view, "Please enter your password", Snackbar.LENGTH_LONG).show();
                    passIn.requestFocus();
                } else if (TextUtils.isEmpty(textPassC)) {
                    Snackbar.make(view, "Please enter your password", Snackbar.LENGTH_LONG).show();
                    passInC.requestFocus();
                } else if (!textPass.equals(textPassC)) {
                    Snackbar.make(view, "Your password does not match", Snackbar.LENGTH_LONG).show();
                    passIn.requestFocus();
                } else if (textNum.length() != 11) {
                    Snackbar.make(view, "Your mobile number does not contain 11 digits", Snackbar.LENGTH_LONG).show();
                    numIN.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Snackbar.make(view, "Number is invalid", Snackbar.LENGTH_LONG).show();
                    numIN.requestFocus();
                } else if (!passisgo) {
                    Snackbar.make(view, "Your password is invalid", Snackbar.LENGTH_LONG).show();
                    passIn.requestFocus();
                } else {
                    progressBar.show();
                    registerUser(textFirstName, textLastName, textEmail, textNum, textType, textPass);
                }

            }
        });
        inputChange();
    }


    @SuppressLint("ResourceType")
    public void passwordcheck() {
        String pass = passIn.getText().toString();

        // For 6 Char
        if (pass.length() >= 6 && pass.length()<= 20) {
            isAtleast6 = true;
            etcardOne.setCardBackgroundColor(Color.parseColor(getString(R.color.colorPrimary)));
        } else {
            isAtleast6 = false;
            etcardOne.setCardBackgroundColor(Color.parseColor(getString(R.color.Gray)));
        }

        // For uppercase
        if (pass.matches("(.*[A-Z].*)")) {
            hasupper = true;
            etcardTwo.setCardBackgroundColor(Color.parseColor(getString(R.color.colorPrimary)));
        } else {
            hasupper = false;
            etcardTwo.setCardBackgroundColor(Color.parseColor(getString(R.color.Gray)));
        }

        // For lowercase
        if (pass.matches("(.*[a-z].*)")) {
            haslower = true;
            etcardThree.setCardBackgroundColor(Color.parseColor(getString(R.color.colorPrimary)));
        } else {
            hasupper = false;
            etcardThree.setCardBackgroundColor(Color.parseColor(getString(R.color.Gray)));
        }

        // For numbers
        if (pass.matches("(.*[0-9].*)")) {
            hasnum = true;
            etcardFour.setCardBackgroundColor(Color.parseColor(getString(R.color.colorPrimary)));
        } else {
            hasnum = false;
            etcardFour.setCardBackgroundColor(Color.parseColor(getString(R.color.Gray)));
        }

        // For symbol
        if (pass.matches("^[a-zA-Z0-9]{1,20}$")) {
            nothis = true;
            etcardFive.setCardBackgroundColor(Color.parseColor(getString(R.color.colorPrimary)));
        } else {
            nothis = false;
            etcardFive.setCardBackgroundColor(Color.parseColor(getString(R.color.Gray)));
        }

        // For spaces
        if (pass.matches("^[a-zA-Z0-9]{1,20}$")) {
            nospaces = true;
            etcardSix.setCardBackgroundColor(Color.parseColor(getString(R.color.colorPrimary)));
        } else {
            nospaces = false;
            etcardSix.setCardBackgroundColor(Color.parseColor(getString(R.color.Gray)));
        }
        checkAllData();
    }

    // if all fields are filled properly the btn color will change
    @SuppressLint("ResourceType")
    private void checkAllData() {

        if(isAtleast6 && hasupper && haslower && hasnum && nothis && nospaces) {
            passisgo = true;
        }else{
            passisgo = false;
        }


}


    private void inputChange(){
        passIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordcheck();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void registerUser(String textFirstName,String textLastName, String textEmail, String textNum, String textType, String textPass) {
        FirebaseAuth auth = FirebaseAuth.getInstance();


        //Create user profile
        auth.createUserWithEmailAndPassword(textEmail,textPass).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //Change user name

                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFirstName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //Defaults
                    int totalstars = 0;
                    int totalrates = 0;

                    //ReadWriteUserDetails
                    ReadWriteUserDetailsUser writeUserDetails = new ReadWriteUserDetailsUser(textFirstName,textLastName ,textEmail,textNum,textType,totalstars, totalrates);


                    //Extract Users from db

                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                //Send Email Verification [WIP]
                                Toasty.success(SignUpActivity.this, "Registration successful", Toasty.LENGTH_LONG).show();

                                //Open User Profile after success

                                Intent intent = new Intent(SignUpActivity.this, UserMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                //Send Permission Level

                                DatabaseReference permlevels = FirebaseDatabase.getInstance().getReference("permissions");
                                permlevels.child(firebaseUser.getUid()).setValue(writeUserDetails);

                                startActivity(intent);
                                finish();
                            }else{
                                Toasty.error(SignUpActivity.this, "User registration failed", Toasty.LENGTH_LONG).show();

                            }
                            //Hide progressbar
                            progressBar.dismiss();
                        }
                    });
                }else{
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
                        Toasty.error(SignUpActivity.this, e.getMessage(), Toasty.LENGTH_LONG).show();
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
