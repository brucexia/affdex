package com.moreants.care.facesdk;

import android.util.Pair;

import com.moreants.care.MetricsManager;
import com.affectiva.android.affdex.sdk.detector.Face;

/**
 * Created by brucexia on 2017-01-07.
 */

public class AffdexEmotions implements Emotions {
    Face.Emotions emotions;

    public AffdexEmotions(Face.Emotions emotions) {
        this.emotions = emotions;
    }

    @Override
    public float getValence() {
        return emotions.getValence();
    }

    @Override
    public float getAnger() {
        return emotions.getAnger();
    }

    @Override
    public float getContempt() {
        return emotions.getContempt();
    }

    @Override
    public float getDisgust() {
        return emotions.getDisgust();
    }

    @Override
    public float getFear() {
        return emotions.getFear();
    }

    @Override
    public float getJoy() {
        return emotions.getJoy();
    }

    @Override
    public float getSadness() {
        return emotions.getSadness();
    }

    @Override
    public float getSurprise() {
        return emotions.getSurprise();
    }

    @Override
    public Pair<String, Float> findDominantEmotion() {
        String dominantMetricName = "";
        Float dominantMetricValue = 50.0f; // no emotion is dominant unless at least greater than this value

        if (getAnger() > dominantMetricValue) {
            dominantMetricName = MetricsManager.getCapitalizedName(MetricsManager.Emotions.ANGER);
            dominantMetricValue = getAnger();
        }
        if (getContempt() > dominantMetricValue) {
            dominantMetricName = MetricsManager.getCapitalizedName(MetricsManager.Emotions.CONTEMPT);
            dominantMetricValue = getContempt();
        }
        if (getDisgust() > dominantMetricValue) {
            dominantMetricName = MetricsManager.getCapitalizedName(MetricsManager.Emotions.DISGUST);
            dominantMetricValue = getDisgust();
        }
        if (getFear() > dominantMetricValue) {
            dominantMetricName = MetricsManager.getCapitalizedName(MetricsManager.Emotions.FEAR);
            dominantMetricValue = getFear();
        }
        if (getJoy() > dominantMetricValue) {
            dominantMetricName = MetricsManager.getCapitalizedName(MetricsManager.Emotions.JOY);
            dominantMetricValue = getJoy();
        }
        if (getSadness() > dominantMetricValue) {
            dominantMetricName = MetricsManager.getCapitalizedName(MetricsManager.Emotions.SADNESS);
            dominantMetricValue = getSadness();
        }
        if (getSurprise() > dominantMetricValue) {
            dominantMetricName = MetricsManager.getCapitalizedName(MetricsManager.Emotions.SURPRISE);
            dominantMetricValue = getSurprise();
        }
        // Ignore VALENCE and ENGAGEMENT

        if (dominantMetricName.isEmpty()) {
            return null;
        } else {
            return new Pair<>(dominantMetricName, dominantMetricValue);
        }
    }
}
