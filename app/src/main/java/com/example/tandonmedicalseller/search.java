package com.example.tandonmedicalseller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class search extends AppCompatActivity implements searchProductInterface {

    private EditText search_et;
    private ImageView search_back_iv, search_filter_iv;
    private FirebaseFirestore mDb;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;
    private String currentUserUid;
    private ProgressBar search_progress_bar;
    private RecyclerView searchProductRV;
    private searchProductAdapter searchProductAdapter;
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
        search_filter_iv = findViewById(R.id.search_filter_iv);
        searchProductRV = findViewById(R.id.search_rv);

        searchProductRV.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        productModelLists = getAllProducts();
        searchProductAdapter = new searchProductAdapter(getApplicationContext(), productModelLists, search.this);
        searchProductRV.setAdapter(searchProductAdapter);

//        search_et.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String search = s.toString();
//                String[] tags = search.split(" ");
//                if (search.isEmpty()) {
//                    productModelLists.clear();
//                    searchProductAdapter.notifyDataSetChanged();
//                } else {
//                    for (String tag : tags) {
//                        searchProduct(tag);
//                    }
//                }
//            }
//        });


        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });


        search_back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        search_filter_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(search.this, filter.class);
                startActivityForResult(intent, 101);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 101) {
            search_et.setText(data.getStringExtra("data"));
        }
    }

    public void startAsyncTask(View v) {
        ExampleAsyncTask task = new ExampleAsyncTask(this);
        task.execute(10);
    }

    private static class ExampleAsyncTask extends AsyncTask<Integer, Integer, String> {
        private WeakReference<search> activityWeakReference;

        ExampleAsyncTask(search activity) {
            activityWeakReference = new WeakReference<search>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            search activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            activity.search_progress_bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            for (int i = 0; i < integers[0]; i++) {
                publishProgress((i * 100) / integers[0]);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return "Finished!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            search activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            activity.search_progress_bar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            search activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
            activity.search_progress_bar.setProgress(0);
            activity.search_progress_bar.setVisibility(View.INVISIBLE);
        }
    }

    private ArrayList<productModelList> getAllProducts() {
        final ArrayList<productModelList> productModelLists = new ArrayList<>();
        mDb.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        productModelList productModelList = new productModelList();
                        productModelList.setName((String) document.get("name"));
                        productModelList.setImageUrl((String) document.get("imageUrl"));
                        productModelList.setPrice((String) document.get("price"));
                        productModelList.setDiscount((String) document.get("discount"));
                        productModelList.setMrp((String) document.get("mrp"));
                        productModelList.setCategory((String) document.get("category"));
                        productModelList.setSellerId((String) document.get("sellerId"));
                        productModelList.setSeller((String) document.get("seller"));
                        productModelList.setProductId((String) document.get("productId"));
                        productModelList.setDescription((String) document.get("description"));
                        productModelList.setPrescription((Boolean) document.get("prescription"));
                        productModelList.setRating((String) document.get("rating"));
                        productModelList.setReview((String) document.get("review"));
                        productModelList.setSellerToken((String) document.get("sellerToken"));
                        productModelLists.add(productModelList);
                    }
                }
            }
        });
        return productModelLists;
    }

    private void filter(String text) {
        ArrayList<productModelList> filteredList = new ArrayList<>();

        for (productModelList item : productModelLists) {
            if (item.name.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        searchProductAdapter.filterList(filteredList);
    }

//    private void searchProduct(String tag) {
//        if (!tag.isEmpty()) {
//            // this search will work for exact words only
//            //.orderBy("tags").startAt(tag).endAt(tag + "\uf8ff")
//            tag = tag.toLowerCase();
//            mDb.collection("products").whereArrayContains("tags", tag).get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful() && task.getResult() != null) {
//                                productModelLists.clear();
//                                searchProductAdapter.notifyDataSetChanged();
//
//                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
//                                    productModelList productModelList = new productModelList();
//                                    productModelList.setName((String) doc.get("name"));
//                                    productModelList.setImageUrl((String) doc.get("imageUrl"));
//                                    productModelList.setPrice((String) doc.get("price"));
//                                    productModelList.setDiscount((String) doc.get("discount"));
//                                    productModelList.setMrp((String) doc.get("mrp"));
//                                    productModelList.setCategory((String) doc.get("category"));
//                                    productModelList.setSellerId((String) doc.get("sellerId"));
//                                    productModelList.setSeller((String) doc.get("seller"));
//                                    productModelList.setProductId((String) doc.get("productId"));
//                                    productModelList.setDescription((String) doc.get("description"));
//                                    productModelLists.add(productModelList);
//                                    searchProductAdapter.notifyDataSetChanged();
//                                }
//                            } else {
//                                String error = task.getException().getMessage();
//                                Toast.makeText(search.this, error, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        }
//    }

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
        intent.putExtra("prescription", productModelLists.get(position).getPrescription());
        intent.putExtra("discount", productModelLists.get(position).getDiscount());
        intent.putExtra("imageUrl", productModelLists.get(position).getImageUrl());
        intent.putExtra("category", productModelLists.get(position).getCategory());
        startActivity(intent);
    }

}