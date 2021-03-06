package com.rong360.creditassitant.util;

import java.io.IOException;

import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.service.PhoneNoticeService;

public class MPlayHelper {
    private static MediaPlayer player;
    private static Vibrator vibrator;

    public static final int MAXIMIUM_DURATION = 25000;
    private static final float volume = 0.75f;

    private static int mUserVolume = -1;
    private static int mMaxVolume;

    public static void playSound(Context context) {
	Log.i("AlarmHelper", "play sound");
	if (PhoneNoticeService.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK
		|| PhoneNoticeService.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
	    return;
	}
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
	    player.setAudioStreamType(AudioManager.STREAM_ALARM);
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
	    mUserVolume = am.getStreamVolume(AudioManager.STREAM_ALARM);
	    mMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_ALARM);
	    am.setStreamVolume(AudioManager.STREAM_ALARM,
		    (int) (mMaxVolume * 0.75), 0);
	    if (mUserVolume == 0) {
		mUserVolume = (int) (mMaxVolume * 0.5);
	    }
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
		am.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
	    }

	    // player.setVolume(0, 0);
	    AudioManager am =
		    (AudioManager) context
			    .getSystemService(Context.AUDIO_SERVICE);
	    am.setStreamVolume(AudioManager.STREAM_ALARM, mUserVolume, 0);
	} else {
	    Log.i("AlarmHelper", "ring zero");
	    AudioManager am =
		    (AudioManager) context
			    .getSystemService(Context.AUDIO_SERVICE);
	    am.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
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
