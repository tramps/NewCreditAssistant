package com.rong360.creditassitant.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
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
	// if (player == null || vibrator == null) {
	AssetFileDescriptor afd =
		context.getResources().openRawResourceFd(R.raw.sound);
	if (afd == null) {
	    return;
	}
	player = new MediaPlayer();
	try {
	    player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
		    afd.getLength());
	    player.setAudioStreamType(AudioManager.STREAM_RING);
	    player.prepare();
	} catch (IllegalArgumentException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalStateException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	vibrator =
		(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	// }
	if (player != null) {
	    player.setLooping(true);
	    // player.setVolume(volume, volume);
	    AudioManager am =
		    (AudioManager) context
			    .getSystemService(Context.AUDIO_SERVICE);
	    mUserVolume = am.getStreamVolume(AudioManager.STREAM_RING);
	    mMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_RING);
	    am.setStreamVolume(AudioManager.STREAM_RING,
		    (int) (mMaxVolume * 0.75), 0);
	    player.start();
	}
	if (vibrator != null) {
	    vibrator.vibrate(MAXIMIUM_DURATION);
	}
    }

    public static void silentAlarm(Context context) {
	if (player != null) {
	    Log.i("AlarmHelper", "silented");
	    try {
		if (player.isLooping()) {
		    player.setLooping(false);
		}
		player.pause();
		player.stop();
		player.release();
	    } catch (IllegalStateException e) {
		AudioManager am =
			    (AudioManager) context
				    .getSystemService(Context.AUDIO_SERVICE);
		    am.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
	    }

	    // player.setVolume(0, 0);
	    AudioManager am =
		    (AudioManager) context
			    .getSystemService(Context.AUDIO_SERVICE);
	    am.setStreamVolume(AudioManager.STREAM_RING, mUserVolume, 0);
	} else {
	    Log.i("AlarmHelper", "ring zero");
	    AudioManager am =
		    (AudioManager) context
			    .getSystemService(Context.AUDIO_SERVICE);
	    am.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
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
