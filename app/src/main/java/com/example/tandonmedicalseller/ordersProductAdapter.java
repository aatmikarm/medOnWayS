package com.example.tandonmedicalseller;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ordersProductAdapter extends RecyclerView.Adapter<ordersProductAdapter.ItemViewHolder> {
    private Context context;
    private ArrayList<productModelList> productModelList;
    private com.example.tandonmedicalseller.ordersProductInterface ordersProductInterface;

    public ordersProductAdapter(Context context, ArrayList<productModelList> productModelList, com.example.tandonmedicalseller.ordersProductInterface ordersProductInterface) {

        this.context = context;
        this.productModelList = productModelList;
        this.ordersProductInterface = ordersProductInterface;

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_rv_product, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        Glide.with(context).load(productModelList.get(position).getImageUrl()).into(holder.productImage);
        holder.productName.setText(productModelList.get(position).name);
        holder.order_status_tv.setText(productModelList.get(position).status);
        holder.productPrice.setText("Rs. " + productModelList.get(position).price + ".00");
        holder.productMrp.setText("Rs. " + productModelList.get(position).mrp + ".00");
        holder.productDiscount.setText(productModelList.get(position).discount + "% OFF");
        holder.productMrp.setPaintFlags(holder.productMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

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
        TextView order_status_tv;
        ConstraintLayout productContainer;
        CardView order_status_cv;

        ItemViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.orders_product_name);
            productImage = itemView.findViewById(R.id.orders_product_image);
            productPrice = itemView.findViewById(R.id.orders_product_discounted_price);
            productMrp = itemView.findViewById(R.id.orders_product_mrp);
            productContainer = itemView.findViewById(R.id.orders_product_container);
            productDiscount = itemView.findViewById(R.id.orders_discount_text_view);
            order_status_cv = itemView.findViewById(R.id.order_status_cv);
            order_status_tv = itemView.findViewById(R.id.order_status_tv);
            order_status_cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ordersProductInterface.ordersProductOnClickInterface(getAdapterPosition());
                }
            });
        }
    }
}
