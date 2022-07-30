package com.example.whereisit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;


import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements RecognizedHandler {

    private final int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};

    PreviewView mPreviewView;
    ImageCapture imageCapture;
    TextView coordinatesText;
    CoordinatesClickListener coordinatesClickListener;
    View cameraRect;
    TextView rectText;
    ImageAnalysis imageAnalysis;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreviewView = findViewById(R.id.camera);
        coordinatesText = findViewById(R.id.coordinates_text);

        coordinatesClickListener = new CoordinatesClickListener(this);
        coordinatesText.setOnClickListener(coordinatesClickListener);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        cameraRect = findViewById(R.id.camera_rect);

        rectText = findViewById(R.id.rectText);

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Rect getRecognizerRect() {

        int actionBarHeight = 160;

        // параметры дисплея
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // пропорции камеры
        @SuppressLint("RestrictedApi") Size size = imageAnalysis.getAttachedSurfaceResolution();

        // локация и размер прямогугольника-сканирования
        Rect rect = new Rect();
        cameraRect.getLocalVisibleRect(rect);
        int[] location = new int[2];
        cameraRect.getLocationOnScreen(location);

        int rectMarginLeft = (int) (location[0] / metrics.density); // 64/2
        int rectWidth = (int) (rect.right/metrics.density); // 64/2
        int recMarginTop = (int) (location[1]/metrics.density);
        int rectHeight = (int) (rect.bottom/metrics.density);

        // вычисляем "темную" зону, невидимую на экране
        int lightZoneSize = (int) (metrics.widthPixels / metrics.density); // H: 720/2 = 360 или V: 1440/2 = 720
        assert size != null;
        int darkZoneSize = (int) Math.abs ((size.getHeight() - lightZoneSize) / 2); // H: abs((480-360)/2) = 60 или V: abs((480-720)/2) = 120


        Rect rectResult = null;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            recMarginTop =  (int) ((location[1] - actionBarHeight) / metrics.density);
            rectResult = new Rect(darkZoneSize + rectMarginLeft, recMarginTop,darkZoneSize + rectMarginLeft + rectWidth, recMarginTop + rectHeight);
        }
        else if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            rectWidth = (int) (rect.right * size.getWidth()/metrics.widthPixels);
            recMarginTop =  (int) ((location[1] - actionBarHeight) / metrics.density);
            rectResult = new Rect(rectMarginLeft, darkZoneSize + recMarginTop, rectMarginLeft + rectWidth, darkZoneSize + recMarginTop + rectHeight );
            int i =1;
        }

        return rectResult;
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, getString(R.string.PERMISSION_NO_GRANTED), Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }


    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException ignored) {
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        imageAnalysis = new ImageAnalysis.Builder()
                .build();

        imageAnalysis.setAnalyzer(Executors.newFixedThreadPool(1), new Recognition(this));

        ImageCapture.Builder builder = new ImageCapture.Builder();

        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();
        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis, imageCapture);

    }


    @Override
    public void handle(Geo geo, Rect rect) {

        //rectText.setText(rect.toString());


        if (coordinatesText.getVisibility() == View.INVISIBLE) {
            coordinatesText.setVisibility(View.VISIBLE);
        }
        coordinatesText.setText(geo.getCoordsString());

        coordinatesClickListener.setCoordinates(geo.getLat(), geo.getLng());


    }


}