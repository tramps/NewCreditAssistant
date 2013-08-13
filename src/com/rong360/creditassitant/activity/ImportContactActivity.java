package com.rong360.creditassitant.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.CommuHandler;
import com.rong360.creditassitant.model.Contact;
import com.rong360.creditassitant.util.StringMatcher;
import com.rong360.creditassitant.widget.IndexableListView;

public class ImportContactActivity extends BaseActionBar {
	private IndexableListView lvIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar(Boolean.TRUE).setTitle("通讯录导入客户");
		
	}
	
	@Override
	protected int getLayout() {
		return R.layout.activity_import_contact;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initContent();
	}
	
	private void initContent() {
		ArrayList<Contact> contacts = CommuHandler.getAllContacts(this);
		ContactAdapter adapter = new ContactAdapter(this, contacts);
		lvIndex.setAdapter(adapter);
	}

	@Override
	protected void initElements() {
		lvIndex = (IndexableListView) findViewById(R.id.lv_index);
	}
	
	private class ContactAdapter extends BaseAdapter implements SectionIndexer {
		private ArrayList<Contact> mContacts; 
		private Context mContext;
		
		public ContactAdapter(Context context, ArrayList<Contact> contacts) {
			mContext = context;
			mContacts = contacts;
		}

		@Override
		public int getCount() {
			return mContacts.size();
		}

		@Override
		public Contact getItem(int position) {
			return mContacts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_contact, null);
			}
			
			TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
			Contact c = getItem(position);
			tvName.setText(c.getName() + " " + c.getTel() + " " + c.getCreateTime());
			
			return convertView;
		}

		private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    	
		@Override
		public int getPositionForSection(int section) {
			// If there is no item for current section, previous section will be selected
			int size = getCount();
			for (int i = section; i >= 0; i--) {
				for (int j = 0; j < size; j++) {
					if (i == 0) {
						// For numeric section
						for (int k = 0; k <= 9; k++) {
							if (StringMatcher.match(String.valueOf(getItem(j).getName().charAt(0)), String.valueOf(k)))
								return j;
						}
					} else {
						if (StringMatcher.match(String.valueOf(getItem(j).getName().charAt(0)), String.valueOf(mSections.charAt(i))))
							return j;
					}
				}
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position){
			return 0;
		}

		@Override
		public Object[] getSections() {
			String[] sections = new String[mSections.length()];
			for (int i = 0; i < mSections.length(); i++)
				sections[i] = String.valueOf(mSections.charAt(i));
			return sections;
		}
		
	}

}
