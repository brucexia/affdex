package com.moreants.glass;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by brucexia on 2017-01-11.
 */

public class FinishActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_finish);
        findViewById(R.id.layoutReplay).setOnClickListener(this);
        findViewById(R.id.layoutBreak).setOnClickListener(this);
    }

    //Beginning of View.OnClickListener

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutReplay: {
                TrainingActivity.startTraining(this, 1);
                finish();
            }
            break;
            case R.id.layoutBreak: {
                FunctionsActivity.start(this);
                finish();
            }
            break;
        }
    }
}
