package com.rong360.creditassitant.task;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rong360.creditassitant.R;

public class WaitDialog extends Dialog {
    private TextView tvHint;

    public WaitDialog(Context context) {
	super(context, R.style.Theme_Dialog_NoFrame);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	setContentView(R.layout.dialog_wait);
//	View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_wait, null); 
	ProgressBar pb = (ProgressBar) findViewById(R.id.pbCircle);
	pb.setIndeterminate(true);
//	pb.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.ic_circle));
        tvHint = (TextView) findViewById(R.id.tvHint);
        super.onCreate(savedInstanceState);
    }
    
    
    public void setHint(String hint) {
	tvHint.setText(hint);
    }
    
    public void setMessage(CharSequence message) {
	Log.i("waiting", "msg:" + message);
	tvHint.setText(message);
    }

}
