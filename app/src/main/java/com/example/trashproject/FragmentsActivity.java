package com.example.trashproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class FragmentsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {


    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.fragmentContainerView);
        mapFragment.getMapAsync(this);

        Fragment accountFragment = new Fragment();

        Fragment cameraFragment = new Fragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                  if(item.getItemId() ==  R.id.menu_map)
                  {
                      fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, mapFragment, null).commit();
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

    }

    @Override
    public void onClick(View view) {




    }
}