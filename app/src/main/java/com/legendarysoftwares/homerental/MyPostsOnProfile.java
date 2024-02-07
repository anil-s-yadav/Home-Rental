package com.legendarysoftwares.homerental;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyPostsOnProfile extends AppCompatActivity {
    private RecyclerView homeRecyclerView;
    private DatabaseReference DBReference;
    private ProfilePostsAdapter profilePostsAdapter; // Declare the adapter as a field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts_on_profile);

        homeRecyclerView = findViewById(R.id.profile_posts_rv);
        ProgressBar progressBar = findViewById(R.id.progressBar);

// Set RecyclerView initially invisible
        homeRecyclerView.setVisibility(View.GONE);

        homeRecyclerView.setLayoutManager(new GridLayoutManager(this,3));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser.getUid();


        try {
            DBReference = FirebaseDatabase.getInstance().getReference("Posted Properties");
            FirebaseRecyclerOptions<PostPropertyModel> options = new FirebaseRecyclerOptions.Builder<PostPropertyModel>()
                    .setQuery(DBReference.orderByChild("ownerId").equalTo(currentUserId), PostPropertyModel.class).build();

            profilePostsAdapter = new ProfilePostsAdapter(options);
            // Show ProgressBar while loading, hide it when data is loaded
            profilePostsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    // Data loaded, hide ProgressBar and show RecyclerView
                    progressBar.setVisibility(View.GONE);
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
