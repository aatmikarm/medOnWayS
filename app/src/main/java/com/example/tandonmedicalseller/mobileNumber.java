package com.example.tandonmedicalseller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class mobileNumber extends AppCompatActivity {

    private EditText mobileNumber_phoneNumber_et, mobileNumber_otp_et;
    private CardView mobileNumber_verify_cv;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore mDb;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number);
        firebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mobileNumber_phoneNumber_et = (EditText) findViewById(R.id.mobileNumber_phoneNumber_et);
        mobileNumber_otp_et = (EditText) findViewById(R.id.mobileNumber_otp_et);
        mobileNumber_verify_cv = (CardView) findViewById(R.id.mobileNumber_verify_cv);

        if (getIntent().getExtras() != null) {
            this.uid = (String) getIntent().getExtras().get("uid");
        }

        mobileNumber_verify_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = mobileNumber_phoneNumber_et.getText().toString();


                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(mobileNumber.this, "Please enter phone number", Toast.LENGTH_LONG).show();
                    return;
                }

                Map<String, Object> user = new HashMap<>();
                user.put("phone", phoneNumber);
                mDb.collection("seller").document(uid).update(user);

                Toast.makeText(mobileNumber.this, "Successfully Verified", Toast.LENGTH_LONG).show();
                startActivity(new Intent(mobileNumber.this, signIn.class));
                finish();
            }
        });


    }
}