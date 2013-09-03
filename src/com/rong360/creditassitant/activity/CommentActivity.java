package com.rong360.creditassitant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.Comment;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.util.GlobalValue;

public class CommentActivity extends BaseActionBar {
    private static final String TAG = "CommentActivity";
    public static String EXTRA_COMMENT = "extra_comment";
    public static String EXTRA_ID = "extra_id";

    public static String RESULT_COMMENT = "result_comment";

    private EditText etComment;
    private String mHisComment;
    private int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getSupportActionBar(false).setTitle("客户备注");
	mHisComment = getIntent().getStringExtra(EXTRA_COMMENT);
	mId = getIntent().getIntExtra(EXTRA_ID, -1);
	if (mId == -1) {
	    Log.e("CommentActivity", "id can't be -1");
	    finish();
	}
	if (mHisComment != null) {
	    etComment.setText(mHisComment);
	    etComment.setSelection(mHisComment.length());
	} else {
	    mHisComment = "";
	}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	menu.add(0, R.id.finish, 0, "完成");
	return false;
    }

    @Override
    protected void onResume() {
	super.onResume();
    }

    @Override
    protected void initElements() {
	etComment = (EditText) findViewById(R.id.etComment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	if (item.getItemId() == R.id.finish) {
	    saveComment();
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }

    private void saveComment() {
	//
	Intent intent = new Intent();
	String comment = etComment.getText().toString();
	if (mHisComment.equalsIgnoreCase(comment)) {
	    setResult(RESULT_CANCELED);
	    finish();
	    return;
	}

//	GlobalValue value = GlobalValue.getIns();
//	Comment c = new Comment();
//	c.setComment(comment);
//	c.setCustomerId(mId);
//	boolean isInsert = value.getCommentHandler(this).insertComment(c);
//	if (isInsert) {
//	    Customer cus = value.getCusmer(mId);
//	    cus.setLastFollowComment(comment);
//	    value.getCustomerHandler(this).updateCustomer(cus);
//	    value.putCustomer(cus);
//	}

	intent.putExtra(RESULT_COMMENT, comment);
	setResult(RESULT_OK, intent);
	finish();
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_comment;
    }

}
