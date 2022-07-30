package com.example.whereisit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class CoordinatesClickListener implements View.OnClickListener {

    float lat = 0;
    float lng = 0;
    Context context;

    public CoordinatesClickListener(Context context) {
        this.context = context;
    }


    public void setCoordinates(float lat, float lng) {
        this.lat = lat;
        this.lng = lng;
    }


    @Override
    public void onClick(View view) {
        if (lat != 0 && lng != 0) {

            Uri page = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng);
            final Intent intent = new Intent(Intent.ACTION_VIEW, page);
            context.startActivity(intent);

//            Intent intent = new Intent(context, MapActivity.class);
//            intent.putExtra("lat", lat);
//            intent.putExtra("lng", lng);
//            context.startActivity(intent);
        }
    }
}
