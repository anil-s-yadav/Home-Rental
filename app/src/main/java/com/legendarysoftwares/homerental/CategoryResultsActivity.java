package com.legendarysoftwares.homerental;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CategoryResultsActivity extends AppCompatActivity {

    private HomeAdapter adapter;
    private TextView tvNoResults;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_results);

        String categoryName = getIntent().getStringExtra("category");
        if (categoryName == null) categoryName = "Properties";

        TextView tvTitle = findViewById(R.id.tv_category_title);
        tvNoResults = findViewById(R.id.tv_no_results);
        ImageView btnBack = findViewById(R.id.btn_back);
        RecyclerView recyclerView = findViewById(R.id.rv_category_results);
        progressBar = findViewById(R.id.progressBar);

        tvTitle.setText(categoryName);
        btnBack.setOnClickListener(v -> onBackPressed());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Posted Properties");
        
        // Match Firebase field "Type" (case-sensitive)
        Query query = mRef.orderByChild("Type").equalTo(categoryName);

        FirebaseRecyclerOptions<PostPropertyModel> options =
                new FirebaseRecyclerOptions.Builder<PostPropertyModel>()
                        .setQuery(query, PostPropertyModel.class)
                        .build();

        adapter = new HomeAdapter(options, this, user) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                progressBar.setVisibility(View.GONE);
                if (getItemCount() == 0) {
                    tvNoResults.setVisibility(View.VISIBLE);
                } else {
                    tvNoResults.setVisibility(View.GONE);
                }
            }
        };

        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}