package com.example.camguard.UI.Admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.camguard.Data.CurrentUser.CurrentUser;
import com.example.camguard.R;
import com.example.camguard.UI.Camera.CameraActivity;
import com.example.camguard.UI.GoogleMaps.FragmentMap;
import com.example.camguard.UI.Login.MainActivity;
import com.example.camguard.UI.User.UserActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    BottomNavigationView bottomNavigationView;
    Button btnDeleteAllUsers, btnDeleteUser, btnDeleteAllMarkers, btnDeleteMarker, btnDeleteMarkerByDesc;
    EditText etDeleteUser, etDeleteMarker, etDeleteMarkerByDesc;
    ModuleAdmin module;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);



        bottomNavigationView = findViewById(R.id.bottom_navigation);
        module = new ModuleAdmin(this);

        btnDeleteAllUsers = findViewById(R.id.btnDeleteUsers);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);
        etDeleteUser = findViewById(R.id.etDeleteUser);
        btnDeleteAllMarkers = findViewById(R.id.btnDeleteMarkers);
        btnDeleteMarker = findViewById(R.id.btnDeleteMarker);
        etDeleteMarker = findViewById(R.id.etDeleteMarker);
        btnDeleteMarkerByDesc = findViewById(R.id.btnDeleteMarkerByDesc);
        etDeleteMarkerByDesc = findViewById(R.id.etDeleteMarkerByDesc);

        btnDeleteMarker.setOnClickListener(this);
        btnDeleteUser.setOnClickListener(this);
        btnDeleteAllUsers.setOnClickListener(this);
        btnDeleteAllMarkers.setOnClickListener(this);
        btnDeleteMarkerByDesc.setOnClickListener(this);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.menu_account)
            {
                Intent intent = new Intent(AdminActivity.this, UserActivity.class);
                startActivity(intent);
            }
            else if(item.getItemId() == R.id.menu_camera)
            {
                Intent intent = new Intent(AdminActivity.this, CameraActivity.class);
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

    @Override
    public void onClick(View view) {
        if(view == btnDeleteAllUsers)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setMessage("Are you sure you want to proceed, this will delete your account!")
                    .setPositiveButton("Yes, I am sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            module.deleteAllData();
                            module.DeleteAllFireStoreData();
                            module.DoNotRemember();
                            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                            Toast.makeText(getBaseContext(), "All data has been successfully deleted", Toast.LENGTH_SHORT).show();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No Don't", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();

        }
        if(view == btnDeleteUser)
        {
            if(!module.FindUser(etDeleteUser.getText().toString())) {
                if(CurrentUser.getName().equals(etDeleteUser.getText().toString()))
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setCancelable(false);
                    dialog.setMessage("Are you sure you want to proceed, this will delete your account!")
                            .setPositiveButton("Yes, I am sure", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    module.deleteOneRow(CurrentUser.getId());
                                    module.DeleteFireStoreUser(CurrentUser.getName());
                                    module.DoNotRemember();
                                    Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No Don't", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();
                }
                else {
                    String UserToDelete = module.getUserByName(etDeleteUser.getText().toString()).getString(0);
                    module.deleteOneRow(UserToDelete);
                    module.DeleteFireStoreUser(CurrentUser.getName());
                }
            }
            else Toast.makeText(this, "User Does Not Exist!", Toast.LENGTH_SHORT).show();
            etDeleteUser.setText("");
        }
        if(view == btnDeleteMarker)
        {
            module.DeleteMarkerByID(etDeleteMarker.getText().toString());
            etDeleteMarker.setText("");
        }
        if(view == btnDeleteAllMarkers)
        {
            module.DeleteAllMarkers();
        }
        if(view == btnDeleteMarkerByDesc)
        {
            module.DeleteMarkerByDesc(etDeleteMarkerByDesc.getText().toString());
            etDeleteMarkerByDesc.setText("");
        }
    }
}