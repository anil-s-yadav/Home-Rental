package com.legendarysoftwares.homerental.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.legendarysoftwares.homerental.PostPropertyModel;
import com.legendarysoftwares.homerental.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Add extends Fragment {
    private static final String TAG = "AddPropertyFragment";
    private final List<Uri> selectedImageUris = new ArrayList<>();
    private EditText postName, postLocation, postPrice;
    private AutoCompleteTextView spinnerPostType;
    private FirebaseUser user;
    private String propertyId;
    private ProgressDialog progressDialog;

    // Use GetMultipleContents for reliable cross-device experience
    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetMultipleContents(),
            uris -> {
                if (uris != null) {
                    handleUris(uris);
                }
            }
    );

    public Add() { /* Required empty public constructor*/ }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            postName = view.findViewById(R.id.editText_post_name);
            postLocation = view.findViewById(R.id.editText_post_location);
            postPrice = view.findViewById(R.id.editText_post_price);
            spinnerPostType = view.findViewById(R.id.spinner_post_type);
            Button uploadPostButton = view.findViewById(R.id.upload_post_button);
            Button imgChooseBtn = view.findViewById(R.id.ImgChooseBtn);

            setupCategories();

            user = FirebaseAuth.getInstance().getCurrentUser();
            propertyId = FirebaseDatabase.getInstance().getReference().push().getKey();

            uploadPostButton.setOnClickListener(v -> {
                if (validateFields()) {
                    uploadInStorageAndDownloadUrls();
                }
            });

            imgChooseBtn.setOnClickListener(v -> {
                try {
                    imagePickerLauncher.launch("image/*");
                } catch (Exception e) {
                    if (isAdded()) Toast.makeText(getContext(), "Gallery error", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated", e);
        }
    }

    private void setupCategories() {
        if (!isAdded() || getContext() == null || spinnerPostType == null) return;
        String[] categories = {"House", "Flat", "Plot", "Office", "Shop", "PG", "Villa", "Bungalow", "Outlet", "Factory", "Cafe", "Hostel", "Warehouse", "Cottage", "Farmhouse"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, categories);
        spinnerPostType.setAdapter(adapter);
    }

    private void handleUris(@NonNull List<Uri> uris) {
        if (!isAdded() || getView() == null) return;

        try {
            selectedImageUris.clear();
            
            // Explicitly cap at 6 images to prevent potential index errors
            int count = Math.min(uris.size(), 6);
            if (uris.size() > 6 && isAdded()) {
                Toast.makeText(getContext(), "Only the first 6 images were selected.", Toast.LENGTH_SHORT).show();
            }

            for (int i = 0; i < count; i++) {
                Uri uri = uris.get(i);
                if (uri != null) selectedImageUris.add(uri);
            }

            updateImagePreviews();
        } catch (Exception e) {
            Log.e(TAG, "Error handling URIs", e);
        }
    }

    private void updateImagePreviews() {
        View v = getView();
        if (v == null) return;

        int[] ids = {
                R.id.choose_post_image_to_upload1, R.id.choose_post_image_to_upload2,
                R.id.choose_post_image_to_upload3, R.id.choose_post_image_to_upload4,
                R.id.choose_post_image_to_upload5, R.id.choose_post_image_to_upload6
        };

        for (int i = 0; i < ids.length; i++) {
            ImageView iv = v.findViewById(ids[i]);
            if (iv == null) continue;

            if (i < selectedImageUris.size()) {
                // Using fit().centerCrop() to prevent OOM crashes with large images
                Picasso.get().load(selectedImageUris.get(i))
                        .placeholder(R.drawable.ic_logo_transparent)
                        .error(R.drawable.ic_logo_transparent)
                        .fit()
                        .centerCrop()
                        .into(iv);
            } else {
                iv.setImageResource(R.drawable.ic_logo_transparent);
            }
        }
    }

    private boolean validateFields() {
        if (user == null) {
            if (isAdded()) Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (postName.getText().toString().trim().isEmpty()) {
            postName.setError("Required");
            return false;
        }
        if (spinnerPostType.getText().toString().trim().isEmpty()) {
            spinnerPostType.setError("Select Type");
            return false;
        }
        if (postLocation.getText().toString().trim().isEmpty()) {
            postLocation.setError("Required");
            return false;
        }
        if (selectedImageUris.isEmpty()) {
            if (isAdded()) Toast.makeText(getContext(), "Please select at least one image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadInStorageAndDownloadUrls() {
        if (!isAdded() || user == null || getContext() == null) return;

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Publishing...");
        progressDialog.setMessage("Uploading images...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (propertyId == null) propertyId = FirebaseDatabase.getInstance().getReference().push().getKey();
        
        List<String> postImageUrls = new ArrayList<>();
        uploadRecursive(0, postImageUrls);
    }

    private void uploadRecursive(int index, List<String> urls) {
        if (!isAdded() || index >= selectedImageUris.size()) {
            saveToDatabase(urls);
            return;
        }

        StorageReference ref = FirebaseStorage.getInstance().getReference("property_images")
                .child(user.getUid()).child(propertyId).child("image" + index);

        ref.putFile(selectedImageUris.get(index))
                .addOnSuccessListener(task -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    urls.add(uri.toString());
                    uploadRecursive(index + 1, urls);
                }))
                .addOnFailureListener(e -> {
                    if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                    if (isAdded()) Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToDatabase(List<String> urls) {
        if (!isAdded() || user == null) {
            if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            return;
        }

        try {
            PostPropertyModel post = new PostPropertyModel();
            post.setTimestamp(System.currentTimeMillis());
            post.setPropertyId(propertyId);
            post.setOwnerId(user.getUid());
            post.setPostTitle(postName.getText().toString().trim());
            post.setPostAddress(postLocation.getText().toString().trim());
            post.setPostPrice(postPrice.getText().toString().trim());
            post.setType(spinnerPostType.getText().toString().trim());
            post.setOwnerName(user.getDisplayName() != null ? user.getDisplayName() : "Owner");
            post.setOwnerPhoto(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");

            for (int i = 0; i < urls.size(); i++) {
                String url = urls.get(i);
                switch (i) {
                    case 0: post.setPostImageUrl1(url); break;
                    case 1: post.setPostImageUrl2(url); break;
                    case 2: post.setPostImageUrl3(url); break;
                    case 3: post.setPostImageUrl4(url); break;
                    case 4: post.setPostImageUrl5(url); break;
                    case 5: post.setPostImageUrl6(url); break;
                }
            }

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Posted Properties");
            dbRef.child(propertyId).setValue(post).addOnCompleteListener(task -> {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                if (task.isSuccessful() && isAdded()) {
                    Toast.makeText(getContext(), "Posted Successfully!", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().beginTransaction().replace(R.id.container, new Home()).commit();
                } else if (isAdded()) {
                    Toast.makeText(getContext(), "Error saving to database", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            Log.e(TAG, "Error saving property", e);
        }
    }
}