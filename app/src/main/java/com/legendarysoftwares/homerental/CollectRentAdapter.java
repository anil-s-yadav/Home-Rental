package com.legendarysoftwares.homerental;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionBarPolicy;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectRentAdapter extends RecyclerView.Adapter<CollectRentAdapter.CollectRentViewHolder>{

    private List<Map<String, Object>> collectRentList;
    private Context context;

    public CollectRentAdapter(Context context) {
        this.collectRentList = new ArrayList<>();
        this.context = context;
    }


    public void setData(List<Map<String, Object>> collectRentList) {
        this.collectRentList = collectRentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CollectRentAdapter.CollectRentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item_payrent_rv, parent, false);
        return new CollectRentAdapter.CollectRentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectRentAdapter.CollectRentViewHolder holder, int position) {
        Map<String, Object> collectRentData = collectRentList.get(position);
        holder.bind(collectRentData);
        // Check the activity type and hide the "Pay Rent" button if it's CollectRentActivity
        if (context instanceof CollectRentAcivity) {
            holder.paymentBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return collectRentList.size();
    }

    static class CollectRentViewHolder extends RecyclerView.ViewHolder {

        // private TextView propertyIdTextView;
        private TextView propertyNameTextView,propertyAddressTextView ;
        private TextView propertyOwnerNameTextView;
        private TextView paymentStatusTextView;
        private TextView paymentAmountTextView;
        private ShapeableImageView propertyImg;
        private Button paymentBtn,paymentHistoryBtn;

        public CollectRentViewHolder(@NonNull View itemView) {
            super(itemView);

            //propertyIdTextView = itemView.findViewById(R.id.text_view_property_id);
            propertyNameTextView = itemView.findViewById(R.id.text_view_property_name);
            propertyAddressTextView = itemView.findViewById(R.id.text_view_property_address);
            propertyOwnerNameTextView = itemView.findViewById(R.id.text_view_property_owner);
            // paymentStatusTextView = itemView.findViewById(R.id.text_view_payment_status);
            paymentAmountTextView = itemView.findViewById(R.id.text_view_property_price);
            propertyImg = itemView.findViewById(R.id.property_image);
            paymentBtn = itemView.findViewById(R.id.btn_pay_rent);
            paymentHistoryBtn = itemView.findViewById(R.id.btn_payment_history);

        }

        public void bind(Map<String, Object> collectRentData) {
            String userId = (String) collectRentData.get("userID");
            String ownerId = (String) collectRentData.get("ownerId");
            String PropertyID = (String) collectRentData.get("PropertyID");

            String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Log.d("owner user = ",""+ownerId +" "+user);
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
                        String propertyAddress = snapshot.child("postTitle").getValue(String.class);
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

}
