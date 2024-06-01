package com.example.camguard.UI.EnableGPS;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.camguard.R;
import com.example.camguard.UI.GoogleMaps.FragmentMap;

public class EnableGPSActivity extends AppCompatActivity {

    Button enable_gps_continue;

    /**
     * Initializes the activity when created.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_gpsactivity);
        enable_gps_continue = findViewById(R.id.enable_gps_continue);

        enable_gps_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send to settings to enable GPS
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
    }

    /**
     * Handles the activity when it is restarted.
     */
    @Override
    protected void onRestart() {
        super.onRestart();

        // Check if GPS is enabled
        if (isGPSEnabled()) {
            Intent intent = new Intent(EnableGPSActivity.this, FragmentMap.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Enable Location", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks if GPS is enabled.
     *
     * @return True if GPS is enabled, false otherwise.
     */
    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}