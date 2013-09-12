package com.rong360.creditassitant.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.PreferenceHelper;

public class SetPassActivity extends BaseActionBar implements OnClickListener {
    public static final String EXTRA_MODE = "extra_mode";

    public static final int MODE_SET = 0;
    public static final int MODE_MODIFY = 1;

    public static final String PRE_KEY_PASS = "pre_key_pass";

    private int mMode;

    private EditText etO;
    private EditText etS;
    private EditText etT;
    private EditText etF;

    private EditText mCurrentE;

    private ArrayList<EditText> mEdits;
    private HashMap<EditText, EditText> mForwardMap;
    private HashMap<EditText, EditText> mBackwardMap;

    private TextView tvHint;
    private TextView tvError;
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

    private int mStep;
    private String mPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	mEdits = new ArrayList<EditText>(4);
	mForwardMap = new HashMap<EditText, EditText>(3);
	mBackwardMap = new HashMap<EditText, EditText>(3);
	mButtons = new ArrayList<Button>();
	mStep = 0;

	super.onCreate(savedInstanceState);
	mMode = getIntent().getIntExtra(EXTRA_MODE, MODE_SET);
	if (mMode == MODE_SET) {
	    getSupportActionBar().setTitle("设置密码");
	} else {
	    getSupportActionBar().setTitle("修改密码");
	}
	
    }

    private void initContent() {
	if (mMode == MODE_SET) {
	    tvHint.setText("请输入密码");
	} else {
	    tvHint.setText("请输入旧密码");
	}
	tvError.setText("");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        initContent();
    }

    @Override
    protected void initElements() {
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
	tvError = (TextView) findViewById(R.id.tvError);

	mCurrentE = etO;
	initButton();
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
    protected int getLayout() {
	return R.layout.activity_set_pass;
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
		if (mMode == MODE_SET) {
		    if (mStep == 0) {
			reInputPass();
		    } else {
			mHandler.post(mValidate);
		    }
		} else {
		    if (mStep == 0 || mStep == 2) {
			mHandler.post(mValidate);
		    } else if (mStep == 1) {
			reInputPass();
		    }
		}
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

    private void reInputPass() {
	mPass = getPass();
	reInput();
	mStep++;
	if (mMode == MODE_SET) {
	    tvHint.setText("请再次输入密码");
	} else {
	    if (mStep == 1) {
		tvHint.setText("请输入新密码");
	    } else {
		tvHint.setText("请再次输入密码");
	    }
	}
    }

    private Handler mHandler = new Handler();
    private Runnable mValidate = new Runnable() {
	public void run() {
	    if (validatePass()) {
		mStep++;
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
	String pass = getPass();
	if (mMode == MODE_SET) {
	    isMatch = checkDoublePass();
	} else {
	    if (mStep == 0) {
		String oldPass =
			PreferenceHelper.getHelper(this).readPreference(
				PRE_KEY_PASS);
		if (pass.equalsIgnoreCase(oldPass)) {
		    tvHint.setText("请输入新的密码");
		    tvError.setText("");
		    reInput();
		    isMatch = true;
		} else {
		    mWrongCount++;
		    int leftCount = MAXIMUM_COUNT - mWrongCount;
		    tvError.setText(pass + "输错5次将锁定，你还有" + leftCount + "次机会");
		    if (leftCount == 0) {
			Intent intent = new Intent(this, LockActivity.class);
			startActivity(intent);
			mWrongCount = 0;
		    }
		}
	    } else {
		isMatch = checkDoublePass();
	    }
	}
	return isMatch;
    }

    private boolean checkDoublePass() {
	String pass = getPass();
	if (pass.equalsIgnoreCase(mPass)) {
	    PreferenceHelper.getHelper(this).writePreference(PRE_KEY_PASS,
		    mPass);
	    finish();
	    return true;
	    
	} else {
	    tvError.setText("密码不匹配，请再输入一次");
	    return false;
	}
    }

    private String getPass() {
	StringBuilder sb = new StringBuilder();
	for (EditText et : mEdits) {
	    sb.append(et.getEditableText().toString());
	}
	return sb.toString();
    }

}
