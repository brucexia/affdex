package com.moreants.glass;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by brucexia on 2017-01-07.
 */
public class DrawingViewConfig {
    public int imageWidth = 1;
    public int surfaceViewWidth = 0;
    public int surfaceViewHeight = 0;
    public float screenToImageRatio = 0;
    public int drawThickness = 0;
    public boolean isDrawPointsEnabled = true; //by default, have the drawing thread draw tracking dots
    public boolean isDimensionsNeeded = true;
    public boolean isDrawAppearanceMarkersEnabled = true; //by default, draw the appearance markers
    public boolean isDrawEmojiMarkersEnabled = true; //by default, draw the dominant emoji markers

    public Paint dominantEmotionLabelPaint;
    public Paint dominantEmotionMetricBarPaint;
    public Paint dominantEmotionValuePaint;
    public Paint faceOvalPaint;
    public int metricBarWidth;
    public float minFaceCoverWidth;

    public void setDominantEmotionLabelPaints(Paint labelPaint, Paint valuePaint) {
        dominantEmotionLabelPaint = labelPaint;
        dominantEmotionValuePaint = valuePaint;
    }

    public void setDominantEmotionMetricBarConfig(Paint metricBarPaint, int metricBarWidth) {
        dominantEmotionMetricBarPaint = metricBarPaint;
        this.metricBarWidth = metricBarWidth;
    }

    public void updateViewDimensions(int surfaceViewWidth, int surfaceViewHeight, int imageWidth, int imageHeight) {
        if (surfaceViewWidth <= 0 || surfaceViewHeight <= 0 || imageWidth <= 0 || imageHeight <= 0) {
            throw new IllegalArgumentException("All dimensions submitted to updateViewDimensions() must be positive");
        }
        this.imageWidth = imageWidth;
        this.surfaceViewWidth = surfaceViewWidth;
        this.surfaceViewHeight = surfaceViewHeight;
        screenToImageRatio = (float) surfaceViewWidth / imageWidth;
        isDimensionsNeeded = false;
        setDrawThickness(Math.min(surfaceViewWidth, surfaceViewHeight) / 200);
        faceOvalPaint = new Paint();
        faceOvalPaint.setStyle(Paint.Style.STROKE);
        faceOvalPaint.setStrokeWidth(drawThickness);
        faceOvalPaint.setColor(Color.RED);
        minFaceCoverWidth = Math.min(surfaceViewWidth, surfaceViewHeight) / 5;
    }

    public void setDrawThickness(int t) {

        if (t <= 0) {
            throw new IllegalArgumentException("Thickness must be positive.");
        }

        drawThickness = t;
    }
}
