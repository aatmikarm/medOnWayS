package com.example.tandonmedicalseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class editProductDetails extends AppCompatActivity implements categoryInterface {

    RecyclerView categoriesRecyclerView;
    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    private static final int PICK_IMAGE_REQUEST = 77;

    String dateandtimepattern = "ssmmHHddMMyyyy";
    String tempProductUrl;

    // intent getters
    String category;
    String productId;
    String seller;
    String description;
    String discount;
    String imageUrl;
    String mrp;
    String name;
    String price;


    ArrayList<categoriesModelList> categoriesModelLists;


    private CardView editBtn;
    private ImageView backBtn, editImage_iv;
    private EditText editName_et, editPrice_et, editMrp_et, editDescription_et;


    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_details);

        


        firebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        editBtn = findViewById(R.id.edit_final_btn);
        backBtn = findViewById(R.id.edit_back_btn);
        editImage_iv = findViewById(R.id.editImage_iv);
        //editImage_iv = findViewById(R.id.editImage_iv);

        editName_et = findViewById(R.id.editName_et);
        editPrice_et = findViewById(R.id.editPrice_et);
        editMrp_et = findViewById(R.id.edit_mrp_et);
        editDescription_et = findViewById(R.id.editDescription_et);

        if (getIntent().getExtras() != null) {
            this.category = (String) getIntent().getExtras().get("category");
            Toast.makeText(this, category, Toast.LENGTH_LONG).show();
            this.productId = (String) getIntent().getExtras().get("productId");
            this.seller = (String) getIntent().getExtras().get("seller");
            this.description = (String) getIntent().getExtras().get("description");
            this.discount = (String) getIntent().getExtras().get("discount");
            this.imageUrl = (String) getIntent().getExtras().get("imageUrl");
            this.mrp = (String) getIntent().getExtras().get("mrp");
            this.name = (String) getIntent().getExtras().get("name");
            this.price = (String) getIntent().getExtras().get("price");

        }


        //show all Product details
        Glide.with(this).load(imageUrl).into(editImage_iv);

        categoriesModelLists = getAllCategories();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                categoriesRecyclerView = findViewById(R.id.categories_recycler_view);
                categoriesAdapterUpload categoriesAdapterUpload = new categoriesAdapterUpload
                        (getApplicationContext(), categoriesModelLists, editProductDetails.this);
                categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                categoriesRecyclerView.setAdapter(categoriesAdapterUpload);

            }
        }, 3000);

        editName_et.setText(name);
        editPrice_et.setText(price);
        editMrp_et.setText(mrp);
        editDescription_et.setText(description);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        editImage_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editNewProduct();

            }
        });

    }


    // Select Image method
    private void SelectImage() {

        //from gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);

        //from camera

    }


    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {   // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                editImage_iv.setImageBitmap(bitmap);

            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    private void editNewProduct() {


        //getting email and password from edit texts
        String price = editPrice_et.getText().toString();
        String mrp = editMrp_et.getText().toString();


        if (Integer.parseInt(price) > Integer.parseInt(mrp)) {
            Toast.makeText(this, "Discounted Price cannot be more than MRP of product", Toast.LENGTH_LONG).show();
            return;
        }


        editImage();

    }

    private void editImage() {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        if (filePath != null){

            Toast.makeText(this, "fill exist", Toast.LENGTH_LONG).show();
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

                            int price = Integer.parseInt(editPrice_et.getText().toString());
                            int mrp = Integer.parseInt(editMrp_et.getText().toString());
                            int discount = 100 - (price * 100) / mrp;

                            String name = editName_et.getText().toString();
                            String[] tagsArray = name.split(" ");
                            List<String> tags = Arrays.asList(tagsArray);

                            Map<String, Object> editNewProduct = new HashMap<>();
                            editNewProduct.put("name", editName_et.getText().toString());
                            editNewProduct.put("price", editPrice_et.getText().toString());
                            editNewProduct.put("mrp", editMrp_et.getText().toString());
                            editNewProduct.put("discount", String.valueOf(discount));
                            editNewProduct.put("category", category);
                            editNewProduct.put("tags", tags);
                            editNewProduct.put("description", editDescription_et.getText().toString());
                            editNewProduct.put("productId", productId);
                            editNewProduct.put("imageUrl", uri.toString());
                            editNewProduct.put("seller", seller);

                            mDb.collection("products").document(productId).update(editNewProduct);
                            progressDialog.dismiss();
                            finish();


                        }
                    });


                }
            });

        }

        if (filePath == null){

            int price = Integer.parseInt(editPrice_et.getText().toString());
            int mrp = Integer.parseInt(editMrp_et.getText().toString());
            int discount = 100 - (price * 100) / mrp;

            Map<String, Object> editNewProduct = new HashMap<>();
            editNewProduct.put("name", editName_et.getText().toString());
            editNewProduct.put("price", editPrice_et.getText().toString());
            editNewProduct.put("mrp", editMrp_et.getText().toString());
            editNewProduct.put("discount", String.valueOf(discount));
            editNewProduct.put("category", category);
            editNewProduct.put("description", editDescription_et.getText().toString());
            editNewProduct.put("productId", productId);
            editNewProduct.put("imageUrl", imageUrl);

            mDb.collection("products").document(productId).update(editNewProduct);
            progressDialog.dismiss();
            finish();

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

        Toast.makeText(this, "Category = " + categoriesModelLists.get(position).getName().toString(), Toast.LENGTH_LONG).show();
        this.category = categoriesModelLists.get(position).getName().toString();

    }
}