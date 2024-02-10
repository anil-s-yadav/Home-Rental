package com.legendarysoftwares.homerental;

import android.content.Intent;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.legendarysoftwares.homerental.fragments.Add;
import com.squareup.picasso.Picasso;
import android.content.Context; // Import the Context class

import java.util.HashMap;
import java.util.Map;


public class HomeAdapter extends FirebaseRecyclerAdapter<PostPropertyModel, HomeAdapter.myViewHolder> {
    private Context context;
    private String currentUserId;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HomeAdapter(@NonNull FirebaseRecyclerOptions<PostPropertyModel> options,Context context, String currentUserId) {
        super(options);
        this.context = context; // Initialize the context
        this.currentUserId=currentUserId;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull PostPropertyModel model) {

        holder.postTitle.setText(model.getPostTitle());
        holder.postAddress.setText(model.getPostAddress());
       holder.postPrice.setText(model.getPostPrice());
       holder.postSellerName.setText(model.getOwnerName());
        Picasso.get().load(model.getPostImageUrl()).into(holder.postImage);
        Picasso.get().load(model.getOwnerPhoto()).into(holder.postSellerDp);


        LoginBottomSheetHelper loginBottomSheetHelper = new LoginBottomSheetHelper(context);
        holder.postSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loginBottomSheetHelper.isLoggedIn()) {
                    loginBottomSheetHelper.showLoginBottomSheet();
                } else {
                    savePostToSaveFragment(model.getPropertyId(), currentUserId, model.getPostTitle(), model.getPostAddress(),
                            model.getPostPrice(), model.getOwnerId(), model.getPostImageUrl(),model.getOwnerName());
                    holder.postSave.setImageResource(R.drawable.heart_fill);
                }
            }
        });
        holder.postChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loginBottomSheetHelper.isLoggedIn()) {
                    loginBottomSheetHelper.showLoginBottomSheet();
                } else {
                    Intent intent=new Intent(context, MassagesActivity.class);
                    context.startActivity(intent);
                }
            }
        });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_post_home_rv,parent,false);
        return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        //CardView postCard;
        ImageView postSellerDp,postShare,postSave,postChat,postImage;
        TextView postTitle, postAddress,postPrice,postCarpetArea,postRentOrSell,
                postStatus,postSellerName,postSellerType;  //postStatus = furnished or not
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

           // postCard = itemView.findViewById(R .id.postCardView);

            postImage = itemView.findViewById(R .id.post_image_from_database);
            postSellerDp = itemView.findViewById(R .id.post_seller_dp);
            postShare = itemView.findViewById(R .id.post_share);
            postSave = itemView.findViewById(R .id.post_save);
            postChat = itemView.findViewById(R .id.post_chat);

            postTitle = itemView.findViewById(R .id.post_title);
            postAddress = itemView.findViewById(R .id.post_address);
            postPrice = itemView.findViewById(R .id.post_price);
            postCarpetArea = itemView.findViewById(R .id.post_carpet_area);
            postRentOrSell = itemView.findViewById(R .id.post_sell_rent);
            postStatus = itemView.findViewById(R .id.post_status);
            postSellerName = itemView.findViewById(R .id.post_seller_name);
            postSellerType = itemView.findViewById(R .id.post_seller_type);

        }
    }


    /*private void savePostToSaveFragment(String postId, String userId, String savedPropertyId) {
        // Implement the logic to save the post to the Save fragment using postId, userId, and savedPropertyId
        DatabaseReference savedPostsReference = FirebaseDatabase.getInstance().getReference("SavedPosts").child(userId);
        // Create a new node with the desired key (newPropertyId)
        savedPostsReference.child(savedPropertyId).setValue(postId);
    }*/

    private void savePostToSaveFragment(String postId, String userId, String postTitle,
                                        String postAddress, String postPrice, String ownerId,
                                        String postImageUrl,String ownerName) {
        // Implement the logic to save the post to the Save fragment using postId and userId
        DatabaseReference saveReference = FirebaseDatabase.getInstance().getReference("SavedPosts")
                .child(userId)
                .child(postId);

        // Set the necessary data for the saved post
        Map<String, Object> savedPostData = new HashMap<>();
        savedPostData.put("propertyId", postId);
        savedPostData.put("postTitle", postTitle);
        savedPostData.put("postAddress", postAddress);
        savedPostData.put("postPrice", postPrice);
        savedPostData.put("ownerId", ownerId);
        savedPostData.put("postImageUrl", postImageUrl);
        savedPostData.put("ownerName", ownerName);

        saveReference.setValue(savedPostData);
    }

}
