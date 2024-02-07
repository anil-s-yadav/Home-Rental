package com.legendarysoftwares.homerental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.legendarysoftwares.homerental.fragments.Profile;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editTextUpdateName, editTextUpdateDOB, editTextUpdateMobile;
    private RadioButton radioButtonUpdateGenderSelected;
    private RadioGroup radioGroupUpdateGender;
    private String textFullName, textDOB, textGender, textMobile;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        progressBar = findViewById(R.id.progressBar);
        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateDOB = findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobile = findViewById(R.id.editText_update_profile_mobile);

        radioGroupUpdateGender = findViewById(R.id.radio_group_update_profile_gender);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= authProfile.getCurrentUser();

        //Show profile data
        showProfile(firebaseUser);

        //Upload profile pic
        TextView UploadProfilepic = findViewById(R.id.textView_profile_upload_pic);
        UploadProfilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UpdateProfileActivity.this, UploadProfilePicActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //Upload Email
        /*TextView updateEmail = findViewById(R.id.textView_profile_update_email);
        updateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UpdateProfileActivity.this, UploadEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });*/
        //Setting Date picker on edittext
        ImageView calenderImageUpdate=findViewById(R.id.imageView_date_picker);
        calenderImageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Extracting saved dd,m,yyyy into difference variables by creating an array delimited by "/"
                String textSADOB[] = textDOB.split("/");

                int day = Integer.parseInt(textSADOB[0]);
                int month = Integer.parseInt(textSADOB[1]) -1 ; //to take care of month index starting with zero.
                int year = Integer.parseInt(textSADOB[2]);
                
                //Date picker Dialog
                DatePickerDialog picker;
                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextUpdateDOB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });
        
        //update profile
        Button buttonUpdateProfile = findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });

    }

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected = findViewById(selectedGenderID);

        //validate mobile number using Matcher and Pattern (regular expression)
        String mobileRegex = "[6-9][0-9]{9}";
        Matcher mobileMatcher;
        Pattern mobilePattern=Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobile);

        if (TextUtils.isEmpty(textFullName)){
            Toast.makeText(UpdateProfileActivity.this,"Please enter your full name!",Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Full name is required");
            editTextUpdateName.requestFocus();
        }else if (TextUtils.isEmpty(textDOB)){
            Toast.makeText(UpdateProfileActivity.this,"Please enter your Date of birth!",Toast.LENGTH_LONG).show();
            editTextUpdateDOB.setError("Date of birth is required");
            editTextUpdateDOB.requestFocus();
        }else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())){
            Toast.makeText(UpdateProfileActivity.this,"Please select your gender!",Toast.LENGTH_LONG).show();
            radioButtonUpdateGenderSelected.setError("Gender is required");
            radioButtonUpdateGenderSelected.requestFocus();
        }else if (TextUtils.isEmpty(textMobile)){
            Toast.makeText(UpdateProfileActivity.this,"Enter your mobile number!",Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile no. is required");
            editTextUpdateMobile.requestFocus();
        }else if (textMobile.length()!=10){
            Toast.makeText(UpdateProfileActivity.this,"Re-enter your mobile number!",Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile no. should be 10 digits!");
            editTextUpdateMobile.requestFocus();
        }else if (!mobileMatcher.find()) {
            Toast.makeText(UpdateProfileActivity.this, "Re-enter your mobile number!", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile no. is not valid!");
            editTextUpdateMobile.requestFocus();
        }else{
            //Obtain the data entered by user
            textGender = radioButtonUpdateGenderSelected.getText().toString();
            textFullName = editTextUpdateName.getText().toString();
            textDOB = editTextUpdateDOB.getText().toString();
            textMobile = editTextUpdateMobile.getText().toString();

            //Insert data to firebase realtime database
            ReadWriteUserDetailsModel writeUserDetailsModel = new ReadWriteUserDetailsModel(textDOB,textGender,textMobile);
            //Extract user reference from Database for "Registered User"
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");;
            String userID = firebaseUser.getUid();
            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeUserDetailsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //Setting new display Name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UpdateProfileActivity.this, "Update Successful!", Toast.LENGTH_SHORT).show();
                        // Create an instance of the Profile fragment
                        Profile profileFragment = new Profile();
                        // Use FragmentTransaction to replace the current fragment with the Profile fragment
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, profileFragment); // R.id.fragment_container is the container view ID in your layout
                        transaction.addToBackStack(null); // Add to back stack to allow navigating back
                        transaction.commit();
                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });

        }

    }

    //fetch data from firebase and display
    private void showProfile(FirebaseUser firebaseUser){
        String userIDofRegistered = firebaseUser.getUid();

        //Extracting user reference from database for "Registered users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetailsModel readUserDetails = snapshot.getValue(ReadWriteUserDetailsModel.class);
                if (readUserDetails!=null){
                    textFullName = firebaseUser.getDisplayName();
                    textDOB = readUserDetails.dob;
                    textGender = readUserDetails.gender;
                    textMobile = readUserDetails.mobile;

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDOB.setText(textDOB);
                    editTextUpdateMobile.setText(textMobile);

                    //show gender through radio button
                    if (textGender.equals("Male")){
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_male);
                    }else {
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_female);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                }else{
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }






}