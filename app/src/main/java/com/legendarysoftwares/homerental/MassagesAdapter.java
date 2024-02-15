package com.legendarysoftwares.homerental;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
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
            userNameTextView = itemView.findViewById(R.id.massage_activity_sender_name);
            userPhoto = itemView.findViewById(R.id.massage_activity_sender_dp);
        }

        public void bind(Map<String, Object> userData) {
            String name = (String) userData.get("name");
            String photoUrl = (String) userData.get("photo");
            String userID = (String) userData.get("userID");
            // Assuming "name" is the key for the user's name in the map
            userNameTextView.setText(name);
            Picasso.get().load(photoUrl).into(userPhoto);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatScreen.class);
                    intent.putExtra("user_name", name);
                    intent.putExtra("user_photo", photoUrl);
                    intent.putExtra("userID", userID);
                    context.startActivity(intent);
                }
            });
        }

    }
}
