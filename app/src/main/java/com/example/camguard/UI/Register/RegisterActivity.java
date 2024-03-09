package com.example.camguard.UI.Register;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.camguard.R;
import com.example.camguard.UI.GoogleMaps.FragmentMap;
import com.example.camguard.UI.Login.MainActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvReg;
    EditText etUser, etEmail, etPassword, etPasswordConfirmation;
    moduleRegister module;
    Button btnRegister;
    CheckBox cb;


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
        etPasswordConfirmation = findViewById(R.id.registerPasswordConfirmationEditText);
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