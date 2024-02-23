package com.example.camguard.UI.Camera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 22;
    ImageView imageView;



    ModuleCamera module;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        imageView = findViewById(R.id.ivCamera);
        module = new ModuleCamera(this);


        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);

        }
        else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.menu_account)
            {
                Intent intent = new Intent(CameraActivity.this, UserActivity.class);
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

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}