package com.example.camguard.UI.Camera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.camguard.R;
import com.example.camguard.UI.GoogleMaps.FragmentMap;
import com.example.camguard.UI.User.UserActivity;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kotlinx.coroutines.internal.ConcurrentLinkedListKt;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 22;
    ImageView imageView;
    Button btnSubmit;
    EditText etReport;
    ModuleCamera module;

    Intent intent;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        imageView = findViewById(R.id.ivCamera);
        btnSubmit = findViewById(R.id.btnSubmit);
        etReport = findViewById(R.id.etReport);
        module = new ModuleCamera(this);

        btnSubmit.setOnClickListener(this);
        imageView.setClickable(true);
        imageView.setOnClickListener(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);

            int height = Integer.parseInt(String.valueOf(Math.round(this.getResources().getDisplayMetrics().density * 460)));
            int width = Integer.parseInt(String.valueOf(Math.round(this.getResources().getDisplayMetrics().density * 400)));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
            imageView.setLayoutParams(params);
        }
        else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.menu_account)
            {
                intent = new Intent(CameraActivity.this, UserActivity.class);
                if (!module.CredentialsExist() && getIntent().getStringExtra("username") != null && !getIntent().getStringExtra("username").equals(""))
                {
                    intent.putExtra("username",getIntent().getStringExtra("username"));
                    intent.putExtra("email",getIntent().getStringExtra("email"));
                }
                startActivity(intent);
            }
            else if(item.getItemId() == R.id.menu_map)
            {
                Intent intent = new Intent(CameraActivity.this, FragmentMap.class);
                startActivity(intent);
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_camera);
    }

    @Override
    public void onClick(View view) {
        if(view == imageView)
        {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_CODE);
        }
        if(view == btnSubmit)
        {
            module.AddReport(module.getIdByName(module.getCredentials()[0]));
            Toast.makeText(this, "Report Submitted successfully", Toast.LENGTH_SHORT).show();
            etReport.setText("");
            intent = new Intent(CameraActivity.this, FragmentMap.class);
            if (!module.CredentialsExist() && getIntent().getStringExtra("username") != null && !getIntent().getStringExtra("username").equals(""))
            {
                intent.putExtra("username",getIntent().getStringExtra("username"));
                intent.putExtra("email",getIntent().getStringExtra("email"));
            }
            startActivity(intent);
        }
    }
}