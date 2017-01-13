package com.moreants.care;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by brucexia on 2017-01-11.
 */

public class SplashActivity extends Activity {
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);
        //TODO: do some background loading here
    }

    @Override
    protected void onStart() {
        super.onStart();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                FunctionsActivity.start(SplashActivity.this);
                if (!isFinishing())
                    finish();
            }
        }, 5000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }
}
