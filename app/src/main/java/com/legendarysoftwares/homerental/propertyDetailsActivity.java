package com.legendarysoftwares.homerental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class propertyDetailsActivity extends AppCompatActivity {

    private String propertyId, ownerId;
    private TextView tvTitle, tvAddress, tvPrice, tvOwnerName, tvDesc, tvReraId, tvImageCount;
    private ImageView ivOwner;
    private ViewPager2 viewPager;
    private ImageButton btnSave;
    private LinearLayout layoutAdminControls;
    private Button btnEdit, btnDelete, btnAskRent, btnViewOwnerProfile;
    private FirebaseUser currentUser;
    private DatabaseReference propertyRef;
    private PostPropertyModel currentProperty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);

        propertyId = getIntent().getStringExtra("propertyId");
        ownerId = getIntent().getStringExtra("ownerId");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        initViews();
        fetchPropertyDetails();

        ImageView backBtn = findViewById(R.id.backBtn);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> onBackPressed());
        }

        btnSave.setOnClickListener(v -> {
            if (currentProperty != null) {
                saveProperty();
            }
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPropertyActivity.class);
            intent.putExtra("propertyId", propertyId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> showDeleteConfirmation());

        btnAskRent.setOnClickListener(v -> {
            if (currentProperty != null) {
                if (currentUser != null && currentUser.getUid().equals(currentProperty.getOwnerId())) {
                    Toast.makeText(this, "You can't send a message to yourself!", Toast.LENGTH_SHORT).show();
                } else {
                    CustomRequestDialog(currentProperty);
                }
            }
        });
    }

    private void initViews() {
        tvTitle = findViewById(R.id.post_title);
        tvAddress = findViewById(R.id.post_address);
        tvPrice = findViewById(R.id.post_price);
        tvOwnerName = findViewById(R.id.owner_name);
        tvDesc = findViewById(R.id.tv_property_desc);
        tvReraId = findViewById(R.id.tv_rera_id);
        tvImageCount = findViewById(R.id.tv_image_count);
        ivOwner = findViewById(R.id.owner_image);
        viewPager = findViewById(R.id.viewPager_images);
        btnSave = findViewById(R.id.btn_save_details);
        layoutAdminControls = findViewById(R.id.layout_admin_controls);
        btnEdit = findViewById(R.id.btn_edit_property);
        btnDelete = findViewById(R.id.btn_delete_property);
        btnAskRent = findViewById(R.id.btn_ask_rent);
        btnViewOwnerProfile = findViewById(R.id.btn_view_owner_profile);
    }

    private void fetchPropertyDetails() {
        if (propertyId == null) return;

        propertyRef = FirebaseDatabase.getInstance().getReference("Posted Properties").child(propertyId);
        propertyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isFinishing()) {
                    currentProperty = snapshot.getValue(PostPropertyModel.class);
                    if (currentProperty != null) {
                        displayDetails();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void displayDetails() {
        tvTitle.setText(currentProperty.getPostTitle());
        tvAddress.setText(currentProperty.getPostAddress());
        tvPrice.setText("₹ " + currentProperty.getPostPrice());
        tvOwnerName.setText(currentProperty.getOwnerName());
        tvReraId.setText("RERA ID: " + (currentProperty.getReraID() != null ? currentProperty.getReraID() : "N/A"));
        
        if (currentProperty.getOwnerPhoto() != null && !currentProperty.getOwnerPhoto().isEmpty()) {
            Picasso.get().load(currentProperty.getOwnerPhoto()).placeholder(R.drawable.ic_profile).into(ivOwner);
        }

        List<String> images = new ArrayList<>();
        addUrlIfValid(images, currentProperty.getPostImageUrl1());
        addUrlIfValid(images, currentProperty.getPostImageUrl2());
        addUrlIfValid(images, currentProperty.getPostImageUrl3());
        addUrlIfValid(images, currentProperty.getPostImageUrl4());
        addUrlIfValid(images, currentProperty.getPostImageUrl5());
        addUrlIfValid(images, currentProperty.getPostImageUrl6());

        if (images.isEmpty()) {
            images.add(""); // Placeholder for no images
        }

        PropertyImagesAdapter adapter = new ImageSliderAdapter(images);
        viewPager.setAdapter(adapter);
        
        tvImageCount.setText("1/" + images.size());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tvImageCount.setText((position + 1) + "/" + images.size());
            }
        });

        if (currentUser != null && currentUser.getUid().equals(currentProperty.getOwnerId())) {
            layoutAdminControls.setVisibility(View.VISIBLE);
            btnAskRent.setVisibility(View.GONE);
        } else {
            layoutAdminControls.setVisibility(View.GONE);
            btnAskRent.setVisibility(View.VISIBLE);
        }
    }

    private void addUrlIfValid(List<String> list, String url) {
        if (url != null && !url.trim().isEmpty()) {
            list.add(url);
        }
    }

    private void saveProperty() {
        if (currentUser == null) {
            new LoginBottomSheetHelper(this).showLoginBottomSheet();
            return;
        }

        DatabaseReference saveRef = FirebaseDatabase.getInstance().getReference("SavedPosts")
                .child(currentUser.getUid()).child(propertyId);

        Map<String, Object> data = new HashMap<>();
        data.put("propertyId", propertyId);
        data.put("ownerId", currentProperty.getOwnerId());
        data.put("ownerName", currentProperty.getOwnerName());
        data.put("postTitle", currentProperty.getPostTitle());
        data.put("postAddress", currentProperty.getPostAddress());
        data.put("postPrice", currentProperty.getPostPrice());
        data.put("postImageUrl", currentProperty.getPostImageUrl1());
        data.put("timestamp", System.currentTimeMillis());

        saveRef.setValue(data).addOnSuccessListener(unused -> {
            btnSave.setImageResource(R.drawable.heart_fill);
            Toast.makeText(this, "Property Saved!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Property")
                .setMessage("Are you sure you want to permanently remove this listing?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    propertyRef.removeValue().addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Property Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void CustomRequestDialog(PostPropertyModel model) {
        View alertCustomDialog = LayoutInflater.from(this).inflate(R.layout.dialog_request_for_rent, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
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
                    Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendRequest(String requestMassage, PostPropertyModel model) {
        if (currentUser == null) {
            new LoginBottomSheetHelper(this).showLoginBottomSheet();
            return;
        }
        if (model.getOwnerId() == null || model.getPropertyId() == null) return;
        
        DatabaseReference userMassageRef = FirebaseDatabase.getInstance().getReference("Massage Requests Activity")
                .child("Send").child(currentUser.getUid());

        Map<String, Object> userMassageData = new HashMap<>();
        userMassageData.put("userID", model.getOwnerId());
        userMassageData.put("PropertyID", model.getPropertyId());
        userMassageRef.child(model.getOwnerId()).setValue(userMassageData);

        DatabaseReference ownerMassageRef = FirebaseDatabase.getInstance().getReference("Massage Requests Activity")
                .child("Receive").child(model.getOwnerId());

        Map<String, Object> ownerMassageData = new HashMap<>();
        ownerMassageData.put("userID", currentUser.getUid());
        ownerMassageData.put("PropertyID", model.getPropertyId());
        ownerMassageRef.child(currentUser.getUid()).setValue(ownerMassageData);

        Intent massagesIntent = new Intent(this, MassagesActivity.class);
        massagesIntent.putExtra("requestMassage", requestMassage);
        startActivity(massagesIntent);
    }

    private class ImageSliderAdapter extends PropertyImagesAdapter {
        public ImageSliderAdapter(List<String> images) {
            super(images);
        }
    }
}

abstract class PropertyImagesAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<PropertyImagesAdapter.ViewHolder> {
    private List<String> images;
    public PropertyImagesAdapter(List<String> images) { this.images = images; }
    
    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
        ImageView iv = new ImageView(parent.getContext());
        iv.setLayoutParams(new android.view.ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ViewHolder(iv);
    }
    
    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = images.get(position);
        ImageView iv = (ImageView) holder.itemView;
        if (url != null && !url.isEmpty()) {
            Picasso.get().load(url).placeholder(R.drawable.ic_logo_transparent).error(R.drawable.ic_logo_transparent).into(iv);
        } else {
            iv.setImageResource(R.drawable.ic_logo_transparent);
        }
    }
    
    @Override public int getItemCount() { return images.size(); }
    static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) { super(itemView); }
    }
}