package com.example.tandonmedicalseller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class productAdapter extends RecyclerView.Adapter<productAdapter.ItemViewHolder> {

    private Context context;
    private ArrayList<com.example.tandonmedicalseller.productModelList> productModelList;

    public productAdapter(Context context, ArrayList<com.example.tandonmedicalseller.productModelList> productModelList) {

        this.context = context;
        this.productModelList = productModelList;

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_product, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        Glide.with(context).load(productModelList.get(position).getImageUrl()).into(holder.productImage);
        holder.productName.setText(productModelList.get(position).name);
        holder.productPrice.setText("Rs. " + productModelList.get(position).price + ".00");
        holder.productMrp.setText("Rs. " + productModelList.get(position).mrp + ".00");
        holder.productDiscount.setText(productModelList.get(position).discount + "% OFF");
        holder.productMrp.setPaintFlags(holder.productMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        holder.productContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "url = "+proRecyclerViewListModels.get(position).getImageUrl(), Toast.LENGTH_LONG).show();
                //add(position , proRecyclerViewListModels.get(1));
                //remove(position);
                Intent intent = new Intent(context, editProductDetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("category", productModelList.get(position).getCategory());
                intent.putExtra("productId", productModelList.get(position).getProductId());
                intent.putExtra("seller", productModelList.get(position).getSeller());
                intent.putExtra("description", productModelList.get(position).getDescription());
                intent.putExtra("discount", productModelList.get(position).getDiscount());
                intent.putExtra("imageUrl", productModelList.get(position).getImageUrl());

                intent.putExtra("mrp", productModelList.get(position).getMrp());
                intent.putExtra("name", productModelList.get(position).getName());
                intent.putExtra("price", productModelList.get(position).getPrice());
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productMrp;
        TextView productDiscount;
        ConstraintLayout productContainer;

        ItemViewHolder(View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.rv_product_name);
            productImage = itemView.findViewById(R.id.rv_product_image);
            productPrice = itemView.findViewById(R.id.rv_product_discounted_price);
            productMrp = itemView.findViewById(R.id.rv_product_mrp);
            productContainer = itemView.findViewById(R.id.product_container);
            productDiscount = itemView.findViewById(R.id.discount_text_view);

        }
    }
}
