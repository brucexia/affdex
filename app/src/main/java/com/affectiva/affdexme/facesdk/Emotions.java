package com.affectiva.affdexme.facesdk;

import android.util.Pair;

import com.affectiva.affdexme.FaceInterface;
import com.affectiva.affdexme.MetricsManager;

/**
 * Created by brucexia on 2017-01-07.
 */

public interface Emotions {
    public float getValence();
    public float getAnger();
    public float getContempt();
    public float getDisgust();
    public float getFear();
    public float getJoy();
    public float getSadness();
    public float getSurprise();
    public Pair<String, Float> findDominantEmotion() ;

}
