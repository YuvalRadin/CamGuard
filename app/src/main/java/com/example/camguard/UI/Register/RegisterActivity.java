package com.example.camguard.UI.Register;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.camguard.Data.CurrentUser.CurrentUser;
import com.example.camguard.R;
import com.example.camguard.UI.GoogleMaps.FragmentMap;
import com.example.camguard.UI.Login.MainActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvReg;
    EditText etUser, etEmail, etPassword, etPasswordConfirmation;
    moduleRegister module;
    Button btnRegister;
    CheckBox cb;
    boolean passwordVisible = false, PasswordVisibleConfirmation = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvReg=findViewById(R.id.loginText);
        tvReg.setOnClickListener(this);

        module = new moduleRegister(this);
        etUser = findViewById(R.id.registerUsernameEditText);
        etEmail = findViewById(R.id.registerEmailEditText);
        etPassword = findViewById(R.id.registerPasswordEditText);
        etPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    if(motionEvent.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))
                    {
                        if(passwordVisible)
                        {
                            etPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility,0);
                            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else
                        {
                            etPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_on,0);
                            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                    }
                }
                return false;
            }
        });
        etPasswordConfirmation = findViewById(R.id.registerPasswordConfirmationEditText);
        etPasswordConfirmation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    if(motionEvent.getRawX() >= (etPasswordConfirmation.getRight() - etPasswordConfirmation.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))
                    {
                        if(PasswordVisibleConfirmation)
                        {
                            etPasswordConfirmation.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility,0);
                            etPasswordConfirmation.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            PasswordVisibleConfirmation = false;
                        } else
                        {
                            etPasswordConfirmation.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_on,0);
                            etPasswordConfirmation.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            PasswordVisibleConfirmation = true;
                        }
                    }
                }
                return false;
            }
        });
        btnRegister = findViewById(R.id.registerButton);
        btnRegister.setOnClickListener(this);
        cb = findViewById(R.id.rememberMeCheckbox);

    }

    @Override
    public void onClick(View view) {

        if(view == tvReg)
        {

            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        }
        if(view == btnRegister)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Location is not enabled - can't proceed", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!module.CheckUps(etUser,etEmail,etPassword,etPasswordConfirmation))
            {
                return;
            }
            CurrentUser.InitializeUser(module.getUserByName(etUser.getText().toString()).getString(1), module.getUserByName(etUser.getText().toString()).getString(3), module.getUserByName(etUser.getText().toString()).getString(0));
            module.SaveUser(etUser, etEmail);
            module.RememberMe(cb.isChecked());
            etPassword.setText("");
            etPasswordConfirmation.setText("");
            etUser.setText("");
            etEmail.setText("");
            Intent intent = new Intent(RegisterActivity.this, FragmentMap.class);
            startActivity(intent);

        }
    }
}