package com.legendarysoftwares.homerental.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.legendarysoftwares.homerental.HomeAdapter;
import com.legendarysoftwares.homerental.MassagesActivity;
import com.legendarysoftwares.homerental.PostPropertyModel;
import com.legendarysoftwares.homerental.R;

public class Home extends Fragment {
    public Home() {}

    private RecyclerView homeRecyclerView;
    private HomeAdapter homeAdapter; // Declare the adapter as a field

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        homeRecyclerView = view.findViewById(R.id.home_recycler_view);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        ImageButton openMassageActivity = view.findViewById(R.id.massages);

        ShapeableImageView ViewFlipperImage1 = view.findViewById(R.id.Images_In_Image_Slider1);
        ShapeableImageView ViewFlipperImage2 = view.findViewById(R.id.Images_In_Image_Slider2);
        ShapeableImageView ViewFlipperImage3 = view.findViewById(R.id.Images_In_Image_Slider3);
        ShapeableImageView ViewFlipperImage4 = view.findViewById(R.id.Images_In_Image_Slider4);
        ShapeableImageView ViewFlipperImage5 = view.findViewById(R.id.Images_In_Image_Slider5);
        ShapeableImageView ViewFlipperImage6 = view.findViewById(R.id.Images_In_Image_Slider6);

        Uri TT = Uri.parse("https://www.google.com/imgres?imgurl=https%3A%2F%2Fcdn.pixabay.com%2Fphoto%2F2015%2F04%2F23%2F22%2F00%2Ftree-736885_1280.jpg&tbnid=aVgXecnmQ_f1MM&vet=12ahUKEwj6iqyMhbSEAxUvbmwGHX-qDCkQMygCegQIARBN..i&imgrefurl=https%3A%2F%2Fpixabay.com%2Fimages%2Fsearch%2Fsea%2F&docid=QG4MQQA3E95exM&w=1280&h=797&q=image&ved=2ahUKEwj6iqyMhbSEAxUvbmwGHX-qDCkQMygCegQIARBN");

        //ViewFlipperImage1.setImageURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/home-rental-7cc1e.appspot.com/o/Images_In_Image_Slider%2Fimages%20(15).jpeg?alt=media&token=9dd7941e-bffa-45ea-91eb-23039ee9dd4c"));
       // ViewFlipperImage2.setImageURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/home-rental-7cc1e.appspot.com/o/Images_In_Image_Slider%2Fimages%20(14).jpeg?alt=media&token=0ff732f4-60bb-4faf-af8b-7de51e0073dd"));
      //  ViewFlipperImage3.setImageURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/home-rental-7cc1e.appspot.com/o/Images_In_Image_Slider%2Fimages%20(16).jpeg?alt=media&token=e0dcc598-9e99-4d35-a51a-ed322fe8fd1d"));
       // ViewFlipperImage4.setImageURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/home-rental-7cc1e.appspot.com/o/Images_In_Image_Slider%2Fimages%20(18).jpeg?alt=media&token=40f4878a-20fa-4162-946c-84d505ca0c64"));
       // ViewFlipperImage5.setImageURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/home-rental-7cc1e.appspot.com/o/Images_In_Image_Slider%2Fimages%20(22).jpeg?alt=media&token=96a33421-5d3a-4912-bfec-98de6f277037"));
        //ViewFlipperImage6.setImageURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/home-rental-7cc1e.appspot.com/o/Images_In_Image_Slider%2Fimages%20(17).jpeg?alt=media&token=48603187-c984-44f1-94eb-16ed8f4aa88f"));

       // ViewFlipperImage6.setImageURI(TT);

        openMassageActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), MassagesActivity.class);
                startActivity(intent);
            }
        });




// Set RecyclerView initially invisible
        homeRecyclerView.setVisibility(View.GONE);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        try {
            DatabaseReference DBReference = FirebaseDatabase.getInstance().getReference("Posted Properties");
            FirebaseRecyclerOptions<PostPropertyModel> options = new FirebaseRecyclerOptions.Builder<PostPropertyModel>()
                    .setQuery(DBReference, PostPropertyModel.class).build();

            homeAdapter = new HomeAdapter(options,requireContext(),getCurrentUserId());
            // Show ProgressBar while loading, hide it when data is loaded
            homeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    // Data loaded, hide ProgressBar and show RecyclerView
                    progressBar.setVisibility(View.GONE);
                    homeRecyclerView.setVisibility(View.VISIBLE);
                }
            });
            homeRecyclerView.setAdapter(homeAdapter);

            // Start listening for changes in the data
            homeAdapter.startListening();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return view;
    }

    private FirebaseUser getCurrentUserId() {
         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user;
    }
    // Add the following lifecycle methods to start and stop listening when the fragment is started and stopped

    @Override
    public void onStart() {
        super.onStart();
        if (homeAdapter != null) {
            homeAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (homeAdapter != null) {
            homeAdapter.stopListening();
        }
    }

}

