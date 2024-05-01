package com.legendarysoftwares.homerental;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResultActivity extends AppCompatActivity {
    private String PropertyType;
    private RecyclerView resultRecyclerView;
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ProgressBar progressBar = findViewById(R.id.progressBar_result);

// These data come from Home Intent
        Intent intent = getIntent();
        if (intent != null) {
            PropertyType = intent.getStringExtra("Type");
            TextView resultType=findViewById(R.id.result_type);
            resultType.setText(PropertyType);
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser.getUid();

        resultRecyclerView=findViewById(R.id.result_recycler_view);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(ResultActivity.this));


        try {
            DatabaseReference DBReference = FirebaseDatabase.getInstance().getReference("Posted Properties");

            FirebaseRecyclerOptions<PostPropertyModel> options = new FirebaseRecyclerOptions.Builder<PostPropertyModel>()
                    .setQuery(DBReference.orderByChild("Type").equalTo(PropertyType), PostPropertyModel.class).build();

            searchAdapter = new SearchAdapter(options, ResultActivity.this, currentUserId);
            resultRecyclerView.setAdapter(searchAdapter);
            searchAdapter.startListening();
            searchAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    progressBar.setVisibility(View.GONE);
                }
            });
            progressBar.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
        }

    }


    // Add the following lifecycle methods to start and stop listening when the fragment is started and stopped
    @Override
    public void onStart() {
        super.onStart();
        if (searchAdapter != null) {
            searchAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (searchAdapter != null) {
            searchAdapter.stopListening();
        }
    }
}