package com.legendarysoftwares.homerental;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class MassagesAdapter extends RecyclerView.Adapter<MassagesAdapter.MassageViewHolder> {

    private List<Map<String, Object>> massageUsers;
    private static Context context;

    public MassagesAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Map<String, Object>> massageUsers) {
        this.massageUsers = massageUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MassageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item_each_massages, parent, false);
        return new MassageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MassageViewHolder holder, int position) {
        if (massageUsers != null && position < massageUsers.size()) {
            Map<String, Object> userData = massageUsers.get(position);
            holder.bind(userData);
        }
    }

    @Override
    public int getItemCount() {
        return massageUsers != null ? massageUsers.size() : 0;
    }

    static class MassageViewHolder extends RecyclerView.ViewHolder {

        private TextView userNameTextView;
        private ShapeableImageView userPhoto;

        public MassageViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.requests_activity_sender_name);
            userPhoto = itemView.findViewById(R.id.requests_activity_sender_dp);
        }

        public void bind(Map<String, Object> userData) {
            String userID = (String) userData.get("userID");
            String propertyID = (String) userData.get("PropertyID");


            assert userID != null;
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(userID);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Check if the user exists
                    if (snapshot.exists()) {
                        // Get user information
                        String senderDisplayName = snapshot.child("name").getValue(String.class);
                        String senderPhotoUrl = snapshot.child("photo").getValue(String.class);

                        // Set user information to the views
                        userNameTextView.setText(senderDisplayName);
                        Picasso.get().load(senderPhotoUrl).into(userPhoto);
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ChatScreen.class);
                                intent.putExtra("user_name", senderDisplayName);
                                intent.putExtra("user_photo", senderPhotoUrl);
                                intent.putExtra("userID", userID);
                                intent.putExtra("propertyID", propertyID);
                                context.startActivity(intent);
                            }
                        });

                    } else {
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                }
            });



        }
    }
}

