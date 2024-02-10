package com.legendarysoftwares.homerental;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.List;
import java.util.Map;

public class MassagesActivity extends AppCompatActivity {

    private MassageActivityAdapter massageAdapter;
    private ProgressBar progressBar;
    private LinearLayout NoMassagesLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massages);

        RecyclerView massageRecyclerView = findViewById(R.id.massages_recycler_view);
        progressBar = findViewById(R.id.progressBar);
        NoMassagesLayout = findViewById(R.id.no_massages_view);

        massageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        massageAdapter = new MassageActivityAdapter(MassagesActivity.this);
        massageRecyclerView.setAdapter(massageAdapter);

        NoMassagesLayout.setVisibility(View.GONE);
        loadMassageUsers();
    }

    private void loadMassageUsers() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            NoMassagesLayout.setVisibility(View.VISIBLE);
            return;
        }

        DatabaseReference massageActivityRef = FirebaseDatabase.getInstance()
                .getReference("Massage Activity")
                .child(currentUser.getUid());

        massageActivityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Map<String, Object>> massageUsers = new ArrayList<>();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Map<String, Object> userData = (Map<String, Object>) userSnapshot.getValue();
                    massageUsers.add(userData);
                }

                massageAdapter.setData(massageUsers);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
