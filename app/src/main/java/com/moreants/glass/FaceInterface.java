package com.moreants.glass;

import android.graphics.PointF;
import android.graphics.RectF;

import com.moreants.glass.facesdk.Appearance;
import com.moreants.glass.facesdk.Emojis;
import com.moreants.glass.facesdk.Emotions;

import java.util.List;

/**
 * Created by brucexia on 2017-01-05.
 */

public interface FaceInterface {
    List<PointF> getFacePoints();

    List<PointF> getLeftEyePoints();

    List<PointF> getRightEyePoints();

    public List<PointF> transformPoints(DrawingViewConfig config, boolean mirrorPoints);

    public RectF getEyesRect();
    public Emotions getEmotions();
    Emojis getEmojis();
    Appearance getAppearance();
}
