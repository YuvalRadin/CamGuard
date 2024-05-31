package com.example.camguard.UI.Camera;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.camguard.Data.CurrentUser.CurrentUser;
import com.example.camguard.R;
import com.example.camguard.UI.Admin.AdminActivity;
import com.example.camguard.UI.GoogleMaps.FragmentMap;
import com.example.camguard.UI.Login.MainActivity;
import com.example.camguard.UI.User.UserActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

import io.grpc.Context;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView;
    Button btnSubmit;
    EditText etReport;
    Bitmap photo;
    Intent intent;
    static BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        imageView = findViewById(R.id.ivCamera);
        imageView.setTag("NoPic");
        btnSubmit = findViewById(R.id.btnSubmit);
        etReport = findViewById(R.id.etReport);

        btnSubmit.setOnClickListener(this);
        imageView.setClickable(true);
        imageView.setOnClickListener(this);



        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if(CurrentUser.getEmail().equals("s16131@nhs.co.il"))
        {
                    bottomNavigationView.getMenu().clear();
                    bottomNavigationView.inflateMenu(R.menu.admin_bottom_navigation_menu);
        }


        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.menu_account)
            {
                intent = new Intent(CameraActivity.this, UserActivity.class);
                startActivity(intent);
            }
            else if(item.getItemId() == R.id.menu_map)
            {
                Intent intent = new Intent(CameraActivity.this, FragmentMap.class);
                startActivity(intent);
            }
            else if(item.getItemId() == R.id.menu_admin)
            {
                intent = new Intent(CameraActivity.this, AdminActivity.class);
                startActivity(intent);
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_camera);
    }
    ActivityResultLauncher<Intent> CameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        photo = (Bitmap) result.getData().getExtras().get("data");
                        imageView.setImageBitmap(photo);
                        imageView.setTag("Pic");


                        int height = Integer.parseInt(String.valueOf(Math.round(getBaseContext().getResources().getDisplayMetrics().density * 320)));
                        int width = Integer.parseInt(String.valueOf(Math.round(getBaseContext().getResources().getDisplayMetrics().density * 240)));
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
                        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        imageView.setLayoutParams(params);

                    }
                }
            });


    ActivityResultLauncher<Intent> GalleryResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        if(!getContentResolver().getType((result.getData().getData())).startsWith("image/"))
                        {
                            Toast.makeText(CameraActivity.this, "Images only!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                            Uri uriPhoto = result.getData().getData();
                            imageView.setImageURI(uriPhoto);
                        try {
                            photo = Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), uriPhoto),240,320, false);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        imageView.setTag("Pic");


                        int height = Integer.parseInt(String.valueOf(Math.round(getBaseContext().getResources().getDisplayMetrics().density * 320)));
                        int width = Integer.parseInt(String.valueOf(Math.round(getBaseContext().getResources().getDisplayMetrics().density * 240)));
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
                        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        imageView.setLayoutParams(params);

                    }
                }
            });
    ActivityResultLauncher<PickVisualMediaRequest> OlderGalleryResultActivity =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    // There are no request codes
                    imageView.setImageURI(uri);
                    try {
                        photo = Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), 240, 320, false);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    imageView.setTag("Pic");


                    int height = Integer.parseInt(String.valueOf(Math.round(getBaseContext().getResources().getDisplayMetrics().density * 320)));
                    int width = Integer.parseInt(String.valueOf(Math.round(getBaseContext().getResources().getDisplayMetrics().density * 240)));
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    imageView.setLayoutParams(params);
                }
            });


    @Override
    public void onClick(View view) {
        if(view == imageView)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Which way would you prefer")
                    .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           if(ActivityCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                               Intent galleryIntent = new Intent(MediaStore.ACTION_PICK_IMAGES);
                               try {
                               GalleryResultLauncher.launch(galleryIntent);
                               } catch (Exception e)
                               {
                                   OlderGalleryResultActivity.launch(new PickVisualMediaRequest.Builder()
                                           .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                           .build());
                               }
                           }
                        }
                    })
                    .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            CameraResultLauncher.launch(cameraIntent);


                        }
                    }).show();

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
            intent.putExtra("Description", etReport.getText().toString().trim());
            intent.putExtra("Picture", photo);
            intent.putExtra("NewReport", true);
            startActivity(intent);
        }
    }

}