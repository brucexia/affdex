package com.affectiva.affdexme;

import android.graphics.PointF;

import java.util.List;

/**
 * Created by brucexia on 2017-01-05.
 */

public interface FaceInterface {
    List<PointF> getFacePoints();

    List<PointF> getLeftEyePoints();

    List<PointF> getRightEyePoints();
}
