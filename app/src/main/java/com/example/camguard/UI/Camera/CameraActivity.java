package com.example.camguard.UI.Camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.camguard.R;
import com.example.camguard.UI.GoogleMaps.FragmentMap;
import com.example.camguard.UI.User.UserActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 22;
    ImageView imageView;
    Button btnSubmit;
    EditText etReport;
    ModuleCamera module;
    Bitmap photo;
    Intent intent;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        imageView = findViewById(R.id.ivCamera);
        imageView.setTag("NoPic");
        btnSubmit = findViewById(R.id.btnSubmit);
        etReport = findViewById(R.id.etReport);
        module = new ModuleCamera(this);

        btnSubmit.setOnClickListener(this);
        imageView.setClickable(true);
        imageView.setOnClickListener(this);

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            imageView.setTag("Pic");

            int height = Integer.parseInt(String.valueOf(Math.round(this.getResources().getDisplayMetrics().density * 320)));
            int width = Integer.parseInt(String.valueOf(Math.round(this.getResources().getDisplayMetrics().density * 240)));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            imageView.setLayoutParams(params);
        }
        else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
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
            if(imageView.getTag().equals("NoPic"))
            {
                Toast.makeText(this, "you must add a picture before submitting!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(etReport.getText().toString().isEmpty())
            {
                etReport.setError("you must add a description");
                return;
            }
            intent = new Intent(CameraActivity.this, FragmentMap.class);
            intent.putExtra("Description", etReport.getText().toString());
            intent.putExtra("Picture", photo);
            intent.putExtra("NewReport", true);
            if (!module.CredentialsExist() && getIntent().getStringExtra("username") != null && !getIntent().getStringExtra("username").equals(""))
            {
                intent.putExtra("username",getIntent().getStringExtra("username"));
                intent.putExtra("email",getIntent().getStringExtra("email"));
            }
            startActivity(intent);
        }
    }
}