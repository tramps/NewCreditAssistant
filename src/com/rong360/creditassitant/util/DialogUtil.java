package com.rong360.creditassitant.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
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

public class DialogUtil {
    public static interface ITimePicker {
	public void onTimePicked (String time);
    }
    
    public static Dialog showTimePicker(Context context, ITimePicker onClick) {
	Dialog dlg = new Dialog(context, R.style.Dialog_PushUp);
	Window w = dlg.getWindow();
	View content = LayoutInflater.from(context).inflate(R.layout.dialog_time_setter, null);
	initTimePicker(context,dlg, content, onClick);
	WindowManager.LayoutParams lp = w.getAttributes();
	lp.x = 0;
	final int cMakeBottom = -1000;
	lp.y = cMakeBottom;
	lp.gravity = Gravity.BOTTOM;
	dlg.onWindowAttributesChanged(lp);
	dlg.setCanceledOnTouchOutside(true);
//	if (onCancel != null) {
//		dlg.setOnCancelListener(onCancel);
//	}
	dlg.setContentView(content);
	dlg.show();
	
	return dlg;
    }

    private static void initTimePicker(Context context, final Dialog dialog, View parent, final ITimePicker picker) {
	final WheelVerticalView wvDate = (WheelVerticalView) parent.findViewById(R.id.wvDate);
	WheelVerticalView wvHour = (WheelVerticalView) parent.findViewById(R.id.wvHour);
	WheelVerticalView wvMin = (WheelVerticalView) parent.findViewById(R.id.wvMin);
	
	
        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(context, 0, 23, "%02d");
        hourAdapter.setItemResource(R.layout.wheel_text_centered);
        hourAdapter.setItemTextResource(R.id.text);
        wvHour.setViewAdapter(hourAdapter);
    
        NumericWheelAdapter minAdapter = new NumericWheelAdapter(context, 0, 59, "%02d");
        minAdapter.setItemResource(R.layout.wheel_text_centered);
        minAdapter.setItemTextResource(R.id.text);
        wvMin.setViewAdapter(minAdapter);

        // set current time
        Calendar calendar = Calendar.getInstance(Locale.US);
        wvHour.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
        wvMin.setCurrentItem(calendar.get(Calendar.MINUTE));
        wvDate.setCurrentItem(calendar.get(Calendar.AM_PM));
        
        final DayArrayAdapter dayAdapter = new DayArrayAdapter(context, calendar);
        wvDate.setViewAdapter(dayAdapter);
        wvDate.setCurrentItem(dayAdapter.getToday());
        
        Button btnOk = (Button) parent.findViewById(R.id.btnOk);
        Button btnCancel = (Button) parent.findViewById(R.id.btnCancel);
        btnOk.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		String time = dayAdapter.getFormattedTime(wvDate.getCurrentItem());
		if (picker != null) {
		    picker.onTimePicked(time);
		    dialog.dismiss();
		}
	    }
	});
        
        btnCancel.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
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
        private final int daysCount = 4;
        
        // Calendar
        Calendar calendar;
        
        /**
         * Constructor
         */
        protected DayArrayAdapter(Context context, Calendar calendar) {
            super(context, R.layout.time_picker_custom_day, NO_RESOURCE);
            this.calendar = calendar;
            
            setItemTextResource(R.id.time2_monthday);
        }
        public int getToday() {
            return daysCount / 2;
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            int day = -daysCount/2 + index;
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

            TextView monthday = (TextView) view.findViewById(R.id.time2_monthday);
            if (day == 0) {
                monthday.setText("Today");
                monthday.setTextColor(0xFF0000F0);
            } else {
                DateFormat format = new SimpleDateFormat("MMM d");
                monthday.setText(format.format(newCalendar.getTime()));
                monthday.setTextColor(0xFF111111);
            }
            DateFormat dFormat = new SimpleDateFormat("MMM d");
            view.setTag(dFormat.format(newCalendar.getTime()));
            return view;
        }
        
        public String getFormattedTime (int index) {
            int day = -daysCount/2 + index;
            Calendar newCalendar = (Calendar) calendar.clone();
            newCalendar.roll(Calendar.DAY_OF_YEAR, day);
            
            String title = "";
            if (day == 0) {
                title = "Today ";
            } else {
                DateFormat format = new SimpleDateFormat("MMM d");
                title = format.format(newCalendar.getTime());
            }
            
            DateFormat format = new SimpleDateFormat("EEE");
            title += format.format(newCalendar.getTime());
            
            return title;
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
