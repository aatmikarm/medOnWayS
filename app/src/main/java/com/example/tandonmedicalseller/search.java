package com.example.tandonmedicalseller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class search extends AppCompatActivity implements searchProductInterface {

    private EditText search_et;
    private ImageView search_back_iv;
    private RecyclerView search_rv;
    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;
    private String currentUserUid;
    private RecyclerView searchProductRV;
    private com.example.tandonmedicalseller.searchProductAdapter searchProductAdapter;
    private ArrayList<productModelList> productModelLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        firebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        currentUserUid = firebaseAuth.getUid();
        search_et = findViewById(R.id.search_et);
        search_back_iv = findViewById(R.id.search_back_iv);
        search_back_iv = findViewById(R.id.search_back_iv);

        searchProductRV = findViewById(R.id.search_rv);

        searchProductRV.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        productModelLists = new ArrayList<>();
        searchProductAdapter = new searchProductAdapter(getApplicationContext(), productModelLists, search.this);
        searchProductRV.setAdapter(searchProductAdapter);

        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();
                String[] tags = search.split(" ");
                if (search.isEmpty()) {
                    productModelLists.clear();
                    searchProductAdapter.notifyDataSetChanged();
                } else {
                    for (String tag : tags) {
                        searchProduct(tag);
                    }
                }
            }
        });
        search_back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void searchProduct(String tag) {
        if (!tag.isEmpty()) {
            // this search will work for exact words only
            //.orderBy("tags").startAt(tag).endAt(tag + "\uf8ff")
            tag = tag.toLowerCase();
            mDb.collection("products").whereArrayContains("tags", tag).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                productModelLists.clear();
                                searchProductAdapter.notifyDataSetChanged();

                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                    productModelList productModelList = new productModelList();
                                    productModelList.setName((String) doc.get("name"));
                                    productModelList.setImageUrl((String) doc.get("imageUrl"));
                                    productModelList.setPrice((String) doc.get("price"));
                                    productModelList.setDiscount((String) doc.get("discount"));
                                    productModelList.setMrp((String) doc.get("mrp"));
                                    productModelList.setCategory((String) doc.get("category"));
                                    productModelList.setSellerId((String) doc.get("sellerId"));
                                    productModelList.setSeller((String) doc.get("seller"));
                                    productModelList.setProductId((String) doc.get("productId"));
                                    productModelList.setDescription((String) doc.get("description"));
                                    productModelLists.add(productModelList);
                                    searchProductAdapter.notifyDataSetChanged();
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(search.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void searchProductOnClickInterface(int position) {
        Intent intent = new Intent(getApplicationContext(), editProductDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("sellerId", productModelLists.get(position).getSellerId());
        intent.putExtra("productId", productModelLists.get(position).getProductId());
        intent.putExtra("seller", productModelLists.get(position).getSeller());
        intent.putExtra("mrp", productModelLists.get(position).getMrp());
        intent.putExtra("name", productModelLists.get(position).getName());
        intent.putExtra("price", productModelLists.get(position).getPrice());
        intent.putExtra("description", productModelLists.get(position).getDescription());
        intent.putExtra("discount", productModelLists.get(position).getDiscount());
        intent.putExtra("imageUrl", productModelLists.get(position).getImageUrl());
        intent.putExtra("category", productModelLists.get(position).getCategory());
        startActivity(intent);
    }
}