package com.example.whereisit;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class STApp extends Application {
    @Override
    public void onCreate() {
        MapKitFactory.setApiKey("11f74694-ed89-4d86-8338-b59d918a44c0"); // апи ключ
        super.onCreate();

    }
}
