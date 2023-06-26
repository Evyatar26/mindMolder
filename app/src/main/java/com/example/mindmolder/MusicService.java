package com.example.mindmolder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.mindmolder.R;

public class MusicService extends Service {
    private MediaPlayer mp;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Start playing music when the service is created
        mp = MediaPlayer.create(this, R.raw.color_game_music);
        mp.setLooping(true);
        mp.start();
    }
/*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Handle start commands, if any

        // Return START_STICKY to ensure the service is restarted if it's killed by the system.
        return START_STICKY;
    }

 */

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the music playback and release the MediaPlayer when the service is destroyed
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public void stopMusic() {
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
