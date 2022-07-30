package com.builderline.whereisit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Recognition implements ImageAnalysis.Analyzer {

    Context context;
    TextRecognizer recognizer;
    RecognizedHandler handler;


    public Recognition(RecognizedHandler handler) {
        this.handler = handler;
        this.recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        @SuppressLint("UnsafeOptInUsageError")
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            int rotationDeg = imageProxy.getImageInfo().getRotationDegrees();
            InputImage image = InputImage.fromMediaImage(mediaImage, rotationDeg);

            Task<Text> result =
                    recognizer.process(image)
                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(@NonNull Text visionText) {
                                    //if (((MainActivity)context).isTimeout) return;

                                    String text = visionText.getText();
                                    List<Text.TextBlock> blocks = visionText.getTextBlocks();

                                    if (!text.isEmpty()) {
                                        Rect rectRecognizer = handler.getRecognizerRect();
                                        for (Text.TextBlock block : blocks) {

                                            Rect blockRect = block.getBoundingBox();
                                            if (rectRecognizer.contains(blockRect)) {

                                                Geo geo = coordinatesFind(block.getText());
                                                if (geo!=null){
                                                    handler.handle(geo, blockRect);
                                                }
                                            }
                                        }



                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<Text>() {
                                @Override
                                public void onComplete(@NonNull Task<Text> task) {
                                    mediaImage.close();
                                    imageProxy.close();
                                }
                            });
        }
    }


    Geo coordinatesFind(String text){

        // -67.344596 32.4355665
        Pattern pattern = Pattern.compile("-?\\d{1,3}.?,?\\d{0,20},?\\s?-?\\d{1,3}.?,?\\d{0,20}");
        Matcher matcher = pattern.matcher(text);

        Geo geo = null;

        if (matcher.find()) {
            String findCoordinates = matcher.group(0);

            assert findCoordinates != null;
            String[] coords = findCoordinates.split("\\s");
            if (coords.length == 2) {
                try {
                    float lat = Float.parseFloat(String.format(coords[0].replaceAll(",", ""), "%.7f"));
                    float lng = Float.parseFloat(String.format(coords[1], "%.7f"));

                    geo = new Geo(lat, lng, String.format("x: %s\ny: %s", lat, lng));

                } catch (Exception ignored) {
                }
            }
        }


        // 83°22'11.25", 24°13'54.024"
        pattern = Pattern.compile("-?\\d{1,3}°?\\s?\\d{1,2}'?\\s?\\d{1,2}.?\\d{0,3}\"?'{0,2}N?n?S?s?,?\\s?-?\\d{1,3}°?\\s?\\d{1,2}'?\\s?\\d{1,2}.?\\d{0,3}\"?'{0,2}E?e?W?w?");
        matcher = pattern.matcher(text);
        if (matcher.find()) {

            String findCoordinates = matcher.group(0);

            assert findCoordinates != null;

            String latZ = "N";
            String lngZ = "E";
            int latZI = 1;
            int lngZI = 1;
            if (findCoordinates.contains("S") || findCoordinates.contains("s")){
                latZ = "S";
                latZI = -1;

            }
            if (findCoordinates.contains("W") || findCoordinates.contains("w")) {
                lngZ = "W";
                lngZI = -1;
            }

            findCoordinates = findCoordinates.replaceAll("[°']", " ")
                    .replaceAll("[^0-9\\s.-]", "");

            String[] coords = findCoordinates.split("\\s");
            if (coords.length == 6) {


                String latS = String.format("%s°%s'%s\"%s", coords[0], coords[1], coords[2], latZ);
                String lngS = String.format("%s°%s'%s\"%s", coords[3], coords[4], coords[5], lngZ);

                try {
                    Geo geoTemp = Geo.calcFromGeographicToDeс(coords);
                    float lat = geoTemp.getLat()*latZI;
                    float lng = geoTemp.getLng()*lngZI;

                    geo = new Geo(lat, lng, String.format(String.format("x: %s\ny: %s", latS, lngS)));

                } catch (Exception ignored) {
                }


            }

        }

        // 66-54346 52-653386
        pattern = Pattern.compile("\\d{2,3}-?\\d{5}.?\\d{0,10},?\\s?y?Y?:?=?\\d{2,3}-?\\d{5}.?\\d{0,10}");
        matcher = pattern.matcher(text);
        if (matcher.find()) {
            String findCoordinates = matcher.group(0);

            assert findCoordinates != null;

            findCoordinates = findCoordinates.replaceAll("[^0-9\\s.]", "");

            String[] coords = findCoordinates.split("\\s");
            if (coords.length == 2) {
                Geo geoTemp = Geo.calcFromRectToDeс(coords);
                float lat = geoTemp.getLat();
                float lng = geoTemp.getLng();

                geo = new Geo(lat, lng, String.format("x: %s\ny: %s", coords[0], coords[1]));


            }


        }

        return geo;
    }

}