package com.legendarysoftwares.homerental.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Add extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<ImageView> propertyImageViews;
    private List<Uri> selectedImageUris;
    private EditText postName, postLocation, postPrice;
    private StorageReference imagesStorageRef;
    private FirebaseUser user;
    private String propertyId;
    private  ProgressDialog progressDialog;

    public Add() { /* Required empty public constructor*/}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        postName = view.findViewById(R.id.editText_post_name);
        postLocation = view.findViewById(R.id.editText_post_location);
        postPrice = view.findViewById(R.id.editText_post_price);
        Button uploadPostButton = view.findViewById(R.id.upload_post_button);

        user = FirebaseAuth.getInstance().getCurrentUser();

        imagesStorageRef = FirebaseStorage.getInstance().getReference("property_images").child(user.getUid());
        propertyId = FirebaseDatabase.getInstance().getReference().push().getKey();

        uploadPostButton.setOnClickListener(v -> {
            uploadInStorageAndDownloadUrls();
        });

        ImageView propertyImageView1 = view.findViewById(R.id.choose_post_image_to_upload1);
        ImageView propertyImageView2 = view.findViewById(R.id.choose_post_image_to_upload2);
        ImageView propertyImageView3 = view.findViewById(R.id.choose_post_image_to_upload3);
        ImageView propertyImageView4 = view.findViewById(R.id.choose_post_image_to_upload4);
        ImageView propertyImageView5 = view.findViewById(R.id.choose_post_image_to_upload5);
        ImageView propertyImageView6 = view.findViewById(R.id.choose_post_image_to_upload6);
        Button imgChooseBtn = view.findViewById(R.id.ImgChooseBtn);

        propertyImageViews = Arrays.asList(propertyImageView1, propertyImageView2, propertyImageView3, propertyImageView4, propertyImageView5, propertyImageView6);
        selectedImageUris = new ArrayList<>();

        imgChooseBtn.setOnClickListener(v -> chooseImages());


        return view;
    }


    private void chooseImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple image selection
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            ClipData clipData = data.getClipData();

            if (clipData != null) { // Multiple images selected
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    selectedImageUris.add(uri);

                    if (i < propertyImageViews.size()) {
                        propertyImageViews.get(i).setImageURI(uri);
                    }
                }
            } else { // Single image selected
                Uri uri = data.getData();
                selectedImageUris.add(uri);

                if (!selectedImageUris.isEmpty() && selectedImageUris.size() <= propertyImageViews.size()) {
                    for (int i = 0; i < selectedImageUris.size(); i++) {
                        propertyImageViews.get(i).setImageURI(selectedImageUris.get(i));
                    }
                }
            }
        }
    }


    private void uploadInStorageAndDownloadUrls() {
        if (selectedImageUris != null && !selectedImageUris.isEmpty()) {

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Updating Profile");
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            if (propertyId != null) {
                // Set up StorageReference with the new structure
                StorageReference userStorageRef = imagesStorageRef.child(user.getUid());
                StorageReference propertyStorageRef = userStorageRef.child(propertyId);

                List<String> postImageUrls = new ArrayList<>(); // Store all image URLs

                // Use a recursive method to handle asynchronous image uploads
                uploadImageRecursively(propertyStorageRef, postImageUrls, 0);
            } else {
                Log.e("AddProperty", "Property ID is null");
            }
        } else {
            Toast.makeText(getContext(), "No images selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private void uploadImageRecursively(StorageReference storageRef, List<String> postImageUrls, int index) {
        if (index < selectedImageUris.size()) {
            StorageReference imageRef = storageRef.child("image" + index); // e.g., image0, image1, ...
            Uri selectedImageUri = selectedImageUris.get(index);

            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image upload success, now get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            // Image URL obtained, add it to the list
                            postImageUrls.add(downloadUri.toString());

                            // Recursively call the method for the next image
                            uploadImageRecursively(storageRef, postImageUrls, index + 1);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                        Log.e("AddProperty", "Image upload failed", e);
                    });
        } else {
            // All images are processed, now upload post to the database
            uploadPostToDatabase(postImageUrls);
        }
    }

    private void uploadPostToDatabase(List<String> postImageUrls) {
        try {
            String ownerID = user.getUid();
            String ownerName = user.getDisplayName();
            String ownerPhoto = user.getPhotoUrl().toString();

            String textPostName = postName.getText().toString();
            String textPostLocation = postLocation.getText().toString();
            String textPostPrice = postPrice.getText().toString();
            long timestamp = System.currentTimeMillis();

            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Posted Properties");

            if (propertyId != null) {
                PostPropertyModel postPropertyModel = new PostPropertyModel(
                    timestamp, propertyId, ownerID, textPostName, textPostLocation, textPostPrice, ownerName, ownerPhoto);

                // Set all image URLs based on the number of images selected

                // Set all image URLs based on the number of images selected
                for (int i = 0; i < postImageUrls.size(); i++) {
                    switch (i) {
                        case 0:
                            postPropertyModel.setPostImageUrl1(postImageUrls.get(i));
                            break;
                        case 1:
                            postPropertyModel.setPostImageUrl2(postImageUrls.get(i));
                            break;
                        case 2:
                            postPropertyModel.setPostImageUrl3(postImageUrls.get(i));
                            break;
                        case 3:
                            postPropertyModel.setPostImageUrl4(postImageUrls.get(i));
                            break;
                        case 4:
                            postPropertyModel.setPostImageUrl5(postImageUrls.get(i));
                            break;
                        case 5:
                            postPropertyModel.setPostImageUrl6(postImageUrls.get(i));
                            break;
                        // Handle additional cases if you have more image views
                    }
                }

                referenceProfile.child(propertyId).setValue(postPropertyModel)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Posted successfully", Toast.LENGTH_SHORT).show();
                                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                                ft.replace(R.id.container, new Home());
                                ft.commit();
                            } else {
                                Toast.makeText(getActivity(), "Posting failed", Toast.LENGTH_SHORT).show();
                                Log.e("AddProperty", "Posting failed", task.getException());
                            }
                        });
            }
        } catch (Exception e) {
            Log.e("AddProperty", "Exception in uploadPost", e);
        }
        progressDialog.dismiss();
    }






/*

    private void uploadInStorageAndDownloadUrls() {
        if (selectedImageUris != null && !selectedImageUris.isEmpty()) {
            List<String> postImageUrls = new ArrayList<>(); // Store all image URLs

            // Iterate through each selected image
            for (int i = 0; i < selectedImageUris.size(); i++) {
                StorageReference imageRef = imagesStorageRef.child(String.valueOf(System.currentTimeMillis()));
                Uri selectedImageUri = selectedImageUris.get(i);

                final int finalI = i;
                imageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Image upload success, now get the download URL
                            imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                // Image URL obtained, add it to the list
                                postImageUrls.add(downloadUri.toString());

                                // If all images are processed, upload post to the database
                                if (finalI == selectedImageUris.size() - 1) {
                                    uploadPostToDatabase(postImageUrls);
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                            Log.e("AddProperty", "Image upload failed", e);
                        });
            }
        } else {
            Toast.makeText(getContext(), "No images selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPostToDatabase(List<String> postImageUrls) {
        try {
            String ownerID = user.getUid();
            String ownerName = user.getDisplayName();
            String ownerPhoto = user.getPhotoUrl().toString();

            String textPostName = postName.getText().toString();
            String textPostLocation = postLocation.getText().toString();
            String textPostPrice = postPrice.getText().toString();

            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Posted Properties");
            String propertyId = referenceProfile.push().getKey();

            if (propertyId != null) {
                PostPropertyModel postPropertyModel = new PostPropertyModel(
                        propertyId, ownerID, textPostName, textPostLocation, textPostPrice, ownerName, ownerPhoto);

                // Set all image URLs based on the number of images selected
                for (int i = 0; i < postImageUrls.size(); i++) {
                    switch (i) {
                        case 0:
                            postPropertyModel.setPostImageUrl1(postImageUrls.get(i));
                            break;
                        case 1:
                            postPropertyModel.setPostImageUrl2(postImageUrls.get(i));
                            break;
                        case 2:
                            postPropertyModel.setPostImageUrl3(postImageUrls.get(i));
                            break;
                        case 3:
                            postPropertyModel.setPostImageUrl4(postImageUrls.get(i));
                            break;
                        case 4:
                            postPropertyModel.setPostImageUrl5(postImageUrls.get(i));
                            break;
                        case 5:
                            postPropertyModel.setPostImageUrl6(postImageUrls.get(i));
                            break;
                        // Handle additional cases if you have more image views
                    }
                }

                referenceProfile.child(propertyId).setValue(postPropertyModel)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Posted successfully", Toast.LENGTH_SHORT).show();

                                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                                ft.replace(R.id.container, new Home());
                                ft.commit();
                            } else {
                                Toast.makeText(getActivity(), "Posting failed", Toast.LENGTH_SHORT).show();
                                Log.e("AddProperty", "Posting failed", task.getException());
                            }
                        });
            } else {
                Log.e("AddProperty", "Property ID is null");
            }
        } catch (Exception e) {
            Log.e("AddProperty", "Exception in uploadPost", e);
        }
    }

*/          // 12.45 18fab




}

