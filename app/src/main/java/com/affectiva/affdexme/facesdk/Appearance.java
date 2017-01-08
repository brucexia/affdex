package com.affectiva.affdexme.facesdk;

import com.affectiva.android.affdex.sdk.detector.Face;

/**
 * Created by brucexia on 2017-01-07.
 */

public interface Appearance {
    public Face.GENDER getGender();

    public Face.ETHNICITY getEthnicity();

    public Face.GLASSES getGlasses();

    public Face.AGE getAge();
}
