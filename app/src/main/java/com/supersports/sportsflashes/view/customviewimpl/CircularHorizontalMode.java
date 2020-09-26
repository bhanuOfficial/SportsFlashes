package com.supersports.sportsflashes.view.customviewimpl;

import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class CircularHorizontalMode implements ItemViewMode {

    private int mCircleOffset = 500;
    private float mDegToRad = 1.0f / 270.0f * (float) Math.PI;
    private float mScalingRatio = 0.0001f;
    private float mTranslationRatio = 0.19f;

    public CircularHorizontalMode() {
    }

    public CircularHorizontalMode(int circleOffset, float degToRad, float scalingRatio, float translationRatio) {
        mCircleOffset = circleOffset;
        mDegToRad = degToRad;
        mScalingRatio = scalingRatio;
        mTranslationRatio = translationRatio;

    }


    @Override
    public void applyToView(View v, RecyclerView parent) {
        float halfWidth = v.getWidth() * 0.5f;
        float parentHalfWidth = parent.getWidth() * 0.5f;
        float x = v.getX();
        float rot = parentHalfWidth - halfWidth - x;

        ViewCompat.setPivotY(v, halfWidth);
        ViewCompat.setPivotX(v, halfWidth);
//        for the view rotation in flot number
        ViewCompat.setRotation(v, -rot * 0.00f);
        ViewCompat.setTranslationY(v, (float) (-Math.cos(rot * mTranslationRatio * mDegToRad) + 1) * mCircleOffset);

        float scale = 1.0f - Math.abs(parentHalfWidth - halfWidth - x) * mScalingRatio;
        ViewCompat.setScaleX(v, scale);
        ViewCompat.setScaleY(v, scale);
    }

}
