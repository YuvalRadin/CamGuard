package com.example.camguard.UI.Login;


import android.Manifest;
import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.camguard.Data.CurrentUser.CurrentUser;
import com.example.camguard.Data.FireBase.FirebaseHelper;
import com.example.camguard.R;
import com.example.camguard.UI.Admin.ModuleAdmin;
import com.example.camguard.UI.GoogleMaps.FragmentMap;
import com.example.camguard.UI.Register.RegisterActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * TextView for the registration link.
     */
    TextView tvReg;

    /**
     * Object for handling login functionalities.
     */
    moduleLogin module;

    /**
     * Button for login.
     */
    Button btnLogin;

    /**
     * Boolean flag indicating password visibility.
     */
    boolean passwordVisible = false;

    /**
     * CheckBox for the "Remember Me" option.
     */
    CheckBox cb;

    /**
     * EditText for username input.
     */
    EditText etUser;

    /**
     * EditText for password input.
     */
    EditText etPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing UI components
        tvReg = findViewById(R.id.registerText);
        tvReg.setOnClickListener(this);

        btnLogin = findViewById(R.id.loginButton);
        btnLogin.setOnClickListener(this);

        etUser = findViewById(R.id.usernameEditText);

        etPass = findViewById(R.id.passwordEditText);
        etPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (etPass.getRight() - etPass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (passwordVisible) {
                            etPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility, 0);
                            etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            etPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_on, 0);
                            etPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                    }
                }
                return false;
            }
        });

        cb = findViewById(R.id.rememberMeCheckbox);

        // Initializing the login module
        module = new moduleLogin(this);
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Loading Info");
        pd.setCancelable(false);
        pd.show();
        module.deleteAllSQLData();
        module.updateAllSQLData(pd);

        // Asking for location permission
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        // Precise location access granted.
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // Only approximate location access granted.
                    } else {
                        // No location access granted.
                    }
                });

        // Requesting location permissions if not granted
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        // Automatically logging in if user credentials exist and are valid
        if (module.CredentialsExist() && module.isExist(module.getCredentials()[0])) {
            CurrentUser.initializeUser(module.getCredentials()[0], module.getCredentials()[1], module.getIdByName(module.getCredentials()[0]));
            Intent intent = new Intent(MainActivity.this, FragmentMap.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnLogin) {
            // Check if location permissions are granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location is not enabled - can't proceed", Toast.LENGTH_SHORT).show();
                return;
            }

            // Intent for navigating to the map fragment
            Intent intent = new Intent(MainActivity.this, FragmentMap.class);

            // Check if user exists in Firebase
            module.doesUserExist(etUser.getText().toString(), etPass.getText().toString(), new FirebaseHelper.SearchComplete() {
                @Override
                public void onSearchComplete(String user, String email, String password, boolean doesExist) {
                    // After the search is complete, check if user exists
                    if (doesExist) {
                        // If the user exists in Firebase but not locally, add them
                        if (module.UserExistsNotLocal(etUser.getText().toString(), etPass.getText().toString())) {
                            module.addUser(user, password, email);
                        }

                        // Log in the user and initialize them
                        module.RememberMe(cb.isChecked());
                        CurrentUser.initializeUser(module.getUserByName(user).getString(1), module.getUserByName(user).getString(3), module.getUserByName(user).getString(0));
                        module.SaveUser(etUser);
                        etUser.setText("");
                        etPass.setText("");
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "User was not found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (view == tvReg) {
            // Intent for navigating to the registration activity
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed()
    {
        if(!getIntent().hasExtra("LoggedOut") && getIntent().getBooleanExtra("LoggedOut", false))
            super.onBackPressed();
    }



}