package com.legendarysoftwares.homerental.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.legendarysoftwares.homerental.LoginBottomSheetHelper;
import com.legendarysoftwares.homerental.MainActivity;
import com.legendarysoftwares.homerental.MyPostsOnProfile;
import com.legendarysoftwares.homerental.R;
import com.legendarysoftwares.homerental.ReadWriteUserDetailsModel;
import com.legendarysoftwares.homerental.UpdateProfileActivity;
import com.legendarysoftwares.homerental.UploadProfilePicActivity;
import com.squareup.picasso.Picasso;

public class Profile extends Fragment {

    private TextView textViewHelloName, textViewEmail;
    private TextView updateProfile,updateEmail, changePassword,deleteUser,logout;
    private String fullName,email,doB,gender,mobile;
    private ShapeableImageView uploadPic;
    private ImageView openProfile;
    private FirebaseAuth authProfile;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Profile() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        updateProfile=view.findViewById(R.id.profile_update_profile);
        updateEmail=view.findViewById(R.id.profile_update_email);
        changePassword=view.findViewById(R.id.profile_update_password);
        deleteUser=view.findViewById(R.id.profile_delete_profile);
        logout=view.findViewById(R.id.profile_logout);
        openProfile=view.findViewById(R.id.imageView_open_profile);

        LoginBottomSheetHelper loginHelper = new LoginBottomSheetHelper(getContext());
        if (!loginHelper.isLoggedIn()){
            logout.setText("Login");
            logout.setTextColor(getResources().getColor(R.color.Green200));
            logout.setBackgroundColor(getResources().getColor(R.color.Green150));
        }

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
                if (loginHelper.isLoggedIn()){
                    authProfile.signOut();
                    Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(),MainActivity.class);
                    startActivity(intent);
                    //Clear the stack to prevent the user to coming back to UseProfile
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }else {
                    loginHelper.showLoginBottomSheet();
                }
            }
        });

        openProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), MyPostsOnProfile.class);
                startActivity(intent);
            }
        });


        textViewHelloName = view.findViewById(R.id.textView_hello_name);
        textViewEmail = view.findViewById(R.id.textView_show_email);
        uploadPic = view.findViewById(R.id.imageView_profile_dp);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser==null){
            LoginBottomSheetHelper loginBottomSheetHelper = new LoginBottomSheetHelper(getContext());
            loginBottomSheetHelper.showLoginBottomSheet();
        }else {
            checkIfEmailIsVerified(firebaseUser);
            showUserProfile(firebaseUser);
        }

        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), UploadProfilePicActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }
    //User coming to userActivity after successful email verification
    private void checkIfEmailIsVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }
    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Extracting user reference from database for registered user
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetailsModel readUserDetailsModel = snapshot.getValue(ReadWriteUserDetailsModel.class);
                if(readUserDetailsModel != null){
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    doB = readUserDetailsModel.getDob();
                    gender = readUserDetailsModel.getGender();
                    mobile = readUserDetailsModel.getMobile();

                    textViewHelloName.setText("Welcome! "+fullName);
                    textViewEmail.setText(email);

                    //Set user dp (After user has uploaded)
                    Uri uri = firebaseUser.getPhotoUrl();
                    //ImageViewer setImageUri() should not be used with regular URIs. So we are using picasso
                    Picasso.get().load(uri).into(uploadPic);//uploadPic is image view
                }else {
                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //to open email in new window.
                startActivity(intent);
            }
        });
        AlertDialog alertDialog= builder.create();
        //alertDialog.setCancelable(false);
        alertDialog.show();
    }

}
