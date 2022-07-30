package com.builderline.whereisit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

import java.util.Objects;

public class MapActivity extends AppCompatActivity {

    float lat = 0;
    float lng = 0;
    MapView mapview;
    FloatingActionButton googleMapButton;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lat = getIntent().getFloatExtra("lat", 0);
        lng = getIntent().getFloatExtra("lng", 0);

        Toast.makeText(this, String.valueOf(lat) + " " + String.valueOf(lng), Toast.LENGTH_LONG).show();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Map
        MapKitFactory.initialize(this);

        setContentView(R.layout.activity_map);

        mapview = (MapView) findViewById(R.id.mapView);
        mapview.getMap().move(
                new CameraPosition(new Point(lat, lng), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        mapview.getMap().getMapObjects().addPlacemark(new Point(lat, lng));

        googleMapButton = findViewById(R.id.google_map_button);


        Context c = this;
        googleMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri page = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng);
                final Intent intent = new Intent(Intent.ACTION_VIEW, page);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapview.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapview.onStop();
        MapKitFactory.getInstance().onStop();
    }
}