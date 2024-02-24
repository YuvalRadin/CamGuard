package com.example.camguard.Data.CustomMarkerAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.camguard.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    Context context;
     Bitmap reportImage;

    public CustomInfoWindowAdapter(Context context, Bitmap bitmap)
    {
        this.context = context;
        this.reportImage = bitmap;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        // Inflate the custom info window layout
        View view = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);

        // Customize the content of the info window
        ImageView imageView = view.findViewById(R.id.MarkerImage);

        // Use Glide to load the image
        Glide.with(context).load(reportImage).into(imageView);
        Glide.with(context).load(reportImage).error(R.drawable.ic_camera);// Set a default error image.into(imageView);

        TextView textView = view.findViewById(R.id.info_window_text);
        textView.setText(marker.getTitle());

        return view;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        // Inflate the custom info window layout
        View view = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);

        // Customize the content of the info window
        ImageView imageView = view.findViewById(R.id.MarkerImage);
        imageView.setImageBitmap(reportImage);  // Set the image resource

        TextView textView = view.findViewById(R.id.info_window_text);
        textView.setText(marker.getTitle());

        return view;
    }
}
