package com.moreants.glass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;

/**
 * Created by brucexia on 2017-01-06.
 */

public class EyeAnimation {
    Context mContext;
    Bitmap bitmaps[];
    int currentFrameIndex;
    static int colors[] = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GRAY, Color.GREEN};
    private static int NUM_OF_FRAMES = colors.length;
    static int ANIMATION_INTERVAL = 100;
    long lastFrameTime;

    public EyeAnimation(Context context) {
        mContext = context;
    }

    public int getCurrentFrameIndex() {
        long currentTime = SystemClock.elapsedRealtime();
        if (lastFrameTime == 0) {
            lastFrameTime = currentTime;
        } else if (currentTime - lastFrameTime < ANIMATION_INTERVAL) {
            return currentFrameIndex;
        } else {
            if (currentFrameIndex == NUM_OF_FRAMES - 1) {
                currentFrameIndex = 0;
            } else {
                currentFrameIndex++;
            }
            lastFrameTime = currentTime;
        }
        return currentFrameIndex;
    }

    public Paint getCurrentStrokePaint(int index) {
        Paint paint3 = new Paint();
        paint3.setColor(colors[index]);
        paint3.setStyle(Paint.Style.STROKE);
        paint3.setStrokeWidth(5);
        paint3.setStrokeJoin(Paint.Join.ROUND);
        return paint3;
    }

    public void reset() {
        lastFrameTime = 0;
        currentFrameIndex = 0;
    }
}
