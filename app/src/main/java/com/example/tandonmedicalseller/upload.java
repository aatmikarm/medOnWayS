package com.example.tandonmedicalseller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class upload extends AppCompatActivity implements categoryInterface {
    RecyclerView categoriesRecyclerView;
    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private String currentUserUid, seller, sellerId, sellerToken, categoryName, tempProductUrl;
    ArrayList<categoriesModelList> categoriesModelLists;
    private CardView uploadBtn;
    private ImageView backBtn, uploadImage_iv;
    private EditText uploadName_et, uploadPrice_et, uploadMrp_et, uploadDiscount_et, uploadDescription_et;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        
        firebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        currentUserUid = firebaseAuth.getUid();
        uploadBtn = findViewById(R.id.upload_final_btn);
        backBtn = findViewById(R.id.upload_back_btn);
        uploadImage_iv = findViewById(R.id.uploadImage_iv);
        uploadName_et = findViewById(R.id.uploadName_et);
        uploadPrice_et = findViewById(R.id.uploadPrice_et);
        uploadMrp_et = findViewById(R.id.upload_mrp_et);
        uploadDescription_et = findViewById(R.id.uploadDescription_et);
        categoriesModelLists = getAllCategories();
        getSellerDetails();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                categoriesRecyclerView = findViewById(R.id.categories_recycler_view);
                categoriesAdapterUpload categoriesAdapterUpload = new categoriesAdapterUpload(getApplicationContext(), categoriesModelLists, upload.this);
                categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                categoriesRecyclerView.setAdapter(categoriesAdapterUpload);
            }
        }, 3000);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
        uploadImage_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadNewProduct();
            }
        });
    }

    private void getSellerDetails() {
        mDb.collection("seller")
                .document(currentUserUid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        seller = document.get("name").toString();
                        sellerId = document.get("uid").toString();
                        sellerToken = document.get("sellerToken").toString();
                    } else {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "failed ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), 77);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 77 && resultCode == RESULT_OK && data != null && data.getData() != null) {   // Get the Uri of data
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadImage_iv.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    private void uploadNewProduct() {
        String name = uploadName_et.getText().toString();
        String price = uploadPrice_et.getText().toString();
        String mrp = uploadMrp_et.getText().toString();
        String category = categoryName;
        String description = uploadDescription_et.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Please enter price", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(mrp)) {
            Toast.makeText(this, "Please enter MRP of Product", Toast.LENGTH_LONG).show();
            return;
        }
        if (Integer.parseInt(price) > Integer.parseInt(mrp)) {
            Toast.makeText(this, "Discounted Price cannot be more than MRP of product", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please enter description", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Please select category", Toast.LENGTH_LONG).show();
            return;
        }
        uploadImage();
    }

    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            Uri file = filePath;
            StorageReference tempRef = storageReference.child("products/" + file.getLastPathSegment());
            UploadTask uploadTask = tempRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    tempRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //setImageUrl(uri.toString());
                            SimpleDateFormat sdf = new SimpleDateFormat("ssmmHHddMMyyyy");
                            final String productId = sdf.format(new Date());
                            int price = Integer.parseInt(uploadPrice_et.getText().toString());
                            int mrp = Integer.parseInt(uploadMrp_et.getText().toString());
                            int discount = 100 - (price * 100) / mrp;
                            String nameTags = uploadName_et.getText().toString();
                            String[] tagsArray = nameTags.split(" ");
                            List<String> tags = Arrays.asList(tagsArray);
                            Map<String, Object> uploadNewProduct = new HashMap<>();
                            uploadNewProduct.put("name", uploadName_et.getText().toString());
                            uploadNewProduct.put("price", uploadPrice_et.getText().toString());
                            uploadNewProduct.put("mrp", uploadMrp_et.getText().toString());
                            uploadNewProduct.put("discount", String.valueOf(discount));
                            uploadNewProduct.put("category", categoryName);
                            uploadNewProduct.put("seller", seller);
                            uploadNewProduct.put("tags", tags);
                            uploadNewProduct.put("sellerId", sellerId);
                            uploadNewProduct.put("sellerToken", sellerToken);
                            uploadNewProduct.put("description", uploadDescription_et.getText().toString());
                            uploadNewProduct.put("productId", productId);
                            uploadNewProduct.put("imageUrl", uri.toString());
                            mDb.collection("products").document(productId).set(uploadNewProduct);
                            mDb.collection("seller").document(sellerId).collection("products").document(productId).set(uploadNewProduct);
                            progressDialog.dismiss();
                            finish();
                        }
                    });
                }
            });
        }
        if (filePath == null) {
            Toast.makeText(this, "Please Upload Image", Toast.LENGTH_LONG).show();
        }
    }

    private void setImageUrl(String uri) {
        this.tempProductUrl = uri;
    }

    private ArrayList<categoriesModelList> getAllCategories() {
        String currentUserUid = firebaseAuth.getUid();
        final ArrayList<categoriesModelList> categoriesModelLists = new ArrayList<>();
        mDb.collection("categories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //it performs a for loop to get each seperate user details and location
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        categoriesModelList categoriesModelList = new categoriesModelList();
                        categoriesModelList.setName((String) document.get("name"));
                        categoriesModelList.setImageUrl((String) document.get("image"));
                        categoriesModelLists.add(categoriesModelList);
                    }
                }
            }
        });
        return categoriesModelLists;
    }

    @Override
    public void categoryOnClickInterface(int position) {
        //Toast.makeText(this, "Category = " + categoriesModelLists.get(position).getName().toString(), Toast.LENGTH_LONG).show();
        this.categoryName = categoriesModelLists.get(position).getName().toString();
    }
}