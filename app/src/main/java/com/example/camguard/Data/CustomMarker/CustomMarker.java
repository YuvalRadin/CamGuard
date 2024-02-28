package com.example.camguard.Data.CustomMarker;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class CustomMarker {
    private LatLng position;
    private String title;
    private Uri imageUrl;

    public CustomMarker(LatLng position, String title, Uri imageUrl) {
        this.position = position;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }
}
