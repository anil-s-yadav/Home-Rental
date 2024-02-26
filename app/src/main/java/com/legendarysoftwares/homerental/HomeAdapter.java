package com.legendarysoftwares.homerental;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import android.content.Context; // Import the Context class
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class HomeAdapter extends FirebaseRecyclerAdapter<PostPropertyModel, HomeAdapter.myViewHolder> {
    private Context context;
    private FirebaseUser user;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HomeAdapter(@NonNull FirebaseRecyclerOptions<PostPropertyModel> options,Context context, FirebaseUser user) {
        super(options);
        this.context = context; // Initialize the context
        this.user = user;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull PostPropertyModel model) {

        holder.postTitle.setText(model.getPostTitle());
        holder.postAddress.setText(model.getPostAddress());
       holder.postPrice.setText(String.format("₹ %s", model.getPostPrice()));
       holder.postSellerName.setText(model.getOwnerName());
        Picasso.get().load(model.getOwnerPhoto()).into(holder.postSellerDp);

        Picasso.get().load(model.getPostImageUrl1()).into(holder.postImage1);
        Picasso.get().load(model.getPostImageUrl2()).into(holder.postImage2);
        Picasso.get().load(model.getPostImageUrl3()).into(holder.postImage3);
        Picasso.get().load(model.getPostImageUrl4()).into(holder.postImage4);
        Picasso.get().load(model.getPostImageUrl5()).into(holder.postImage5);
        Picasso.get().load(model.getPostImageUrl6()).into(holder.postImage6);



        LoginBottomSheetHelper loginBottomSheetHelper = new LoginBottomSheetHelper(context);
        holder.postSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loginBottomSheetHelper.isLoggedIn()) {
                    loginBottomSheetHelper.showLoginBottomSheet();
                } else {
                    long timestamp = System.currentTimeMillis();
                    savePostToSaveFragment(timestamp, model.getPropertyId(), user, model.getPostTitle(), model.getPostAddress(),
                            model.getPostPrice(), model.getOwnerId(), model.getPostImageUrl1(),model.getOwnerName());
                    holder.postSave.setImageResource(R.drawable.heart_fill);
                }
            }
        });
        holder.postChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getUid().equals(model.getOwnerId())) {
                    Toast.makeText(context, "You can't send a massage to yourself!", Toast.LENGTH_SHORT).show();
                }else {
                    CustomRequestDialog(model); // Pass the PostPropertyModel to the dialog
                }
            }
        });

                            /*Intent chatIntent = new Intent(context, ChatScreen.class);
                    chatIntent.putExtra("sourceActivity", "HomeAdapter"); // to identify were user came from
                    chatIntent.putExtra("userName",user.getDisplayName().toString());
                    chatIntent.putExtra("propertyName",model.getPostTitle());
                    context.startActivity(chatIntent);*/

        holder.OpenPostDetails.setOnClickListener(v -> {
            Intent intent=new Intent(context, propertyDetailsActivity.class);
            context.startActivity(intent);
        });

    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_post_home_rv,parent,false);
        return new myViewHolder(view);
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        //CardView postCard;
        ShapeableImageView postSellerDp;
        ImageView postShare,postSave;
        private LinearLayout OpenPostDetails;
        private ShapeableImageView postImage1,postImage2,postImage3,postImage4,postImage5, postImage6;
        TextView postTitle, postAddress,postPrice,postCarpetArea,postRentOrSell,postChat,
                postStatus,postSellerName,postSellerType;  //postStatus = furnished or not
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

           // postCard = itemView.findViewById(R .id.postCardView);

            postImage1 = itemView.findViewById(R .id.post_image_from_database1);
            postImage2 = itemView.findViewById(R .id.post_image_from_database2);
            postImage3= itemView.findViewById(R .id.post_image_from_database3);
            postImage4 = itemView.findViewById(R .id.post_image_from_database4);
            postImage5 = itemView.findViewById(R .id.post_image_from_database5);
            postImage6 = itemView.findViewById(R .id.post_image_from_database6);

            postSellerDp = itemView.findViewById(R .id.post_seller_dp);
            postShare = itemView.findViewById(R .id.post_share);
            postSave = itemView.findViewById(R .id.post_save);
            postChat = itemView.findViewById(R .id.post_chat);
            OpenPostDetails = itemView.findViewById(R.id.linearLayout_openPropertyDetails);

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

    private void savePostToSaveFragment(long timestamp, String postId, FirebaseUser userId, String postTitle,
                                        String postAddress, String postPrice, String ownerId,
                                        String postImageUrl, String ownerName) {
        // Implement the logic to save the post to the Save fragment using postId and userId
        DatabaseReference saveReference = FirebaseDatabase.getInstance().getReference("SavedPosts")
                .child(user.getUid())
                .child(postId);

        // Set the necessary data for the saved post
        Map<String, Object> savedPostData = new HashMap<>();
        savedPostData.put("timestamp", timestamp);
        savedPostData.put("propertyId", postId);
        savedPostData.put("postTitle", postTitle);
        savedPostData.put("postAddress", postAddress);
        savedPostData.put("postPrice", postPrice);
        savedPostData.put("ownerId", ownerId);
        savedPostData.put("postImageUrl", postImageUrl);
        savedPostData.put("ownerName", ownerName);

        saveReference.setValue(savedPostData);
    }

    private void CustomRequestDialog(PostPropertyModel model) {

        View alertCustomDialog = LayoutInflater.from(context).inflate(R.layout.dialog_request_for_rent,null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setView(alertCustomDialog);
        EditText requestMassage = alertCustomDialog.findViewById(R.id.request_massage);
        Button cancelButton = (Button) alertCustomDialog.findViewById(R.id.cancel_btn);
        Button sendButton = (Button) alertCustomDialog.findViewById(R.id.send_btn);
        ShapeableImageView propertyPreview1 = alertCustomDialog.findViewById(R.id.dialog_img_preview1);
        ShapeableImageView propertyPreview2 = alertCustomDialog.findViewById(R.id.dialog_img_preview2);
        ShapeableImageView propertyPreview3 = alertCustomDialog.findViewById(R.id.dialog_img_preview3);
        ShapeableImageView propertyPreview4 = alertCustomDialog.findViewById(R.id.dialog_img_preview4);
        ShapeableImageView propertyPreview5 = alertCustomDialog.findViewById(R.id.dialog_img_preview5);
        ShapeableImageView propertyPreview6 = alertCustomDialog.findViewById(R.id.dialog_img_preview6);
        Picasso.get().load(model.getPostImageUrl1()).into(propertyPreview1);
        Picasso.get().load(model.getPostImageUrl2()).into(propertyPreview2);
        Picasso.get().load(model.getPostImageUrl3()).into(propertyPreview3);
        Picasso.get().load(model.getPostImageUrl4()).into(propertyPreview4);
        Picasso.get().load(model.getPostImageUrl5()).into(propertyPreview5);
        Picasso.get().load(model.getPostImageUrl6()).into(propertyPreview6);

        final  AlertDialog dialog = alertDialog.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        cancelButton.setOnClickListener(v -> {dialog.cancel(); });

        sendButton.setOnClickListener(v -> {
            String massage = requestMassage.getText().toString();
            if (!massage.isEmpty()) {
                sendRequest(massage, model);
                dialog.cancel();
            } else {
                // Handle case where massage is empty
                Toast.makeText(context, "Please enter a massage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRequest(String requestMassage, PostPropertyModel model) {
        LoginBottomSheetHelper loginBottomSheetHelper = new LoginBottomSheetHelper(context);

        if (!loginBottomSheetHelper.isLoggedIn()) {
            loginBottomSheetHelper.showLoginBottomSheet();
        } else {
            // Save owner in user's chat Activity
            DatabaseReference userMassageRef = FirebaseDatabase.getInstance().getReference("Massage Requests Activity")
                    .child("Send").child(user.getUid());

            Map<String, Object> userMassageData = new HashMap<>();
            userMassageData.put("userID", model.getOwnerId());
            userMassageData.put("PropertyID", model.getPropertyId());

            userMassageRef.child(model.getOwnerId()).setValue(userMassageData);

            // Save user in owner's chat Activity
            DatabaseReference ownerMassageRef = FirebaseDatabase.getInstance().getReference("Massage Requests Activity")
                    .child("Receive").child(model.getOwnerId());

            Map<String, Object> ownerMassageData = new HashMap<>();
            ownerMassageData.put("userID", user.getUid());
            ownerMassageData.put("PropertyID", model.getPropertyId());

            ownerMassageRef.child(user.getUid()).setValue(ownerMassageData);

            Intent massagesIntent = new Intent(context, MassagesActivity.class);
            massagesIntent.putExtra("requestMassage", requestMassage);
            context.startActivity(massagesIntent);
        }
    }



}

