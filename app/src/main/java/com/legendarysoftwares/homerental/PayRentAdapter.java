package com.legendarysoftwares.homerental;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PayRentAdapter extends RecyclerView.Adapter<PayRentAdapter.PayRentViewHolder> {

    private List<Map<String, Object>> payRentList;
    private static Context context;

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
        private Button payBtn, paymentHistoryBtn;
        private static final String KEY_ID = "rzp_test_SULomt8owpdDRC"; //  Razorpay Key ID

        public PayRentViewHolder(@NonNull View itemView) {
            super(itemView);

            //propertyIdTextView = itemView.findViewById(R.id.text_view_property_id);
            propertyNameTextView = itemView.findViewById(R.id.text_view_property_name);
            propertyAddressTextView = itemView.findViewById(R.id.text_view_property_address);
            propertyOwnerNameTextView = itemView.findViewById(R.id.text_view_property_owner);
           // paymentStatusTextView = itemView.findViewById(R.id.text_view_payment_status);
            paymentAmountTextView = itemView.findViewById(R.id.text_view_property_price);
            propertyImg = itemView.findViewById(R.id.property_image);
            payBtn = itemView.findViewById(R.id.btn_pay_rent);
            paymentHistoryBtn = itemView.findViewById(R.id.btn_payment_history);
        }

        public void bind(Map<String, Object> payRentData) {
            //propertyIdTextView.setText("Property ID: " + payRentData.get("propertyId"));
            String userId = (String) payRentData.get("userID");
            String ownerId = (String) payRentData.get("ownerId");
            String PropertyID = (String) payRentData.get("PropertyID");

            showPropertyData(PropertyID);

            paymentHistoryBtn.setOnClickListener(View->{
                Intent i=new Intent(context, PaymentHistoryActivity.class);
                context.startActivity(i);
            });


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
                        paymentAmountTextView.setText(propertyPrice);
                        Picasso.get().load(PropertyImg).into(propertyImg);

                        String price = convertPrice(propertyPrice);
                        payBtn.setText("pay " + price);
                        Log.d("Modified Price",price);
                        Log.d("real Price",propertyPrice);

                        payBtn.setOnClickListener(View ->{
                            payNow(price);
                        });

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

        private void payNow(String price) {
            double payPrice = Double.parseDouble(price);
            Checkout checkout = new Checkout();
            checkout.setKeyID(KEY_ID);
            try {
                JSONObject options = new JSONObject();

                options.put("name", "Home Rentals");
                options.put("description", "Reference No. #123456");
                options.put("image","https://firebasestorage.googleapis.com/v0/b/home-rental-7cc1e.appspot.com/o/app_logi.png?alt=media&token=bbd2e020-eada-4d0c-ac62-41bea7f96321");
                options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
                options.put("theme.color", "#5E40E0");
                options.put("currency", "INR");
                options.put("amount", payPrice*100);//pass amount in currency subunits
                options.put("prefill.email", "payment@home-rentals.com");
                options.put("prefill.contact","9892986314");
                JSONObject retryObj = new JSONObject();
                retryObj.put("enabled", true);
                retryObj.put("max_count", 2);
                options.put("retry", retryObj);

                checkout.open((Activity) context, options);

            } catch(Exception e) {
                Log.e("MY_Error", "Error in starting Razorpay Checkout", e);
            }
        }




        public String convertPrice(String price) {
            // Split the price into the numeric part and the suffix
            String[] parts = price.split(" ");
            // Check if the price has a suffix (e.g., Cr, k)
            if (parts.length == 2) {
                double numericPart = Double.parseDouble(parts[0]);
                String suffix = parts[1];

                // Convert the numeric part based on the suffix
                switch (suffix.toLowerCase()) {
                    case "cr":
                        numericPart *= 10000000; // 1 Cr = 10,000,000
                        break;
                    case "k":
                        numericPart *= 1000; // 1 k = 1,000
                        break;
                }
                return String.valueOf((int) numericPart); // Return as integer without commas
            } else {
                return price;
            }
        }



    }//class PayrentViewHolder ends
}// class payrentAdapter ends


