package com.legendarysoftwares.homerental;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailUpdateActivity extends AppCompatActivity {
    private EditText currentEmailEditText, passwordEditText, newEmailEditText;
    private Button Authenticate, EmailResetBtn;
    private FirebaseAuth mAuth;
    private  ProgressBar progressBar;
    private LinearLayout authenticateLayout, resetLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_update);

        mAuth = FirebaseAuth.getInstance();
         progressBar = findViewById(R.id.progressBar);

        currentEmailEditText = findViewById(R.id.current_email);
        passwordEditText = findViewById(R.id.current_password);
        newEmailEditText = findViewById(R.id.new_email);

        Authenticate = findViewById(R.id.email_reset_Autehnticate_btn);
        EmailResetBtn = findViewById(R.id.button_reset_email);

        authenticateLayout = findViewById(R.id.authenticate_email_layout);
        resetLayout = findViewById(R.id.enter_email_layout);

        resetLayout.setVisibility(View.GONE);

        ImageView back = findViewById(R.id.email_reset_back_btn);
        back.setOnClickListener((View)->{
            onBackPressed();
        });

        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setOnClickListener((View)-> {
                if (passwordEditText.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.eye_close);
                }else {
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.eye_open);
                }
        });

        Authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentEmail = currentEmailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(currentEmail) || !Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
                    currentEmailEditText.setError("Valid email is required");
                    currentEmailEditText.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Valid email is required");
                    passwordEditText.requestFocus();
                }else{
                        reauthenticateUser(currentEmail, password);
                }

            }
        });

        EmailResetBtn.setOnClickListener((View)->{
          String newEmail = newEmailEditText.getText().toString().trim();
            if (TextUtils.isEmpty(newEmail) || !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                newEmailEditText.setError("Valid email is required");
                newEmailEditText.requestFocus();
            }else {
                changeUserEmail(newEmail);
            }
        });

    }

    private void reauthenticateUser(String currentEmail, String password) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                authenticateLayout.setVisibility(View.GONE);
                                resetLayout.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(EmailUpdateActivity.this, "Authentication failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void changeUserEmail(final String newEmail) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updateEmail(newEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(EmailUpdateActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                                finish(); // Close the activity
                            } else {
                                Toast.makeText(EmailUpdateActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("MYERROR",e.getMessage());
                        }
                    });
        }

    }
}