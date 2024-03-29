package com.legendarysoftwares.homerental.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.legendarysoftwares.homerental.CollectRentAcivity;
import com.legendarysoftwares.homerental.LoginBottomSheetHelper;
import com.legendarysoftwares.homerental.MainActivity;
import com.legendarysoftwares.homerental.MyPostsOnProfile;
import com.legendarysoftwares.homerental.PayRentActivity;
import com.legendarysoftwares.homerental.R;
import com.legendarysoftwares.homerental.UpdateProfileActivity;
import com.squareup.picasso.Picasso;

public class Profile extends Fragment {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ConstraintLayout openProfile, collectRent, payRent;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        TextView updateProfile = view.findViewById(R.id.profile_update_profile);
        TextView updateEmail = view.findViewById(R.id.profile_update_email);
        TextView changePassword = view.findViewById(R.id.profile_update_password);
        TextView deleteUser = view.findViewById(R.id.profile_delete_profile);
        TextView logout = view.findViewById(R.id.profile_logout);
        openProfile = view.findViewById(R.id.layout_open_profile);
        payRent = view.findViewById(R.id.layout_pay_rent);
        collectRent = view.findViewById(R.id.layout_collect_rent);


        TextView textViewHelloName = view.findViewById(R.id.textView_hello_name);
        TextView textViewEmail = view.findViewById(R.id.textView_show_email);
        ShapeableImageView userProfilePic = view.findViewById(R.id.imageView_profile_dp);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        LoginBottomSheetHelper loginBottomSheetHelper = new LoginBottomSheetHelper(getContext());


        if (user==null){
            logout.setText("Login");
            logout.setTextColor(getResources().getColor(R.color.Green200));
            logout.setBackgroundColor(getResources().getColor(R.color.Green150));

            loginBottomSheetHelper.showLoginBottomSheet();
        }else {
            user.reload();
            textViewHelloName.setText(user.getDisplayName());
            textViewEmail.setText(user.getEmail());
            Picasso.get().load(user.getPhotoUrl()).into(userProfilePic);
            if(!user.isEmailVerified()) {
                showAlertDialog();
            }
        }



        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), MyPostsOnProfile.class);
                startActivity(intent);
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),UpdateProfileActivity.class);
                startActivity(intent);
               // ((Activity) getActivity()).overridePendingTransition(0, 0); // No animation while transaction
            }
        });
        updateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginBottomSheetHelper.isLoggedIn()){
                    auth.signOut();
                    Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(),MainActivity.class);
                    startActivity(intent);
                    //Clear the stack to prevent the user to coming back to UseProfile
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }else {
                    loginBottomSheetHelper.showLoginBottomSheet();
                }
            }
        });

        openProfile.setOnClickListener(v -> {
                Intent intent=new Intent(getContext(), MyPostsOnProfile.class);
                startActivity(intent);
        });

        payRent.setOnClickListener(v -> {
            Intent intent=new Intent(getContext(), PayRentActivity.class);
            startActivity(intent);
        });

        collectRent.setOnClickListener(v -> {
            Intent intent=new Intent(getContext(), CollectRentAcivity.class);
            startActivity(intent);
        });


        return view;
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email to continue.");

        //open email app
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); //to open email in new window.
                startActivity(intent);
                user.reload();

            }
        }).setNegativeButton("Re-Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.sendEmailVerification();
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //to open email in new window.
                startActivity(intent);
            }
        });
        AlertDialog alertDialog= builder.create();
        //alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (user!=null) {
            user.reload();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        super.onResume();
        if (user!=null) {
            user.reload();
        }
    }
}
