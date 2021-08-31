package com.example.tandonmedicalseller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {


    private EditText resetPassEmail_et;
    private CardView resetPass_cv;
    private ProgressBar reset_progressBar;
    private ImageView reset_back_iv;
    private FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetPassEmail_et =findViewById(R.id.resetPassEmail_et);
        reset_progressBar =findViewById(R.id.reset_progressBar);
        reset_back_iv = findViewById(R.id.reset_back_iv);
        resetPass_cv = findViewById(R.id.resetPass_cv);

        auth = FirebaseAuth.getInstance();

        resetPass_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = resetPassEmail_et.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                reset_progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }

                                reset_progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });

        reset_back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordActivity.this, signIn.class);
                startActivity(intent);
            }
        });

    }
}
