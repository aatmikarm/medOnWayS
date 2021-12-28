package com.example.tandonmedicalseller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class profile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 77;

    private TextView profileName_tv, profile_no_of_orders, profile_no_of_prescriptions;
    private EditText profileName_et, profileEmail_et, profilePhone_et, profileBio_et;
    private ImageView logOut_iv, profileBack_iv, profile_iv;
    private CardView profileUpload_cv;

    private String currentUserUid;
    private Uri filePath;

    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        

        profileName_tv = findViewById(R.id.profileName_tv);
        profileBack_iv = findViewById(R.id.profileBack_iv);
        profileUpload_cv = findViewById(R.id.profileUpload_cv);

        profileEmail_et = findViewById(R.id.profileEmail_et);
        profileName_et = findViewById(R.id.profileName_et);
        profilePhone_et = findViewById(R.id.profilePhone_et);
        profileBio_et = findViewById(R.id.profileBio_et);

        logOut_iv = findViewById(R.id.logOut_iv);
        profile_no_of_orders = findViewById(R.id.profile_no_of_orders);
        profile_no_of_prescriptions = findViewById(R.id.profile_no_of_prescriptions);
        profile_iv = findViewById(R.id.profile_iv);

        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserUid = firebaseAuth.getUid();

        mDb.collection("seller")
                .document(currentUserUid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        profileName_tv.setText(document.get("name").toString());

                        profileName_et.setText(document.get("name").toString());
                        profileEmail_et.setText(document.get("email").toString());
                        profilePhone_et.setText(document.get("phone").toString());
                        profileBio_et.setText(document.get("bio").toString());


                    } else {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "failed ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setCurrentUserImage();


        profile_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });


        profileBack_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logOut_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        profileUpload_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDb.collection("seller").document(currentUserUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Map<String, Object> updateUserInfo = new HashMap<>();
                        updateUserInfo.put("name", profileName_et.getText().toString());
                        updateUserInfo.put("phone", profilePhone_et.getText().toString());
                        updateUserInfo.put("bio", profileBio_et.getText().toString());

                        mDb.collection("seller").document(currentUserUid).update(updateUserInfo);
                        profileName_tv.setText(profileName_et.getText().toString());
                        Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });


    }

    // Select Image method
    private void SelectImage() {   // select image from photos in galary funtion
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }


    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(profile.this, signIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setCurrentUserImage() {

        StorageReference ref = mStorageRef.child("images/" + currentUserUid).child("profilepic.jpg");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageView imageView;
                imageView = findViewById(R.id.profile_iv);
                Glide.with(getApplicationContext()).load(uri).into(imageView);

                Map<String, Object> sellerImageUrl = new HashMap<>();
                sellerImageUrl.put("imageUrl", uri.toString());
                mDb.collection("seller").document(currentUserUid).update(sellerImageUrl);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {   // Get the Uri of data
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_iv.setImageBitmap(bitmap);
                try {
                    uploadImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() throws IOException {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final String uid = firebaseAuth.getUid();
            StorageReference childRef2 = mStorageRef.child("images/" + uid).child("profilepic.jpg");
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask2 = childRef2.putBytes(data);
            uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });

        }
    }

}