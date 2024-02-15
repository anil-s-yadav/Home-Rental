package com.legendarysoftwares.homerental;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    private MassagesAdapter massageAdapter;
    private ProgressBar progressBar;
    private LinearLayout NoMassagesLayout;
    private RecyclerView massageRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massages);

        massageRecyclerView = findViewById(R.id.massages_recycler_view);
        progressBar = findViewById(R.id.progressBar);
        NoMassagesLayout = findViewById(R.id.no_massages_view);

        Button goBack = findViewById(R.id.massages_btn_goBack);
        goBack.setOnClickListener(v -> {
            Intent intent=new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK
                    |Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });


        massageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        massageAdapter = new MassagesAdapter(MassagesActivity.this);

        massageRecyclerView.setAdapter(massageAdapter);

        loadMassageUsers();
    }

    private void loadMassageUsers() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
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
                    if (userData==null){
                        massageRecyclerView.setVisibility(View.GONE);
                        NoMassagesLayout.setVisibility(View.VISIBLE);
                    }else {
                        massageUsers.add(userData);
                        massageRecyclerView.setVisibility(View.VISIBLE);
                        massageAdapter.setData(massageUsers);
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
}
