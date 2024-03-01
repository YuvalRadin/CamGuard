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

    Context context;
     Uri url;

     String Reporter;

    ImageView imageView;

    public CustomInfoWindowAdapter(Context context, Uri url, String reporter)
    {
        this.context = context;
        this.url = Uri.parse(url.toString());
        View view = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
        this.imageView = view.findViewById(R.id.MarkerImage);
        this.Reporter = reporter;

    }

    public ImageView getImageView() {
        return imageView;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        // Inflate the custom info window layout
        View view = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);


        return view;
    }


    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        // Inflate the custom info window layout
        View view = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);

//         Customize the content of the info window
        ImageView imageView = view.findViewById(R.id.MarkerImage);

        Glide.with(context).load(marker.getTag().toString()).placeholder(context.getDrawable(R.drawable.ic_camera)).centerCrop().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.e("ImageLoading", "Image load failed: " + e.getMessage());
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.d("ImageLoading", "Image load successful " + url.getLastPathSegment().toString());
                imageView.setImageDrawable(resource);

                return false;
            }
        }).into(imageView);


        TextView textView = view.findViewById(R.id.info_window_text);
        textView.setText(marker.getTitle());

        TextView textView2 = view.findViewById(R.id.report_id);
        textView2.append(marker.getSnippet().toString());

        TextView textview3 = view.findViewById(R.id.tvReporter);
        textview3.append(Reporter);

        return view;
    }
}
