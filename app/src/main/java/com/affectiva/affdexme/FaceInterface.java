package com.affectiva.affdexme;

import android.graphics.PointF;
import android.graphics.RectF;

import com.affectiva.affdexme.facesdk.Appearance;
import com.affectiva.affdexme.facesdk.Emojis;
import com.affectiva.affdexme.facesdk.Emotions;
import com.affectiva.android.affdex.sdk.detector.Face;

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
