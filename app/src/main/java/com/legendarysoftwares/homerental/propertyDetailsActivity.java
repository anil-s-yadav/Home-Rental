package com.legendarysoftwares.homerental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class propertyDetailsActivity extends AppCompatActivity {
    private String propertyID, userID;
    private ImageButton saveBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ImageView back = findViewById(R.id.backBtn_propertyDetails);
        back.setOnClickListener((View)->{
            onBackPressed();
        });

        /*Intent intent = getIntent();
        if (intent != null) {
            propertyID = intent.getStringExtra("propertyID");
            Log.d("MYMASSAGE",propertyID);
        }else {
            Log.d("MYMASSAGE", "Intent is null");
        }*/

        saveBtn=findViewById(R.id.property_details_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            getdata();
            }
        });

    }

    private Void getdata() {
        DatabaseReference propertyRef = FirebaseDatabase.getInstance().getReference()
                .child("Posted Properties").child(propertyID);

// Add a ValueEventListener to retrieve the property information
        propertyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String propertyTitle = dataSnapshot.child("postTitle").getValue(String.class);
                    String propertyAddress = dataSnapshot.child("postAddress").getValue(String.class);
                    String ownerId = dataSnapshot.child("ownerId").getValue(String.class);
                    String ownerName = dataSnapshot.child("ownerName").getValue(String.class);
                    String postImageUrl = dataSnapshot.child("postImageUrl").getValue(String.class);
                    String postPrice = dataSnapshot.child("postPrice").getValue(String.class);

                    // Now you can use this information to populate views or perform other actions
                    long timestamp = System.currentTimeMillis();
                    savePost(timestamp, propertyTitle, propertyAddress,
                            postPrice, ownerId, postImageUrl, ownerName);
                    saveBtn.setImageResource(R.drawable.save_fill);

                } else {
                    // Property does not exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        return null;
    }


    private void savePost(long timestamp, String postTitle,
                                        String postAddress, String postPrice, String ownerId,
                                        String postImageUrl, String ownerName) {
        // Implement the logic to save the post to the Save fragment using postId and userId
        DatabaseReference saveReference = FirebaseDatabase.getInstance().getReference("SavedPosts")
                .child(userID)
                .child(propertyID);

        // Set the necessary data for the saved post
        Map<String, Object> savedPostData = new HashMap<>();
        savedPostData.put("timestamp", timestamp);
        savedPostData.put("propertyId", propertyID);
        savedPostData.put("postTitle", postTitle);
        savedPostData.put("postAddress", postAddress);
        savedPostData.put("postPrice", postPrice);
        savedPostData.put("ownerId", ownerId);
        savedPostData.put("postImageUrl", postImageUrl);
        savedPostData.put("ownerName", ownerName);

        saveReference.setValue(savedPostData);
    }

}