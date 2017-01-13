package com.moreants.care;

import android.content.Context;
import android.media.MediaPlayer;

import com.moreants.care.utils.MediaHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by brucexia on 2017-01-11.
 */

public class TrainingController {
    public static final int DEFAULT_TRAINING_DURATION = 5;
    Timer timer;
    int durationInMinutes;
    List<Listener> listeners;
    Context mContext;
    MediaHelper mediaHelper;
    static WeakReference<TrainingController> weakReference;


    public static TrainingController getInstance(Context context) {
        if (weakReference == null || weakReference.get() == null) {
            TrainingController controller = new TrainingController(context, DEFAULT_TRAINING_DURATION);
            weakReference = new WeakReference<TrainingController>(controller);
            return controller;
        }
        return weakReference.get();
    }

    public TrainingController(Context context, int durationInMinutes) {
        mContext = context;
        this.durationInMinutes = durationInMinutes;
        listeners = new ArrayList<>();
        mediaHelper = new MediaHelper(context);
        playEyeContactEncourage();
    }

    public void startTraining() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (Listener listener : listeners) {
                    listener.onComplete();
                }
            }
        }, durationInMinutes * 60 * 1000);
    }

    public void stopTraining() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void playEyeContactEncourage() {
        mediaHelper.playAudioFromAsset(mContext, Audios.eyeContactEncourageFile);
    }

    interface Listener {
        void onComplete();
    }
}
