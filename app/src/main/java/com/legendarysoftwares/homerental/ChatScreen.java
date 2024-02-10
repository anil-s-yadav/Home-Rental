package com.legendarysoftwares.homerental;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
// ChatScreen.java

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ChatScreen extends AppCompatActivity {

    private TextView userNameTextView;
    private ImageView userPhotoImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        userNameTextView = findViewById(R.id.chat_userName);
        userPhotoImageView = findViewById(R.id.chat_userPhoto);

        // Retrieve data from the Intent
        Intent intent = getIntent();
        if (intent != null) {
            String userName = intent.getStringExtra("user_name");
            String userPhotoUrl = intent.getStringExtra("user_photo");

            // Set the received data to the TextView and ImageView
            if (userName != null) {
                userNameTextView.setText(userName);
            }

            if (userPhotoUrl != null) {
                Picasso.get().load(userPhotoUrl).into(userPhotoImageView);
            }
        }
    }
}
