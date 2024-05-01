package com.legendarysoftwares.homerental;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        ImageView back = findViewById(R.id.BackBtn_PandP);
        back.setOnClickListener((View)->{
            onBackPressed();
        });
    }
}