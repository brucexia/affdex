package com.affectiva.affdexme.utils.convexhull;

import android.graphics.PointF;

import com.affectiva.affdexme.FaceInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brucexia on 2016-12-22.
 */

public class FaceLandmarks implements FaceInterface {
    public static final int OuterRightEye = 16;
    public static final int InnerRightEye = 17;
    public static final int UpperCornerRightEye = 30;
    public static final int LowerCornerRightEye = 31;

    public static final int InnerLeftEye = 18;
    public static final int OuterLeftEye = 19;
    public static final int UpperCornerLeftEye = 32;
    public static final int LowerCornerLeftEye = 33;

    List<PointF> mFacePoints;

    public FaceLandmarks(List<PointF> facePoints) {
        mFacePoints = facePoints;
    }

    @Override
    public List<PointF> getFacePoints() {
        return mFacePoints;
    }

    public ArrayList<PointF> getLeftEyePoints() {
        ArrayList list = new ArrayList();
        list.add(mFacePoints.get(InnerLeftEye));
        list.add(mFacePoints.get(UpperCornerLeftEye));
        list.add(mFacePoints.get(OuterLeftEye));
        list.add(mFacePoints.get(LowerCornerLeftEye));
        return list;
    }

    public ArrayList<PointF> getRightEyePoints() {
        ArrayList list = new ArrayList();
        list.add(mFacePoints.get(InnerRightEye));
        list.add(mFacePoints.get(UpperCornerRightEye));
        list.add(mFacePoints.get(OuterRightEye));
        list.add(mFacePoints.get(LowerCornerRightEye));
        return list;
    }
    //toDO: add emotions
}
