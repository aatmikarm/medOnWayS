package com.example.tandonmedicalseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class orders extends AppCompatActivity  implements ordersProductInterface {

    private RecyclerView ordersProductRecyclerView;
    private String currentUserUid;
    private ImageView orders_back_iv;
    private String sellerId;
    private String seller;

    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;
    private ArrayList<productModelList> productModelLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);


        firebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        currentUserUid = firebaseAuth.getUid();

        orders_back_iv = findViewById(R.id.orders_back_iv);

        productModelLists = getOrdersProducts();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ordersProductRecyclerView = findViewById(R.id.orders_list_recycler_view);
                ordersProductAdapter ordersProductAdapter = new ordersProductAdapter(getApplicationContext(), productModelLists, orders.this);
                ordersProductRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                ordersProductRecyclerView.setAdapter(ordersProductAdapter);

            }
        }, 3000);

        orders_back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        productModelLists = getOrdersProducts();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ordersProductRecyclerView = findViewById(R.id.orders_list_recycler_view);
                ordersProductAdapter ordersProductAdapter = new ordersProductAdapter(getApplicationContext(), productModelLists, orders.this);
                ordersProductRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                ordersProductRecyclerView.setAdapter(ordersProductAdapter);

            }
        }, 3000);
    }

    private ArrayList<productModelList> getOrdersProducts() {

        final ArrayList<productModelList> productModelLists = new ArrayList<>();

        mDb.collection("seller").document(currentUserUid).collection("orders")
                .whereEqualTo("status", "on the way")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //it performs a for loop to get each seperate user details and location
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        productModelList productModelList = new productModelList();

                        productModelList.setName((String) document.get("name"));
                        productModelList.setImageUrl((String) document.get("imageUrl"));
                        productModelList.setPrice((String) document.get("price"));
                        productModelList.setDiscount((String) document.get("discount"));
                        productModelList.setMrp((String) document.get("mrp"));
                        productModelList.setSellerId((String) document.get("sellerId"));
                        productModelList.setSeller((String) document.get("seller"));
                        productModelList.setCategory((String) document.get("category"));
                        productModelList.setProductId((String) document.get("productId"));
                        productModelList.setProductOrderId((String) document.get("productOrderId"));
                        productModelList.setDescription((String) document.get("description"));
                        productModelList.setStatus((String) document.get("status"));
                        productModelList.setUserId((String) document.get("userId"));
                        productModelList.setProductQuantity((String) document.get("productQuantity"));
                        productModelList.setProductOrderPlacedTime((String) document.get("productOrderPlacedTime"));

                        productModelLists.add(productModelList);

                    }

                }
            }
        });
        mDb.collection("seller").document(currentUserUid).collection("orders")
                .whereEqualTo("status", "delivered")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //it performs a for loop to get each seperate user details and location
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        productModelList productModelList = new productModelList();

                        productModelList.setName((String) document.get("name"));
                        productModelList.setImageUrl((String) document.get("imageUrl"));
                        productModelList.setPrice((String) document.get("price"));
                        productModelList.setDiscount((String) document.get("discount"));
                        productModelList.setMrp((String) document.get("mrp"));
                        productModelList.setSellerId((String) document.get("sellerId"));
                        productModelList.setSeller((String) document.get("seller"));
                        productModelList.setCategory((String) document.get("category"));
                        productModelList.setProductId((String) document.get("productId"));
                        productModelList.setProductOrderId((String) document.get("productOrderId"));
                        productModelList.setDescription((String) document.get("description"));
                        productModelList.setStatus((String) document.get("status"));
                        productModelList.setUserId((String) document.get("userId"));
                        productModelList.setProductQuantity((String) document.get("productQuantity"));
                        productModelList.setProductOrderPlacedTime((String) document.get("productOrderPlacedTime"));

                        productModelLists.add(productModelList);

                    }

                }
            }
        });
        return productModelLists;

    }

    @Override
    public void ordersProductOnClickInterface(int position) {
        Intent intent = new Intent(getApplicationContext(), productStatus.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("userId", productModelLists.get(position).getUserId());
        intent.putExtra("productId", productModelLists.get(position).getProductId());
        intent.putExtra("productOrderId", productModelLists.get(position).getProductOrderId());
        startActivity(intent);
    }
}