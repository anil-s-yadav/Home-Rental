package com.legendarysoftwares.homerental;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
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
    private RecyclerView massagesRecyclerView;
    private MaterialCardView requestsCardView;
    private TextView tvRequestsCount;
    private List<Map<String, Object>> requestList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massages);

        user = FirebaseAuth.getInstance().getCurrentUser();

        progressBar = findViewById(R.id.progressBar);
        NoMassagesLayout = findViewById(R.id.no_massages_view);
        NoMassagesLayout.setVisibility(View.GONE);
        requestsCardView = findViewById(R.id.requestsCardView);
        tvRequestsCount = findViewById(R.id.tvRequestsCount);

        Button goBack = findViewById(R.id.massages_btn_goBack);
        ImageView backBtn = findViewById(R.id.backBtn);
        
        View.OnClickListener goBackListener = v -> {
            try {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e("MassagesActivity", "Error navigating back", e);
            }
        };
        
        goBack.setOnClickListener(goBackListener);
        backBtn.setOnClickListener(goBackListener);

        massagesRecyclerView = findViewById(R.id.massages_recycler_view);

        // For massages RecyclerView
        massagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        massagesAdapter = new MassagesAdapter(this);
        massagesRecyclerView.setAdapter(massagesAdapter);

        // For requests Adapter (used in dialog)
        requestsAdapter = new MassagesRequestsAdapter(this);

        requestsCardView.setOnClickListener(v -> {
            showRequestsDialog();
        });

        loadRequestsData();
        loadMassagesData();
    } // End of OnCreate Method

    private void showRequestsDialog() {
        if (requestList == null || requestList.isEmpty()) {
            Toast.makeText(this, "No pending requests", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_requests_list);

            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            RecyclerView dialogRecyclerView = dialog.findViewById(R.id.dialog_requests_recycler_view);
            dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            dialogRecyclerView.setAdapter(requestsAdapter);
            requestsAdapter.setData(requestList);

            Button btnClose = dialog.findViewById(R.id.btn_close_dialog);
            btnClose.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        } catch (Exception e) {
            Log.e("MassagesActivity", "Error showing requests dialog", e);
            Toast.makeText(this, "Could not open requests", Toast.LENGTH_SHORT).show();
        }
    }


    // Load data for requests 
    private void loadRequestsData() {
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
            DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("Massage Requests Activity")
                    .child("Receive")
                    .child(user.getUid());

            requestsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        requestList.clear();

                        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                            Map<String , Object> requestData = (Map<String, Object>) requestSnapshot.getValue();
                            if (requestData != null) {
                                requestList.add(requestData);
                            }
                        }

                        if (!requestList.isEmpty()) {
                            requestsCardView.setVisibility(View.VISIBLE);
                            tvRequestsCount.setText(requestList.size() + " new requests");
                            NoMassagesLayout.setVisibility(View.GONE);
                        } else {
                            requestsCardView.setVisibility(View.GONE);
                        }

                        progressBar.setVisibility(View.GONE);
                        requestsAdapter.setData(requestList);
                    } catch (Exception e) {
                        Log.e("MassagesActivity", "Error processing requests", e);
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MassagesActivity", "Firebase request error: " + error.getMessage());
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    // Load data for massages RecyclerView
    private void loadMassagesData() {
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
            DatabaseReference massagesRef = FirebaseDatabase.getInstance().getReference("Massage Requests Activity")
                    .child("Send")
                    .child(user.getUid());

            massagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        List<Map<String, Object>> massagesList = new ArrayList<>();

                        for (DataSnapshot massageSnapshot : snapshot.getChildren()) {
                            Map<String, Object> massageData = (Map<String, Object>) massageSnapshot.getValue();
                            if (massageData != null) {
                                massagesList.add(massageData);
                            }
                        }

                        if (!massagesList.isEmpty()) {
                            NoMassagesLayout.setVisibility(View.GONE);
                        } else {
                            // Only show "No Messages" if both requests and active messages are empty
                            if (requestList.isEmpty()) {
                                NoMassagesLayout.setVisibility(View.VISIBLE);
                            }
                        }

                        progressBar.setVisibility(View.GONE);
                        massagesAdapter.setData(massagesList);
                    } catch (Exception e) {
                        Log.e("MassagesActivity", "Error parsing messages", e);
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MassagesActivity", "Database error: " + error.getMessage());
                    progressBar.setVisibility(View.GONE);
                    
                    if (requestList.isEmpty()) {
                        NoMassagesLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            NoMassagesLayout.setVisibility(View.VISIBLE);
        }
    }
}



