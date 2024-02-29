package com.example.camguard.Data.CustomMarker;

import android.net.Uri;

import com.example.camguard.Data.CustomMarkerAdapter.CustomInfoWindowAdapter;
import com.google.android.gms.maps.model.LatLng;

public class CustomMarker {
    private LatLng latLng;
    private String description;
    private CustomInfoWindowAdapter customInfoWindowAdapter;
    private Uri uri;

    public CustomMarker(LatLng latLng, String description, CustomInfoWindowAdapter customInfoWindowAdapter, Uri uri) {

        this.latLng = latLng;
        this.description = description;
        this.customInfoWindowAdapter = customInfoWindowAdapter;
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CustomInfoWindowAdapter getCustomInfoWindowAdapter() {
        return customInfoWindowAdapter;
    }

    public void setCustomInfoWindowAdapter(CustomInfoWindowAdapter customInfoWindowAdapter) {
        this.customInfoWindowAdapter = customInfoWindowAdapter;
    }


}
