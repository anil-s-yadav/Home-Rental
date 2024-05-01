package com.legendarysoftwares.homerental;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class propertyDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);

        ImageView back = findViewById(R.id.backBtn_propertyDetails);
        back.setOnClickListener((View)->{
            onBackPressed();
        });
    }
}