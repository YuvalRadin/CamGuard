package com.example.camguard.UI.GoogleMaps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.camguard.Data.CustomMarkerAdapter.CustomInfoWindowAdapter;
import com.example.camguard.R;
import com.example.camguard.UI.Camera.CameraActivity;
import com.example.camguard.UI.User.UserActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class FragmentMap extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {


    BottomNavigationView bottomNavigationView;
    static GoogleMap mMap;
    Context context;
    static LatLng latLng;
    Bitmap reportImage;
    moduleMap module;
    boolean isNewReport;



    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);
        context = getBaseContext();

        FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.bottom_navigation);


        module = new moduleMap(this);
        Intent intent = new Intent(FragmentMap.this, UserActivity.class);
        Intent intent2 = new Intent(FragmentMap.this, CameraActivity.class);
        if (!module.DoesRemember() && !module.getCredentials()[0].equals("")) {
            intent.putExtra("username", module.getCredentials()[0]);
            intent.putExtra("email", module.getCredentials()[1]);
            intent2.putExtra("username", module.getCredentials()[0]);
            intent2.putExtra("email", module.getCredentials()[1]);
            module.DoNotRemember();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.fragmentContainerView);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_account) {
                startActivity(intent);
            } else if (item.getItemId() == R.id.menu_camera) {
                startActivity(intent2);
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_map);

        isNewReport = getIntent().getBooleanExtra("NewReport", false);
        if (isNewReport) {
            reportImage = (Bitmap) getIntent().getParcelableExtra("Picture");
            Toast.makeText(context, "report added successfully", Toast.LENGTH_SHORT).show();
        }




    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;



        CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter(this, reportImage);
        mMap.setInfoWindowAdapter(customInfoWindowAdapter);
        getLastLocation();
        if (isNewReport) {
            addMarker();
        }

    }

    public void getLastLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(!mMap.isMyLocationEnabled()) {
            mMap.setMyLocationEnabled(true);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    }
                });
    }

    public void addMarker()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(!mMap.isMyLocationEnabled()) {
            mMap.setMyLocationEnabled(true);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(getIntent().getStringExtra("Description")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        mMap.addMarker(markerOptions);
                    }
                });
    }



    @Override
    public void onClick(View view) {




    }


}