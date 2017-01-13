package com.moreants.care;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

/**
 * Created by brucexia on 2017-01-06.
 */

public class FaceAnimationV1 {
    Context mContext;
    Bitmap bitmaps[];
    int currentFrameIndex;
    static int colors[] = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GRAY, Color.GREEN, Color.MAGENTA, Color.WHITE, Color.RED, Color.YELLOW};
    private static int NUM_OF_FRAMES = colors.length;

    public FaceAnimationV1(Context context) {
        mContext = context;
    }

    public int getCurrentFrameIndex() {
        if (currentFrameIndex == NUM_OF_FRAMES - 1) {
            currentFrameIndex = 0;
        } else {
            currentFrameIndex++;
        }
        return currentFrameIndex;
    }

    public Paint getCurrentStrokePaint(int index) {
        Paint paint3 = new Paint();
        paint3.setColor(ContextCompat.getColor(mContext, R.color.face_oval_color));
        paint3.setStyle(Paint.Style.STROKE);
        paint3.setStrokeWidth(5);
        paint3.setStrokeJoin(Paint.Join.ROUND);
        return paint3;
    }
}
