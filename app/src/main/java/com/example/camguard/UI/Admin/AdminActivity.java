package com.example.camguard.UI.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.camguard.R;
import com.example.camguard.UI.Camera.CameraActivity;
import com.example.camguard.UI.GoogleMaps.FragmentMap;
import com.example.camguard.UI.User.UserActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);



        bottomNavigationView = findViewById(R.id.bottom_navigation);
        ModuleAdmin module = new ModuleAdmin(this);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.menu_account)
            {
                Intent intent = new Intent(AdminActivity.this, UserActivity.class);
                if (!module.CredentialsExist() && getIntent().getStringExtra("username") != null && !getIntent().getStringExtra("username").equals(""))
                {
                    intent.putExtra("username",getIntent().getStringExtra("username"));
                    intent.putExtra("email",getIntent().getStringExtra("email"));
                }
                startActivity(intent);
            }
            else if(item.getItemId() == R.id.menu_camera)
            {
                Intent intent = new Intent(AdminActivity.this, CameraActivity.class);
                if (!module.CredentialsExist() && getIntent().getStringExtra("username") != null && !getIntent().getStringExtra("username").equals(""))
                {
                    intent.putExtra("username",getIntent().getStringExtra("username"));
                    intent.putExtra("email",getIntent().getStringExtra("email"));
                }
                startActivity(intent);
            }
            else if(item.getItemId() == R.id.menu_map)
            {
                Intent intent = new Intent(AdminActivity.this, FragmentMap.class);
                startActivity(intent);
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_admin);
    }
}