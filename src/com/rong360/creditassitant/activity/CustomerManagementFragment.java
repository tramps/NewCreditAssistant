package com.rong360.creditassitant.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.widget.TitleBarCenter;

public class CustomerManagementFragment extends BaseFragment implements OnClickListener {
	private Button btnImport;
	private ArrayList<Customer> mCustomers;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBarCenter center = getSupportActionBarCenter(Boolean.TRUE);
		center.showLeft();
		center.setTitle("客户管理");
		setReuseOldViewEnable(true);
		setHasOptionsMenu(false);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add(0, R.id.newCustomer, 0, "new customer").setIcon(R.drawable.ic_add);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.newCustomer) {
			Intent intent = new Intent(mContext, AddCustomerActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		initContent();
	}
	
	@Override
	protected void initElement() {
		btnImport = (Button) findViewById(R.id.btnImport);
		btnImport.setOnClickListener(this);
	}

	private void initContent() {
//		if 
	}

	@Override
	protected int getLayout() {
		return R.layout.fragment_customers;
	}

	@Override
	public void onClick(View v) {
		if (v == btnImport) {
			Intent intent = new Intent(mContext, ImportContactActivity.class);
			startActivity(intent);
		}
	}
}
