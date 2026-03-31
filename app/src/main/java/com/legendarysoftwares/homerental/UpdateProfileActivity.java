package com.legendarysoftwares.homerental;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editTextUpdateName, editTextUpdateDOB, editTextUpdateMobile, editTextUserAbout;
    private RadioButton radioButtonUpdateGenderSelected;
    private RadioGroup radioGroupUpdateGender;
    private ShapeableImageView ProfilePic;
    private String textFullName, textDOB, textGender, textMobile, aboutUser;
    private ProgressBar progressBar;
    private FirebaseUser user;
    private ProgressDialog progressDialog;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        progressBar = findViewById(R.id.progressBar);
        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateDOB = findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobile = findViewById(R.id.editText_update_profile_mobile);
        editTextUserAbout = findViewById(R.id.editText_update_profile_about);
        radioGroupUpdateGender = findViewById(R.id.radio_group_update_profile_gender);
        ProfilePic = findViewById(R.id.updateImageViewProfile);
        ImageView btnBack = findViewById(R.id.back_button_update); // Added back button reference

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        showDetails();

        Button openGalleryBtn = findViewById(R.id.open_gallery);
        openGalleryBtn.setOnClickListener(v -> openGallery());

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        ImageView calenderImageUpdate = findViewById(R.id.imageView_date_picker);
        calenderImageUpdate.setOnClickListener(view -> {
            int day = 1, month = 0, year = 2000;
            if (!TextUtils.isEmpty(textDOB) && textDOB.contains("/")) {
                String[] textSADOB = textDOB.split("/");
                if (textSADOB.length == 3) {
                    day = Integer.parseInt(textSADOB[0]);
                    month = Integer.parseInt(textSADOB[1]) - 1;
                    year = Integer.parseInt(textSADOB[2]);
                }
            }
            DatePickerDialog picker = new DatePickerDialog(UpdateProfileActivity.this, (view1, year1, month1, dayOfMonth) -> 
                editTextUpdateDOB.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
            picker.show();
        });

        Button buttonUpdateProfile = findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(v -> updateProfile());
    }

    private void showDetails() {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isFinishing()) {
                    ReadWriteUserDetailsModel readUserDetails = snapshot.getValue(ReadWriteUserDetailsModel.class);
                    if (readUserDetails != null) {
                        textFullName = user.getDisplayName();
                        textDOB = readUserDetails.getDob();
                        textGender = readUserDetails.getGender();
                        textMobile = readUserDetails.getMobile();
                        aboutUser = readUserDetails.getAbout();

                        if (user.getPhotoUrl() != null) {
                            Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.ic_logo_transparent).into(ProfilePic);
                        }

                        editTextUpdateName.setText(textFullName);
                        editTextUpdateDOB.setText(textDOB);
                        editTextUpdateMobile.setText(textMobile);
                        editTextUserAbout.setText(aboutUser);

                        if ("Male".equals(textGender)) {
                            radioGroupUpdateGender.check(R.id.radio_male);
                        } else if ("Female".equals(textGender)) {
                            radioGroupUpdateGender.check(R.id.radio_female);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isFinishing()) {
                    Toast.makeText(UpdateProfileActivity.this, "Error fetching details", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            ProfilePic.setImageURI(uriImage);
        }
    }

    private void updateProfile() {
        int selectedGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
        if (selectedGenderID == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }
        radioButtonUpdateGenderSelected = findViewById(selectedGenderID);
        textGender = radioButtonUpdateGenderSelected.getText().toString();
        textFullName = editTextUpdateName.getText().toString().trim();
        textDOB = editTextUpdateDOB.getText().toString().trim();
        textMobile = editTextUpdateMobile.getText().toString().trim();
        aboutUser = editTextUserAbout.getText().toString().trim();

        if (TextUtils.isEmpty(textFullName)) {
            editTextUpdateName.setError("Name required");
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        uploadPic();
    }

    private void uploadPic() {
        if (uriImage != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("UserProfilePics").child(user.getUid());
            storageReference.putFile(uriImage).addOnSuccessListener(taskSnapshot -> 
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> updateFirebaseProfile(uri.toString())));
        } else {
            updateFirebaseProfile(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
        }
    }

    private void updateFirebaseProfile(String photoUrl) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(textFullName)
                .setPhotoUri(Uri.parse(photoUrl)).build();

        user.updateProfile(profileChangeRequest).addOnSuccessListener(unused -> {
            ReadWriteUserDetailsModel writeUserDetailsModel = new ReadWriteUserDetailsModel(textFullName, photoUrl, user.getEmail(), textDOB, textGender, textMobile, aboutUser);
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
            referenceProfile.child(user.getUid()).setValue(writeUserDetailsModel).addOnCompleteListener(task -> {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(UpdateProfileActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    finish(); // This fixes the crash on back navigation
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}