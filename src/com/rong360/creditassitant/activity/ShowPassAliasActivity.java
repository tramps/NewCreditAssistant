package com.rong360.creditassitant.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.PassCheckHelper;

public class ShowPassAliasActivity extends Activity implements OnClickListener {
    private static final String TAG = "ShowPassActivity";
    private EditText etO;
    private EditText etS;
    private EditText etT;
    private EditText etF;

    private EditText mCurrentE;

    private ArrayList<EditText> mEdits;
    private HashMap<EditText, EditText> mForwardMap;
    private HashMap<EditText, EditText> mBackwardMap;

    private TextView tvHint;
    private int mWrongCount = 0;
    private static final int MAXIMUM_COUNT = 5;

    private Button btnOne;
    private Button btnTwo;
    private Button btnThree;
    private Button btnFour;
    private Button btnFive;
    private Button btnSix;
    private Button btnSeven;
    private Button btnEight;
    private Button btnNine;
    private Button btnZero;
    private Button btnClean;
    private LinearLayout llBack;

    private ArrayList<Button> mButtons;
    private boolean mIsBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	setContentView(R.layout.activity_show_pass_key);
	super.onCreate(savedInstanceState);
	mEdits = new ArrayList<EditText>(4);
	mForwardMap = new HashMap<EditText, EditText>(3);
	mBackwardMap = new HashMap<EditText, EditText>(3);
	mButtons = new ArrayList<Button>();
	initContent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
	super.onNewIntent(intent);
	mWrongCount = 0;
	tvHint.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
	super.onResume();
	mWrongCount = 0;
	tvHint.setVisibility(View.GONE);
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

	initButton();
    }

    @Override
    public void onClick(View v) {
	if (mButtons.contains(v)) {
	    Button clicked = (Button) v;
	    if (mIsBack && mCurrentE.getText().length() > 0) {
		mCurrentE = mForwardMap.get(mCurrentE);
	    }
	    mCurrentE.setText(clicked.getText());
	    mCurrentE = mForwardMap.get(mCurrentE);
	    if (mCurrentE == null) {
		mHandler.post(mValidate);
	    }
	    mIsBack = false;
	} else if (v == llBack) {
	    if (!mIsBack) {
		mCurrentE = mBackwardMap.get(mCurrentE);
		if (mCurrentE == null) {
		    mCurrentE = etO;
		    return;
		}

	    }
	    mIsBack = true;
	    mCurrentE.setText("");
	    mCurrentE = mBackwardMap.get(mCurrentE);
	    if (mCurrentE == null) {
		mCurrentE = etO;
	    }
	} else if (v == btnClean) {
	    reInput();
	    mIsBack = false;
	}
    }

    private void initButton() {
	btnOne = (Button) findViewById(R.id.btnOne);
	btnTwo = (Button) findViewById(R.id.btnTwo);
	btnThree = (Button) findViewById(R.id.btnThree);
	btnFour = (Button) findViewById(R.id.btnFour);
	btnFive = (Button) findViewById(R.id.btnFive);
	btnSix = (Button) findViewById(R.id.btnSix);
	btnSeven = (Button) findViewById(R.id.btnSeven);
	btnEight = (Button) findViewById(R.id.btnEight);
	btnNine = (Button) findViewById(R.id.btnNine);
	btnClean = (Button) findViewById(R.id.btnClear);
	btnZero = (Button) findViewById(R.id.btnZero);
	llBack = (LinearLayout) findViewById(R.id.llBack);
	mButtons.add(btnOne);
	mButtons.add(btnTwo);
	mButtons.add(btnFour);
	mButtons.add(btnThree);
	mButtons.add(btnFive);
	mButtons.add(btnSix);
	mButtons.add(btnEight);
	mButtons.add(btnSeven);
	mButtons.add(btnNine);
	mButtons.add(btnZero);
	btnClean.setOnClickListener(this);
	llBack.setOnClickListener(this);

	for (Button b : mButtons) {
	    b.setOnClickListener(this);
	}
    }

    @Override
    public void onBackPressed() {
    }

    private Handler mHandler = new Handler();

    private Runnable mValidate = new Runnable() {
	public void run() {
	    if (validatePass()) {
		finish();
		PassCheckHelper.getInstance(ShowPassAliasActivity.this).init();
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
	for (EditText et : mEdits) {
	    sb.append(et.getEditableText().toString());
	}
	if (sb.toString().equalsIgnoreCase("9527")) {
	    isMatch = true;
	    mWrongCount = 0;
	} else {
	    mWrongCount++;
	    int leftCount = MAXIMUM_COUNT - mWrongCount;
	    tvHint.setText(sb.toString() + "输错5次将锁定，你还有" + leftCount + "次机会");
	    tvHint.setVisibility(View.VISIBLE);
	    if (leftCount == 0) {
		Intent intent = new Intent(this, LockActivity.class);
		startActivity(intent);
	    }
	}
	return isMatch;
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	Log.i(TAG, "destroed");
	// PassCheckHelper.getInstance(this).init();
    }

}
