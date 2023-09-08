package com.tourism.apps;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

public class RoundedOutlineProvider extends ViewOutlineProvider {
    private float cornerRadius;

    public RoundedOutlineProvider(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), cornerRadius);
    }
}
