package com.legendarysoftwares.homerental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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

import com.razorpay.Account;

public class PayRentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_rent);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        RecyclerView payRentRecyclerView = findViewById(R.id.pay_rent_recyclerView);

        payRentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        PayRentAdapter payRentAdapter = new PayRentAdapter(this);
        payRentRecyclerView.setAdapter(payRentAdapter);

        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);

            DatabaseReference propertyToPayRentsRef = FirebaseDatabase.getInstance().getReference("PropertiesOnRent");
            Query query = propertyToPayRentsRef.orderByChild("userId").equalTo(user);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Map<String, Object>> payRentList = new ArrayList<>();

                    // Iterate through the dataSnapshot to retrieve Map<String, Object> objects
                    for (DataSnapshot propertySnapshot : snapshot.getChildren()) {
                        Map<String, Object> payRentData = (Map<String, Object>) propertySnapshot.getValue();
                        if (payRentData != null) {
                            payRentList.add(payRentData);
                        }
                    }

                    // Update the adapter with the new data
                    payRentAdapter.setData(payRentList);
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
