package com.moreants.glass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.List;

/**
 */
public class TrainingActivity extends Activity implements CameraView.OnCameraViewEventListener,
        AsyncFrameDetector.OnDetectorEventListener,
        TrainingController.Listener {
    public static final String KEY_DURATION = "duration";
    //state booleans
    boolean isCameraStarted = false;
    boolean isSDKRunning = false;

    //variables used to determine the FPS rates of frames sent by the camera and processed by the SDK
    long numberCameraFramesReceived = 0;
    long lastCameraFPSResetTime = -1L;
    long numberSDKFramesReceived = 0;
    long lastSDKFPSResetTime = -1L;

    //floats to ensure the timestamps we send to FrameDetector are sequentially increasing
    float lastTimestamp = -1f;
    final float epsilon = .01f;

    CameraView cameraView; // controls the camera
    AsyncFrameDetector asyncDetector; // runs FrameDetector on a background thread
    DrawingView drawingView;

    ViewGroup mainLayout;
    int cameraPreviewWidth = 0;
    int cameraPreviewHeight = 0;

    TrainingController trainingController;

    public static void startTraining(Context context, int duration) {
        Intent intent = new Intent(context, TrainingActivity.class);
        intent.putExtra(KEY_DURATION, duration);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        mainLayout = (ViewGroup) findViewById(R.id.main_layout);
        //set up metrics view

        //Init TextViews

        //set up CameraView
        cameraView = (CameraView) findViewById(R.id.camera_view);
        cameraView.setOnCameraViewEventListener(this);
        drawingView = (DrawingView) findViewById(R.id.drawing_view);

        drawingView.setZOrderMediaOverlay(true);
        cameraView.setZOrderMediaOverlay(false);
        drawingView.setFaceAnimation(new FaceAnimationV1(this));
        drawingView.setEyeAnimation(new EyeAnimation(this));
        drawingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.reset();
            }
        });
        asyncDetector = new AsyncFrameDetector(this);
        asyncDetector.setOnDetectorEventListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        trainingController = TrainingController.getInstance(this);
        trainingController.addListener(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        trainingController.stopTraining();
        trainingController.removeListener(this);
    }

    void resetFPS() {
        lastCameraFPSResetTime = lastSDKFPSResetTime = SystemClock.elapsedRealtime();
        numberCameraFramesReceived = numberSDKFramesReceived = 0;
    }

    CameraHelper.CameraType cameraIndex = CameraHelper.CameraType.CAMERA_BACK;

    void startCamera() {
        if (isCameraStarted) {
            cameraView.stopCamera();
        }
        cameraView.startCamera(cameraIndex);
        isCameraStarted = true;
        asyncDetector.reset();
    }

    void stopCamera() {
        if (!isCameraStarted)
            return;

        cameraView.stopCamera();
        isCameraStarted = false;
    }

    void startSdk() {
        isSDKRunning = true;
        asyncDetector.start();
    }

    void stopSdk() {
        isSDKRunning = false;
        asyncDetector.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        asyncDetector.start();
        startCamera();

        resetFPS();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (asyncDetector.isRunning()) {
            asyncDetector.stop();
        }
        stopCamera();
    }

    @Override
    public void onCameraFrameAvailable(byte[] frame, int width, int height, Frame.ROTATE rotation) {
        numberCameraFramesReceived += 1;
//        cameraFPS.setText(String.format("CAM: %.3f", 1000f * (float) numberCameraFramesReceived / (SystemClock.elapsedRealtime() - lastCameraFPSResetTime)));

        float timeStamp = (float) SystemClock.elapsedRealtime() / 1000f;
        if (timeStamp > (lastTimestamp + epsilon)) {
            lastTimestamp = timeStamp;
            asyncDetector.process(createFrameFromData(frame, width, height, rotation), timeStamp);
        }
    }

    @Override
    public void onCameraStarted(boolean success, Throwable error) {
        //TODO: change status here
    }

    @Override
    public void onSurfaceViewSizeChanged() {
        asyncDetector.reset();
    }

    float lastReceivedTimestamp = -1f;

    @Override
    public void onImageResults(List<Face> faces, Frame image, float timeStamp) {
        //statusTextView.setText(String.format("Most recent time stamp: %.4f",timeStamp));
        float timeStamp1 = (float) SystemClock.elapsedRealtime() / 1000f;

        Log.d(TAG, String.format("onImageResults most recent time stamp:%.4f, %.4f, %.4f", lastTimestamp, timeStamp1, timeStamp1 - lastTimestamp));
        if (timeStamp < lastReceivedTimestamp)
            throw new RuntimeException("Got a timestamp out of order!");
        lastReceivedTimestamp = timeStamp;

        if (faces == null || faces.isEmpty()) {
            drawingView.invalidatePoints();
            return; //No Face Detected
        }
//        PointF points[] = faces.get(0).getFacePoints();
//        StringBuilder stringBuilder = new StringBuilder();
//        for (PointF pointF : points) {
//            stringBuilder.append(String.format("(%.0f,%.0f),", pointF.x, pointF.y));
//        }
//        Log.d(TAG, String.format("TrainingActivity.onImageResults: frame w:%d,frame h:%d, orientation %s", image.getWidth(), image.getHeight(), image.getTargetRotation()));

//        Log.d(TAG, "TrainingActivity face points:" + stringBuilder.toString());
        transformPoints(faces.get(0).getFacePoints(), image.getWidth(), image.getHeight(), image.getTargetRotation());

        drawingView.updatePoints(faces, cameraIndex == CameraHelper.CameraType.CAMERA_FRONT);

        numberSDKFramesReceived += 1;

    }

    public static final String TAG = TrainingActivity.class.getSimpleName();

    @Override
    public void onFrameSizeSelected(int width, int height, Frame.ROTATE rotation) {
        Log.d(TAG, String.format("onFrameSizeSelected width:%d,height %d, rotation: %s", width, height, rotation));
        if (rotation == Frame.ROTATE.BY_90_CCW || rotation == Frame.ROTATE.BY_90_CW) {
            cameraPreviewWidth = height;
            cameraPreviewHeight = width;
        } else {
            cameraPreviewWidth = width;
            cameraPreviewHeight = height;
        }
        trainingController.startTraining();
        mainLayout.post(new Runnable() {
            @Override
            public void run() {
                //Get the screen width and height, and calculate the new app width/height based on the surfaceview aspect ratio.
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int layoutWidth = displaymetrics.widthPixels;
                int layoutHeight = displaymetrics.heightPixels;

                if (cameraPreviewWidth == 0 || cameraPreviewHeight == 0 || layoutWidth == 0 || layoutHeight == 0)
                    return;

                float layoutAspectRatio = (float) layoutWidth / layoutHeight;
                float cameraPreviewAspectRatio = (float) cameraPreviewWidth / cameraPreviewHeight;

                int newWidth;
                int newHeight;

                if (cameraPreviewAspectRatio > layoutAspectRatio) {
                    newWidth = layoutWidth;
                    newHeight = (int) (layoutWidth / cameraPreviewAspectRatio);
                } else {
                    newWidth = (int) (layoutHeight * cameraPreviewAspectRatio);
                    newHeight = layoutHeight;
                }
                Log.d(TAG, String.format("onFrameSizeSelected width:%d,height %d, rotation: %s", newWidth, newHeight, cameraPreviewAspectRatio));

                drawingView.updateViewDimensions(newWidth, newHeight, cameraPreviewWidth, cameraPreviewHeight);

                ViewGroup.LayoutParams params = mainLayout.getLayoutParams();
                params.height = newHeight;
                params.width = newWidth;
                mainLayout.setLayoutParams(params);

                //Now that our main layout has been resized, we can remove the progress bar that was obscuring the screen (its purpose was to obscure the resizing of the SurfaceView)
            }
        });
    }

    @Override
    public void onDetectorStarted() {

    }

    static Frame createFrameFromData(byte[] frameData, int width, int height, Frame.ROTATE rotation) {
        Frame.ByteArrayFrame frame = new Frame.ByteArrayFrame(frameData, width, height, Frame.COLOR_FORMAT.YUV_NV21);
        frame.setTargetRotation(rotation);
        return frame;
    }

    void transformPoints(PointF[] points, int width, int height, Frame.ROTATE rotation) {
//        Frame.revertPointRotation(points, width, height, rotation);
        switch (rotation) {
            case NO_ROTATION: {
                if (cameraIndex == CameraHelper.CameraType.CAMERA_FRONT) {
                    for (PointF pointF : points) {
                        pointF.x = width - pointF.x;
                    }
                }
            }
            break;
            case BY_90_CCW: {
                for (PointF pointF : points) {
                    float tmp = pointF.x;
                    pointF.x = pointF.y;
                    pointF.y = tmp;
                }
            }
            break;
            case BY_90_CW: {
                for (PointF pointF : points) {
                    float tmp = pointF.x;
                    pointF.x = pointF.y;
                    pointF.y = width - tmp;
                }
                break;
            }
        }
    }

    @Override
    public void onComplete() {
        Intent intent = new Intent(this, FinishActivity.class);
        startActivity(intent);
        if (!isFinishing())
            finish();
    }
}
