package com.moreants.care;

import android.graphics.PointF;
import android.graphics.RectF;

import com.moreants.care.facesdk.Appearance;
import com.moreants.care.facesdk.Emojis;
import com.moreants.care.facesdk.Emotions;

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