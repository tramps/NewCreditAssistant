package com.rong360.creditassitant.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.util.Log;

import com.rong360.creditassitant.model.Customer;

public class ModelHeler {
    private static final String TAG = "ModelHeler";

    public static boolean match(String value, String keyword) {
	if (value == null || keyword == null)
	    return false;
	if (keyword.length() > value.length())
	    return false;

	int i = 0, j = 0;
	do {
	    if (keyword.charAt(j) == value.charAt(i)) {
		i++;
		j++;
	    } else if (j > 0)
		break;
	    else
		i++;
	} while (i < value.length() && j < keyword.length());

	return (j == keyword.length()) ? true : false;
    }

    public static boolean isTelEqual(String firTel, String secTel) {
//	Log.i(TAG, firTel + " " + secTel);
	if (firTel == null || secTel == null) {
	    return false;
	}
	byte[] firs = firTel.getBytes();
	byte[] secs = secTel.getBytes();

	if (firs.length < 8 || secs.length < 8) {
//	    Log.i(TAG, "length < 8");
	    return false;
	}

	for (int i = 0, n = firs.length - 1, m = secs.length - 1; i < 8; i++, n--, m--) {
	    if (firs[n] != secs[m]) {
		return false;
	    }
	}

	return true;
    }

    public static void orderCustomersByTime(ArrayList<Customer> customers) {
	Collections.sort(customers, new Comparator<Customer>() {

	    @Override
	    public int compare(Customer lhs, Customer rhs) {
		return lhs.getTime() >= rhs.getTime() ? 1 : -1;
	    }
	});
    }
}
