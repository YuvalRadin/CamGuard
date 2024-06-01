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

    // UI elements
    BottomNavigationView bottomNavigationView;
    Button btnDeleteAllUsers, btnDeleteUser, btnDeleteAllMarkers, btnDeleteMarker, btnDeleteMarkerByDesc;
    EditText etDeleteUser, etDeleteMarker, etDeleteMarkerByDesc;

    // Module for administrative operations
    ModuleAdmin module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize UI elements
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

        // Set click listeners
        btnDeleteMarker.setOnClickListener(this);
        btnDeleteUser.setOnClickListener(this);
        btnDeleteAllUsers.setOnClickListener(this);
        btnDeleteAllMarkers.setOnClickListener(this);
        btnDeleteMarkerByDesc.setOnClickListener(this);

        // Set bottom navigation item selected listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_account) {
                Intent intent = new Intent(AdminActivity.this, UserActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_camera) {
                Intent intent = new Intent(AdminActivity.this, CameraActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.menu_map) {
                Intent intent = new Intent(AdminActivity.this, FragmentMap.class);
                startActivity(intent);
            }
            return true;
        });

        // Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.menu_admin);
    }

    @Override
    public void onClick(View view) {
        if (view == btnDeleteAllUsers) {
            // Show confirmation dialog to delete all users
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setMessage("Are you sure you want to proceed, this will delete your account!")
                    .setPositiveButton("Yes, I am sure", (dialogInterface, i) -> {
                        module.deleteAllData();
                        module.deleteAllFireStoreUsers();
                        module.DoNotRemember();
                        Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                        Toast.makeText(getBaseContext(), "All data has been successfully deleted", Toast.LENGTH_SHORT).show();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("No Don't", (dialogInterface, i) -> {})
                    .show();
        } else if (view == btnDeleteUser) {
            // Handle delete user operation
            if (!module.FindUser(etDeleteUser.getText().toString())) {
                if (CurrentUser.getName().equals(etDeleteUser.getText().toString())) {
                    // Show confirmation dialog to delete current user
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setCancelable(false);
                    dialog.setMessage("Are you sure you want to proceed, this will delete your account!")
                            .setPositiveButton("Yes, I am sure", (dialogInterface, i) -> {
                                module.deleteOneRow(CurrentUser.getId());
                                module.deleteFireStoreUser(CurrentUser.getName());
                                module.DoNotRemember();
                                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            })
                            .setNegativeButton("No Don't", (dialogInterface, i) -> {})
                            .show();
                } else {
                    String UserToDelete = module.getUserByName(etDeleteUser.getText().toString()).getString(0);
                    module.deleteOneRow(UserToDelete);
                    module.deleteFireStoreUser(CurrentUser.getName());
                }
            } else {
                Toast.makeText(this, "User Does Not Exist!", Toast.LENGTH_SHORT).show();
            }
            etDeleteUser.setText("");
        } else if (view == btnDeleteMarker) {
            // Handle delete marker by ID operation
            module.deleteMarkerByID(etDeleteMarker.getText().toString());
            etDeleteMarker.setText("");
        } else if (view == btnDeleteAllMarkers) {
            // Handle delete all markers operation
            module.deleteAllMarkers();
        } else if (view == btnDeleteMarkerByDesc) {
            // Handle delete marker by description operation
            module.deleteMarkerByDesc(etDeleteMarkerByDesc.getText().toString());
            etDeleteMarkerByDesc.setText("");
        }
    }
}