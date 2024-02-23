package com.legendarysoftwares.homerental;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MyPostsOnProfile extends AppCompatActivity {
    private ShapeableImageView userProfile;
    private ImageView noPosts;
    private TextView textViewUserName, textViewUserAbout, textViewSeeRequests, textViewEditProfile;
    private RecyclerView homeRecyclerView;
    private DatabaseReference DBReference;
    private ProfilePostsAdapter profilePostsAdapter; // Declare the adapter as a field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts_on_profile);

        userProfile = findViewById(R.id.profile_pic);
        textViewUserName = findViewById(R.id.postOnProfile_username);
        textViewUserName = findViewById(R.id.postOnProfile_username);
        textViewUserAbout = findViewById(R.id.userAbout);
        textViewSeeRequests = findViewById(R.id.see_requests);
        textViewEditProfile = findViewById(R.id.edit_profile);
        noPosts = findViewById(R.id.no_posts);

        textViewSeeRequests.setOnClickListener(v -> {
            Intent intent=new Intent(this, MassagesActivity.class);
            startActivity(intent);
        });
        textViewEditProfile.setOnClickListener(v -> {
            Intent intent=new Intent(this, UpdateProfileActivity.class);
            startActivity(intent);
        });

        LoginBottomSheetHelper bottomSheetHelper=new LoginBottomSheetHelper(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.reload();
            textViewUserName.setText(user.getDisplayName());
            Picasso.get().load(user.getPhotoUrl()).into(userProfile);

            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Registered Users")
                    .child(user.getUid());
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        ReadWriteUserDetailsModel model = snapshot.getValue(ReadWriteUserDetailsModel.class);
                        if (model != null) {
                            textViewUserAbout.setText(model.getAbout());
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            bottomSheetHelper.showLoginBottomSheet();
        }











        homeRecyclerView = findViewById(R.id.profile_posts_rv);
        homeRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        try {
            DBReference = FirebaseDatabase.getInstance().getReference("Posted Properties");
            FirebaseRecyclerOptions<PostPropertyModel> options = new FirebaseRecyclerOptions.Builder<PostPropertyModel>()
                    .setQuery(DBReference.orderByChild("ownerId").equalTo(user.getUid()), PostPropertyModel.class).build();

            profilePostsAdapter = new ProfilePostsAdapter(options);
            // Show ProgressBar while loading, hide it when data is loaded
            profilePostsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    // Data loaded, hide ProgressBar and show RecyclerView
                    noPosts.setVisibility(View.GONE);
                    homeRecyclerView.setVisibility(View.VISIBLE);
                }
            });
            homeRecyclerView.setAdapter(profilePostsAdapter);

            // Start listening for changes in the data
            profilePostsAdapter.startListening();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (profilePostsAdapter != null) {
            profilePostsAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (profilePostsAdapter != null) {
            profilePostsAdapter.stopListening();
        }
    }




    }
