package com.moreants.care;

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
    List<TrainingControllerInterface> listeners;

    public static TrainingController getInstance() {
        return new TrainingController(DEFAULT_TRAINING_DURATION);
    }

    public TrainingController(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
        listeners = new ArrayList<>();
    }

    public void startTraining() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

            }
        }, durationInMinutes * 60 * 1000);
    }

    public void stopTraining() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void addListener(TrainingControllerInterface listener) {
        listeners.add(listener);
    }

    public void removeListener(TrainingControllerInterface listener) {
        listeners.remove(listener);
    }

    interface TrainingControllerInterface {
        void onComplete();
    }
}
