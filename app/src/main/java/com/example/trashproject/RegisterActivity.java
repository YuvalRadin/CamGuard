package com.example.trashproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvReg;
    EditText etUser, etEmail, etPassword, etPasswordConfirmation;
    MyDatabaseHelper myDatabaseHelper;
    Button btnRegister, DeleteAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvReg=findViewById(R.id.loginText);
        tvReg.setOnClickListener(this);

        myDatabaseHelper = new MyDatabaseHelper(this);
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
            myDatabaseHelper.deleteAllData();
        }

        if(view == tvReg)
        {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        }
        if(view == btnRegister)
        {
            // username validity checkups
            if(etUser.getText().toString().equals(""))
            {
                Toast.makeText(this, "Fill username", Toast.LENGTH_SHORT).show();
                return;
            }
            if(etUser.getText().toString().length() < 3)
            {
                Toast.makeText(this, "Username must be over 3 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // email validity checkups
            if(etEmail.getText().toString().indexOf("@") <= 1)
            {
                Toast.makeText(this, "invalid email (x@)", Toast.LENGTH_SHORT).show();
                return;
            }
            if(etEmail.getText().toString().indexOf("@") != etEmail.getText().toString().lastIndexOf("@"))
            {
                Toast.makeText(this, "invalid email (@@)", Toast.LENGTH_SHORT).show();
                return;
             }
            if(etEmail.getText().toString().indexOf(".") - etEmail.getText().toString().indexOf("@") <= 3)
            {
                Toast.makeText(this, "invalid email (.@)", Toast.LENGTH_SHORT).show();
                return;
            }
            if(etEmail.getText().toString().indexOf(".") != etEmail.getText().toString().lastIndexOf("."))
            {
                Toast.makeText(this, "invalid email (..)", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!(etEmail.getText().toString().contains(".com")) && !(etEmail.getText().toString().contains(".co.")))
            {
                Toast.makeText(this, "invalid email (com/co)", Toast.LENGTH_SHORT).show();
                return;
            }



            //password validity checkups
            if(etPassword.getText().toString().equals(""))
            {
                Toast.makeText(this, "Fill password", Toast.LENGTH_SHORT).show();
                return;
            }
            if(etPassword.getText().toString().length() < 3)
            {
                Toast.makeText(this, "Password isn't strong enough", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!(etPassword.getText().toString().equals(etPasswordConfirmation.getText().toString())))
            {
                Toast.makeText(this, "Password Confirmation does not match", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!(myDatabaseHelper.FindUser(etUser.getText().toString())))
            {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                return;
            }


            myDatabaseHelper.addUser(etUser.getText().toString(),etPassword.getText().toString(),etEmail.getText().toString());
            etPassword.setText("");
            etPasswordConfirmation.setText("");
            etUser.setText("");
            etEmail.setText("");
            Intent intent = new Intent(RegisterActivity.this, FragmentsActivity.class);
            startActivity(intent);


        }
    }
}