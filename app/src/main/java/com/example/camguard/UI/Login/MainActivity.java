package com.example.camguard.UI.Login;


import android.Manifest;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.camguard.Data.CurrentUser.CurrentUser;
import com.example.camguard.Data.FireBase.FirebaseHelper;
import com.example.camguard.Data.Repository.Repository;
import com.example.camguard.R;
import com.example.camguard.UI.GoogleMaps.FragmentMap;
import com.example.camguard.UI.Register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvReg;
    moduleLogin module;
    Button btnLogin;
    boolean passwordVisible = false;
    CheckBox cb;
    EditText etUser, etPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvReg = findViewById(R.id.registerText);
        tvReg.setOnClickListener(this);

        btnLogin = findViewById(R.id.loginButton);
        btnLogin.setOnClickListener(this);

        etUser =findViewById(R.id.usernameEditText);

        etPass = findViewById(R.id.passwordEditText);
        etPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        etPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    if(motionEvent.getRawX() >= (etPass.getRight() - etPass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))
                    {
                        if(passwordVisible)
                        {
                            etPass.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility,0);
                            etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;

                        } else
                        {
                            etPass.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_on,0);
                            etPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;

                        }
                    }

                }
                return false;
            }
        });

        cb = findViewById(R.id.rememberMeCheckbox);


        module = new moduleLogin(this);


        //Asking if Location Permission is Granted
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );
        //If not granted asking for permission:
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        //if username is already connected log-in immediately.
        if(module.CredentialsExist() && module.isExist(module.getCredentials()[0]))
        {
            CurrentUser.InitializeUser(module.getCredentials()[0], module.getCredentials()[1], module.getIdByName(module.getCredentials()[0]));
            Intent intent = new Intent(MainActivity.this, FragmentMap.class);
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View view) {

        if(view == btnLogin)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Location is not enabled - can't proceed", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, FragmentMap.class);

            //checking if user exists in firebase
            module.doesUserExist(etUser.getText().toString(), etPass.getText().toString(), new FirebaseHelper.SearchComplete() {
                @Override
                public void onSearchComplete(String user, String email, String password, boolean doesExist) {
                    //after the search is complete checking if he found one
                    if(doesExist)
                    {
                        //if search was completed but he does not exist in local database add him
                        if (module.UserExistsNotLocal(etUser.getText().toString(), etPass.getText().toString())) {
                            module.addUser(user, password, email);
                        }

                        //log in the user + initialize him
                        module.RememberMe(cb.isChecked());
                        CurrentUser.InitializeUser(module.getUserByName(user).getString(1), module.getUserByName(user).getString(3), module.getUserByName(user).getString(0));
                        module.SaveUser(etUser);
                        etUser.setText("");
                        etPass.setText("");
                        startActivity(intent);
                    }
                    else Toast.makeText(MainActivity.this, "User was not found", Toast.LENGTH_SHORT).show();
                }

            });
        }

        if(view == tvReg)
        {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        }

    }


}