package com.builderline.whereisit;

import android.graphics.Rect;

public interface RecognizedHandler {

    void handle(Geo geo, Rect rect);
    Rect getRecognizerRect();

}
