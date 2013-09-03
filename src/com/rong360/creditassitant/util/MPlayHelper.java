package com.rong360.creditassitant.util;

import com.rong360.creditassitant.R;

import android.app.ActionBar.Tab;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

public class MPlayHelper {
    private static MediaPlayer player;
    private static Vibrator vibrator;

    public static final int MAXIMIUM_DURATION = 60000;
    private static final float volume = 0.75f;

    public static void playSound(Context context) {
//	if (player == null || vibrator == null) {
	    player = MediaPlayer.create(context, R.raw.sound);
	    vibrator =
		    (Vibrator) context
			    .getSystemService(Context.VIBRATOR_SERVICE);
//	}
	if (player != null) {
	    player.setLooping(true);
	    player.setVolume(volume, volume);
	    player.start();
	}
	if (vibrator != null) {
	    vibrator.vibrate(MAXIMIUM_DURATION);
	}
    }

    public static void silentAlarm(Context context) {
	if (player != null) {
	    player.stop();
	}

	if (vibrator != null) {
	    vibrator.cancel();
	}
    }

}
