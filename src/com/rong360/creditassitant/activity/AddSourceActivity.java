package com.rong360.creditassitant.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.text.InputFilter.LengthFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.MyToast;
import com.rong360.creditassitant.util.PreferenceHelper;

public class AddSourceActivity extends BaseActionBar {
    public static final String PRE_KEY_SOURCES = "pre_key_sources";
    private EditText etSource;
    private ArrayList<String> mSources;
    private PreferenceHelper mHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSources = new ArrayList<String>(); 
        getSupportActionBar().setTitle("添加客户来源");
        mHelper = PreferenceHelper.getHelper(this);
        String source = mHelper.readPreference(PRE_KEY_SOURCES);
        if (source != null && source.length() > 0) {
            String[] sources = source.split(";");
            for (String s : sources) {
        	mSources.add(s);
            }
        }
        mSources.add("自有客户");
        mSources.add("融360自动导入");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	menu.add(0, R.id.finish, 0, "确定");
	return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	if (item.getItemId() == R.id.finish) {
	    String source = etSource.getText().toString().trim();
	    if (source.length() > 10) {
		MyToast.makeText(this, "太长了，亲~", Toast.LENGTH_SHORT).show();
		return true;
	    }
	    if (source.length() == 0) {
		MyToast.makeText(this, "请输入客户来源", Toast.LENGTH_SHORT).show();
		return true;
	    } else {
		for (String hSource : mSources) {
		    if (hSource.equalsIgnoreCase(source)) {
			MyToast.makeText(this, "该客户来源已存在", Toast.LENGTH_SHORT).show();
			return true;
		    }
		}
		mSources.add(0,source);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mSources.size() - 2; i++) {
		    sb.append(mSources.get(i));
		    sb.append(";");
		}
		PreferenceHelper pref = PreferenceHelper.getHelper(this);
		pref.writePreference(PRE_KEY_SOURCES, sb.toString());
	    }
	    finish();
	    return true;

	}
	return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void initElements() {
	etSource = (EditText) findViewById(R.id.etSource);
    }
    
    @Override
    protected int getLayout() {
	return R.layout.activity_add_source;
    }

}
