package com.legendarysoftwares.homerental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatScreen extends AppCompatActivity {

    private EditText typedMassage;
    String  reciverUid,receiverName,SenderUID,propertyID;
    FirebaseDatabase database;
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

        /*if (getIntent().hasExtra("sourceActivity")) {
            String sourceActivity = getIntent().getStringExtra("sourceActivity");
            // Now I know from which activity the intent was launched
            // I can use the sourceActivity variable as needed
            TextView UserName = findViewById(R.id.tv_user_name);
            TextView PropertyName = findViewById(R.id.tv_property_name);
            Button backBtn = findViewById(R.id.alert_back_btn);
            Button AcceptBtn = findViewById(R.id.alert_accept_btn);

            Intent intent = getIntent();
            String massageSenderName = intent.getStringExtra("userName");
            String SelectedPropertyName = intent.getStringExtra("propertyName");


            UserName.setText("Request by, " + massageSenderName);
            PropertyName.setText(massageSenderName + " Is requested for Inquaring about "+ SelectedPropertyName +
                    "property. to continue discussion please accept this massage by clicking accpet butto.");

            LinearLayout layoutTyping = findViewById(R.id.linearLayout_typing);
            layoutTyping.setVisibility(View.GONE);
            acceptRequestTv.setVisibility(View.GONE);


            userPhotoImageView.setVisibility(View.GONE);
            userNameTextView.setText("New Request");
        }*/


        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String user = CurrentUser.getUid();

            // These data come from MassageAdapterintent
            Intent MassageAdapterintent = getIntent();

            if (MassageAdapterintent != null) {
                // These data come from MassageAdapterintent
                receiverName = MassageAdapterintent.getStringExtra("user_name");
                receiverPhotoUrl = MassageAdapterintent.getStringExtra("user_photo");
                reciverUid = MassageAdapterintent.getStringExtra("userID");
                propertyID = MassageAdapterintent.getStringExtra("propertyID");

                // Set the received data to the TextView and ImageView
                if (receiverName != null) {
                    userNameTextView.setText(receiverName);
                }

                if (receiverPhotoUrl != null) {
                    Picasso.get().load(receiverPhotoUrl).into(userPhotoImageView);
                }
                if (!reciverUid.equals(user)){
                    acceptRequestTv.setText("Approval Panding");
                }
            }

            database = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();

            messagesArrayList = new ArrayList<>();

            msgRecyclerView = findViewById(R.id.msg_recycler_view);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setStackFromEnd(true);

            msgRecyclerView.setLayoutManager(linearLayoutManager);

            mmessagesAdpter = new ChatScreenAdapter(ChatScreen.this, messagesArrayList);
            msgRecyclerView.setAdapter(mmessagesAdpter);

            senderImg = firebaseAuth.getCurrentUser().getPhotoUrl().toString();
            SenderUID = firebaseAuth.getCurrentUser().getUid();
            senderRoom = SenderUID + reciverUid;
            receiverRoom = reciverUid + SenderUID;


            DatabaseReference chatreference = database.getReference().child("chats").child(senderRoom)
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


            sendMassageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = typedMassage.getText().toString();
                    if (message.isEmpty()) {
                        Toast.makeText(ChatScreen.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    typedMassage.setText("");
                    Date date = new Date();
                    ChatScreenModelClass ChatsModel = new ChatScreenModelClass(message, SenderUID, date.getTime());

                    database = FirebaseDatabase.getInstance();
                    database.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .push().setValue(ChatsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    database.getReference().child("chats")
                                            .child(receiverRoom)
                                            .child("messages")
                                            .push().setValue(ChatsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                }
                            });
                }
            });


    }  // On create ends
}  // Class end
