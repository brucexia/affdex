package com.moreants.glass.control;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.moreants.glass.R;

public class ScanActivity extends Activity implements View.OnClickListener {

    TextView scanResultView;
    View scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        scanButton = findViewById(R.id.scanButton);
        scanResultView = (TextView) findViewById(R.id.scanResult);
        scanButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scanButton: {
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.initiateScan();
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            // handle scan result
            scanResultView.setText("Scanning result: \n" + scanResult.getContents());
        }
    }
}
