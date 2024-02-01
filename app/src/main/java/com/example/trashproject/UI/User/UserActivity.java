package com.example.trashproject.UI.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trashproject.R;
import com.example.trashproject.UI.GoogleMaps.FragmentMap;
import com.example.trashproject.UI.Login.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    ModuleUser module;
    BottomNavigationView bottomNavigationView;
    static String[] Credentials;
    TextView tvUsername, tvEmail, tvReports;
    Button btnLogout;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        module = new ModuleUser(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_account);

        tvUsername = findViewById(R.id.nameTextView);
        tvEmail = findViewById(R.id.emailTextView);
        tvReports = findViewById(R.id.reportCountTextView);
        btnLogout = findViewById(R.id.LogOutButton);
        btnLogout.setOnClickListener(this);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.menu_map)
            {
                Intent intent = new Intent(UserActivity.this, FragmentMap.class);
                startActivity(intent);
            }
            else if(item.getItemId() == R.id.menu_camera)
            {

            }
            return true;
        });

        if(module.CredentialsExist()) {
            Credentials = module.getCredentials();
        }
        else if (!getIntent().getStringExtra("username").equals(""))
        {
            Credentials = new String[2];
            Credentials[0] = getIntent().getStringExtra("username");
            Credentials[1] = getIntent().getStringExtra("email");
        }
            tvUsername.setText(Credentials[0]);
            tvEmail.setText(Credentials[1]);
            tvReports.setText("Reports: " + module.getReports(Credentials[0]));
            if (!module.DoesRemember()) {
                module.DoNotRemember();
            }





    }

    @Override
    public void onClick(View view) {
        if(view == btnLogout)
        {
            module.DoNotRemember();
            Intent intent = new Intent(UserActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}