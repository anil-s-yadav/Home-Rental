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
import com.legendarysoftwares.homerental.CategoryResultsActivity;
import com.legendarysoftwares.homerental.PostPropertyModel;
import com.legendarysoftwares.homerental.R;
import com.legendarysoftwares.homerental.SaveAdapter;

import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Search extends Fragment {

    private SearchView searchView;
    private RecyclerView searchRecyclerView;
    private SaveAdapter saveAdapter;
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout linearLayoutOptions;

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

        searchView = view.findViewById(R.id.searchView);
        searchRecyclerView = view.findViewById(R.id.searchRecycler_View);
        horizontalScrollView = view.findViewById(R.id.nestedScrollView);
        linearLayoutOptions = view.findViewById(R.id.linearlayout_options);

        searchRecyclerView.setVisibility(View.GONE);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        View.OnClickListener categoryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = "";
                int id = v.getId();
                if (id == R.id.search_house) category = "House";
                else if (id == R.id.search_flat) category = "Flat";
                else if (id == R.id.search_plot) category = "Plot";
                else if (id == R.id.search_office_space) category = "Office";
                else if (id == R.id.search_shop) category = "Shop";
                else if (id == R.id.search_pg) category = "PG";
                else if (id == R.id.search_villa) category = "Villa";
                else if (id == R.id.search_bunglow) category = "Bungalow";
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
            }
        };

        view.findViewById(R.id.search_house).setOnClickListener(categoryClickListener);
        view.findViewById(R.id.search_flat).setOnClickListener(categoryClickListener);
        view.findViewById(R.id.search_plot).setOnClickListener(categoryClickListener);
        view.findViewById(R.id.search_office_space).setOnClickListener(categoryClickListener);
        view.findViewById(R.id.search_shop).setOnClickListener(categoryClickListener);
        view.findViewById(R.id.search_pg).setOnClickListener(categoryClickListener);
        view.findViewById(R.id.search_villa).setOnClickListener(categoryClickListener);
        view.findViewById(R.id.search_bunglow).setOnClickListener(categoryClickListener);
        view.findViewById(R.id.search_outlets).setOnClickListener(categoryClickListener);
        view.findViewById(R.id.search_factory).setOnClickListener(categoryClickListener);
        view.findViewById(R.id.search_cafe).setOnClickListener(categoryClickListener);
        view.findViewById(R.id.search_hostel).setOnClickListener(categoryClickListener);
        view.findViewById(R.id.search_warehouse).setOnClickListener(categoryClickListener);
        view.findViewById(R.id.search_cottage).setOnClickListener(categoryClickListener);
        view.findViewById(R.id.search_farmhouse).setOnClickListener(categoryClickListener);
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            horizontalScrollView.setVisibility(View.VISIBLE);
            linearLayoutOptions.setVisibility(View.VISIBLE);
            searchRecyclerView.setVisibility(View.GONE);
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = (currentUser != null) ? currentUser.getUid() : "";

        DatabaseReference searchReference = FirebaseDatabase.getInstance().getReference("Posted Properties");

        FirebaseRecyclerOptions<PostPropertyModel> options = new FirebaseRecyclerOptions.Builder<PostPropertyModel>()
                .setQuery(searchReference.orderByChild("postTitle").startAt(query).endAt(query + "\uf8ff"),
                        PostPropertyModel.class).build();

        saveAdapter = new SaveAdapter(options, requireContext(), currentUserId);
        saveAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                horizontalScrollView.setVisibility(View.GONE);
                linearLayoutOptions.setVisibility(View.GONE);
                searchRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        searchRecyclerView.setAdapter(saveAdapter);
        saveAdapter.startListening();
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