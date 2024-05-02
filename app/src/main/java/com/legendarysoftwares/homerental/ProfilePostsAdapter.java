package com.legendarysoftwares.homerental;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.legendarysoftwares.homerental.fragments.Add;
import com.squareup.picasso.Picasso;
import android.content.Context; // Import the Context class
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class ProfilePostsAdapter extends FirebaseRecyclerAdapter<PostPropertyModel, ProfilePostsAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ProfilePostsAdapter(@NonNull FirebaseRecyclerOptions<PostPropertyModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull PostPropertyModel model) {

        Picasso.get().load(model.getPostImageUrl1()).into(holder.imageView);

        holder.imageView.setOnClickListener((v) -> {
            // Initializing the popup menu and giving the reference as current context
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v); // Provide the 'v' parameter here
            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(R.menu.post_on_profile_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.view_property) {
                        Intent ProfileIntent=new Intent(v.getContext(), propertyDetailsActivity.class);
                        ProfileIntent.putExtra("propertyID",model.getPropertyId());
                        ProfileIntent.putExtra("ownerId",model.getOwnerId());
                        v.getContext().startActivity(ProfileIntent);
                        return true;
                    } else if (id == R.id.delete_property) {

                        DatabaseReference propertyRef = FirebaseDatabase.getInstance().getReference()
                                .child("Posted Properties").child(model.getPropertyId());
                        propertyRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(v.getContext(), "Property deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(v.getContext(), "Failed to delete property: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        return true;
                    }
                    return false;
                }
            });
            popupMenu.show();
        });



    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_post_for_profile,parent,false);
        return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);

        }

    }

}
