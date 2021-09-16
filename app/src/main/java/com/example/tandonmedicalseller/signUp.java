package com.example.tandonmedicalseller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class signUp extends AppCompatActivity {

    private TextView signup_login_tv, signup_reset_tv, signup_tv;
    private EditText signupName_et, signupEmail_et, signupPhone_et, signupPassword_et;
    private ImageView signup_back_iv;
    private ProgressBar signup_progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore mDb;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "DocSnippets";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        signupName_et = (EditText) findViewById(R.id.signupName_et);
        signupEmail_et = (EditText) findViewById(R.id.signupEmail_et);
        signupPhone_et = (EditText) findViewById(R.id.signupPhone_et);
        signupPassword_et = (EditText) findViewById(R.id.signupPassword_et);

        signup_login_tv = findViewById(R.id.signup_login_tv);
        signup_reset_tv = findViewById(R.id.signup_reset_tv);
        signup_tv = findViewById(R.id.signup_tv);

        signup_progressBar = (ProgressBar) findViewById(R.id.signup_progressBar);

        signup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String signupName = signupName_et.getText().toString();
                String signupEmail = signupEmail_et.getText().toString();
                String signupPhone = signupPhone_et.getText().toString().trim();
                String signupPassword = signupPassword_et.getText().toString().trim();

                if (TextUtils.isEmpty(signupName)) {
                    Toast.makeText(signUp.this, "Please enter name", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(signupPhone)) {
                    Toast.makeText(signUp.this, "Please enter phone number", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(signupEmail)) {
                    Toast.makeText(signUp.this, "Please enter email", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(signupPassword)) {
                    Toast.makeText(signUp.this, "Please enter password", Toast.LENGTH_LONG).show();
                    return;
                }

                if (signupPassword.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                signup_progressBar.setVisibility(View.VISIBLE);

               firebaseAuth.createUserWithEmailAndPassword(signupEmail,signupPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {

                       if (task.isSuccessful()) {
                           String uid = firebaseAuth.getUid();
                           Date CurrentDateAndTime = new Date();

                           Map<String, Object> seller = new HashMap<>();
                           seller.put("uid", uid);
                           seller.put("name", signupName);
                           seller.put("phone", signupPhone);
                           seller.put("email", signupEmail);
                           seller.put("password", signupPassword);
                           seller.put("bio", "");
                           seller.put("CurrentDateAndTime", CurrentDateAndTime);

                           mDb.collection("seller").document(uid).set(seller);

                           Toast.makeText(signUp.this, "Successfully registered", Toast.LENGTH_LONG).show();
                           startActivity(new Intent(getApplicationContext(), MainActivity.class));
                           finish();

                       } else {
                           Toast.makeText(signUp.this, "Registration Error", Toast.LENGTH_LONG).show();
                       }
                       signup_progressBar.setVisibility(View.GONE);

                   }
               });

            }

        });

        signup_login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signUp.this, signIn.class));
            }
        });

        signup_reset_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signUp.this, ResetPasswordActivity.class));
            }
        });
    }

}