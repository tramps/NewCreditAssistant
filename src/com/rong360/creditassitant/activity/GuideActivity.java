package com.rong360.creditassitant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.PreferenceHelper;

public class GuideActivity extends BaseActionBar {

    private ViewPager vp;
    private ImageAdapter mAdapter;

    private static final String PRE_KEY_FIRST = "pre_key_first";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	String first =
		PreferenceHelper.getHelper(this).readPreference(PRE_KEY_FIRST);
	if (first != null) {
	    Intent intent = new Intent(this, MainTabHost.class);
	    startActivity(intent);
	    finish();
	    return;
	} else {
	    PreferenceHelper.getHelper(this).writePreference(PRE_KEY_FIRST,
		    "first");
	}

	mAdapter = new ImageAdapter(this);
	vp.setAdapter(mAdapter);
    }

    @Override
    protected void initElements() {
	vp = (ViewPager) findViewById(R.id.pager);
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_guide;
    }

    public class ImageAdapter extends PagerAdapter {

	private int[] images = new int[] { R.drawable.loading1,
		R.drawable.loading2, R.drawable.loading3, R.drawable.loading4 };

	private Context mContext;

	public ImageAdapter(Context context) {
	    mContext = context;
	}

	@Override
	public int getCount() {
	    return images.length;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
	    return arg0 == (View) arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
	    ImageView view =
		    (ImageView) getLayoutInflater().inflate(
			    R.layout.list_item_image, null);
	    view.setImageResource(images[position]);
	    view.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
		    if (position == images.length - 1) {
			Intent intent =
				new Intent(GuideActivity.this,
					MainTabHost.class);
			startActivity(intent);
			finish();
		    }
		}
	    });
	    // added to ViewPager, container == ViewPager
	    container.addView(view);
	    return view;
	}

	@Override
	public void
		destroyItem(ViewGroup container, int position, Object object) {
	    container.removeView((View) object);
	}
    }
}
