package com.legendarysoftwares.homerental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditPropertyActivity extends AppCompatActivity {

    private String propertyId;
    private TextInputEditText etName, etLocation, etPrice, etRera;
    private AutoCompleteTextView spinnerType;
    private MaterialButton btnUpdate;
    private ProgressBar progressBar;
    private DatabaseReference propertyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_property);

        propertyId = getIntent().getStringExtra("propertyId");
        if (propertyId == null) {
            finish();
            return;
        }

        initViews();
        setupSpinner();
        loadPropertyData();

        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());
        btnUpdate.setOnClickListener(v -> updateProperty());
    }

    private void initViews() {
        etName = findViewById(R.id.edit_post_name);
        etLocation = findViewById(R.id.edit_post_location);
        etPrice = findViewById(R.id.edit_post_price);
        etRera = findViewById(R.id.edit_rera_id);
        spinnerType = findViewById(R.id.edit_spinner_post_type);
        btnUpdate = findViewById(R.id.btn_update_property);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupSpinner() {
        String[] categories = {"House", "Flat", "Plot", "Office", "Shop", "PG", "Villa", "Bungalow", "Outlet", "Factory", "Cafe", "Hostel", "Warehouse", "Cottage", "Farmhouse"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        spinnerType.setAdapter(adapter);
    }

    private void loadPropertyData() {
        progressBar.setVisibility(View.VISIBLE);
        propertyRef = FirebaseDatabase.getInstance().getReference("Posted Properties").child(propertyId);
        propertyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostPropertyModel model = snapshot.getValue(PostPropertyModel.class);
                if (model != null) {
                    etName.setText(model.getPostTitle());
                    etLocation.setText(model.getPostAddress());
                    etPrice.setText(model.getPostPrice());
                    etRera.setText(model.getReraID());
                    spinnerType.setText(model.getType(), false);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateProperty() {
        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String type = spinnerType.getText().toString().trim();
        String rera = etRera.getText().toString().trim();

        if (name.isEmpty() || location.isEmpty() || price.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> updates = new HashMap<>();
        updates.put("postTitle", name);
        updates.put("postAddress", location);
        updates.put("postPrice", price);
        updates.put("Type", type);
        updates.put("reraID", rera);

        propertyRef.updateChildren(updates).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toast.makeText(this, "Property updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}