package com.moreants.glass;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by brucexia on 2017-01-11.
 */

public class SplashActivity extends Activity {
    Timer timer;
    TextView greetingTextView, dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);
        greetingTextView = ((TextView) findViewById(R.id.greetingTextView));
        dateTextView = ((TextView) findViewById(R.id.dateTextView));
        greetingTextView.setText(getString(R.string.welcome_name, "Michael"));
        dateTextView.setText(DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
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
