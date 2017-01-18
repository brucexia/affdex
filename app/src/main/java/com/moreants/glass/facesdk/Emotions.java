package com.moreants.glass.facesdk;

import android.util.Pair;

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
