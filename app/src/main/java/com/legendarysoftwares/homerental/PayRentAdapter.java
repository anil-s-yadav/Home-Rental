package com.legendarysoftwares.homerental;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PayRentAdapter extends RecyclerView.Adapter<PayRentAdapter.PayRentViewHolder> {

    private List<Map<String, Object>> payRentList;
    private Context context;

    public PayRentAdapter(Context context) {
        this.payRentList = new ArrayList<>();
        this.context = context;
    }


    public void setData(List<Map<String, Object>> payRentList) {
        this.payRentList = payRentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PayRentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item_payrent_rv, parent, false);
        return new PayRentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PayRentViewHolder holder, int position) {
        Map<String, Object> payRentData = payRentList.get(position);
        holder.bind(payRentData);
    }

    @Override
    public int getItemCount() {
        return payRentList.size();
    }

    static class PayRentViewHolder extends RecyclerView.ViewHolder {

       // private TextView propertyIdTextView;
        private TextView propertyNameTextView,propertyAddressTextView ;
        private TextView propertyOwnerNameTextView;
        private TextView paymentStatusTextView;
        private TextView paymentAmountTextView;
        private ShapeableImageView propertyImg;

        public PayRentViewHolder(@NonNull View itemView) {
            super(itemView);

            //propertyIdTextView = itemView.findViewById(R.id.text_view_property_id);
            propertyNameTextView = itemView.findViewById(R.id.text_view_property_name);
            propertyAddressTextView = itemView.findViewById(R.id.text_view_property_address);
            propertyOwnerNameTextView = itemView.findViewById(R.id.text_view_property_owner);
           // paymentStatusTextView = itemView.findViewById(R.id.text_view_payment_status);
            paymentAmountTextView = itemView.findViewById(R.id.text_view_property_price);
            propertyImg = itemView.findViewById(R.id.property_image);
        }

        public void bind(Map<String, Object> payRentData) {
            //propertyIdTextView.setText("Property ID: " + payRentData.get("propertyId"));
            String userId = (String) payRentData.get("userID");
            String ownerId = (String) payRentData.get("ownerId");
            String PropertyID = (String) payRentData.get("PropertyID");

            showPropertyData(PropertyID);



        } // void bind(..) ends;

        private void showPropertyData(String propertyID) {
            DatabaseReference propertyRef = FirebaseDatabase.getInstance().getReference("Posted Properties").child(propertyID);
            propertyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Check if the user exists
                    if (snapshot.exists()) {
                        // Get user information
                        String propertyName = snapshot.child("postTitle").getValue(String.class);
                        String propertyAddress = snapshot.child("postAddress").getValue(String.class);
                        String propertyPrice = snapshot.child("postPrice").getValue(String.class);
                        String OwnerName = snapshot.child("ownerName").getValue(String.class);
                        String PropertyImg = snapshot.child("postImageUrl1").getValue(String.class);

                        // Set user information to the views
                        propertyOwnerNameTextView.setText(OwnerName);
                        propertyNameTextView.setText(propertyName);
                        propertyAddressTextView.setText(propertyAddress);
                        Picasso.get().load(PropertyImg).into(propertyImg);
                    } else {
                        Log.d("User not found", "User with UID " + propertyID);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                }
            });

        }





    }//class PayrentViewHolder ends
}// class payrentAdapter ends


