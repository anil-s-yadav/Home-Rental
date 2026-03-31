package com.legendarysoftwares.homerental;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import android.content.Context;
import android.widget.Toast;

public class SaveAdapter extends FirebaseRecyclerAdapter<PostPropertyModel, SaveAdapter.myViewHolder> {
    private Context context;
    private String currentUserId;

    public SaveAdapter(@NonNull FirebaseRecyclerOptions<PostPropertyModel> options, Context context, String currentUserId) {
        super(options);
        this.context = context;
        this.currentUserId = currentUserId;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull PostPropertyModel model) {
        holder.savePostName.setText(model.getPostTitle());
        holder.savePostAdd.setText(model.getPostAddress());
        holder.savePostOwner.setText(String.format("By, %s", model.getOwnerName()));
        holder.savePostPrice.setText(model.getPostPrice());

        // In SavedPosts, the image field is "postImageUrl"
        String imageUrl = model.getPostImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_logo_transparent).into(holder.savePostImg);
        } else {
            holder.savePostImg.setImageResource(R.drawable.ic_logo_transparent);
        }

        holder.savePostDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference savedReference = FirebaseDatabase.getInstance().getReference("SavedPosts")
                        .child(currentUserId).child(model.getPropertyId());

                savedReference.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Property removed from saved posts", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("RemoveProperty", "Failed to remove property: " + e.getMessage());
                                Toast.makeText(context, "Failed to remove property", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_saved_item_rv, parent, false);
        return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        private TextView savePostName, savePostAdd, savePostPrice, savePostOwner;
        private ImageView savePostDelete;
        private ShapeableImageView savePostImg;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            savePostName = itemView.findViewById(R.id.save_post_name);
            savePostAdd = itemView.findViewById(R.id.save_post_add);
            savePostOwner = itemView.findViewById(R.id.save_post_owner);
            savePostPrice = itemView.findViewById(R.id.text_view_property_price);
            savePostImg = itemView.findViewById(R.id.property_image);
            savePostDelete = itemView.findViewById(R.id.save_post_delete);
        }
    }
}