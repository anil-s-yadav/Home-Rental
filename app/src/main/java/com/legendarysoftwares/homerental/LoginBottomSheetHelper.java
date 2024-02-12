package com.legendarysoftwares.homerental;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.legendarysoftwares.homerental.R;
import com.legendarysoftwares.homerental.fragments.Profile;

public class LoginBottomSheetHelper {
    private EditText editTextLoginEmail, editTextLoginPwd;
    private TextView forgetPassword, registerBtn;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private final Context context;

    public LoginBottomSheetHelper(Context context) {
        this.context = context;
        this.authProfile = FirebaseAuth.getInstance();
    }

    public void showLoginBottomSheet() {
        View view = LayoutInflater.from(context).inflate(R.layout.login_bottomsheet_helper, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        editTextLoginEmail=view.findViewById(R.id.editText_login_email);
        editTextLoginPwd = view.findViewById(R.id.editText_login_pwd);
        forgetPassword = view.findViewById(R.id.textView_forgot_password_link);
        ImageView imageViewShowHidePwd = view.findViewById(R.id.imageView_show_hide_pwd);
        progressBar = view.findViewById(R.id.progressBar);
        Button loginBtn = view.findViewById(R.id.button_login);
        registerBtn = view.findViewById(R.id.textView_register_link);

        authProfile = FirebaseAuth.getInstance();


        //Set hide pwd icon
        //imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.eye_close);
                }else {
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.eye_open);
                }
            }
        });

        //Login user
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(context, "Please Enter your Email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(context, "re-enter your Email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Valid email is required");
                    editTextLoginEmail.requestFocus();
                }else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("Password is required");
                    editTextLoginPwd.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);
                    bottomSheetDialog.dismiss();
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, registerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK
                        |Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                bottomSheetDialog.dismiss();
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ForgotPasswordActivity.class);
                context.startActivity(intent);
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog dialog1 = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = dialog1.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        bottomSheetDialog.show();
    }
    public boolean isLoggedIn() { //Change method name as userIsloggedIn()
        return authProfile.getCurrentUser() != null;
    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //Get instance for the current user
                    FirebaseUser firebaseUser=authProfile.getCurrentUser();
                    Toast.makeText(context, "You are logged in now", Toast.LENGTH_SHORT).show();
                    //open userprofile activity
                    if (context instanceof MainActivity) {
                        MainActivity mainActivity = (MainActivity) context;
                        // Access the FragmentManager from MainActivity
                        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                        // Begin the FragmentTransaction
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        // Replace with the appropriate container ID and Profile fragment instance
                        fragmentTransaction.replace(R.id.container, new Profile());
                        // Add to back stack if needed
                        fragmentTransaction.addToBackStack(null);
                        // Commit the transaction
                        fragmentTransaction.commit();
                    }
                }else {
                    try {
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        editTextLoginEmail.setText("User does not exits or is no longer valid. Please register again."+e);
                        editTextLoginEmail.requestFocus();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        editTextLoginEmail.setText("Invalid credential kindly check and re-enter."+e);
                        editTextLoginEmail.requestFocus();
                    }catch (Exception e) {
                        Log.d("loginActivity",e.getMessage());
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }


}

// I used this code for move from login Activity to Profile fragment.
/*if (context instanceof MainActivity){
        MainActivity mainActivity=(MainActivity)context;
        // Access the FragmentManager from MainActivity
        FragmentManager fragmentManager=mainActivity.getSupportFragmentManager();
        // Begin the FragmentTransaction
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        // Replace with the appropriate container ID and Profile fragment instance
        fragmentTransaction.replace(R.id.container,new Profile());
        // Add to back stack if needed
        fragmentTransaction.addToBackStack(null);
        // Commit the transaction
        fragmentTransaction.commit();
 }*/
