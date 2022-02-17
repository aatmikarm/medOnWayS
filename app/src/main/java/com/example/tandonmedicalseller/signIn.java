package com.example.tandonmedicalseller;

import android.content.Intent;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class signIn extends AppCompatActivity {


    private EditText signin_Email_et, signin_Password_et;
    private TextView signin_login_tv, signin_forgot_tv, signin_createAccount_tv;
    private ImageView signin_iv;
    private ProgressBar signin_progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore mDb;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("157671413790-a148v3i9ngrfobso9ud9lrh8f58450tg.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 77);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            verifyUserType();
        }

        signin_Email_et = (EditText) findViewById(R.id.signin_Email_et);
        signin_Password_et = (EditText) findViewById(R.id.signin_Password_et);
        signin_login_tv = (TextView) findViewById(R.id.signin_login_tv);
        signin_createAccount_tv = (TextView) findViewById(R.id.signup_login_tv);
        signin_forgot_tv = (TextView) findViewById(R.id.signup_reset_tv);
        signin_progressBar = (ProgressBar) findViewById(R.id.signIn_progressBar);
        signin_progressBar.setVisibility(View.GONE);


        signin_login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signin_Email_et.getText().toString();
                final String password = signin_Password_et.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                signin_progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(signIn.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                signin_progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    Toast.makeText(signIn.this, "check your email and password or sign up", Toast.LENGTH_LONG).show();
                                } else {
                                    verifyUserType();
                                }
                            }
                        });
            }
        });

        signin_createAccount_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signIn.this, signUp.class));
            }
        });

        signin_forgot_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signIn.this, resetPassword.class));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 77) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

            AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            firebaseAuth.signInWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    if (acct != null) {

                        if (authResult.getAdditionalUserInfo().isNewUser()) {
                            Toast.makeText(signIn.this, "Please Register your account", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(signIn.this, signUp.class));
                            finish();
                        } else {
                            startActivity(new Intent(signIn.this, MainActivity.class));
                            finish();
                        }

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(signIn.this, "Error", Toast.LENGTH_LONG).show();
                }
            });

        } catch (ApiException e) {


        }
    }


    private void verifyUserType() {
        mDb = FirebaseFirestore.getInstance();
        mDb.collection("seller").document(firebaseAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String type = document.get("type").toString();
                        if (type != null && type.equals("seller")) {
                            if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                signin_progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(signIn.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(signIn.this, "Your Email is not verified! Please verify your email address", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            signin_progressBar.setVisibility(View.GONE);
                            Toast.makeText(signIn.this, "You are not authorized to access this application!",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                        }
                    } else {
                        signin_progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "user does not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    signin_progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "failed ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}