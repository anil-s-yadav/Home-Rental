package com.legendarysoftwares.homerental.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.legendarysoftwares.homerental.PostPropertyModel;
import com.legendarysoftwares.homerental.R;
import com.legendarysoftwares.homerental.ResultActivity;
import com.legendarysoftwares.homerental.SearchAdapter;

import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Search extends Fragment {

    private ImageView House, Flat, Plot, Office, Shop, PG, Villa, Bunglow, Outlet, Factory, Cafe,
            Hostel, Warehouse, Cottage, Farmhouse;
    private SearchView searchView;
    private RecyclerView searchRecyclerView;
    private SearchAdapter SearchAdapter;
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout linearLayoutOptions;

    // Add any other necessary variables

    public Search() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        House= view.findViewById(R.id.search_house);
        Flat= view.findViewById(R.id.search_flat);
        Plot= view.findViewById(R.id.search_plot);
        Office= view.findViewById(R.id.search_office_space);
        Shop= view.findViewById(R.id.search_shop);
        PG= view.findViewById(R.id.search_pg);
        Villa= view.findViewById(R.id.search_villa);
        Bunglow= view.findViewById(R.id.search_bunglow);
        Outlet= view.findViewById(R.id.search_outlet);
        Factory= view.findViewById(R.id.search_factory);
        Cafe= view.findViewById(R.id.search_cafe);
        Hostel= view.findViewById(R.id.search_hostel);
        Warehouse= view.findViewById(R.id.search_warehouse);
        Cottage= view.findViewById(R.id.search_cottage);
        Farmhouse= view.findViewById(R.id.search_farmhouse);

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



        searchView = view.findViewById(R.id.searchView);
        searchRecyclerView = view.findViewById(R.id.searchRecycler_View);
        horizontalScrollView = view.findViewById(R.id.nestedScrollView);
        linearLayoutOptions = view.findViewById(R.id.linearlayout_options);

        searchRecyclerView.setVisibility(View.GONE);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Set up SearchView listener for query changes
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search when user submits the query
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // You can perform search dynamically as the user types
                performSearch(newText);
                return true;
            }
        });
    }

    private void performSearch(String query) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser.getUid();

        DatabaseReference searchReference = FirebaseDatabase.getInstance().getReference("Posted Properties");

        FirebaseRecyclerOptions<PostPropertyModel> options = new FirebaseRecyclerOptions.Builder<PostPropertyModel>()
                .setQuery(searchReference.orderByChild("postTitle").startAt(query).endAt(query + "\uf8ff"),
                        PostPropertyModel.class).build();
            Log.w("SearchFragment", "performSearch: Query - " + query);

        SearchAdapter = new SearchAdapter(options, requireContext(),currentUserId);
        SearchAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {

                horizontalScrollView.setVisibility(View.GONE);
                linearLayoutOptions.setVisibility(View.GONE);
                searchRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        searchRecyclerView.setAdapter(SearchAdapter);
        SearchAdapter.startListening();
    }


    public void onStart() {
        super.onStart();
        if (SearchAdapter != null) {
            SearchAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (SearchAdapter != null) {

            SearchAdapter.stopListening();
        }
    }
}