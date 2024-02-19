package com.legendarysoftwares.homerental;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class MassagesRequestsAdapter extends RecyclerView.Adapter<MassagesRequestsAdapter.RequestViewHolder> {

    private List<Map<String, Object>> requestsData;
    private static Context context;

    public MassagesRequestsAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Map<String, Object>> requestsData) {
        this.requestsData = requestsData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item_each_requests, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        if (requestsData != null && position < requestsData.size()) {
            Map<String, Object> requestData = requestsData.get(position);
            holder.bind(requestData);
        }
    }

    @Override
    public int getItemCount() {
        return requestsData != null ? requestsData.size() : 0;
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {

        private TextView userNameTextView;
        private Button viewDetailsBtn;
        private ShapeableImageView userPhoto;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.requests_activity_sender_name);
            userPhoto = itemView.findViewById(R.id.requests_activity_sender_dp);
            viewDetailsBtn = itemView.findViewById(R.id.btn_view_sender_details);
        }

        public void bind(Map<String, Object> requestData) {
            String name = (String) requestData.get("name");
            String photoUrl = (String) requestData.get("photo");
            String userID = (String) requestData.get("userID");
            String propertyID = (String) requestData.get("propertyID");

            userNameTextView.setText(name);
            Picasso.get().load(photoUrl).into(userPhoto);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle click for requests item if needed
                }
            });
        }
    }
}
