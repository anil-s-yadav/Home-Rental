package com.legendarysoftwares.homerental.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.legendarysoftwares.homerental.PostPropertyModel;
import com.legendarysoftwares.homerental.R;

public class Add extends Fragment {
    private ImageView propertyImageView;
    private EditText postName, postLocation,postPrice;
    private Button uploadPostButton;
    private Uri uriImage;
    private StorageReference imagesStorageRef;
    private static final int PICK_IMAGE_REQUEST=1;

    public Add(){ /* Required empty public constructor*/}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        propertyImageView = view.findViewById(R.id.choose_post_image_to_upload);
        postName= view.findViewById(R.id.editText_post_name);
        postLocation= view.findViewById(R.id.editText_post_location);
        postPrice= view.findViewById(R.id.editText_post_price);
        uploadPostButton = view.findViewById(R.id.upload_post_button);

        imagesStorageRef = FirebaseStorage.getInstance().getReference("property_images");

        propertyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        uploadPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProperty();
                //uploadPost(textPostName,textPostLocation,textPostPrice,imageUri);
            }
        });

        return view;
    }


    // Method to open the image chooser
    private void imageChooser() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data.getData() !=null){
            uriImage = data.getData();
            propertyImageView.setImageURI(uriImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void addProperty() {
        String textPostName = postName.getText().toString();
        String textPostLocation = postLocation.getText().toString();
        String textPostPrice = postPrice.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get the user's display name
        String ownerName = user.getDisplayName();
        String ownerPhoto = user.getPhotoUrl().toString();


        if (uriImage != null) {
            final StorageReference imageRef = imagesStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(uriImage));

            imageRef.putFile(uriImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image upload success, now get the download URL
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    // Image URL obtained, now add property details to the database
                                    String postImageUrl = downloadUri.toString();
                                    uploadPost(textPostName, textPostLocation, textPostPrice, postImageUrl,ownerName,ownerPhoto);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                            Log.e("AddProperty", "Image upload failed", e);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPost(String textPostName, String textPostLocation, String textPostPrice,
                            String postImageUrl,String ownerName,String ownerPhoto) {
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = auth.getCurrentUser();
            String ownerID = firebaseUser.getUid();

            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Posted Properties");
            String propertyId = referenceProfile.push().getKey();

            if (propertyId != null) {
                PostPropertyModel postPropertyModel = new PostPropertyModel(propertyId, ownerID, textPostName,
                        textPostLocation, textPostPrice, postImageUrl,ownerName,ownerPhoto);

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

}