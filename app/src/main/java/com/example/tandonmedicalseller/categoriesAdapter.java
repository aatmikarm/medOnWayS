package com.example.tandonmedicalseller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class categoriesAdapter extends RecyclerView.Adapter<categoriesAdapter.ItemViewHolder> {

    private Context context;
    private ArrayList<categoriesModelList> categoriesModelLists;

    public categoriesAdapter(Context context , ArrayList<categoriesModelList> categoriesModelLists){

        this.context = context;
        this.categoriesModelLists = categoriesModelLists;

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_recycler_view,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        Glide.with(context).load(categoriesModelLists.get(position).getImageUrl()).into(holder.categoriesImage);
        holder.categoriesName.setText(categoriesModelLists.get(position).name);
    }

    @Override
    public int getItemCount() {
        return categoriesModelLists.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView categoriesImage;
        TextView categoriesName;

        ItemViewHolder(View itemView) {
            super(itemView);

            categoriesName = itemView.findViewById(R.id.rv_categories_name);
            categoriesImage = itemView.findViewById(R.id.rv_categories_image);


        }
    }


}
