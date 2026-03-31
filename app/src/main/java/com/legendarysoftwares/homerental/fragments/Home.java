package com.legendarysoftwares.homerental.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.legendarysoftwares.homerental.CategoryResultsActivity;
import com.legendarysoftwares.homerental.HomeAdapter;
import com.legendarysoftwares.homerental.MassagesActivity;
import com.legendarysoftwares.homerental.PostPropertyModel;
import com.legendarysoftwares.homerental.R;

public class Home extends Fragment {
    private static final String TAG = "HomeFragmentDebug";
    private RecyclerView homeRecyclerView;
    private HomeAdapter homeAdapter;
    private ShimmerFrameLayout shimmer;

    // To prevent "Inconsistency detected" crash
    private final RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (isAdded() && shimmer != null && homeRecyclerView != null) {
                shimmer.stopShimmer();
                shimmer.setVisibility(View.GONE);
                homeRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    };

    public Home() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        homeRecyclerView = view.findViewById(R.id.home_recycler_view);
        shimmer = view.findViewById(R.id.home_shimmer);
        ImageButton openMassageActivity = view.findViewById(R.id.massages);

        if (shimmer != null) shimmer.startShimmer();

        if (openMassageActivity != null) {
            openMassageActivity.setOnClickListener(v -> 
                startActivity(new Intent(getContext(), MassagesActivity.class)));
        }

        setupCategoryListeners(view);

        // Using SafeLinearLayoutManager to catch RecyclerView internal measurement crashes
        homeRecyclerView.setLayoutManager(new SafeLinearLayoutManager(getContext()));

        setupAdapter();

        return view;
    }

    private void setupAdapter() {
        try {
            DatabaseReference DBReference = FirebaseDatabase.getInstance().getReference("Posted Properties");
            FirebaseRecyclerOptions<PostPropertyModel> options = new FirebaseRecyclerOptions.Builder<PostPropertyModel>()
                    .setQuery(DBReference.orderByChild("timestamp").limitToLast(50), PostPropertyModel.class).build();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            homeAdapter = new HomeAdapter(options, getContext(), user);
            homeAdapter.registerAdapterDataObserver(dataObserver);
            homeRecyclerView.setAdapter(homeAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up adapter", e);
        }
    }

    private void setupCategoryListeners(View view) {
        View.OnClickListener listener = v -> {
            String category = "";
            int id = v.getId();
            if (id == R.id.search_house) category = "House";
            else if (id == R.id.search_flat) category = "Flat";
            else if (id == R.id.search_plot) category = "Plot";
            else if (id == R.id.search_pg) category = "PG";
            else if (id == R.id.search_villa) category = "Villa";
            else if (id == R.id.search_bunglow) category = "Bungalow";
            else if (id == R.id.search_office_space) category = "Office";
            else if (id == R.id.search_shop) category = "Shop";
            else if (id == R.id.search_outlets) category = "Outlet";
            else if (id == R.id.search_factory) category = "Factory";
            else if (id == R.id.search_cafe) category = "Cafe";
            else if (id == R.id.search_hostel) category = "Hostel";
            else if (id == R.id.search_warehouse) category = "Warehouse";
            else if (id == R.id.search_cottage) category = "Cottage";
            else if (id == R.id.search_farmhouse) category = "Farmhouse";

            Intent intent = new Intent(getContext(), CategoryResultsActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        };

        int[] ids = {R.id.search_house, R.id.search_flat, R.id.search_plot, R.id.search_pg, 
                     R.id.search_villa, R.id.search_bunglow, R.id.search_office_space, 
                     R.id.search_shop, R.id.search_outlets, R.id.search_factory, 
                     R.id.search_cafe, R.id.search_hostel, R.id.search_warehouse, 
                     R.id.search_cottage, R.id.search_farmhouse};
        
        for (int id : ids) {
            View v = view.findViewById(id);
            if (v != null) v.setOnClickListener(listener);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (homeAdapter != null) homeAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (homeAdapter != null) homeAdapter.stopListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (homeAdapter != null) {
            try {
                homeAdapter.unregisterAdapterDataObserver(dataObserver);
            } catch (Exception e) {
                // Already unregistered
            }
        }
    }

    /**
     * Custom LinearLayoutManager to catch the "Inconsistency detected" crash
     */
    private static class SafeLinearLayoutManager extends LinearLayoutManager {
        public SafeLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("SafeLayoutManager", "Inconsistency detected and caught", e);
            }
        }
    }
}