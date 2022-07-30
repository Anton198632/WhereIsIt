package com.example.whereisit;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class TransferAnim implements Animation.AnimationListener {

    View view;
    float fromXDelta;
    float toXDelta;
    float fromYDelta;
    float toYDelta;
    int duration;

    public TransferAnim(View view, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, int duration) {
        this.view = view;
        this.fromXDelta = fromXDelta;
        this.toXDelta = toXDelta;
        this.fromYDelta = fromYDelta;
        this.toYDelta = toYDelta;
        this.duration = duration;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
//        TranslateAnimation translateAnimation = new TranslateAnimation(
//                fromXDelta, toXDelta, fromYDelta,  toXDelta
//        );
//        translateAnimation.setDuration(duration);
//        view.startAnimation(translateAnimation);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}