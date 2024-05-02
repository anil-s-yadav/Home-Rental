package com.legendarysoftwares.homerental;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class PaymentHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        ImageView back = findViewById(R.id.backBtn_payHistory);
        back.setOnClickListener((View)->{
            onBackPressed();
        });
    }
}