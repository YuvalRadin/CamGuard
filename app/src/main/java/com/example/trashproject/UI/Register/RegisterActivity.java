package com.example.trashproject.UI.Register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trashproject.R;
import com.example.trashproject.UI.GoogleMaps.FragmentMap;
import com.example.trashproject.UI.Login.MainActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvReg;
    EditText etUser, etEmail, etPassword, etPasswordConfirmation;
    moduleRegister module;
    Button btnRegister, DeleteAll;
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
        DeleteAll = findViewById(R.id.deleteAll);
        DeleteAll.setOnClickListener(this);
        cb = findViewById(R.id.rememberMeCheckbox);

    }

    @Override
    public void onClick(View view) {
        if(view == DeleteAll)
        {
            module.DeleteAllData();
        }

        if(view == tvReg)
        {

            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        }
        if(view == btnRegister)
        {
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