package com.affectiva.affdexme.facesdk;

import com.affectiva.android.affdex.sdk.detector.Face;

/**
 * Created by brucexia on 2017-01-07.
 */

public interface Emojis {
    public Face.EMOJI getDominantEmoji();

    public float getSmiley();

    public float getScream();

    public float getLaughing();

    public float getKissing();

    public float getStuckOutTongue();

    public float getDominant();

    public float getRage();

    public float getStuckOutTongueWinkingEye();

    public float getSmirk();

    public float getFlushed();

    public float getRelaxed();

    public float getWink();

    public float getDisappointed();
}
