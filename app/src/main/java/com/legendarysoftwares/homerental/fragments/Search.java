package com.legendarysoftwares.homerental.fragments;

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
import com.legendarysoftwares.homerental.SaveAdapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Search extends Fragment {

    private SearchView searchView;
    private RecyclerView searchRecyclerView;
    private SaveAdapter saveAdapter;
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

        searchView = view.findViewById(R.id.searchView);
        searchRecyclerView = view.findViewById(R.id.searchRecycler_View);
        horizontalScrollView = view.findViewById(R.id.scrollView);
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

        saveAdapter = new SaveAdapter(options, requireContext(),currentUserId);
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