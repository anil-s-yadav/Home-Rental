package com.legendarysoftwares.homerental;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class HomeAdapter extends FirebaseRecyclerAdapter<PostPropertyModel, HomeAdapter.myViewHolder> {
    private static final String TAG = "HomeAdapterDebug";
    private Context context;
    private FirebaseUser user;

    public HomeAdapter(@NonNull FirebaseRecyclerOptions<PostPropertyModel> options, Context context, FirebaseUser user) {
        super(options);
        this.context = context;
        this.user = user;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull PostPropertyModel model) {
        Log.d(TAG, "Binding item at position: " + position);
        
        if (model == null) {
            Log.e(TAG, "Model is null at position: " + position);
            return;
        }

        try {
            // Log every field to see where it breaks
            Log.d(TAG, "Property Title: " + model.getPostTitle());
            holder.postTitle.setText(model.getPostTitle() != null ? model.getPostTitle() : "No Title");

            Log.d(TAG, "Property Address: " + model.getPostAddress());
            holder.postAddress.setText(model.getPostAddress() != null ? model.getPostAddress() : "No Address");

            Log.d(TAG, "Property Price: " + model.getPostPrice());
            holder.postPrice.setText(String.format("₹ %s", model.getPostPrice() != null ? model.getPostPrice() : "0"));

            Log.d(TAG, "Owner Name: " + model.getOwnerName());
            holder.postSellerName.setText(model.getOwnerName() != null ? model.getOwnerName() : "Unknown");

            // Safe Image Loading for Owner
            if (model.getOwnerPhoto() != null && !model.getOwnerPhoto().isEmpty()) {
                Log.d(TAG, "Loading Owner Photo: " + model.getOwnerPhoto());
                Picasso.get().load(model.getOwnerPhoto()).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(holder.postSellerDp);
            } else {
                holder.postSellerDp.setImageResource(R.drawable.ic_profile);
            }

            // Safe Image Loading for Property
            loadPropertyImageWithLog(model.getPostImageUrl1(), holder.postImage1, "Image 1");
            loadPropertyImageWithLog(model.getPostImageUrl2(), holder.postImage2, "Image 2");
            loadPropertyImageWithLog(model.getPostImageUrl3(), holder.postImage3, "Image 3");
            loadPropertyImageWithLog(model.getPostImageUrl4(), holder.postImage4, "Image 4");
            loadPropertyImageWithLog(model.getPostImageUrl5(), holder.postImage5, "Image 5");
            loadPropertyImageWithLog(model.getPostImageUrl6(), holder.postImage6, "Image 6");

        } catch (Exception e) {
            Log.e(TAG, "CRITICAL ERROR in onBindViewHolder at position " + position, e);
        }

        LoginBottomSheetHelper loginBottomSheetHelper = new LoginBottomSheetHelper(context);
        
        holder.postSave.setOnClickListener(v -> {
            if (!loginBottomSheetHelper.isLoggedIn()) {
                loginBottomSheetHelper.showLoginBottomSheet();
            } else {
                savePostToSaveFragment(model);
                holder.postSave.setImageResource(R.drawable.heart_fill);
            }
        });

        holder.postChat.setOnClickListener(v -> {
            if (user != null && model.getOwnerId() != null && user.getUid().equals(model.getOwnerId())) {
                Toast.makeText(context, "You can't send a message to yourself!", Toast.LENGTH_SHORT).show();
            } else {
                CustomRequestDialog(model);
            }
        });

        if (holder.OpenPostDetails != null) {
            holder.OpenPostDetails.setOnClickListener(v -> {
                Context vContext = v.getContext();
                Intent intent = new Intent(vContext, propertyDetailsActivity.class);
                intent.putExtra("propertyId", model.getPropertyId());
                intent.putExtra("ownerId", model.getOwnerId());
                vContext.startActivity(intent);
            });
        }
    }

    private void loadPropertyImageWithLog(String url, ImageView iv, String label) {
        if (iv == null) return;
        if (url != null && !url.isEmpty()) {
            Log.d(TAG, "Loading " + label + ": " + url);
            Picasso.get().load(url).placeholder(R.drawable.ic_logo_transparent).error(R.drawable.ic_logo_transparent).fit().centerCrop().into(iv);
        } else {
            iv.setImageResource(R.drawable.ic_logo_transparent);
        }
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_post_home_rv, parent, false);
        return new myViewHolder(view);
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView postSellerDp;
        ImageView postSave;
        LinearLayout OpenPostDetails;
        ShapeableImageView postImage1, postImage2, postImage3, postImage4, postImage5, postImage6;
        TextView postTitle, postAddress, postPrice, postChat, postSellerName;
        
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage1 = itemView.findViewById(R.id.post_image_from_database1);
            postImage2 = itemView.findViewById(R.id.post_image_from_database2);
            postImage3 = itemView.findViewById(R.id.post_image_from_database3);
            postImage4 = itemView.findViewById(R.id.post_image_from_database4);
            postImage5 = itemView.findViewById(R.id.post_image_from_database5);
            postImage6 = itemView.findViewById(R.id.post_image_from_database6);
            postSellerDp = itemView.findViewById(R.id.post_seller_dp);
            postSave = itemView.findViewById(R.id.post_save);
            postChat = itemView.findViewById(R.id.post_chat);
            OpenPostDetails = itemView.findViewById(R.id.linearLayout_openPropertyDetails);
            postTitle = itemView.findViewById(R.id.post_title);
            postAddress = itemView.findViewById(R.id.post_address);
            postPrice = itemView.findViewById(R.id.post_price);
            postSellerName = itemView.findViewById(R.id.post_seller_name);
        }
    }

    private void savePostToSaveFragment(PostPropertyModel model) {
        if (user == null || model.getPropertyId() == null) return;
        DatabaseReference saveReference = FirebaseDatabase.getInstance().getReference("SavedPosts")
                .child(user.getUid())
                .child(model.getPropertyId());

        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", System.currentTimeMillis());
        data.put("propertyId", model.getPropertyId());
        data.put("postTitle", model.getPostTitle() != null ? model.getPostTitle() : "");
        data.put("postAddress", model.getPostAddress() != null ? model.getPostAddress() : "");
        data.put("postPrice", model.getPostPrice() != null ? model.getPostPrice() : "");
        data.put("ownerId", model.getOwnerId() != null ? model.getOwnerId() : "");
        data.put("postImageUrl", model.getPostImageUrl1() != null ? model.getPostImageUrl1() : "");
        data.put("ownerName", model.getOwnerName() != null ? model.getOwnerName() : "");

        saveReference.setValue(data).addOnSuccessListener(unused -> {
            if (context != null) Toast.makeText(context, "Saved to favorites", Toast.LENGTH_SHORT).show();
        });
    }

    private void CustomRequestDialog(PostPropertyModel model) {
        if (context == null) return;
        View alertCustomDialog = LayoutInflater.from(context).inflate(R.layout.dialog_request_for_rent, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(alertCustomDialog);
        
        EditText requestMassage = alertCustomDialog.findViewById(R.id.request_massage);
        Button cancelButton = alertCustomDialog.findViewById(R.id.cancel_btn);
        Button sendButton = alertCustomDialog.findViewById(R.id.send_btn);
        
        final AlertDialog dialog = alertDialog.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        if (cancelButton != null) cancelButton.setOnClickListener(v -> dialog.cancel());
        if (sendButton != null) {
            sendButton.setOnClickListener(v -> {
                String message = requestMassage.getText().toString();
                if (!message.isEmpty()) {
                    sendRequest(message, model);
                    dialog.cancel();
                } else {
                    Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendRequest(String requestMassage, PostPropertyModel model) {
        if (user == null || model.getOwnerId() == null || model.getPropertyId() == null) return;
        
        DatabaseReference userMassageRef = FirebaseDatabase.getInstance().getReference("Massage Requests Activity")
                .child("Send").child(user.getUid());

        Map<String, Object> userMassageData = new HashMap<>();
        userMassageData.put("userID", model.getOwnerId());
        userMassageData.put("PropertyID", model.getPropertyId());
        userMassageRef.child(model.getOwnerId()).setValue(userMassageData);

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