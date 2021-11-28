package com.example.tandonmedicalseller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView total_earning_tv,order_count_tv;
    private ImageView orders_history_iv, profile;
    private CardView uploadBtn,search_cv;
    private String sellerUid;
    private int orderCount;
    private float totalEarning;
    private RecyclerView productRecyclerView;
    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        orders_history_iv = findViewById(R.id.orders_history_iv);
        profile = findViewById(R.id.seller_profile_image_view);
        order_count_tv = findViewById(R.id.order_count_tv);
        total_earning_tv = findViewById(R.id.total_earning_tv);
        search_cv = findViewById(R.id.search_cv);
        uploadBtn = findViewById(R.id.upload_btn);
        firebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        sellerUid = firebaseAuth.getUid();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();
        setCurrentUserImage();
        getSellerOrderCount();
        getSellerTotalEarning();
        orders_history_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), orders.class));
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), profile.class));
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), upload.class));
            }
        });
        search_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), search.class));
            }
        });
    }



    private void getSellerTotalEarning() {

        mDb.collection("seller").document(sellerUid).collection("orders")
                .whereEqualTo("status", "delivered")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    totalEarning = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        totalEarning = totalEarning +(Float.parseFloat((String) document.get("price")) * Float.parseFloat((String) document.get("productQuantity")));
                    }
                    total_earning_tv.setText(String.valueOf(totalEarning)+" Rs");
                }
            }
        });
    }

    private void getSellerOrderCount() {

        mDb.collection("seller").document(sellerUid).collection("orders")
                .whereEqualTo("status", "on the way")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    orderCount = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        orderCount++;
                    }
                    order_count_tv.setText(String.valueOf(orderCount));
                }
            }
        });
    }

    //best user current location code ever
    private void getCurrentLocation() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    Location location = task.getResult();
                    if (location != null) {
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        Map<String, Object> updateUserLocation = new HashMap<>();
                        updateUserLocation.put("geo_point", geoPoint);
                        mDb.collection("seller").document(firebaseAuth.getUid()).update(updateUserLocation);
                    }
                }
            });
        }
    }

    private void setCurrentUserImage() {
        final String uid = firebaseAuth.getUid();
        StorageReference ref = mStorageRef.child("images/" + uid).child("profilepic.jpg");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageView imageView;
                imageView = findViewById(R.id.seller_profile_image_view);
                Glide.with(getApplicationContext()).load(uri).into(imageView);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ArrayList<productModelList> productModelLists = getAllProducts();
        setCurrentUserImage();
        getSellerOrderCount();
        getSellerTotalEarning();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                productRecyclerView = findViewById(R.id.product_list_recycler_view);
                productAdapter productAdapter = new productAdapter(getApplicationContext(), productModelLists);
                productRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                productRecyclerView.setAdapter(productAdapter);
            }
        }, 3000);
    }

    private ArrayList<productModelList> getAllProducts() {
        String currentUserUid = firebaseAuth.getUid();
        final ArrayList<productModelList> productModelLists = new ArrayList<>();
        mDb.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //it performs a for loop to get each seperate user details and location
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        productModelList productModelList = new productModelList();
                        productModelList.setName((String) document.get("name"));
                        productModelList.setSeller((String) document.get("seller"));
                        productModelList.setImageUrl((String) document.get("imageUrl"));
                        productModelList.setPrice((String) document.get("price"));
                        productModelList.setDiscount((String) document.get("discount"));
                        productModelList.setMrp((String) document.get("mrp"));
                        productModelList.setCategory((String) document.get("category"));
                        productModelList.setProductId((String) document.get("productId"));
                        productModelList.setDescription((String) document.get("description"));
                        productModelLists.add(productModelList);
                    }
                }
            }
        });
        return productModelLists;
    }

}