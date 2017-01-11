package com.moreants.care;

import android.graphics.PointF;
import android.graphics.RectF;

import com.moreants.care.facesdk.AffdexAppearance;
import com.moreants.care.facesdk.AffdexEmojis;
import com.moreants.care.facesdk.AffdexEmotions;
import com.moreants.care.facesdk.Appearance;
import com.moreants.care.facesdk.Emojis;
import com.moreants.care.facesdk.Emotions;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.ArrayList;
import java.util.Arrays;
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

    Face sdkFace;
    List<PointF> mFacePoints;

    public FaceLandmarks(Face face) {
        sdkFace = face;
        mFacePoints = new ArrayList(Arrays.asList(face.getFacePoints()));
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

    public PointF getOuterRightEye() {
        return mFacePoints.get(OuterRightEye);
    }

    public PointF getOuterLeftEye() {
        return mFacePoints.get(OuterLeftEye);
    }

    public RectF getEyesRect() {
        RectF rect = new RectF(mFacePoints.get(OuterLeftEye).x,
                Math.min(mFacePoints.get(UpperCornerLeftEye).y, mFacePoints.get(UpperCornerRightEye).y),
                mFacePoints.get(OuterRightEye).x,
                Math.max(mFacePoints.get(LowerCornerLeftEye).y, mFacePoints.get(LowerCornerRightEye).y));
        return rect;
    }
    //toDO: add emotions


    @Override
    public Emotions getEmotions() {
        return new AffdexEmotions(sdkFace.emotions);
    }

    @Override
    public Emojis getEmojis() {
        return new AffdexEmojis(sdkFace.emojis);
    }

    @Override
    public Appearance getAppearance() {
        return new AffdexAppearance(sdkFace.appearance);
    }

    public List<PointF> transformPoints(DrawingViewConfig config, boolean mirrorPoints) {
        return transformFacePoints(mFacePoints, mirrorPoints, config);
    }

    List<PointF> transformFacePoints(List<PointF> points, boolean mirrorPoints, DrawingViewConfig config) {
        for (PointF p : points) {
            PointF q = getTransformedPointF(p, mirrorPoints, config);
        }
        return mFacePoints;
    }

    PointF getTransformedPointF(PointF result, boolean mirrorPoints, DrawingViewConfig config) {
        if (mirrorPoints) {
            result.x = (config.imageWidth - result.x) * config.screenToImageRatio;
        } else {
            result.x = (result.x) * config.screenToImageRatio;
        }
        result.y = (result.y) * config.screenToImageRatio;
        return result;
    }
}
