package com.legendarysoftwares.homerental;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MassagesActivity extends AppCompatActivity {

    private MassagesRequestsAdapter requestsAdapter;
    private MassagesAdapter massagesAdapter;
    private ProgressBar progressBar;
    private LinearLayout NoMassagesLayout;
    private FirebaseUser user;
    private RecyclerView requestsRecyclerView, massagesRecyclerView;
    private boolean isRequestsVisible = false; // Add this boolean flag


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massages);

        user = FirebaseAuth.getInstance().getCurrentUser();


        //requestsRecyclerViews = findViewById(R.id.requests_recyclerView);
        progressBar = findViewById(R.id.progressBar);
        NoMassagesLayout = findViewById(R.id.no_massages_view);

        Button goBack = findViewById(R.id.massages_btn_goBack);
        goBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        ImageView showRequestsImage = findViewById(R.id.img_show_requests);
        ImageView showRequestsImageArrow = findViewById(R.id.img_show_massages_arrow);
        TextView textViewShowrequests = findViewById(R.id.tv_show_requests);
        LinearLayout linearLayoutShowrequests = findViewById(R.id.linearLayout_show_requests);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRequestsVisibility();
            }
        };
        showRequestsImage.setOnClickListener(onClickListener);
        showRequestsImageArrow.setOnClickListener(onClickListener);
        textViewShowrequests.setOnClickListener(onClickListener);
        linearLayoutShowrequests.setOnClickListener(onClickListener);


        requestsRecyclerView = findViewById(R.id.requests_recyclerView);
        massagesRecyclerView = findViewById(R.id.massages_recycler_view);

        // For requests RecyclerView
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestsAdapter = new MassagesRequestsAdapter(this);
        requestsRecyclerView.setAdapter(requestsAdapter);

        // For massages RecyclerView
        massagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        massagesAdapter = new MassagesAdapter(this);
        massagesRecyclerView.setAdapter(massagesAdapter);

        //requestsRecyclerViews.setAdapter(massageAdapter);
        loadRequestsData();
        // Load data for massages RecyclerView
        loadMassagesData();


    } // End of OnCreate Method

    // This method used to show hide requests
    private void toggleRequestsVisibility() {
        isRequestsVisible = !isRequestsVisible;

        if (isRequestsVisible) {
            requestsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            requestsRecyclerView.setVisibility(View.GONE);
        }
    }



    // Load data for requests RecyclerView
    private void loadRequestsData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("Massage Requests Activity")
                    .child("Receive")
                    .child(user.getUid());

            requestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Map<String, Object>> requestList = new ArrayList<>();

                    for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                        Map<String, Object> requestData = (Map<String, Object>) requestSnapshot.getValue();
                        if (requestData != null) {
                            requestList.add(requestData);
                        }
                    }

                    // Now you have the list of requests, update your RecyclerView adapter
                    // and notify the adapter about the data change.
                    // For example, if you're using MassagesAdapter, call massageAdapter.setData(requestList);
                    requestsAdapter.setData(requestList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                }
            });
        }
    }

    // Load data for massages RecyclerView
    private void loadMassagesData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference massagesRef = FirebaseDatabase.getInstance().getReference("Massage Requests Activity")
                    .child("Send")
                    .child(user.getUid());

            massagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Map<String, Object>> massagesList = new ArrayList<>();

                    for (DataSnapshot massageSnapshot : snapshot.getChildren()) {
                        Map<String, Object> massageData = (Map<String, Object>) massageSnapshot.getValue();
                        if (massageData != null) {
                            massagesList.add(massageData);
                        }
                    }

                    // Now you have the list of massages, update your RecyclerView adapter
                    // and notify the adapter about the data change.
                    // For example, if you're using MassagesAdapter, call massageAdapter.setData(massagesList);
                    massagesAdapter.setData(massagesList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                }
            });
        }
    }
}

/*
    private void loadSendUsers() {
        if (user == null) {
            return;
        }

        DatabaseReference massageActivityRef = FirebaseDatabase.getInstance()
                .getReference("Massage Requests Activity")
                .child("Send").child(user.getUid());

        massageActivityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Map<String, Object>> massageUsers = new ArrayList<>();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Map<String, Object> userData = (Map<String, Object>) userSnapshot.getValue();
                    if (userData==null){
                        Log.d("user data = ","null null null null null ");
                        requestsRecyclerViews.setVisibility(View.GONE);
                        NoMassagesLayout.setVisibility(View.VISIBLE);
                    }else {
                        Log.d("user data = ", Arrays.toString(new Object[]{userData}));
                        massageUsers.add(userData);
                        requestsRecyclerViews.setVisibility(View.VISIBLE);
                        massagesAdapter.setData(massageUsers);
                        massageRecyclerView.setAdapter(massagesAdapter);

                        progressBar.setVisibility(View.GONE);
                        NoMassagesLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                progressBar.setVisibility(View.GONE);
            }
        });
    }
*/



