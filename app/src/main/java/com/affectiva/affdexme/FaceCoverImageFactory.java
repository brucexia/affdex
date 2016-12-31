package com.affectiva.affdexme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.affectiva.android.affdex.sdk.detector.Face;

/**
 * Created by brucexia on 2016-12-22.
 */

public class FaceCoverImageFactory {
    Context mContext;

    public FaceCoverImageFactory(Context context) {
        mContext = context;
    }

    public Bitmap getARBitmap(Face face) {
        Face.Emotions emotions = face.emotions;
        if (emotions.getJoy() > 30) {
            return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.angrybird_head);
        }
        return null;
    }
}
