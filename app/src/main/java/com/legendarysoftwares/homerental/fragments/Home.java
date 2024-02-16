package com.legendarysoftwares.homerental.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.legendarysoftwares.homerental.HomeAdapter;
import com.legendarysoftwares.homerental.MassagesActivity;
import com.legendarysoftwares.homerental.PostPropertyModel;
import com.legendarysoftwares.homerental.R;

public class Home extends Fragment {
    public Home() {}

    private RecyclerView homeRecyclerView;
    private HomeAdapter homeAdapter; // Declare the adapter as a field

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        homeRecyclerView = view.findViewById(R.id.home_recycler_view);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        ImageButton openMassageActivity = view.findViewById(R.id.massages);

        openMassageActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), MassagesActivity.class);
                startActivity(intent);
            }
        });




// Set RecyclerView initially invisible
        homeRecyclerView.setVisibility(View.GONE);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        try {
            DatabaseReference DBReference = FirebaseDatabase.getInstance().getReference("Posted Properties");
            FirebaseRecyclerOptions<PostPropertyModel> options = new FirebaseRecyclerOptions.Builder<PostPropertyModel>()
                    .setQuery(DBReference, PostPropertyModel.class).build();

            homeAdapter = new HomeAdapter(options,requireContext(),getCurrentUserId());
            // Show ProgressBar while loading, hide it when data is loaded
            homeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    // Data loaded, hide ProgressBar and show RecyclerView
                    progressBar.setVisibility(View.GONE);
                    homeRecyclerView.setVisibility(View.VISIBLE);
                }
            });
            homeRecyclerView.setAdapter(homeAdapter);

            // Start listening for changes in the data
            homeAdapter.startListening();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return view;
    }

    private FirebaseUser getCurrentUserId() {
         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user;
    }
    // Add the following lifecycle methods to start and stop listening when the fragment is started and stopped

    @Override
    public void onStart() {
        super.onStart();
        if (homeAdapter != null) {
            homeAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (homeAdapter != null) {
            homeAdapter.stopListening();
        }
    }

}

