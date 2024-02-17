package com.legendarysoftwares.homerental;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.legendarysoftwares.homerental.fragments.Add;
import com.squareup.picasso.Picasso;
import android.content.Context; // Import the Context class
import android.widget.Toast;

public class SaveAdapter extends FirebaseRecyclerAdapter<PostPropertyModel, SaveAdapter.myViewHolder> {
    private Context context;
    private String currentUserId;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public SaveAdapter(@NonNull FirebaseRecyclerOptions<PostPropertyModel> options,Context context,String currentUserId) {
        super(options);
        this.context = context;
        this.currentUserId=currentUserId;

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull PostPropertyModel model) {

        holder.savePostName.setText(model.getPostTitle());
        holder.savePostAdd.setText(model.getPostAddress());
        holder.savePostOwner.setText("By, "+model.getOwnerName());
        holder.savePostPrice.setText(model.getPostPrice());
        Picasso.get().load(model.getPostImageUrl1()).into(holder.savePostImg);

        holder.savePostDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference savedReference = FirebaseDatabase.getInstance().getReference("SavedPosts")
                        .child(currentUserId).child(model.getPropertyId());

                savedReference.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Property removed successfully
                                Toast.makeText(context, "Property removed from saved posts", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to remove property
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
        private ImageView  savePostDelete;
        private ShapeableImageView savePostImg;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize ViewHolder views
            savePostName = itemView.findViewById(R.id.save_post_name);
            savePostAdd = itemView.findViewById(R.id.save_post_add);
            savePostOwner = itemView.findViewById(R.id.save_post_owner);
            savePostPrice = itemView.findViewById(R.id.save_post_price);

            savePostImg = itemView.findViewById(R.id.save_post_image);
            savePostDelete = itemView.findViewById(R.id.save_post_delete);
        }
    }


}



