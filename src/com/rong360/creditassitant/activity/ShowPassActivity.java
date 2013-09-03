package com.rong360.creditassitant.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.PassCheckHelper;

public class ShowPassActivity extends Activity implements TextWatcher,
	OnClickListener {
    private static final String TAG = "ShowPassActivity";
    private EditText etO;
    private EditText etS;
    private EditText etT;
    private EditText etF;

    private EditText mCurrentE;

    private ArrayList<EditText> mEdits;
    private HashMap<EditText, EditText> mForwardMap;
    private HashMap<EditText, EditText> mBackwardMap;
    private HashMap<EditText, Integer> mValues;

    private TextView tvHint;
    private boolean mIsback = false;

    private Button btnOne;
    private Button btnTwo;
    private Button btnThree;
    private Button btnFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	setContentView(R.layout.activity_show_pass);
	super.onCreate(savedInstanceState);
	mEdits = new ArrayList<EditText>(4);
	mForwardMap = new HashMap<EditText, EditText>(3);
	mBackwardMap = new HashMap<EditText, EditText>(3);
	mValues = new HashMap<EditText, Integer>();
	initContent();
	// setTextChangeListener();
	// setOnTouchListener();
	etO.requestFocus();
	// etO.setFocusable(true);
    }

    private void setTextChangeListener() {
	for (EditText et : mEdits) {
	    et.addTextChangedListener(this);
	    et.setRawInputType(InputType.TYPE_CLASS_PHONE);
	}
    }

    private void setOnTouchListener() {
	for (EditText et : mEdits) {
	    et.setOnTouchListener(mOnTouchListener);
	}
    }

    private OnTouchListener mOnTouchListener = new OnTouchListener() {

	@Override
	public boolean onTouch(View v, MotionEvent event) {
	    Log.i(TAG, "ontouch;");
	    if (v instanceof EditText) {
		EditText et = (EditText) v;
		et.setInputType(InputType.TYPE_CLASS_PHONE);
		InputMethodManager imm =
			(InputMethodManager) ShowPassActivity.this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// if (!imm.isActive()) {
		Log.i(TAG, "show;");
		imm.toggleSoftInputFromWindow(et.getWindowToken(), 0,
			InputMethodManager.SHOW_FORCED);
		// }
		return true;
	    }
	    Log.i(TAG, "false;");
	    return false;
	}
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	Log.i(TAG, "keycode" + keyCode);
	if (keyCode != KeyEvent.KEYCODE_DEL) {
	    mValues.put(mCurrentE, keyCode);
	    mCurrentE.setText("●");
	    mIsback = false;
	    mCurrentE = mForwardMap.get(mCurrentE);
	    if (mCurrentE == null) {
		mCurrentE = etF;
		mHandler.postDelayed(mValidate, 200);
	    }
	} else {
	    mIsback = true;
	    if (mIsback) {
		mCurrentE = mBackwardMap.get(mCurrentE);
		if (mCurrentE == null) {
		    mCurrentE = etO;
		}
	    }
	    mCurrentE.setText("");
	}
	return super.onKeyDown(keyCode, event);

    }

    private void initContent() {
	etO = (EditText) findViewById(R.id.etPassO);
	etS = (EditText) findViewById(R.id.etPassS);
	etT = (EditText) findViewById(R.id.etPassT);
	etF = (EditText) findViewById(R.id.etPassF);
	mEdits.add(etO);
	mEdits.add(etS);
	mEdits.add(etT);
	mEdits.add(etF);

	mForwardMap.put(etO, etS);
	mForwardMap.put(etS, etT);
	mForwardMap.put(etT, etF);

	mBackwardMap.put(etS, etO);
	mBackwardMap.put(etT, etS);
	mBackwardMap.put(etF, etT);

	tvHint = (TextView) findViewById(R.id.tvHint);

	mCurrentE = etO;

	// EditText etNumber = (EditText) findViewById(R.id.etNumber);
	// etNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
	// etNumber.setClickable(false);
	// // etNumber.setFocusable(false);
	initButton();
    }

    @Override
    public void onClick(View v) {
	if (v == btnOne) {
	    mCurrentE.setText(btnOne.getText());
	} else if (v == btnTwo) {
	    mCurrentE.setText(btnTwo.getText());
	}

	// mCurrentE = mForwardMap.get(mCurrentE);
    }

    private void initButton() {
	btnOne = (Button) findViewById(R.id.btnOne);
	btnTwo = (Button) findViewById(R.id.btnTwo);
	btnThree = (Button) findViewById(R.id.btnThree);
	btnFour = (Button) findViewById(R.id.btnFour);
	btnOne.setOnClickListener(this);
	btnTwo.setOnClickListener(this);
	btnThree.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
	    int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
	String text = s.toString();
	if (text.length() == 1) {
	    mCurrentE = mForwardMap.get(mCurrentE);
	    if (mCurrentE == null) {
		mCurrentE = etF;
		mHandler.postDelayed(mValidate, 200);
	    }
	} else {
	    Log.i(TAG, "back");
	    mCurrentE = mBackwardMap.get(mCurrentE);
	    if (mCurrentE == null) {
		mCurrentE = etO;
	    }
	}
	mCurrentE.requestFocus();
	Log.i(TAG, text + mCurrentE.getId());
    }

    private Handler mHandler = new Handler();

    private Runnable mValidate = new Runnable() {
	public void run() {
	    if (validatePass()) {
		finish();
	    } else {
		reInput();
	    }
	}
    };

    private void reInput() {
	for (EditText et : mEdits) {
	    et.setText("");
	}

	mCurrentE = etO;
    }

    private boolean validatePass() {
	boolean isMatch = false;
	StringBuilder sb = new StringBuilder();
	for (Integer va : mValues.values()) {
	    sb.append(va);
	}
	if (sb.toString().equalsIgnoreCase("8888")) {
	    isMatch = true;
	} else {
	    tvHint.setText("wrong pass: " + sb.toString());
	}
	return isMatch;
    }

    @Override
    protected void onPause() {
	super.onPause();
	PassCheckHelper.getInstance(this).init();
    }

}
