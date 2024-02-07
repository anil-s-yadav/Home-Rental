package com.legendarysoftwares.homerental.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.legendarysoftwares.homerental.PostPropertyModel;
import com.legendarysoftwares.homerental.R;
import com.legendarysoftwares.homerental.SaveAdapter;

// Saved.java
public class Saved extends Fragment {
    private RecyclerView saveRecyclerView;
    private DatabaseReference savedPostsReference;
    private SaveAdapter saveAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        saveRecyclerView = view.findViewById(R.id.save_recycler_view);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        saveRecyclerView.setVisibility(View.GONE);
        saveRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inside your activity or fragment where you need the current user ID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            savedPostsReference = FirebaseDatabase.getInstance().getReference("SavedPosts").child(currentUserId);
            FirebaseRecyclerOptions<PostPropertyModel> options = new FirebaseRecyclerOptions.Builder<PostPropertyModel>()
                    .setQuery(savedPostsReference, PostPropertyModel.class).build();

            saveAdapter = new SaveAdapter(options, requireContext(),currentUserId);
            saveAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    progressBar.setVisibility(View.GONE);
                    saveRecyclerView.setVisibility(View.VISIBLE);
                }
            });

            saveRecyclerView.setAdapter(saveAdapter);
            saveAdapter.startListening();
        } else {
            // The user is not authenticated, handle this case accordingly
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (saveAdapter != null) {
            saveAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (saveAdapter != null) {
            saveAdapter.stopListening();
        }
    }
}
