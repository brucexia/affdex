/**
 * Copyright (c) 2016 Affectiva Inc.
 * See the file license.txt for copying permission.
 */

package com.moreants.glass;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.Frame.ROTATE;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*
 * AffdexMe is an app that demonstrates the use of the Affectiva Android SDK.  It uses the
 * front-facing camera on your Android device to view, process and analyze live video of your face.
 * Start the app and you will see your own face on the screen and metrics describing your
 * expressions.
 *
 * Tapping the screen will bring up a menu with options to display the Processed Frames Per Second metric,
 * display facial tracking points, and control the rate at which frames are processed by the SDK.
 *
 * Most of the methods in this file control the application's UI. Therefore, if you are just interested in learning how the Affectiva SDK works,
 *  you will find the calls relevant to the use of the SDK in the initializeCameraDetector(), startCamera(), stopCamera(),
 *  and onImageResults() methods.
 *
 * This class implements the Detector.ImageListener interface, allowing it to receive the onImageResults() event.
 * This class implements the Detector.FaceListener interface, allowing it to receive the onFaceDetectionStarted() and
 * onFaceDetectionStopped() events.
 * This class implements the CameraDetector.CameraSurfaceViewListener interface, allowing it to receive
 * onSurfaceViewAspectRatioChanged() events.
 *
 * In order to use this project, you will need to:
 * - Obtain the SDK from Affectiva (visit http://www.affdex.com/mobile-sdk)
 * - Copy the SDK assets folder contents into this project's assets folder under AffdexMe/app/src/main/assets
 * - Copy the contents of the SDK's libs folder into this project's libs folder under AffdexMe/app/lib
 * - Copy the armeabi-v7a folder (found in the SDK libs folder) into this project's jniLibs folder under AffdexMe/app/src/main/jniLibs
 * - Add your license file to the assets/Affdex folder and rename to license.txt.
 * (Note: if you name the license file something else you will need to update the licensePath in the initializeCameraDetector() method in MainActivity)
 * - Build the project
 * - Run the app on an Android device with a front-facing camera
 */

public class MainActivity extends Activity
        implements Detector.FaceListener, Detector.ImageListener, CameraDetector.CameraEventListener,
        View.OnTouchListener, ActivityCompat.OnRequestPermissionsResultCallback, DrawingView.DrawingThreadEventListener {

    public static final int MAX_SUPPORTED_FACES = 3;
    public static final boolean STORE_RAW_SCREENSHOTS = false; // setting to enable saving the raw images when taking screenshots
    public static final int NUM_METRICS_DISPLAYED = 6;
    private static final String LOG_TAG = "AffdexMe";
    private static final int CAMERA_PERMISSIONS_REQUEST = 42;  //value is arbitrary (between 0 and 255)
    private static final int EXTERNAL_STORAGE_PERMISSIONS_REQUEST = 73;
    int cameraPreviewWidth = 0;
    int cameraPreviewHeight = 0;
    CameraDetector.CameraType cameraType;
    boolean mirrorPoints = false;
    private boolean cameraPermissionsAvailable = false;
    private boolean storagePermissionsAvailable = false;
    private CameraDetector detector = null;
    private RelativeLayout metricViewLayout;
    private LinearLayout leftMetricsLayout;
    private LinearLayout rightMetricsLayout;
    private MetricDisplay[] metricDisplays;
    private TextView[] metricNames;
    private TextView fpsName;
    private TextView fpsPct;
    private TextView pleaseWaitTextView;
    private ProgressBar progressBar;
    private RelativeLayout mainLayout; //layout, to be resized, containing all UI elements
    private RelativeLayout progressBarLayout; //layout used to show progress circle while camera is starting
    private LinearLayout permissionsUnavailableLayout; //layout used to notify the user that not enough permissions have been granted to use the app
    private SurfaceView cameraView; //SurfaceView used to display camera images
    private DrawingView drawingView; //SurfaceView containing its own thread, used to draw facial tracking dots
    private ImageButton settingsButton;
    private ImageButton cameraButton;
    private ImageButton screenshotButton;
    private Frame mostRecentFrame;
    private boolean isMenuVisible = false;
    private boolean isFPSVisible = false;
    private boolean isMenuShowingForFirstTime = true;
    private long firstSystemTime = 0;
    private float numberOfFrames = 0;
    private long timeToUpdate = 0;
    private boolean isFrontFacingCameraDetected = true;
    private boolean isBackFacingCameraDetected = true;
    private boolean multiFaceModeEnabled = false;

    TextView scoreView;
    SharedPreferences sharedPreferences;
    Timer scoringTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //To maximize UI space, we declare our app to be full-screen
//        preproccessMetricImages();
        setContentView(R.layout.activity_main);
        initializeUI();
        checkForCameraPermissions();
        determineCameraAvailability();
        initializeCameraDetector();
    }

    /**
     * Only load the files onto disk the first time the app opens
     */
    private void checkForCameraPermissions() {
        cameraPermissionsAvailable =
                ContextCompat.checkSelfPermission(
                        getApplicationContext(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        if (!cameraPermissionsAvailable) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showPermissionExplanationDialog(CAMERA_PERMISSIONS_REQUEST);
            } else {
                // No explanation needed, we can request the permission.
                requestCameraPermissions();
            }
        }
    }

    private void checkForStoragePermissions() {
        storagePermissionsAvailable =
                ContextCompat.checkSelfPermission(
                        getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (!storagePermissionsAvailable) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showPermissionExplanationDialog(EXTERNAL_STORAGE_PERMISSIONS_REQUEST);
            } else {
                // No explanation needed, we can request the permission.
                requestStoragePermissions();
            }
        } else {
            takeScreenshot(screenshotButton);
        }
    }

    private void requestStoragePermissions() {
        if (!storagePermissionsAvailable) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSIONS_REQUEST);

            // EXTERNAL_STORAGE_PERMISSIONS_REQUEST is an app-defined int constant that must be between 0 and 255.
            // The callback method gets the result of the request.
        }
    }

    private void requestCameraPermissions() {
        if (!cameraPermissionsAvailable) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSIONS_REQUEST);

            // CAMERA_PERMISSIONS_REQUEST is an app-defined int constant that must be between 0 and 255.
            // The callback method gets the result of the request.
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSIONS_REQUEST) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.CAMERA)) {
                    cameraPermissionsAvailable = (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }

            if (!cameraPermissionsAvailable) {
                permissionsUnavailableLayout.setVisibility(View.VISIBLE);
            } else {
                permissionsUnavailableLayout.setVisibility(View.GONE);
            }
        }

        if (requestCode == EXTERNAL_STORAGE_PERMISSIONS_REQUEST) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    storagePermissionsAvailable = (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }

            if (storagePermissionsAvailable) {
                // resume taking the screenshot
                takeScreenshot(screenshotButton);
            }
        }

    }

    private void showPermissionExplanationDialog(int requestCode) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        // set title
        alertDialogBuilder.setTitle(getResources().getString(R.string.insufficient_permissions));

        // set dialog message
        if (requestCode == CAMERA_PERMISSIONS_REQUEST) {
            alertDialogBuilder
                    .setMessage(getResources().getString(R.string.permissions_camera_needed_explanation))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.understood), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            requestCameraPermissions();
                        }
                    });
        } else if (requestCode == EXTERNAL_STORAGE_PERMISSIONS_REQUEST) {
            alertDialogBuilder
                    .setMessage(getResources().getString(R.string.permissions_storage_needed_explanation))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.understood), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            requestStoragePermissions();
                        }
                    });
        }

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    /**
     * We check to make sure the device has a front-facing camera.
     * If it does not, we obscure the app with a notice informing the user they cannot
     * use the app.
     */
    void determineCameraAvailability() {
        PackageManager manager = getPackageManager();
        isFrontFacingCameraDetected = manager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        isBackFacingCameraDetected = manager.hasSystemFeature(PackageManager.FEATURE_CAMERA);

        if (!isFrontFacingCameraDetected && !isBackFacingCameraDetected) {
            progressBar.setVisibility(View.INVISIBLE);
            pleaseWaitTextView.setVisibility(View.INVISIBLE);
            TextView notFoundTextView = (TextView) findViewById(R.id.not_found_textview);
            notFoundTextView.setVisibility(View.VISIBLE);
        }

        //set default camera settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //restore the camera type settings
        String cameraTypeName = sharedPreferences.getString("cameraType", CameraDetector.CameraType.CAMERA_FRONT.name());
        if (cameraTypeName.equals(CameraDetector.CameraType.CAMERA_FRONT.name())) {
            cameraType = CameraDetector.CameraType.CAMERA_FRONT;
            mirrorPoints = true;
        } else {
            cameraType = CameraDetector.CameraType.CAMERA_BACK;
            mirrorPoints = false;
        }
    }

    void initializeUI() {

        //Get handles to UI objects
        ViewGroup activityLayout = (ViewGroup) findViewById(android.R.id.content);
        progressBarLayout = (RelativeLayout) findViewById(R.id.progress_bar_cover);
        permissionsUnavailableLayout = (LinearLayout) findViewById(R.id.permissionsUnavialableLayout);
        metricViewLayout = (RelativeLayout) findViewById(R.id.metric_view_group);
        leftMetricsLayout = (LinearLayout) findViewById(R.id.left_metrics);
        rightMetricsLayout = (LinearLayout) findViewById(R.id.right_metrics);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        fpsPct = (TextView) findViewById(R.id.fps_value);
        fpsName = (TextView) findViewById(R.id.fps_name);
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        drawingView = (DrawingView) findViewById(R.id.drawing_view);
        settingsButton = (ImageButton) findViewById(R.id.settings_button);
        cameraButton = (ImageButton) findViewById(R.id.camera_button);
        screenshotButton = (ImageButton) findViewById(R.id.screenshot_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        pleaseWaitTextView = (TextView) findViewById(R.id.please_wait_textview);
        Button retryPermissionsButton = (Button) findViewById(R.id.retryPermissionsButton);


        scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setText(String.valueOf(sharedPreferences.getInt("score", 0)));
        //Load Application Font and set UI Elements to use it
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Square.ttf");

        /**
         * This app uses two SurfaceView objects: one to display the camera image and the other to draw facial tracking dots.
         * Since we want the tracking dots to appear over the camera image, we use SurfaceView.setZOrderMediaOverlay() to indicate that
         * cameraView represents our 'media', and drawingView represents our 'overlay', so that Android will render them in the
         * correct order.
         */
        drawingView.setZOrderMediaOverlay(true);
        cameraView.setZOrderMediaOverlay(false);

        //Attach event listeners to the menu and edit box
        activityLayout.setOnTouchListener(this);

        //Attach event listerner to drawing view
        drawingView.setEventListener(this);

        drawingView.setFaceAnimation(new FaceAnimationV1(this));
        drawingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.reset();
            }
        });

        retryPermissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermissions();
            }
        });
    }

    void initializeCameraDetector() {
        /* Put the SDK in camera mode by using this constructor. The SDK will be in control of
         * the camera. If a SurfaceView is passed in as the last argument to the constructor,
         * that view will be painted with what the camera sees.
         */
        detector = new CameraDetector(this, cameraType, cameraView, 1, Detector.FaceDetectorMode.LARGE_FACES);
        detector.setImageListener(this);
        detector.setFaceListener(this);
        detector.setOnCameraEventListener(this);
    }

    /*
     * We use onResume() to restore application settings using the SharedPreferences object
     */
    @Override
    public void onResume() {
        super.onResume();
        checkForCameraPermissions();
        restoreApplicationSettings();
        setMenuVisible(true);
        isMenuShowingForFirstTime = true;
    }

    /*
     * We use the Shared Preferences object to restore application settings.
     */
    public void restoreApplicationSettings() {

        //restore the camera type settings
        String cameraTypeName = sharedPreferences.getString("cameraType", CameraDetector.CameraType.CAMERA_FRONT.name());
        if (cameraTypeName.equals(CameraDetector.CameraType.CAMERA_FRONT.name())) {
            setCameraType(CameraDetector.CameraType.CAMERA_FRONT);
        } else {
            setCameraType(CameraDetector.CameraType.CAMERA_BACK);
        }

        //restore camera processing rate
        int detectorProcessRate = PreferencesUtils.getFrameProcessingRate(sharedPreferences);
        detector.setMaxProcessRate(detectorProcessRate);
//        drawingView.invalidateDimensions();

        //if we are in multiface mode, we need to enable the detection of all emotions
        if (multiFaceModeEnabled) {
            detector.setDetectAllEmotions(true);
        }
    }

    /**
     * Reset the variables used to calculate processed frames per second.
     **/
    public void resetFPSCalculations() {
        firstSystemTime = SystemClock.elapsedRealtime();
        timeToUpdate = firstSystemTime + 1000L;
        numberOfFrames = 0;
    }

    /**
     * We want to start the camera as late as possible, so it does not freeze the application before it has been visually resumed.
     * We thus post a runnable that will take care of starting the camera.
     * We also reset variables used to calculate the Processed Frames Per Second.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && isFrontFacingCameraDetected) {
            cameraView.post(new Runnable() {
                @Override
                public void run() {
                    mainWindowResumedTasks();
                }
            });
        }
    }

    void mainWindowResumedTasks() {

        //Notify the user that they can't use the app without authorizing these permissions.
        if (!cameraPermissionsAvailable) {
            permissionsUnavailableLayout.setVisibility(View.VISIBLE);
            return;
        }

        startDetector();

    }

    void startDetector() {
        if (!isBackFacingCameraDetected && !isFrontFacingCameraDetected)
            return; //without any cameras detected, we cannot proceed

        detector.setDetectValence(true); //this app will always detect valence
        if (!detector.isRunning()) {
            try {
                detector.start();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
    }

    @Override
    public void onFaceDetectionStarted() {

    }

    @Override
    public void onFaceDetectionStopped() {
    }


    void updateScore(Face face) {
        if (scoringTimer == null) {
            scoringTimer = new Timer();
            scoringTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    final int score = sharedPreferences.getInt("score", 0) + 1;
                    sharedPreferences.edit().putInt("score", score).apply();
                    scoreView.post(new Runnable() {
                        @Override
                        public void run() {
                            scoreView.setText(String.valueOf(score));
                        }
                    });
                }
            }, new Date(System.currentTimeMillis()), 1000);
        }
    }

    void stopTimer() {
        if (scoringTimer != null) {
            scoringTimer.cancel();
        }
        scoringTimer = null;
    }

    /**
     * This event is received every time the SDK processes a frame.
     */
    @Override
    public void onImageResults(List<Face> faces, Frame image, float timeStamp) {
        mostRecentFrame = image;

        //If the faces object is null, we received an unprocessed frame
        if (faces == null) {
            return;
        }

        //At this point, we know the frame received was processed, so we perform our processed frames per second calculations
        performFPSCalculations();

        //If faces.size() is 0, we received a frame in which no face was detected
        if (faces.size() <= 0) {
            drawingView.invalidatePoints();
            stopTimer();
        } else if (faces.size() == 1) {
            metricViewLayout.setVisibility(View.VISIBLE);

            PointF points[] = faces.get(0).getFacePoints();
            StringBuilder stringBuilder = new StringBuilder();
            for (PointF pointF : points) {
                stringBuilder.append(String.format("(%.3f,%.3f),", pointF.x, pointF.y));
            }
            Log.d(TAG, String.format("onImageResults: frame w:%d,frame h:%d, orientation %s", image.getWidth(), image.getHeight(), image.getTargetRotation()));
            Log.d(TAG, String.format("MainActivity face points %s:", stringBuilder.toString()));
            drawingView.updatePoints(faces, mirrorPoints);

        } else {
            // metrics overlay is hidden in multi face mode
            metricViewLayout.setVisibility(View.GONE);

            // always update points in multi face mode
            drawingView.updatePoints(faces, mirrorPoints);
        }
    }

    List<FaceInterface> getFaces(List<Face> faces) {
        List<FaceInterface> facePoints = new ArrayList<>();
        for (Face face : faces) {
            facePoints.add(new FaceLandmarks(face, mirrorPoints));
        }
        return facePoints;
    }

    public void takeScreenshot(View view) {
        // Check the permissions to see if we are allowed to save the screenshot
        if (!storagePermissionsAvailable) {
            checkForStoragePermissions();
            return;
        }

        drawingView.requestBitmap();

        /**
         * A screenshot of the drawing view is generated and processing continues via the callback
         * onBitmapGenerated() which calls processScreenshot().
         */
    }

    /**
     * FPS measurement simply uses SystemClock to measure how many frames were processed since
     * the FPS variables were last reset.
     * The constants 1000L and 1000f appear because .elapsedRealtime() measures time in milliseconds.
     * Note that if 20 frames per second are processed, this method could run for 1.5 years without being reset
     * before numberOfFrames overflows.
     */
    void performFPSCalculations() {
        numberOfFrames += 1;
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime > timeToUpdate) {
            float framesPerSecond = (numberOfFrames / (float) (currentTime - firstSystemTime)) * 1000f;
            fpsPct.setText(String.format(" %.1f", framesPerSecond));
            timeToUpdate = currentTime + 1000L;
        }
    }

    /**
     * Although we start the camera in onWindowFocusChanged(), we stop it in onPause(), and set detector to be null so that when onWindowFocusChanged()
     * is called it restarts the camera. We also set the Progress Bar to be visible, so the camera (which may need resizing when the app
     * is resumed) is obscured.
     */
    @Override
    public void onPause() {
        super.onPause();
        progressBarLayout.setVisibility(View.VISIBLE);

        stopDetector();
    }

    void stopDetector() {
        if (detector.isRunning()) {
            try {
                detector.stop();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }

        detector.setDetectAllEmotions(false);
        detector.setDetectAllExpressions(false);
        detector.setDetectAllAppearances(false);
        detector.setDetectAllEmojis(false);
    }


    /**
     * When the user taps the screen, hide the menu if it is visible and show it if it is hidden.
     **/
    void setMenuVisible(boolean b) {
        isMenuShowingForFirstTime = false;
        isMenuVisible = b;
        if (b) {
            settingsButton.setVisibility(View.VISIBLE);
            cameraButton.setVisibility(View.VISIBLE);
            screenshotButton.setVisibility(View.VISIBLE);

        } else {

            settingsButton.setVisibility(View.INVISIBLE);
            cameraButton.setVisibility(View.INVISIBLE);
            screenshotButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * If a user has a phone with a physical menu button, they may expect it to toggle
     * the menu, so we add that functionality.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            setMenuVisible(!isMenuVisible);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setMenuVisible(!isMenuVisible);
        }
        return false;
    }
    public static final String TAG = MainActivity.class.getSimpleName();

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void onCameraSizeSelected(int cameraWidth, int cameraHeight, ROTATE rotation) {
        Log.d(TAG, String.format("MainActivity.onFrameSizeSelected width:%d,height %d, rotation: %s", cameraWidth, cameraHeight, rotation));
        if (rotation == ROTATE.BY_90_CCW || rotation == ROTATE.BY_90_CW) {
            cameraPreviewWidth = cameraHeight;
            cameraPreviewHeight = cameraWidth;
        } else {
            cameraPreviewWidth = cameraWidth;
            cameraPreviewHeight = cameraHeight;
        }
        drawingView.setThickness((int) (cameraPreviewWidth / 100f));

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

                drawingView.updateViewDimensions(newWidth, newHeight, cameraPreviewWidth, cameraPreviewHeight);
                Log.d(TAG, String.format("MainActivity.onFrameSizeSelected width:%d,height %d, rotation: %s", newWidth, newHeight, cameraPreviewAspectRatio));

                ViewGroup.LayoutParams params = mainLayout.getLayoutParams();
                params.height = newHeight;
                params.width = newWidth;
                mainLayout.setLayoutParams(params);

                //Now that our main layout has been resized, we can remove the progress bar that was obscuring the screen (its purpose was to obscure the resizing of the SurfaceView)
                progressBarLayout.setVisibility(View.GONE);
            }
        });

    }

    public void camera_button_click(View view) {
        //Toggle the camera setting
        setCameraType(cameraType == CameraDetector.CameraType.CAMERA_FRONT ? CameraDetector.CameraType.CAMERA_BACK : CameraDetector.CameraType.CAMERA_FRONT);
    }

    private void setCameraType(CameraDetector.CameraType type) {
        SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        //If a settings change is necessary
        if (cameraType != type) {
            switch (type) {
                case CAMERA_BACK:
                    if (isBackFacingCameraDetected) {
                        cameraType = CameraDetector.CameraType.CAMERA_BACK;
                        mirrorPoints = false;
                    } else {
                        Toast.makeText(this, "No back-facing camera found", Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                case CAMERA_FRONT:
                    if (isFrontFacingCameraDetected) {
                        cameraType = CameraDetector.CameraType.CAMERA_FRONT;
                        mirrorPoints = true;
                    } else {
                        Toast.makeText(this, "No front-facing camera found", Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                default:
                    Log.e(LOG_TAG, "Unknown camera type selected");
            }

            detector.setCameraType(cameraType);
            preferencesEditor.putString("cameraType", cameraType.name());
            preferencesEditor.apply();
        }
    }

    @Override
    public void onBitmapGenerated(@NonNull final Bitmap bitmap) {
    }
}