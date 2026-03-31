package com.legendarysoftwares.homerental;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class ProfilePostsAdapter extends FirebaseRecyclerAdapter<PostPropertyModel, ProfilePostsAdapter.myViewHolder> {

    public ProfilePostsAdapter(@NonNull FirebaseRecyclerOptions<PostPropertyModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull PostPropertyModel model) {
        Picasso.get().load(model.getPostImageUrl1()).into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), propertyDetailsActivity.class);
            intent.putExtra("propertyId", model.getPropertyId());
            intent.putExtra("ownerId", model.getOwnerId());
            v.getContext().startActivity(intent);
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_post_for_profile, parent, false);
        return new myViewHolder(view);
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}