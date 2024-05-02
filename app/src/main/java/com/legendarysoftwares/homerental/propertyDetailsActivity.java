package com.legendarysoftwares.homerental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class propertyDetailsActivity extends AppCompatActivity {
    private String propertyID = "-Nr-B_4lS4ZwQlQ9JCz0", userID, ownerID = "CAbky7pEw4baZU12PY4rq92E2uB3";
    private ImageButton saveBtn;
    private ImageView propertyImage;
    private Button viewProfile, askForrent;
    private ShapeableImageView userProfile;
    private TextView ToolbarTitle, propertyTitle2, propertyaddress, propertyPrice, propertyUnit, propertyCarpetArea,
            averagePrice, reraId1, reraId2,userName, usertype, userName2, userAbout ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);

        loadDetails();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ImageView back = findViewById(R.id.backBtn_propertyDetails);
        back.setOnClickListener((View)->{
            onBackPressed();
        });

        propertyImage = findViewById(R.id.post_property_image);
        viewProfile = findViewById(R.id.button_view_profile);
        askForrent = findViewById(R.id.ask_for_rent_btn);
        userProfile = findViewById(R.id.property_details_user_image);
        //ToolbarTitle = findViewById(R.id.pd_tv_property_title);
        propertyTitle2 = findViewById(R.id.property_name);
        propertyaddress = findViewById(R.id.property_address);
        propertyPrice = findViewById(R.id.property_price);
        propertyUnit = findViewById(R.id.tv_unit);
        propertyCarpetArea = findViewById(R.id.tv_carpet_area);
        averagePrice = findViewById(R.id.tv_average_price);
        reraId1 = findViewById(R.id.reraid_tv1);
        reraId2 = findViewById(R.id.tv_rera_id);
        userName = findViewById(R.id.username);
        usertype = findViewById(R.id.usertype);
        userName2 = findViewById(R.id.tv_about_user);
        userAbout = findViewById(R.id.tv_about_user_data);


        Intent HomeAdapterIntent = getIntent();
        Intent SearchAdapterintent = getIntent();
        Intent SaveAdapterIntent = getIntent();
        Intent ProfileIntent = getIntent();
        Intent ResultIntent = getIntent();
        if (HomeAdapterIntent != null) {
            propertyID = HomeAdapterIntent.getStringExtra("propertyID");
            ownerID = HomeAdapterIntent.getStringExtra("ownerId");
        }else if (SearchAdapterintent != null){
            propertyID = SearchAdapterintent.getStringExtra("propertyID");
            ownerID = SearchAdapterintent.getStringExtra("ownerId");
        }else if (SaveAdapterIntent != null){
            propertyID = SaveAdapterIntent.getStringExtra("propertyID");
            ownerID = SaveAdapterIntent.getStringExtra("ownerId");
        }else if (ProfileIntent != null){
            propertyID = ProfileIntent.getStringExtra("propertyID");
            ownerID = ProfileIntent.getStringExtra("ownerId");
        }else if (ResultIntent != null){
            propertyID = ResultIntent.getStringExtra("propertyID");
            ownerID = ResultIntent.getStringExtra("ownerId");
        }
        else Toast.makeText(this, "Intent is null", Toast.LENGTH_SHORT).show();

        saveBtn=findViewById(R.id.property_details_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            getdata();
            }
        });

        viewProfile.setOnClickListener(View -> {

        });
        askForrent.setOnClickListener(View ->{
            Intent intent = new Intent(propertyDetailsActivity.this,MassagesActivity.class);
            startActivity(intent);
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



    private Void loadDetails() {
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
                    String postImageUrl = dataSnapshot.child("postImageUrl1").getValue(String.class);
                    String postPrice = dataSnapshot.child("postPrice").getValue(String.class);

                   // String postUnit = dataSnapshot.child("postPrice").getValue(String.class);
                    //String postCarpetArea = dataSnapshot.child("postPrice").getValue(String.class);
                    String reraid = dataSnapshot.child("reraID").getValue(String.class);

                    Picasso.get().load(postImageUrl).into(propertyImage);
                    propertyTitle2.setText(propertyTitle);
                    propertyaddress.setText(propertyAddress);
                    propertyPrice.setText(postPrice);
                    //propertyUnit.setText(postUnit);
                    //propertyCarpetArea.setText(postCarpetArea);
                    //averagePrice.setText(postPrice);
                    reraId1.setText(reraid);
                    reraId2.setText(reraid);
                    userName.setText(ownerName);

                } else {
                    // Property does not exist
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });


        DatabaseReference ownerRef = FirebaseDatabase.getInstance().getReference()
                .child("Registered Users").child(ownerID);
        ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String ownerImage = dataSnapshot.child("photo").getValue(String.class);
                    String UserAbout = dataSnapshot.child("about").getValue(String.class);

                    Picasso.get().load(ownerImage).into(userProfile);
                    userAbout.setText(UserAbout);

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


}