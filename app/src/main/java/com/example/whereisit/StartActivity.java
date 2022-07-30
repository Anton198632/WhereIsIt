package com.example.whereisit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.example.whereisit.animation.AnimationHandler;

public class StartActivity extends AppCompatActivity {

    ImageView phoneImage;
    ImageView screenPhoneImage;
    ImageView screenImage;
    ImageView finderImage;
    Button startButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        phoneImage = findViewById(R.id.phone_image);
        screenPhoneImage = findViewById(R.id.screen_phone_image);
        screenImage = findViewById(R.id.screen_image);
        finderImage = findViewById(R.id.finder_image);

        startButton = findViewById(R.id.start_button);

        Context context = this;
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        });


        animationStart();


    }


    private void animationStart() {
        screenPhoneImage.setVisibility(View.VISIBLE);
        finderImage.setVisibility(View.INVISIBLE);

        AnimationDrawable animationDrawable = (AnimationDrawable) screenImage.getDrawable();
        animationDrawable.selectDrawable(0);

        TranslateAnimation translateAnimation = new TranslateAnimation(-2000, 0, 2000, 0);
        translateAnimation.setDuration(2000);
        translateAnimation.setAnimationListener(new AnimationHandler() {

            @Override
            public void onAnimationEnd(Animation animation) {

                animationDrawable.selectDrawable(1);

                // перемещение пальца
                TranslateAnimation translateAnimationFinder = new TranslateAnimation(2000, 0, 2000, 0);
                translateAnimationFinder.setDuration(1500);
                translateAnimationFinder.setAnimationListener(new AnimationHandler() {

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        animationDrawable.selectDrawable(2);

                        screenPhoneImage.setVisibility(View.INVISIBLE);
                        TranslateAnimation translateAnimationFinderPost = new TranslateAnimation(0, 2000, 0, 2000);
                        translateAnimationFinderPost.setAnimationListener(new AnimationHandler() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                finderImage.setVisibility(View.INVISIBLE);
                                startButton.setVisibility(View.VISIBLE);
                                animationStart();
                            }

                        });

                        translateAnimationFinderPost.setDuration(1500);
                        finderImage.startAnimation(translateAnimationFinderPost);
                    }




                });

                finderImage.startAnimation(translateAnimationFinder);
                finderImage.setVisibility(View.VISIBLE);

            }

        });

        phoneImage.startAnimation(translateAnimation);
        screenPhoneImage.startAnimation(translateAnimation);
    }

}