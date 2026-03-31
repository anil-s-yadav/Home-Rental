package com.legendarysoftwares.homerental;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class registerActivity extends AppCompatActivity {
    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDOB, editTextRegisterMobile, editTextRegisterPwd, editTextRegisterConfirmPwd;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private ShapeableImageView ProfilePic;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private final String aboutUser = "About me...";
    private Uri uriImage;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    uriImage = result.getData().getData();
                    if (uriImage != null) {
                        Picasso.get().load(uriImage).placeholder(R.drawable.ic_profile).fit().centerCrop().into(ProfilePic);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupListeners();
    }

    private void initViews() {
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDOB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);
        ProfilePic = findViewById(R.id.registerImageViewProfile);
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
    }

    private void setupListeners() {
        findViewById(R.id.open_gallery).setOnClickListener(v -> openGallery());

        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(v -> togglePasswordVisibility(editTextRegisterPwd, imageViewShowHidePwd));

        ImageView imageViewShowHideConfirmPwd = findViewById(R.id.imageView_show_hide_confirm_pwd);
        imageViewShowHideConfirmPwd.setOnClickListener(v -> togglePasswordVisibility(editTextRegisterConfirmPwd, imageViewShowHideConfirmPwd));

        findViewById(R.id.imageView_date_picker).setOnClickListener(v -> showDatePicker());

        findViewById(R.id.button_register).setOnClickListener(v -> validateAndRegister());
    }

    private void togglePasswordVisibility(EditText editText, ImageView icon) {
        if (editText.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            icon.setImageResource(R.drawable.eye_close);
        } else {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            icon.setImageResource(R.drawable.eye_open);
        }
        editText.setSelection(editText.getText().length());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> 
                editTextRegisterDOB.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }

    private void validateAndRegister() {
        String fullName = editTextRegisterFullName.getText().toString().trim();
        String email = editTextRegisterEmail.getText().toString().trim();
        String dob = editTextRegisterDOB.getText().toString().trim();
        String mobile = editTextRegisterMobile.getText().toString().trim();
        String pwd = editTextRegisterPwd.getText().toString();
        String confirmPwd = editTextRegisterConfirmPwd.getText().toString();

        if (TextUtils.isEmpty(fullName)) {
            editTextRegisterFullName.setError("Required");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextRegisterEmail.setError("Invalid Email");
            return;
        }
        
        int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Select Gender", Toast.LENGTH_SHORT).show();
            return;
        }
        radioButtonRegisterGenderSelected = findViewById(selectedGenderId);
        String gender = radioButtonRegisterGenderSelected.getText().toString();

        if (mobile.length() != 10) {
            editTextRegisterMobile.setError("10 digits required");
            return;
        }
        if (!pwd.equals(confirmPwd)) {
            editTextRegisterConfirmPwd.setError("Passwords mismatch");
            return;
        }
        if (uriImage == null) {
            Toast.makeText(this, "Select profile picture", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        registerUser(fullName, email, dob, gender, mobile, pwd);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void registerUser(String fullName, String email, String dob, String gender, String mobile, String pwd) {
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                uploadProfilePicture(task.getResult().getUser(), fullName, email, dob, gender, mobile);
            } else {
                if (progressDialog != null) progressDialog.dismiss();
                handleRegistrationError(task.getException());
            }
        });
    }

    private void uploadProfilePicture(FirebaseUser user, String fullName, String email, String dob, String gender, String mobile) {
        if (user == null) return;

        StorageReference ref = FirebaseStorage.getInstance().getReference("UserProfilePics").child(user.getUid());
        ref.putFile(uriImage).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .setPhotoUri(uri).build();
            
            user.updateProfile(profileUpdates).addOnCompleteListener(t -> {
                saveUserDataToDatabase(user, fullName, email, dob, gender, mobile, uri.toString());
            });
        })).addOnFailureListener(e -> {
            if (progressDialog != null) progressDialog.dismiss();
            Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveUserDataToDatabase(FirebaseUser user, String fullName, String email, String dob, String gender, String mobile, String photoUrl) {
        ReadWriteUserDetailsModel model = new ReadWriteUserDetailsModel(fullName, photoUrl, email, dob, gender, mobile, aboutUser);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Registered Users");
        dbRef.child(user.getUid()).setValue(model).addOnCompleteListener(task -> {
            if (progressDialog != null) progressDialog.dismiss();
            if (task.isSuccessful()) {
                user.sendEmailVerification();
                Toast.makeText(this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleRegistrationError(Exception e) {
        if (e instanceof FirebaseAuthWeakPasswordException) {
            editTextRegisterPwd.setError("Weak password");
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            editTextRegisterEmail.setError("Email already in use");
        } else {
            Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}