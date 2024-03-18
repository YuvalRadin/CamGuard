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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_gpsactivity);
        enable_gps_continue = findViewById(R.id.enable_gps_continue);

        enable_gps_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //send to settings
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //check if GPS is On
        if (isGPSEnabled())
        {
            Intent intent = new Intent(EnableGPSActivity.this, FragmentMap.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Enable Location", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}