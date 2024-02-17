package com.legendarysoftwares.homerental.fragments;

import android.app.Activity;
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
    private ImageView propertyImageView1, propertyImageView2, propertyImageView3, propertyImageView4, propertyImageView5, propertyImageView6;
    private Button imgChooseBtn;
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<ImageView> propertyImageViews;
    private List<Uri> selectedImageUris;
    private EditText postName, postLocation,postPrice;
    private Button uploadPostButton;
    private StorageReference imagesStorageRef;
    public Add(){ /* Required empty public constructor*/}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        postName= view.findViewById(R.id.editText_post_name);
        postLocation= view.findViewById(R.id.editText_post_location);
        postPrice= view.findViewById(R.id.editText_post_price);
        uploadPostButton = view.findViewById(R.id.upload_post_button);

        imagesStorageRef = FirebaseStorage.getInstance().getReference("property_images");


        uploadPostButton.setOnClickListener(v -> {
            uploadInStorageAndDownloadUrls();
        });


        propertyImageView1 = view.findViewById(R.id.choose_post_image_to_upload1);
        propertyImageView2 = view.findViewById(R.id.choose_post_image_to_upload2);
        propertyImageView3 = view.findViewById(R.id.choose_post_image_to_upload3);
        propertyImageView4 = view.findViewById(R.id.choose_post_image_to_upload4);
        propertyImageView5 = view.findViewById(R.id.choose_post_image_to_upload5);
        propertyImageView6 = view.findViewById(R.id.choose_post_image_to_upload6);
        imgChooseBtn = view.findViewById(R.id.ImgChooseBtn);

        propertyImageViews = Arrays.asList(propertyImageView1, propertyImageView2, propertyImageView3, propertyImageView4, propertyImageView5,propertyImageView6);
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
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
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





    /*private void uploadInStorageAndDownloadUrls() {

        if (selectedImageUris != null && !selectedImageUris.isEmpty()) {
            final StorageReference imageRef = imagesStorageRef.child(String.valueOf(System.currentTimeMillis()));

            imageRef.putFile(selectedImageUris.get(0)) // Use only the first image for now
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image upload success, now get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            // Image URL obtained, now add property details to the database
                            String postImageUrl1 = downloadUri.toString();
                            uploadPostToDatabase(postImageUrl1);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                        Log.e("AddProperty", "Image upload failed", e);
                    });
        } else {
            Toast.makeText(getContext(), "No images selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPostToDatabase(String postImageUrl1) {

        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            String ownerID = user.getUid();
            String ownerName = user.getDisplayName();               // Get the user's display name
            String ownerPhoto = user.getPhotoUrl().toString();       // Make sure to handle null here

            String textPostName = postName.getText().toString();
            String textPostLocation = postLocation.getText().toString();
            String textPostPrice = postPrice.getText().toString();


            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Posted Properties");
            String propertyId = referenceProfile.push().getKey();

            if (propertyId != null) {
                PostPropertyModel postPropertyModel = new PostPropertyModel(
                        propertyId, ownerID, textPostName, textPostLocation, textPostPrice, ownerName, ownerPhoto);

                // Set image URLs separately
                postPropertyModel.setPostImageUrl1(postImageUrl1);

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
    }*/      //  1:52 PM






    /*
    private void uploadPost(String textPostName, String textPostLocation, String textPostPrice,String ownerName,String ownerPhoto,
                            String postImageUrl1,String postImageUrl2,String postImageUrl3,String postImageUrl4,String postImageUrl5,String postImageUrl6) {
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = auth.getCurrentUser();
            String ownerID = firebaseUser.getUid();

            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Posted Properties");
            String propertyId = referenceProfile.push().getKey();

            if (propertyId != null) {
                PostPropertyModel postPropertyModel = new PostPropertyModel(propertyId, ownerID, textPostName,
                        textPostLocation, textPostPrice,ownerName,ownerPhoto,postImageUrl1,postImageUrl2,postImageUrl3,postImageUrl4,postImageUrl5,postImageUrl6);

                referenceProfile.child(propertyId).setValue(postPropertyModel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Posted successfully", Toast.LENGTH_SHORT).show();

                                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                                    ft.replace(R.id.container, new Home());
                                    ft.commit();
                                } else {
                                    Toast.makeText(getActivity(), "Posting failed", Toast.LENGTH_SHORT).show();
                                    Log.e("AddProperty", "Posting failed", task.getException());
                                }
                            }
                        });
            } else {
                Log.e("AddProperty", "Property ID is null");
            }
        } catch (Exception e) {
            Log.e("AddProperty", "Exception in uploadPost", e);
        }
    }

*/

}