package com.legendarysoftwares.homerental;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchAdapter extends FirebaseRecyclerAdapter<PostPropertyModel, SearchAdapter.myViewHolder> {
    private Context context;
    private String currentUserId;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public SearchAdapter(@NonNull FirebaseRecyclerOptions<PostPropertyModel> options, Context context, String currentUserId) {
        super(options);
        this.context = context;
        this.currentUserId = currentUserId;
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchAdapter.myViewHolder holder, int position, @NonNull PostPropertyModel model) {

        holder.PostName.setText(model.getPostTitle());
        holder.PostAdd.setText(model.getPostAddress());
        holder.PostOwner.setText(String.format("By, %s", model.getOwnerName()));
        holder.PostPrice.setText(model.getPostPrice());

        holder.PostImg.setOnClickListener(View ->{
            Intent SearchAdapterintent =new Intent(context, propertyDetailsActivity.class);
            SearchAdapterintent.putExtra("PropertyId",model.getPropertyId());
            SearchAdapterintent.putExtra("ownerId",model.getOwnerId());
            context.startActivity(SearchAdapterintent);
        });

        Log.e("Picasso", "Error loading image: " + model.getPostImageUrl2());

        Picasso.get().load(model.getPostImageUrl2()).into(holder.PostImg);

        holder.PostDelete.setVisibility(View.GONE);

    }

    @NonNull
    @Override
    public SearchAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_saved_item_rv, parent, false);
        return new SearchAdapter.myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        private TextView PostName, PostAdd, PostPrice, PostOwner;
        private ShapeableImageView PostImg;
        private  ImageView PostDelete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize ViewHolder views
            PostName = itemView.findViewById(R.id.save_post_name);
            PostAdd = itemView.findViewById(R.id.save_post_add);
            PostOwner = itemView.findViewById(R.id.save_post_owner);
            PostPrice = itemView.findViewById(R.id.text_view_property_price);

            PostImg = itemView.findViewById(R.id.property_image);

            PostDelete = itemView.findViewById(R.id.save_post_delete);


        }
    }


}
