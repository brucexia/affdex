package com.affectiva.affdexme.facesdk;

import com.affectiva.android.affdex.sdk.detector.Face;

/**
 * Created by brucexia on 2017-01-07.
 */

public class AffdexEmojis implements Emojis{
    Face.Emojis sdkEmojis;

    public AffdexEmojis(Face.Emojis sdkEmojis) {
        this.sdkEmojis = sdkEmojis;
    }

    public Face.EMOJI getDominantEmoji() {
        return sdkEmojis.getDominantEmoji();
    }

    public float getSmiley() {
        return sdkEmojis.getSmiley();
    }

    public float getScream() {
        return sdkEmojis.getScream();
    }

    public float getLaughing() {
        return sdkEmojis.getLaughing();
    }

    public float getKissing() {
        return sdkEmojis.getKissing();
    }

    public float getStuckOutTongue() {
        return sdkEmojis.getStuckOutTongue();
    }

    public float getDominant() {
        return sdkEmojis.getDominant();
    }

    public float getRage() {
        return sdkEmojis.getRage();
    }

    public float getStuckOutTongueWinkingEye() {
        return sdkEmojis.getStuckOutTongueWinkingEye();
    }

    public float getSmirk() {
        return sdkEmojis.getSmirk();
    }

    public float getFlushed() {
        return sdkEmojis.getFlushed();
    }

    public float getRelaxed() {
        return sdkEmojis.getRelaxed();
    }

    public float getWink() {
        return sdkEmojis.getWink();
    }

    public float getDisappointed() {
        return sdkEmojis.getDisappointed();
    }
}
