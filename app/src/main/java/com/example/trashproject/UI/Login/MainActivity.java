package com.example.trashproject.UI.Login;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trashproject.R;
import com.example.trashproject.UI.GoogleMaps.FragmentMap;
import com.example.trashproject.UI.Register.RegisterActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvReg;
    moduleLogin module;
    Button btnLogin;

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
        cb = findViewById(R.id.rememberMeCheckbox);


        module = new moduleLogin(this);
    }

    @Override
    public void onClick(View view) {

        if(view == btnLogin)
        {
            Intent intent = new Intent(MainActivity.this, FragmentMap.class);
            switch(module.isExist(etUser,etPass))
            {
                case 0:
                {
                    if(cb.isChecked())
                    {
                        module.RememberMe(etUser);
                    }
                    startActivity(intent);
                    return;
                }
                case 1:
                {
                    etUser.setError("Invalid username/password");
                    return;
                }
                case 2:
                {
                    etUser.setError("invalid email/password");
                    return;
                }
            }
        }

        if(view == tvReg)
        {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}