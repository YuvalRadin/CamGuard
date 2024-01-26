package com.example.trashproject.UI.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trashproject.DB.MyDatabaseHelper;
import com.example.trashproject.R;
import com.example.trashproject.RegisterActivity;
import com.example.trashproject.UI.GoogleMaps.FragmentsActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvReg;
    modleLogin modle;
    Button btnLogin;
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

        modle = new modleLogin(this);
    }

    @Override
    public void onClick(View view) {

        if(view == btnLogin)
        {
            Intent intent = new Intent(MainActivity.this, FragmentsActivity.class);
            switch(modle.isExist(etUser,etPass))
            {
                case 0:
                {
                    startActivity(intent);
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