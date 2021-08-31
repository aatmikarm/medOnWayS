package com.example.tandonmedicalseller;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class categoriesAdapterUpload extends RecyclerView.Adapter<categoriesAdapterUpload.ItemViewHolder> {

    private Context context;
    private ArrayList<categoriesModelList> categoriesModelLists;
    private categoryInterface categoryInterface;

    int selected_position = 0;

    public categoriesAdapterUpload(Context context, ArrayList<categoriesModelList> categoriesModelLists, com.example.tandonmedicalseller.categoryInterface categoryInterface) {
        this.context = context;
        this.categoriesModelLists = categoriesModelLists;
        this.categoryInterface = categoryInterface;
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


        // Here I am just highlighting the background
        holder.relativeLayout.setBackgroundColor(selected_position == position ? Color.argb(255, 237, 47, 101) : Color.TRANSPARENT);




    }

    @Override
    public int getItemCount() {
        return categoriesModelLists.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView categoriesImage;
        TextView categoriesName;
        RelativeLayout relativeLayout;

        ItemViewHolder(View itemView) {
            super(itemView);

            categoriesName = itemView.findViewById(R.id.rv_categories_name);
            categoriesImage = itemView.findViewById(R.id.rv_categories_image);
            relativeLayout = itemView.findViewById(R.id.main_profile_image_View_container_rl);


            categoriesImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryInterface.categoryOnClickInterface(getAdapterPosition());

                    if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

                    // Updating old as well as new positions
                    notifyItemChanged(selected_position);
                    selected_position = getAdapterPosition();
                    notifyItemChanged(selected_position);

                }
            });
        }
    }


}
