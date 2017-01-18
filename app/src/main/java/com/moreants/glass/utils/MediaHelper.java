package com.moreants.glass.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by brucexia on 2017-01-12.
 */

public class MediaHelper {
    MediaPlayer player;
    Context mContext;

    public MediaHelper(Context context) {
        mContext = context;
        this.player = new MediaPlayer();
    }

    public void playAudioFromAsset(Context context, String filename) {
        try {
            AssetFileDescriptor descriptor = context.getAssets().openFd(filename);
            player.reset();
            player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            player.prepare();
            player.start();
        } catch (IOException ioe) {
            Log.e(MediaHelper.class.getSimpleName(), "playAudioFromAsset failed", ioe);
        }
    }
}
