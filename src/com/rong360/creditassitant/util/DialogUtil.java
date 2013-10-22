package com.rong360.creditassitant.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.widget.wheel.WheelVerticalView;
import com.rong360.creditassitant.widget.wheel.adapter.AbstractWheelTextAdapter;
import com.rong360.creditassitant.widget.wheel.adapter.NumericWheelAdapter;
import com.umeng.analytics.MobclickAgent;

public class DialogUtil {
    public static interface ITimePicker {
	public void onTimePicked(String time, Calendar alarm);
    }

    public static Dialog showTimePicker(Context context, ITimePicker onClick) {
	Dialog dlg = new Dialog(context, R.style.Dialog_PushUp);
	Window w = dlg.getWindow();
	View content =
		LayoutInflater.from(context).inflate(
			R.layout.dialog_time_setter, null);
	initTimePicker(context, dlg, content, onClick);
	WindowManager.LayoutParams lp = w.getAttributes();
	lp.x = 0;
	final int cMakeBottom = -1000;
	lp.y = cMakeBottom;
	lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	lp.gravity = Gravity.BOTTOM;
	dlg.onWindowAttributesChanged(lp);
	dlg.setCanceledOnTouchOutside(true);
	// if (onCancel != null) {
	// dlg.setOnCancelListener(onCancel);
	// }
	dlg.setContentView(content);
	dlg.show();

	return dlg;
    }

    private static void initTimePicker(final Context context,
	    final Dialog dialog, View parent, final ITimePicker picker) {
	final WheelVerticalView wvDate =
		(WheelVerticalView) parent.findViewById(R.id.wvDate);
	final WheelVerticalView wvHour =
		(WheelVerticalView) parent.findViewById(R.id.wvHour);
	final WheelVerticalView wvMin =
		(WheelVerticalView) parent.findViewById(R.id.wvMin);

	final NumericWheelAdapter hourAdapter =
		new NumericWheelAdapter(context, 0, 23, "%02d");
	hourAdapter.setItemResource(R.layout.wheel_text_centered);
	hourAdapter.setItemTextResource(R.id.text);
	wvHour.setViewAdapter(hourAdapter);

	final NumericWheelAdapter minAdapter =
		new NumericWheelAdapter(context, 0, 58, "%02d");
	minAdapter.setItemResource(R.layout.wheel_text_centered);
	minAdapter.setItemTextResource(R.id.text);
	wvMin.setViewAdapter(minAdapter);

	// set current time
	Calendar calendar = Calendar.getInstance(Locale.US);
	wvHour.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
	int min = (calendar.get(Calendar.MINUTE) + 7) / 15;
	Log.i("dialog", "min : " + min);
	if (min > 3) {
	    min = 3;
	}
	wvMin.setCurrentItem(min);
	wvDate.setCurrentItem(calendar.get(Calendar.AM_PM));

	final DayArrayAdapter dayAdapter =
		new DayArrayAdapter(context, calendar);
	wvDate.setViewAdapter(dayAdapter);
	wvDate.setCurrentItem(dayAdapter.getToday());

	Button btnOk = (Button) parent.findViewById(R.id.btnOk);
	Button btnCancel = (Button) parent.findViewById(R.id.btnCancel);
	btnOk.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		MobclickAgent.onEvent(context, RongStats.TIME_OK);
		Calendar alarm = dayAdapter.getDay(wvDate.getCurrentItem());
		String time = dayAdapter.getFormattedTime(alarm);
		int hour = hourAdapter.getCurrentItem(wvHour.getCurrentItem());
		alarm.set(Calendar.HOUR_OF_DAY, hour);
		int min = minAdapter.getCurrentItem(wvMin.getCurrentItem());
		alarm.set(Calendar.MINUTE, min);
		if (alarm.getTimeInMillis() <= System.currentTimeMillis()) {
		    MyToast.makeText(context, "提醒时间必须大于当前时间").show();
		    return;
		}
		time += " " + hour + ":";
		time += min;
		if (min == 0) {
		    time += "0";
		}
		if (picker != null) {
		    picker.onTimePicked(time, alarm);
		    dialog.dismiss();
		}
	    }
	});

	btnCancel.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		MobclickAgent.onEvent(context, RongStats.TIME_CANCEL);
		dialog.dismiss();
	    }
	});
    }

    /**
     * Day adapter
     * 
     */
    private static class DayArrayAdapter extends AbstractWheelTextAdapter {
	// Count of days to be shown
	private final int daysCount = 30;

	// Calendar
	Calendar calendar;
	
	private int color;
	/**
	 * Constructor
	 */
	protected DayArrayAdapter(Context context, Calendar calendar) {
	    super(context, R.layout.time_picker_custom_day, NO_RESOURCE);
	    this.calendar = calendar;
	    color = context.getResources().getColor(R.color.dialog);
	    setItemTextResource(R.id.time2_monthday);
	}

	public int getToday() {
	    return 0;
	}

	@Override
	public View getItem(int index, View cachedView, ViewGroup parent) {
	    int day = index;
	    Calendar newCalendar = (Calendar) calendar.clone();
	    newCalendar.roll(Calendar.DAY_OF_YEAR, day);

	    View view = super.getItem(index, cachedView, parent);

	    TextView weekday = (TextView) view.findViewById(R.id.time2_weekday);
	    if (day == 0) {
		weekday.setText("");
	    } else {
		DateFormat format = new SimpleDateFormat("EEE");
		weekday.setText(format.format(newCalendar.getTime()));
	    }

	    TextView monthday =
		    (TextView) view.findViewById(R.id.time2_monthday);
	    if (day == 0) {
		monthday.setText("今天");
		monthday.setTextColor(color);
	    } else {
		DateFormat format = new SimpleDateFormat("MMM dd");
		monthday.setText(format.format(newCalendar.getTime()));
		monthday.setTextColor(0xFF111111);
	    }
	    DateFormat dFormat = new SimpleDateFormat("MMM dd");
	    view.setTag(dFormat.format(newCalendar.getTime()));
	    return view;
	}

	public String getFormattedTime(Calendar calc) {
	    String title = "";
	    if (calc.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(
		    Calendar.DAY_OF_MONTH)) {
		title = "今天 ";
	    } else {
		DateFormat format = new SimpleDateFormat("MMM dd");
		title = format.format(calc.getTime());
	    }

	    DateFormat format = new SimpleDateFormat("EEE");
	    title += format.format(calc.getTime());

	    return title;
	}

	public Calendar getDay(int index) {
	    int day = index;
	    Calendar newCalendar = (Calendar) calendar.clone();
	    newCalendar.roll(Calendar.DAY_OF_YEAR, day);

	    return newCalendar;
	}

	@Override
	public int getItemsCount() {
	    return daysCount + 1;
	}

	@Override
	protected CharSequence getItemText(int index) {
	    return "";
	}
    }

}
