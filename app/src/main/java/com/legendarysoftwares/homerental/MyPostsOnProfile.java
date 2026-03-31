package com.legendarysoftwares.homerental;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
    private ProfilePostsAdapter profilePostsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts_on_profile);

        userProfile = findViewById(R.id.profile_pic);
        textViewUserName = findViewById(R.id.postOnProfile_username);
        textViewUserAbout = findViewById(R.id.userAbout);
        textViewSeeRequests = findViewById(R.id.see_requests);
        textViewEditProfile = findViewById(R.id.edit_profile);
        noPosts = findViewById(R.id.no_posts);
        ImageView btnBack = findViewById(R.id.btn_back_myposts);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        textViewSeeRequests.setOnClickListener(v -> {
            startActivity(new Intent(this, MassagesActivity.class));
        });
        textViewEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, UpdateProfileActivity.class));
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            textViewUserName.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
            if (user.getPhotoUrl() != null) {
                Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.ic_logo_transparent).into(userProfile);
            }

            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Registered Users")
                    .child(user.getUid());
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!isFinishing() && snapshot.exists()) {
                        ReadWriteUserDetailsModel model = snapshot.getValue(ReadWriteUserDetailsModel.class);
                        if (model != null) {
                            textViewUserAbout.setText(model.getAbout() != null ? model.getAbout() : "No info");
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            homeRecyclerView = findViewById(R.id.profile_posts_rv);
            // Use SafeGridLayoutManager to prevent inconsistency crashes
            homeRecyclerView.setLayoutManager(new SafeGridLayoutManager(this, 3));
            
            DBReference = FirebaseDatabase.getInstance().getReference("Posted Properties");
            FirebaseRecyclerOptions<PostPropertyModel> options = new FirebaseRecyclerOptions.Builder<PostPropertyModel>()
                    .setQuery(DBReference.orderByChild("ownerId").equalTo(user.getUid()), PostPropertyModel.class).build();

            profilePostsAdapter = new ProfilePostsAdapter(options);
            profilePostsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    if (!isFinishing()) {
                        noPosts.setVisibility(View.GONE);
                        homeRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            });
            homeRecyclerView.setAdapter(profilePostsAdapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (profilePostsAdapter != null) profilePostsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (profilePostsAdapter != null) profilePostsAdapter.stopListening();
    }

    private static class SafeGridLayoutManager extends GridLayoutManager {
        public SafeGridLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }
        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("SafeLayout", "Caught inconsistency in MyPosts");
            }
        }
    }
}