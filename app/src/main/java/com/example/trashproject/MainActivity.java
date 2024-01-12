package com.example.trashproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvReg;
    MyDatabaseHelper myDatabaserHelper;
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

        myDatabaserHelper = new MyDatabaseHelper(this);
    }

    @Override
    public void onClick(View view) {

        if(view == btnLogin)
        {
            if(etUser.getText().toString().contains("@"))
            {
                if (myDatabaserHelper.LoginUser(etUser.getText().toString(), etPass.getText().toString(), 2)) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(this, "invalid email / password", Toast.LENGTH_SHORT).show();
            }
            else {
                if (myDatabaserHelper.LoginUser(etUser.getText().toString(), etPass.getText().toString(), 1)) {
                    Intent intent = new Intent(MainActivity.this, FragmentsActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(this, "invalid username / password", Toast.LENGTH_SHORT).show();
            }
        }

        if(view == tvReg)
        {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}