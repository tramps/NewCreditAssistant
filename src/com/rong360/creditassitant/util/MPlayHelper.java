package com.rong360.creditassitant.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import com.rong360.creditassitant.R;

public class MPlayHelper {
    private static MediaPlayer player;
    private static Vibrator vibrator;

    public static final int MAXIMIUM_DURATION = 60000;
    private static final float volume = 0.75f;
    
    private static int mUserVolume = -1;
    private static int mMaxVolume;

    public static void playSound(Context context) {
//	if (player == null || vibrator == null) {
	    player = MediaPlayer.create(context, R.raw.sound);
	    vibrator =
		    (Vibrator) context
			    .getSystemService(Context.VIBRATOR_SERVICE);
//	}
	if (player != null) {
	    player.setLooping(true);
//	    player.setVolume(volume, volume);
	    AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	    mUserVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
	    mMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (mMaxVolume * 0.8), 0);
//	    player.start();
	}
	if (vibrator != null) {
	    vibrator.vibrate(MAXIMIUM_DURATION);
	}
    }

    public static void silentAlarm(Context context) {
	if (player != null) {
	    Log.i("AlarmHelper", "silented");
	    try {
		player.pause();
		player.release();
	    } catch (IllegalStateException e) {
	    }
	    
//	    player.setVolume(0, 0);
	    AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	    am.setStreamVolume(AudioManager.STREAM_MUSIC, mUserVolume, 0);
	} else {
	    Log.i("AlarmHelper", "new pause");
	    player = MediaPlayer.create(context, R.raw.sound);
	    player.pause();
	    player.stop();
	}

	if (vibrator != null) {
	    vibrator.cancel();
	} else {
	    vibrator =
		    (Vibrator) context
			    .getSystemService(Context.VIBRATOR_SERVICE);
	    vibrator.cancel();
	}
    }

}
