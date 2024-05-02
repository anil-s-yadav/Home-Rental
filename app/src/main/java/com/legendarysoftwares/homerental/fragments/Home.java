package com.legendarysoftwares.homerental.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

    private RecyclerView homeRecyclerView;
    private HomeAdapter homeAdapter;
    private ShimmerFrameLayout shimmerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupClickListeners(view);

        return view;
    }

    private void initializeViews(View view) {
        homeRecyclerView = view.findViewById(R.id.home_recycler_view);
        shimmerLayout = view.findViewById(R.id.home_shimmer);
    }

    private void setupRecyclerView() {
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        try {
            DatabaseReference DBReference = FirebaseDatabase.getInstance().getReference("Posted Properties");
            FirebaseRecyclerOptions<PostPropertyModel> options = new FirebaseRecyclerOptions.Builder<PostPropertyModel>()
                    .setQuery(DBReference.orderByChild("timestamp").limitToLast(50), PostPropertyModel.class).build();

            homeAdapter = new HomeAdapter(options, requireContext(), getCurrentUserId());
            homeRecyclerView.setAdapter(homeAdapter);

            // Show shimmer animation while loading
            shimmerLayout.startShimmer();
            homeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    // Data loaded, hide shimmer animation
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                    homeRecyclerView.setVisibility(View.VISIBLE);
                }
            });
            homeAdapter.startListening();
        } catch (Exception e) {
            // Log the error or display a message to the user
            e.printStackTrace();
        }
    }

    private void setupClickListeners(View view) {
        ImageView[] propertyViews = {view.findViewById(R.id.home_house), view.findViewById(R.id.home_flat),
                view.findViewById(R.id.home_plot), view.findViewById(R.id.home_office_space),
                view.findViewById(R.id.home_shop), view.findViewById(R.id.home_pg),
                view.findViewById(R.id.home_villa), view.findViewById(R.id.home_bunglow),
                view.findViewById(R.id.home_outlets), view.findViewById(R.id.home_factory),
                view.findViewById(R.id.home_cafe), view.findViewById(R.id.home_hostel),
                view.findViewById(R.id.home_warehouse), view.findViewById(R.id.home_cottage),
                view.findViewById(R.id.home_farmhouse)};
        String[] propertyNames = {"House", "Flat", "Plot", "Office", "Shop", "PG", "Villa", "Bunglow",
                "Outlet", "Factory", "Cafe", "Hostel", "Warehouse", "Cottage", "Farmhouse"};

        for (int i = 0; i < propertyViews.length; i++) {
            final String propertyName = propertyNames[i];
            propertyViews[i].setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ResultActivity.class);
                intent.putExtra("Type", propertyName);
                startActivity(intent);
            });
        }

        ImageButton openMessagesActivity = view.findViewById(R.id.massages);
        openMessagesActivity.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MassagesActivity.class);
            startActivity(intent);
        });
    }

    private FirebaseUser getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

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
