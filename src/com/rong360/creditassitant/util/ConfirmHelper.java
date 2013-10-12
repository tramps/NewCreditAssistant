package com.rong360.creditassitant.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.rong360.creditassitant.R;

public class ConfirmHelper {
    public static void showFollowDialog(final Context context, final String title,
	    final int pos, final OnRefresh cancel) {
	final AlertDialog dialog = new AlertDialog.Builder(context).create();

	View content =
		LayoutInflater.from(context).inflate(
			R.layout.dialog_confirm, null);
	
	TextView tvTitle = (TextView) content.findViewById(R.id.tvTitle);
	TextView tvHint = (TextView) content.findViewById(R.id.tvHint);
	
	Button btnDelete = (Button) content.findViewById(R.id.btnDelete);
	Button btnCancel = (Button) content.findViewById(R.id.btnCancel);
	
	btnDelete.setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		if (cancel != null) {
		    cancel.onRefresh(pos);
		}
		dialog.cancel();
	    }
	});
	
	btnCancel.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		dialog.cancel();
	    }
	});
	
	tvTitle.setText("确定删除[" + title + "]来源？");
	tvHint.setText("已经标注该来源的客户将丢失来源信息");
	
	dialog.show();
	dialog.setCanceledOnTouchOutside(true);
	dialog.setContentView(content);
    }
    
    public static void showSafeDialog(final Context context, final OnRefresh cancel) {
	final AlertDialog dialog = new AlertDialog.Builder(context).create();

	View content =
		LayoutInflater.from(context).inflate(
			R.layout.dialog_safe, null);
	
	TextView tvTitle = (TextView) content.findViewById(R.id.tvTitle);
	TextView tvHint = (TextView) content.findViewById(R.id.tvHint);
	
	Button btnDelete = (Button) content.findViewById(R.id.btnDelete);
	Button btnCancel = (Button) content.findViewById(R.id.btnCancel);
	
	btnDelete.setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		cancel.onRefresh(0);
		dialog.cancel();
	    }
	});
	
	btnCancel.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		dialog.cancel();
	    }
	});
	
	tvTitle.setText("确定关闭保险箱");
	tvHint.setText("关闭保险箱将不能保护您的资料");
	
	dialog.show();
	dialog.setCanceledOnTouchOutside(true);
	dialog.setContentView(content);
    }

    public static interface OnRefresh {
	public void onRefresh(int pos);
    }
}
