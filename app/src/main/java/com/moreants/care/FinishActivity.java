package com.moreants.care;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;

/**
 * Created by brucexia on 2017-01-11.
 */

public class FinishActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_functions);
    }

    //Beginning of View.OnClickListener

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEyeContact: {
                Intent intent = new Intent(this, TrainingActivity.class);
                startActivity(intent);
                finish();
            }
            break;
        }
    }
}