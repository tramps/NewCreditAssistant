package com.rong360.creditassitant.widget.wheel.adapter;

import android.content.Context;
import android.util.Log;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter extends AbstractWheelTextAdapter {
    
    /** The default min value */
    public static final int DEFAULT_MAX_VALUE = 9;

    /** The default max value */
    private static final int DEFAULT_MIN_VALUE = 0;
    
    // Values
    private int minValue;
    private int maxValue;
    
    // format
    private String format;
    
    /**
     * Constructor
     * @param context the current context
     */
    public NumericWheelAdapter(Context context) {
        this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
    }

    /**
     * Constructor
     * @param context the current context
     * @param minValue the spinnerwheel min value
     * @param maxValue the spinnerwheel max value
     */
    public NumericWheelAdapter(Context context, int minValue, int maxValue) {
        this(context, minValue, maxValue, null);
    }

    /**
     * Constructor
     * @param context the current context
     * @param minValue the spinnerwheel min value
     * @param maxValue the spinnerwheel max value
     * @param format the format string
     */
    public NumericWheelAdapter(Context context, int minValue, int maxValue, String format) {
        super(context);
        
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = minValue + index;
            if (maxValue == 59) {
        	value = minValue + index * 15;
            }
            return format != null ? String.format(format, value) : Integer.toString(value);
        }
        return null;
    }
    
    public int getCurrentItem(int index) {
	if (maxValue == 59) {
	    return minValue + index * 15;
	} else {
	    return minValue + index;
	}
    }

    @Override
    public int getItemsCount() {
	if (maxValue == 59) {
	    return 4;
	} else {
	    return maxValue - minValue + 1;
	}
    }    
}
