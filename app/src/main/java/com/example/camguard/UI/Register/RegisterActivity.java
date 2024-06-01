package com.example.camguard.UI.Register;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.camguard.Data.CurrentUser.CurrentUser;
import com.example.camguard.Data.FireBase.FirebaseHelper;
import com.example.camguard.R;
import com.example.camguard.UI.GoogleMaps.FragmentMap;
import com.example.camguard.UI.Login.MainActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvReg;
    EditText etUser, etEmail, etPassword, etPasswordConfirmation;
    moduleRegister module;
    Button btnRegister;
    CheckBox cb;
    boolean passwordVisible = false, PasswordVisibleConfirmation = false;
    String ExistingPassword;


    /**
     * Initializes the Register Activity layout and its components.
     *
     * @param savedInstanceState A Bundle containing the saved instance state.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize TextView for navigating to the login activity
        tvReg=findViewById(R.id.loginText);
        tvReg.setOnClickListener(this);

        // Initialize moduleRegister instance for handling registration tasks
        module = new moduleRegister(this);

        // Initialize EditText fields for username, email, password, and password confirmation
        etUser = findViewById(R.id.registerUsernameEditText);
        etEmail = findViewById(R.id.registerEmailEditText);
        etPassword = findViewById(R.id.registerPasswordEditText);
        etPasswordConfirmation = findViewById(R.id.registerPasswordConfirmationEditText);

        // Set onTouchListener to toggle password visibility for password EditText
        etPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(passwordVisible) {
                            etPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility,0);
                            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            etPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_on,0);
                            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                    }
                }
                return false;
            }
        });

        // Set onTouchListener to toggle password visibility for password confirmation EditText
        etPasswordConfirmation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (etPasswordConfirmation.getRight() - etPasswordConfirmation.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(PasswordVisibleConfirmation) {
                            etPasswordConfirmation.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility,0);
                            etPasswordConfirmation.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            PasswordVisibleConfirmation = false;
                        } else {
                            etPasswordConfirmation.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_on,0);
                            etPasswordConfirmation.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            PasswordVisibleConfirmation = true;
                        }
                    }
                }
                return false;
            }
        });

        // Initialize Button for registration
        btnRegister = findViewById(R.id.registerButton);
        btnRegister.setOnClickListener(this);

        // Initialize CheckBox for remembering registration details
        cb = findViewById(R.id.rememberMeCheckbox);
    }

    /**
     * Handles click events for views in the Register Activity.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        if(view == tvReg) {
            // Navigate to the login activity when the "Login" TextView is clicked
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        }
        if(view == btnRegister) {
            // Check if location permissions are granted
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location is not enabled - can't proceed", Toast.LENGTH_SHORT).show();
                return;
            }
            // Validate user inputs
            if(!module.CheckUps(etUser,etEmail,etPassword,etPasswordConfirmation)) {
                return;
            }
            // Check if the user and email already exist in Firebase
            module.doesUserAndEmailExist(etUser.getText().toString(), etEmail.getText().toString(), new FirebaseHelper.CredentialsCheck() {
                @Override
                public void onCredentialsCheckComplete(boolean doesUserExist, boolean doesEmailExist) {
                    if(!doesEmailExist && !doesUserExist) {
                        // Add user to Firebase and initialize CurrentUser
                        module.addUser(etUser.getText().toString(),etPassword.getText().toString(),etEmail.getText().toString());
                        module.AddUserToFireBase(etUser.getText().toString(),etEmail.getText().toString(),etPassword.getText().toString());
                        CurrentUser.initializeUser(module.getUserByName(etUser.getText().toString()).getString(1), module.getUserByName(etUser.getText().toString()).getString(3), module.getUserByName(etUser.getText().toString()).getString(0));
                        module.SaveUser(etUser, etEmail);
                        module.RememberMe(cb.isChecked());
                        etPassword.setText("");
                        etPasswordConfirmation.setText("");
                        etUser.setText("");
                        etEmail.setText("");
                        Toast.makeText(RegisterActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, FragmentMap.class);
                        startActivity(intent);
                    }
                    if(doesUserExist) {
                        // Display error message if username already exists
                        etUser.setError("Username already exists");
                        if(module.UserExistsNotLocal(etUser.getText().toString(), etEmail.getText().toString())) {
                            // Retrieve user data from Firebase and add to local database
                            module.retrieveDocs(1, new FirebaseHelper.DocsRetrievedListener() {
                                @Override
                                public void onDocsRetrieved(Task<QuerySnapshot> task) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        if(document.getData().get("name").toString().equals(etUser.getText().toString())) {
                                            ExistingPassword = document.getData().get("password").toString();
                                            if (module.findUser(etUser.getText().toString())) {
                                                module.addUser(etUser.getText().toString(), ExistingPassword, etEmail.getText().toString());
                                            }
                                            else etUser.setError("User Already Exists");
                                        }
                                    }
                                }
                            });
                        }
                    }
                    if(doesEmailExist) {
                        // Display error message if email already exists
                        etEmail.setError("Email already exists");
                        if(module.UserExistsNotLocal(etUser.getText().toString(), etEmail.getText().toString())) {
                            // Retrieve user data from Firebase and add to local database
                            module.retrieveDocs(1, new FirebaseHelper.DocsRetrievedListener() {
                                @Override
                                public void onDocsRetrieved(Task<QuerySnapshot> task) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        if(document.getData().get("email").toString().equals(etEmail.getText().toString())) {
                                            ExistingPassword = document.getData().get("password").toString();
                                            if (module.findEmail(etEmail.getText().toString())) {
                                                module.addUser(etUser.getText().toString(), ExistingPassword, etEmail.getText().toString());
                                            }
                                            else etEmail.setError("Email Already Exists");
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }



}