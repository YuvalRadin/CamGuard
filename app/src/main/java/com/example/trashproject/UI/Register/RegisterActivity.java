package com.example.trashproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trashproject.UI.GoogleMaps.FragmentsActivity;
import com.example.trashproject.UI.Login.MainActivity;
import com.example.trashproject.UI.Register.modleRegister;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvReg;
    EditText etUser, etEmail, etPassword, etPasswordConfirmation;
    modleRegister modle;
    Button btnRegister, DeleteAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvReg=findViewById(R.id.loginText);
        tvReg.setOnClickListener(this);

        modle = new modleRegister(this);
        etUser = findViewById(R.id.registerUsernameEditText);
        etEmail = findViewById(R.id.registerEmailEditText);
        etPassword = findViewById(R.id.registerPasswordEditText);
        etPasswordConfirmation = findViewById(R.id.registerPasswordConfirmationEditText);
        btnRegister = findViewById(R.id.registerButton);
        btnRegister.setOnClickListener(this);
        DeleteAll = findViewById(R.id.deleteAll);
        DeleteAll.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == DeleteAll)
        {
            modle.DeleteAllData();
        }

        if(view == tvReg)
        {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        }
        if(view == btnRegister)
        {
            if(!modle.CheckUps(etUser,etEmail,etPassword,etPasswordConfirmation))
            {
                return;
            }

            Intent intent = new Intent(RegisterActivity.this, FragmentsActivity.class);
            startActivity(intent);

        }
    }
}