package com.example.camguard.Data.CustomMarkerAdapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.camguard.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;


public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    // Context reference for accessing resources and system services
    Context context;

    // URI for the image URL
    Uri url;

    // ImageView for displaying marker image
    ImageView imageView;

    /**
     * Constructor for CustomInfoWindowAdapter.
     * @param context The context of the calling activity or fragment.
     * @param url The URI of the marker image.
     */
    public CustomInfoWindowAdapter(Context context, Uri url) {
        this.context = context;
        this.url = Uri.parse(url.toString());

        // Inflate the custom info window layout and initialize ImageView
        View view = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
        this.imageView = view.findViewById(R.id.MarkerImage);
    }

    /**
     * Retrieves the ImageView associated with the custom info window layout.
     * @return The ImageView associated with the custom info window.
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Called when the contents of the InfoWindow are requested.
     * @param marker The marker for which the InfoWindow is being populated.
     * @return The contents of the InfoWindow as a View.
     */
    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        // Inflate the custom info window layout
        View view = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
        return view;
    }

    /**
     * Called when the entire InfoWindow is requested.
     * @param marker The marker for which the InfoWindow is being populated.
     * @return The entire InfoWindow as a View.
     */
    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        // Inflate the custom info window layout
        View view = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);

        // Retrieve references to views within the custom info window layout
        ImageView imageView = view.findViewById(R.id.MarkerImage);
        TextView textView = view.findViewById(R.id.info_window_text);
        TextView textView2 = view.findViewById(R.id.report_id);
        TextView textView3 = view.findViewById(R.id.tvReporter);

        // Load marker image using Glide library
        Glide.with(context)
                .load(marker.getTag().toString())
                .placeholder(context.getDrawable(R.drawable.ic_camera))
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Log error if image loading fails
                        Log.e("ImageLoading", "Image load failed: " + e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Log success and set the image drawable
                        Log.d("ImageLoading", "Image load successful " + url.getLastPathSegment().toString());
                        imageView.setImageDrawable(resource);
                        return false;
                    }
                })
                .into(imageView);

        // Set text for other views in the info window
        textView.setText(marker.getTitle());
        textView2.append(marker.getSnippet().toString().substring(0, marker.getSnippet().indexOf(" ")));
        textView3.setText(marker.getSnippet().substring(marker.getSnippet().indexOf(" ") + 1));

        return view;
    }
}
