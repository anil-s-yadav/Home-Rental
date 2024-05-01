package com.legendarysoftwares.homerental.fragments;

import android.content.Intent;
import android.net.Uri;
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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.legendarysoftwares.homerental.HomeAdapter;
import com.legendarysoftwares.homerental.MassagesActivity;
import com.legendarysoftwares.homerental.PostPropertyModel;
import com.legendarysoftwares.homerental.R;
import com.legendarysoftwares.homerental.ResultActivity;

public class Home extends Fragment {
    public Home() {}

    private ImageView House, Flat, Plot, Office, Shop, PG, Villa, Bunglow, Outlet, Factory, Cafe,
    Hostel, Warehouse, Cottage, Farmhouse;

    private RecyclerView homeRecyclerView;
    private HomeAdapter homeAdapter; // Declare the adapter as a field

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        House= view.findViewById(R.id.home_house);
        Flat= view.findViewById(R.id.home_flat);
        Plot= view.findViewById(R.id.home_plot);
        Office= view.findViewById(R.id.home_office_space);
        Shop= view.findViewById(R.id.home_shop);
        PG= view.findViewById(R.id.home_pg);
        Villa= view.findViewById(R.id.home_villa);
        Bunglow= view.findViewById(R.id.home_bunglow);
        Outlet= view.findViewById(R.id.home_outlets);
        Factory= view.findViewById(R.id.home_factory);
        Cafe= view.findViewById(R.id.home_cafe);
        Hostel= view.findViewById(R.id.home_hostel);
        Warehouse= view.findViewById(R.id.home_warehouse);
        Cottage= view.findViewById(R.id.home_cottage);
        Farmhouse= view.findViewById(R.id.home_farmhouse);

// Assuming all your views are in an array or list
        View[] views = {House, Flat, Plot, Office, Shop, PG, Villa, Bunglow, Outlet, Factory, Cafe, Hostel, Warehouse, Cottage, Farmhouse};
        String[] names = {"House", "Flat", "Plot", "Office", "Shop", "PG", "Villa", "Bunglow", "Outlet", "Factory", "Cafe", "Hostel", "Warehouse", "Cottage", "Farmhouse"};

        for (int i = 0; i < views.length; i++) {
            final String name = names[i];
            final View itemViews = views[i];

            itemViews.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ResultActivity.class);
                intent.putExtra("Type", name);
                getContext().startActivity(intent);
            });
        }




        homeRecyclerView = view.findViewById(R.id.home_recycler_view);
        ImageButton openMassageActivity = view.findViewById(R.id.massages);

        ShimmerFrameLayout shimmer = view.findViewById(R.id.home_shimmer);
        shimmer.startShimmer();

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
                    .setQuery(DBReference.orderByChild("timestamp").limitToLast(50), PostPropertyModel.class).build();

            homeAdapter = new HomeAdapter(options,requireContext(),getCurrentUserId());
            // Show ProgressBar while loading, hide it when data is loaded
            homeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    // Data loaded, hide ProgressBar and show RecyclerView
                    shimmer.stopShimmer();
                    shimmer.hideShimmer();
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

