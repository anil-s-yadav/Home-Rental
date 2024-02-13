package com.legendarysoftwares.homerental;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class registerActivity extends AppCompatActivity {
    private EditText  editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDOB, editTextRegisterMobile, editTextRegisterPwd, editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker;
    private ShapeableImageView ProfilePic;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private static final int PICK_IMAGE_REQUEST=1;
    private Uri uriImage, userProfilePhotoOnStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toast.makeText(registerActivity.this,"You can Register now!",Toast.LENGTH_LONG).show();

        progressBar=findViewById(R.id.progressBar);
        editTextRegisterFullName=findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail=findViewById(R.id.editText_register_email);
        editTextRegisterDOB=findViewById(R.id.editText_register_dob);
        editTextRegisterMobile=findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd=findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd=findViewById(R.id.editText_register_confirm_password);

        Button openGalleryBtn = findViewById(R.id.open_gallery);
        ProfilePic = findViewById(R.id.registerImageViewProfile);
        openGalleryBtn.setOnClickListener(v -> {
            openGallery();
        });

        //Radio button for gender
        radioGroupRegisterGender=findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        //Setting Date picker on edittext
        ImageView calenderImage = findViewById(R.id.imageView_date_picker);
        calenderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar= Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //Date picker Dialog
                picker = new DatePickerDialog(registerActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDOB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected =findViewById(selectedGenderId);

                //obtain the entered data
                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDOB = editTextRegisterDOB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
                String textGender; //can't obtain before know the selected value

                //validate mobile number using Matcher and Pattern (regular expression)
                String mobileRegex = "[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern=Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);

                if (TextUtils.isEmpty(textFullName)){
                    Toast.makeText(registerActivity.this,"Please enter your full name!",Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                }else if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(registerActivity.this,"Please enter your Email!",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(registerActivity.this,"Please re-enter your email!",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Valid Email is required");
                    editTextRegisterEmail.requestFocus();
                }else if (TextUtils.isEmpty(textDOB)){
                    Toast.makeText(registerActivity.this,"Please enter your Date of birth!",Toast.LENGTH_LONG).show();
                    editTextRegisterDOB.setError("Date of birth is required");
                    editTextRegisterDOB.requestFocus();
                }else if (TextUtils.isEmpty(textFullName)){
                    Toast.makeText(registerActivity.this,"You can Register now!",Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                }else if (radioGroupRegisterGender.getCheckedRadioButtonId()==-1){
                    Toast.makeText(registerActivity.this,"Please select your gender!",Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Gender is required");
                    radioButtonRegisterGenderSelected.requestFocus();
                }else if (TextUtils.isEmpty(textMobile)){
                    Toast.makeText(registerActivity.this,"Enter your mobile number!",Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile no. is required");
                    editTextRegisterMobile.requestFocus();
                }else if (textMobile.length()!=10){
                    Toast.makeText(registerActivity.this,"Re-enter your mobile number!",Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile no. should be 10 digits!");
                    editTextRegisterMobile.requestFocus();
                }else if (!mobileMatcher.find()) {
                    Toast.makeText(registerActivity.this, "Re-enter your mobile number!", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile no. is not valid!");
                    editTextRegisterMobile.requestFocus();
                }else if (TextUtils.isEmpty(textPwd)){
                    Toast.makeText(registerActivity.this,"Enter Password!",Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is required!");
                    editTextRegisterPwd.requestFocus();
                }else if (textPwd.length()<8){
                    Toast.makeText(registerActivity.this,"Password should be 8 digits!",Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is too weak");
                    editTextRegisterPwd.requestFocus();
                }else if (TextUtils.isEmpty(textConfirmPwd)) {
                    Toast.makeText(registerActivity.this, "Please confirm your password!", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation is required!");
                    editTextRegisterConfirmPwd.requestFocus();
                }else if (!textPwd.equals(textConfirmPwd)){
                    Toast.makeText(registerActivity.this,"Please confirm your password!",Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Password should be match!");
                    editTextRegisterConfirmPwd.requestFocus();
                    //cleared the entered password
                    editTextRegisterConfirmPwd.clearComposingText();
                    editTextRegisterPwd.clearComposingText();
                }else{
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDOB, textGender,textMobile,textPwd);
                }

            }
        });

    }
    private void openGallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data.getData() !=null){
            uriImage = data.getData();
            ProfilePic.setImageURI(uriImage);
        }
    }

    private void uploadPic(String FullName){
        if (uriImage!=null){
           StorageReference storageReference= FirebaseStorage.getInstance().getReference("UserProfilePics")
                   .child(user.getUid());
            storageReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            userProfilePhotoOnStorage = uri;
                            UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder()
                                    .setDisplayName(FullName)
                                    .setPhotoUri(userProfilePhotoOnStorage).build();
                            user.updateProfile(profileChangeRequest);
                        }
                    });
                }
            });
        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(registerActivity.this, "Image not selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(String textFullName, String textEmail, String textDOB,String textGender, String textMobile, String textPwd) {
        auth=FirebaseAuth.getInstance();
        //create user
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(registerActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    user = auth.getCurrentUser();

                    uploadPic(textFullName);

                    Toast.makeText(registerActivity.this,"User register successful!",Toast.LENGTH_SHORT).show();

                    //here i can add upload profile method. call here and pass firebaseuser as parameter.
                    //Enter user data to realtime database
                    ReadWriteUserDetailsModel readWriteUserDetailsModel=new ReadWriteUserDetailsModel(textFullName,textEmail,textDOB,textGender,textMobile);
                    //Extracting user reference from Database for "Register users"
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                    referenceProfile.child(user.getUid()).setValue(readWriteUserDetailsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //send verification email
                                user.sendEmailVerification();

                                Toast.makeText(registerActivity.this,"User register successful! Now verify Email",Toast.LENGTH_SHORT).show();
                                // Create an instance of the Profile fragment
                                Intent intent=new Intent(registerActivity.this, MyPostsOnProfile.class);
                                startActivity(intent);
                                Log.d("Name and PhotoUrl",user.getDisplayName()+" "+user.getPhotoUrl());
                            }else {
                                Toast.makeText(registerActivity.this,"User register Unsuccessful try again!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                }else{
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e) {
                        editTextRegisterPwd.setError("Your password is too weak! Enter a strong password");
                        editTextRegisterPwd.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        editTextRegisterPwd.setError("Your Email is Invalid or already in use. Kindly re-enter.");
                        editTextRegisterPwd.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e){
                        editTextRegisterEmail.setError("User is already  register! Try another email or Login.");
                        editTextRegisterEmail.requestFocus();
                    }catch (Exception e){
                        Log.d("RegisterActivity",e.getMessage());
                        Toast.makeText(registerActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

}
