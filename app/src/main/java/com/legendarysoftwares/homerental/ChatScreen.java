package com.legendarysoftwares.homerental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
// ChatScreen.java

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatScreen extends AppCompatActivity {

    private EditText typedMassage;
    String  reciverUid,receiverName,SenderUID,PropertyID;
    FirebaseAuth firebaseAuth;
    public  static String senderImg;
    public  static String receiverPhotoUrl;
    String senderRoom, receiverRoom;
    RecyclerView msgRecyclerView;
    ArrayList<ChatScreenModelClass> messagesArrayList;
    ChatScreenAdapter mmessagesAdpter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        TextView acceptRequestTv = findViewById(R.id.tv_accept_request);
        TextView userNameTextView = findViewById(R.id.chat_userName); // It is name of person I am now chatting with
        ShapeableImageView userPhotoImageView = findViewById(R.id.chat_userPhoto); // it is image of person i am chatting with
        typedMassage = findViewById(R.id.editText_typed_massage); // were type the msg edit text view
        ImageView sendMassageBtn = findViewById(R.id.imageView_send_msg); // button to send the massage

// ------------------------------------------------ Setting Toolbar like Layout ______________________________________________________________________________________________________

        // These data come from MassageAdapterIntent
        Intent MassageAdapterIntent = getIntent();
        if (MassageAdapterIntent != null) {
            // These data come from MassageAdapterIntent
            receiverName = MassageAdapterIntent.getStringExtra("user_name");
            receiverPhotoUrl = MassageAdapterIntent.getStringExtra("user_photo");
            reciverUid = MassageAdapterIntent.getStringExtra("userID");
            PropertyID = MassageAdapterIntent.getStringExtra("PropertyID");

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Posted Properties").child(PropertyID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Check if the user exists
                    if (snapshot.exists()) {
                        // Get property information
                        String ownerId = snapshot.child("ownerId").getValue(String.class);
                        Log.d("ownerId = ", ownerId);

                        // Set the received data to the TextView and ImageView
                        if (receiverName != null)
                            userNameTextView.setText(receiverName);

                        if (receiverPhotoUrl != null)
                            Picasso.get().load(receiverPhotoUrl).into(userPhotoImageView);

                        if (!reciverUid.equals(ownerId)) {
                            DatabaseReference propertyOnRentRef = FirebaseDatabase.getInstance().getReference("PropertiesOnRent").child(PropertyID);
                            propertyOnRentRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String rentalStatus = dataSnapshot.child("rentalStatus").getValue(String.class);
                                        Log.d("RentalStatus =", "" + rentalStatus);

                                        if ("Rented".equals(rentalStatus)) {
                                            acceptRequestTv.setText("Rented Successfully!");
                                            // Disable click listener
                                            acceptRequestTv.setOnClickListener(null);
                                        } else {
                                            acceptRequestTv.setText("Approve for Rent");

                                            // Inside onCreate or where you initialize acceptRequestTv
                                            acceptRequestTv.setOnClickListener(v -> {
                                                DatabaseReference propertyRef = FirebaseDatabase.getInstance().getReference("PropertiesOnRent").child(PropertyID);
                                                Map<String, Object> updateData = new HashMap<>();
                                                updateData.put("rentalStatus", "Rented");
                                                updateData.put("ownerId", ownerId);
                                                updateData.put("userId", reciverUid);
                                                updateData.put("PropertyID", PropertyID);

                                                propertyRef.updateChildren(updateData)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Log.d("FirebaseUpdate = ", "Update successful");
                                                            acceptRequestTv.setText("Rented Successfully!");
                                                            // Disable click listener
                                                            acceptRequestTv.setOnClickListener(null);
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.e("FirebaseUpdate = ", "Update failed: " + e.getMessage());
                                                        });
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("FirebaseError", "Error fetching rental status: " + error.getMessage());
                                }
                            });
                        } else {
                            DatabaseReference propertyOnRentRef = FirebaseDatabase.getInstance().getReference("PropertiesOnRent").child(PropertyID);
                            propertyOnRentRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String rentalStatus = dataSnapshot.child("rentalStatus").getValue(String.class);
                                        Log.d("Else RentalStatus =", "" + rentalStatus);

                                        if ("Rented".equals(rentalStatus)) {
                                            acceptRequestTv.setText("Rented Successfully!");
                                        } else {
                                            acceptRequestTv.setText("Panding");
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("FirebaseError", "Error fetching rental status: " + error.getMessage());
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                }
            });
        }



// ------------------------------ Setting RecyclerView to See massages ______________________________________________________________________________________________________

        firebaseAuth = FirebaseAuth.getInstance();
        senderImg = firebaseAuth.getCurrentUser().getPhotoUrl().toString();
        SenderUID = firebaseAuth.getCurrentUser().getUid();
        senderRoom = SenderUID + reciverUid;
        receiverRoom = reciverUid + SenderUID;

        messagesArrayList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        mmessagesAdpter = new ChatScreenAdapter(ChatScreen.this, messagesArrayList);
        msgRecyclerView.setAdapter(mmessagesAdpter);


        DatabaseReference chatreference = FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom)
                .child("messages");
        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatScreenModelClass messages = dataSnapshot.getValue(ChatScreenModelClass.class);
                    messagesArrayList.add(messages);
                }
                mmessagesAdpter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendMassageBtn.setOnClickListener(v -> sendMassage());


    }  // On create ends


    private void sendMassage() {
        String message = typedMassage.getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(ChatScreen.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
            return;
        }
        typedMassage.setText("");
        Date date = new Date();
        ChatScreenModelClass ChatsModel = new ChatScreenModelClass(message, SenderUID, date.getTime());

        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .push().setValue(ChatsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseDatabase.getInstance().getReference().child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .push().setValue(ChatsModel);
                    }
                });
    }
}  // Class end

