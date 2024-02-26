package com.legendarysoftwares.homerental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectRentAcivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_rent_acivity);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        RecyclerView payRentRecyclerView = findViewById(R.id.collect_rent_recyclerView);

        payRentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CollectRentAdapter collectRentAdapter=new CollectRentAdapter(this);
        payRentRecyclerView.setAdapter(collectRentAdapter);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
            DatabaseReference propertyCollectRentsRef = FirebaseDatabase.getInstance().getReference("PropertiesOnRent");
            Query query = propertyCollectRentsRef.orderByChild("ownerId").equalTo(user.getUid());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Map<String, Object>> collectRentList = new ArrayList<>();

                    // Iterate through the dataSnapshot to retrieve Map<String, Object> objects
                    for (DataSnapshot propertySnapshot : snapshot.getChildren()) {
                        Map<String, Object> collectRentData = (Map<String, Object>) propertySnapshot.getValue();
                        if (collectRentData != null) {
                            collectRentList.add(collectRentData);
                            Log.d("owner user = ",""+propertyCollectRentsRef.orderByChild("ownerId")+user.getUid());

                        }
                    }

                    // Update the adapter with the new data
                    collectRentAdapter.setData(collectRentList);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                }
            });

        }

    }

}