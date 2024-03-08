package com.example.camguard.UI.User;

import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.camguard.R;
import com.example.camguard.UI.Admin.AdminActivity;
import com.example.camguard.UI.Camera.CameraActivity;
import com.example.camguard.UI.GoogleMaps.FragmentMap;
import com.example.camguard.UI.Login.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    ModuleUser module;
    BottomNavigationView bottomNavigationView;
    static String[] Credentials;
    TextView tvUsername, tvEmail, tvReports;
    Button btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        module = new ModuleUser(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        tvUsername = findViewById(R.id.nameTextView);
        tvEmail = findViewById(R.id.emailTextView);
        tvReports = findViewById(R.id.reportCountTextView);
        btnLogout = findViewById(R.id.LogOutButton);
        btnLogout.setOnClickListener(this);


        if(module.CredentialsExist()) {
            Credentials = module.getCredentials();
        }
        else if (getIntent().getStringExtra("username") != null && !getIntent().getStringExtra("username").equals(""))
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

        if(Credentials[1].equals("s16131@nhs.co.il"))
        {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.admin_bottom_navigation_menu);
        }
        bottomNavigationView.setSelectedItemId(R.id.menu_account);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.menu_map)
            {
                Intent intent = new Intent(UserActivity.this, FragmentMap.class);
                intent.putExtra("username",Credentials[0]);
                intent.putExtra("email",Credentials[1]);
                startActivity(intent);
            }
            else if(item.getItemId() == R.id.menu_camera)
            {
                Intent intent = new Intent(UserActivity.this, CameraActivity.class);
                intent.putExtra("username",Credentials[0]);
                intent.putExtra("email",Credentials[1]);
                startActivity(intent);
            }
            else if(item.getItemId() == R.id.menu_admin)
            {
                Intent intent = new Intent(UserActivity.this, AdminActivity.class);
                intent.putExtra("username",Credentials[0]);
                intent.putExtra("email",Credentials[1]);
                startActivity(intent);
            }
            return true;
        });



    }

    @Override
    public void onClick(View view) {
        if(view == btnLogout)
        {
            module.DoNotRemember();
            Intent intent = new Intent(UserActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}