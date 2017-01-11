package com.moreants.care.utils;

import android.graphics.PointF;

/**
 * Created by brucexia on 2016-12-20.
 */

public class Geomath {
    public static PointF centerCoordinate(PointF[] points) {
        float centroidX = 0, centroidY = 0;

        for (PointF knot : points) {
            centroidX += knot.x;
            centroidY += knot.y;
        }
        return new PointF(centroidX / points.length, centroidY / points.length);
    }

}
