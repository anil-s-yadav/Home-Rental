package com.legendarysoftwares.homerental;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
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
        private FirebaseUser user;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.requests_activity_sender_name);
            userPhoto = itemView.findViewById(R.id.requests_activity_sender_dp);
            viewDetailsBtn = itemView.findViewById(R.id.btn_view_sender_details);
        }

        public void bind(Map<String, Object> requestData) {
            String SenderUserID = (String) requestData.get("userID"); // who send me request I have their userID
            String PropertyID = (String) requestData.get("PropertyID");
            //I got the request That means I am the owner,
            // thats why, user = ownerID

                // Load user information based on senderUID
                loadSenderUserInfo(SenderUserID);
                user = FirebaseAuth.getInstance().getCurrentUser();

                viewDetailsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomRequestDialog(PropertyID,SenderUserID);
                    }
                });
        }
//End of OnBind


        private void loadSenderUserInfo(String senderUID) {
            // Retrieve user information based on senderUID
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(senderUID);

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
                    } else {
                        Log.d("User not found", "User with UID " + senderUID + " not found.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                }
            });
        }



        private void CustomRequestDialog(String PropertyID,String SenderUserID) {
            Log.d("Dialog PropertyID = ", PropertyID.toString());

            View alertCustomDialog = LayoutInflater.from(context).inflate(R.layout.dialog_accept_request, null);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            alertDialog.setView(alertCustomDialog);

            TextView TVpropertyId = alertCustomDialog.findViewById(R.id.property_name_and_id);
            Button cancelButton = alertCustomDialog.findViewById(R.id.cancel_btn);
            Button RequestDeleteButton = alertCustomDialog.findViewById(R.id.request_delete_btn);
            Button RequestAcceptButton = alertCustomDialog.findViewById(R.id.request_accept_btn);

            TVpropertyId.setText(PropertyID);
            Log.d("TVpropertyId PropertyID = ", String.valueOf(TVpropertyId));

            final AlertDialog dialog = alertDialog.create();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            cancelButton.setOnClickListener(v -> {
                dialog.cancel();
            });

            RequestAcceptButton.setOnClickListener(v -> {

                // flow will be paytm -> anil -> anil's data.
                //I made these function assuming Anil send request to paytm, now Anil is user and Paytm is owner.
                // in void bind(..) method, I got Anil's ID and propertyID. transfer here as...
                // anil'sId = SenderUserID , PropertyID = PropertyID, and ownerId = userID.   (because owner can get requested for rend only)
                DatabaseReference userMassageRef = FirebaseDatabase.getInstance().getReference("Massage Requests Activity")
                        .child("Send").child(user.getUid());

                Map<String, Object> userMassageData = new HashMap<>();
                userMassageData.put("userID", SenderUserID);
                userMassageData.put("PropertyID",PropertyID);

                userMassageRef.child(SenderUserID).setValue(userMassageData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DatabaseReference userDeleteMassage = FirebaseDatabase.getInstance().getReference("Massage Requests Activity")
                                .child("Receive").child(user.getUid()).child(SenderUserID);
                        userDeleteMassage.removeValue();
                        Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            });

            RequestDeleteButton.setOnClickListener(v -> {
                DatabaseReference userDeleteMassage = FirebaseDatabase.getInstance().getReference("Massage Requests Activity")
                        .child("Receive").child(user.getUid()).child(SenderUserID);
                userDeleteMassage.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                });
            });
        }

    } //End of     static class RequestViewHolder extends RecyclerView.ViewHolder {



}