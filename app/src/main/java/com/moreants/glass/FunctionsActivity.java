package com.moreants.glass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;

/**
 * Created by brucexia on 2017-01-11.
 */

public class FunctionsActivity extends Activity implements View.OnClickListener {
    Timer timer;

    public static void start(Context context) {
        Intent intent = new Intent(context, FunctionsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_functions);
        findViewById(R.id.btnEyeContact).setOnClickListener(this);
    }

    //Beginning of View.OnClickListener

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEyeContact: {
                TrainingActivity.startTraining(this, 1);
                finish();
            }
            break;
        }
    }
}
