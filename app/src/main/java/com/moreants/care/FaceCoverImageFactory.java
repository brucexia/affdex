package com.moreants.care;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by brucexia on 2016-12-22.
 */

public class FaceCoverImageFactory {
    Context mContext;

    public FaceCoverImageFactory(Context context) {
        mContext = context;
    }

    public Bitmap getARBitmap(FaceInterface face) {
        return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pikachu);
    }
}
