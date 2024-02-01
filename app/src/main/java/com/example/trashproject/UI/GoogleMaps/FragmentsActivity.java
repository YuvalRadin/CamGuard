package com.example.trashproject.UI.GoogleMaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.trashproject.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class FragmentsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {


    BottomNavigationView bottomNavigationView;
    GoogleMap mMap;
    Context context;

    LatLng latLng;

    boolean flag = true;

    private FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);
        context = getBaseContext();

        FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.fragmentContainerView);
        mapFragment.getMapAsync(this);


        Fragment accountFragment = new Fragment();

        Fragment cameraFragment = new Fragment();

        mapFragment.setRetainInstance(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() ==  R.id.menu_map)
                {
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, mapFragment, null).commit();
                    getLastLocation();
                }
                else if(item.getItemId() == R.id.menu_account)
                {

                    fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, accountFragment, null).commit();

                }
                else if(item.getItemId() == R.id.menu_camera)
                {
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, cameraFragment, null).commit();

                }
                return true;
            }
         });


    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

       getLastLocation();
    }

    public void getLastLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location)
                    {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latLng = new LatLng(location.getLatitude(),location.getLongitude());

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title("Your Location")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            Toast.makeText(context, latLng.toString() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {




    }
}