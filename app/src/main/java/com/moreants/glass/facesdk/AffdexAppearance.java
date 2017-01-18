package com.moreants.glass.facesdk;

import com.affectiva.android.affdex.sdk.detector.Face;

/**
 * Created by brucexia on 2017-01-07.
 */

public class AffdexAppearance implements  Appearance{
    Face.Appearance sdkAppearance;

    public AffdexAppearance(Face.Appearance sdkAppearance) {
        this.sdkAppearance = sdkAppearance;
    }

    public Face.GENDER getGender() {
        return sdkAppearance.getGender();
    }

    public Face.ETHNICITY getEthnicity() {
        return sdkAppearance.getEthnicity();
    }

    public Face.GLASSES getGlasses() {
        return sdkAppearance.getGlasses();
    }

    public Face.AGE getAge() {
        return sdkAppearance.getAge();
    }
}
