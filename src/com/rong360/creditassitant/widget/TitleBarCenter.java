package com.rong360.creditassitant.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.ActionProvider;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.DisplayUtils;
import com.rong360.creditassitant.widget.FitTextView.ITitle;

@TargetApi(14)
public class TitleBarCenter extends RelativeLayout implements OnClickListener,
	ITitle {
    private static final String TAG = TitleBarCenter.class.getSimpleName();

    private Button mLeft;
    private Button mRight;
    private TextView mTitle;
    private RelativeLayout mRlCenter;
    private View mIndictator;
    private Fragment mFragment;
    private MenuImpl mMenuImpl;
    private Map<View, MenuItem> mMenuItemMap;

    public TitleBarCenter(Context context) {
	this(context, (Fragment) null);
    }

    public TitleBarCenter(Context context, Fragment fragment) {
	this(context, (AttributeSet) null, fragment);
    }

    public TitleBarCenter(Context context, AttributeSet attrs) {
	this(context, attrs, null);
    }

    public TitleBarCenter(Context context, AttributeSet attrs, Fragment fragment) {
	this(context, attrs, R.attr.titleBarStyle, fragment);
    }

    public TitleBarCenter(Context context, AttributeSet attrs, int defStyle,
	    Fragment fragment) {
	super(context, attrs, defStyle);
	mMenuItemMap = new HashMap<View, MenuItem>();
	mFragment = fragment;
	LayoutInflater inflater =
		(LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	inflater.inflate(R.layout.widget_titlebar, this);
	initElements();
	initHomeAndBack();
	initMenuItems();
    }

    private void initElements() {
	mLeft = (Button) findViewById(R.id.back);
	mTitle = (TextView) findViewById(R.id.tv_title);
	mIndictator = findViewById(R.id.iv_indictator);
	mRlCenter = (RelativeLayout) findViewById(R.id.rlTitle);
	mLeft.setOnClickListener(this);
	mLeft.setVisibility(View.VISIBLE);
	mLeft.setBackgroundResource(R.drawable.ic_group_send);
	
	mRight = (Button) findViewById(R.id.add);
//	mRight.setOnClickListener(this);
    }

    public void setTitleListener(OnClickListener onClick) {
	mRlCenter.setOnClickListener(onClick);
    }

    public void setMsgListener(OnClickListener onclick) {
	mLeft.setOnClickListener(onclick);
    }
    
    public void setAddListener(OnClickListener onclick) {
	mRight.setVisibility(View.VISIBLE);
	mRight.setOnClickListener(onclick);
    }

    public void displayIndicator(boolean isShow) {
	if (isShow) {
	    mIndictator.setVisibility(View.VISIBLE);
	} else {
	    mIndictator.setVisibility(View.GONE);
	}
    }

    public void hideLeft() {
	mLeft.setVisibility(View.INVISIBLE);
    }

    public void showLeft() {
	mLeft.setVisibility(View.VISIBLE);
    }

    public void setLeftButton(String handle, int bkgResId) {
	mLeft.setText(handle);
	mLeft.setTextColor(Color.WHITE);
	mLeft.setBackgroundResource(bkgResId);
    }

    private void initHomeAndBack() {
	MenuItemImpl homeItem = new MenuItemImpl(getContext());
	homeItem.mItemId = mLeft.getId();
	mMenuItemMap.put(mLeft, homeItem);
    }

    private void initMenuItems() {
	Context context = getContext();
	mMenuImpl = new MenuImpl(context);
	if (mFragment != null) {
	    mFragment.onCreateOptionsMenu(mMenuImpl, null);
	} else if (context instanceof Activity) {
	    Activity act = (Activity) context;
	    act.onCreateOptionsMenu(mMenuImpl);
	}
	int lastId = 0;
	int margin = DisplayUtils.getPixel(context, 10);
	for (int i = 0; i < mMenuImpl.size(); i++) {
	    MenuItem menuItem = mMenuImpl.getItem(i);
	    if (menuItem.getIcon() != null) {
//		mRight.setVisibility(vi)
		// itemView = new Button(context);
		// ((Button)itemView).setBackgroundResource(R.drawable.ic_add);
		// ((ImageButton)itemView).setBackgroundResource(R.drawable.bkg_blue);
	    } else {
		View itemView;
		itemView = new Button(context);
		((Button) itemView).setText(menuItem.getTitle());
		((Button) itemView).setTextColor(Color.WHITE);
		((Button) itemView).setBackgroundResource(R.drawable.bkg_green);
		itemView.setId(menuItem.getItemId());
		itemView.setOnClickListener(this);
		mMenuItemMap.put(itemView, menuItem);
		addView(itemView);
		// itemView.setPadding(margin, 0, margin, 0);
		RelativeLayout.LayoutParams param =
			(LayoutParams) itemView.getLayoutParams();
		param.rightMargin = margin;
		param.addRule(RelativeLayout.CENTER_VERTICAL);
		if (lastId == 0) {
		    param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		} else {
		    param.addRule(RelativeLayout.LEFT_OF, lastId);
		}
		lastId = itemView.getId();
	    }
	}
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	int mode = MeasureSpec.getMode(widthMeasureSpec);
	int width = MeasureSpec.getSize(widthMeasureSpec);
	if (mode == MeasureSpec.AT_MOST) {
	    setMeasuredDimension(width, getMeasuredHeight());
	}
	// int buttonWidth = Math.max(mHome.getMeasuredWidth(),
	// mBack.getMeasuredWidth());
	// int buttonCount = Math.max(1, mMenuImpl.size());
	// MarginLayoutParams params = (MarginLayoutParams) mTitle
	// .getLayoutParams();
	// // TODO
	// params.leftMargin = buttonWidth * buttonCount;
	// params.rightMargin = buttonWidth * buttonCount;
    }

    @Override
    public void onClick(View v) {
	Context context = getContext();
	MenuItem item = mMenuItemMap.get(v);
	if (item != null) {
	    if (mFragment != null) {
		mFragment.onOptionsItemSelected(item);
	    } else if (context instanceof Activity) {
		Activity act = (Activity) context;
		act.onOptionsItemSelected(item);
	    }
	} 
    }

    public void setDisplayHomeAsUpEnabled(boolean b) {
	if (b) {
	    mLeft.setVisibility(View.GONE);
	} else {
	    mLeft.setVisibility(View.VISIBLE);
	}
    }

    public TextView getTitleText() {
	return mTitle;
    }

    public void setTitle(String s) {
	mTitle.setText(s);
    }

    private class MenuImpl implements Menu {
	private Context mContext;
	private List<MenuItem> mMenuItems;

	public MenuImpl(Context context) {
	    mContext = context;
	    mMenuItems = new ArrayList<MenuItem>();
	}

	@Override
	public MenuItem add(CharSequence title) {
	    return add(0, 0, 0, title);
	}

	@Override
	public MenuItem add(int titleRes) {
	    return add(mContext.getString(titleRes));
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order,
		CharSequence title) {
	    MenuItemImpl item = new MenuItemImpl(mContext);
	    item.mItemId = itemId;
	    item.mGroupId = groupId;
	    item.mTitle = title;
	    mMenuItems.add(item);
	    return item;
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, int titleRes) {
	    return add(groupId, itemId, order, mContext.getResources()
		    .getString(titleRes));
	}

	@Override
	public SubMenu addSubMenu(CharSequence title) {
	    return null;
	}

	@Override
	public SubMenu addSubMenu(int titleRes) {
	    return null;
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order,
		CharSequence title) {
	    return null;
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order,
		int titleRes) {
	    return null;
	}

	@Override
	public int addIntentOptions(int groupId, int itemId, int order,
		ComponentName caller, Intent[] specifics, Intent intent,
		int flags, MenuItem[] outSpecificItems) {
	    return 0;
	}

	@Override
	public void removeItem(int id) {
	}

	@Override
	public void removeGroup(int groupId) {
	}

	@Override
	public void clear() {
	}

	@Override
	public void setGroupCheckable(int group, boolean checkable,
		boolean exclusive) {
	}

	@Override
	public void setGroupVisible(int group, boolean visible) {
	}

	@Override
	public void setGroupEnabled(int group, boolean enabled) {
	}

	@Override
	public boolean hasVisibleItems() {
	    return false;
	}

	@Override
	public MenuItem findItem(int id) {
	    return null;
	}

	@Override
	public int size() {
	    return mMenuItems.size();
	}

	@Override
	public MenuItem getItem(int index) {
	    return mMenuItems.get(index);
	}

	@Override
	public void close() {
	}

	@Override
	public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
	    return false;
	}

	@Override
	public boolean isShortcutKey(int keyCode, KeyEvent event) {
	    return false;
	}

	@Override
	public boolean performIdentifierAction(int id, int flags) {
	    return false;
	}

	@Override
	public void setQwertyMode(boolean isQwerty) {
	}
    }

    private class MenuItemImpl implements MenuItem {
	private int mItemId;
	private int mGroupId;
	private CharSequence mTitle;
	private Drawable mIcon;
	private Context mContext;

	public MenuItemImpl(Context context) {
	    mContext = context;
	}

	@Override
	public int getItemId() {
	    return mItemId;
	}

	@Override
	public int getGroupId() {
	    return mGroupId;
	}

	@Override
	public int getOrder() {
	    return 0;
	}

	@Override
	public MenuItem setTitle(CharSequence title) {
	    mTitle = title;
	    return this;
	}

	@Override
	public MenuItem setTitle(int title) {
	    return setTitle(mContext.getString(title));
	}

	@Override
	public CharSequence getTitle() {
	    return mTitle;
	}

	@Override
	public MenuItem setTitleCondensed(CharSequence title) {
	    return null;
	}

	@Override
	public CharSequence getTitleCondensed() {
	    return null;
	}

	@Override
	public MenuItem setIcon(Drawable icon) {
	    mIcon = icon;
	    return this;
	}

	@Override
	public MenuItem setIcon(int iconRes) {
	    return setIcon(mContext.getResources().getDrawable(iconRes));
	}

	@Override
	public Drawable getIcon() {
	    return mIcon;
	}

	@Override
	public MenuItem setIntent(Intent intent) {
	    return null;
	}

	@Override
	public Intent getIntent() {
	    return null;
	}

	@Override
	public MenuItem setShortcut(char numericChar, char alphaChar) {
	    return null;
	}

	@Override
	public MenuItem setNumericShortcut(char numericChar) {
	    return null;
	}

	@Override
	public char getNumericShortcut() {
	    return 0;
	}

	@Override
	public MenuItem setAlphabeticShortcut(char alphaChar) {
	    return null;
	}

	@Override
	public char getAlphabeticShortcut() {
	    return 0;
	}

	@Override
	public MenuItem setCheckable(boolean checkable) {
	    return null;
	}

	@Override
	public boolean isCheckable() {
	    return false;
	}

	@Override
	public MenuItem setChecked(boolean checked) {
	    return null;
	}

	@Override
	public boolean isChecked() {
	    return false;
	}

	@Override
	public MenuItem setVisible(boolean visible) {
	    return null;
	}

	@Override
	public boolean isVisible() {
	    return false;
	}

	@Override
	public MenuItem setEnabled(boolean enabled) {
	    return null;
	}

	@Override
	public boolean isEnabled() {
	    return false;
	}

	@Override
	public boolean hasSubMenu() {
	    return false;
	}

	@Override
	public SubMenu getSubMenu() {
	    return null;
	}

	@Override
	public MenuItem setOnMenuItemClickListener(
		OnMenuItemClickListener menuItemClickListener) {
	    return null;
	}

	@Override
	public ContextMenuInfo getMenuInfo() {
	    return null;
	}

	@Override
	public void setShowAsAction(int actionEnum) {
	}

	@Override
	public MenuItem setShowAsActionFlags(int actionEnum) {
	    return null;
	}

	@Override
	public MenuItem setActionView(View view) {
	    return null;
	}

	@Override
	public MenuItem setActionView(int resId) {
	    return null;
	}

	@Override
	public View getActionView() {
	    return null;
	}

	@Override
	public MenuItem setActionProvider(ActionProvider actionProvider) {
	    return null;
	}

	@Override
	public ActionProvider getActionProvider() {
	    return null;
	}

	@Override
	public boolean expandActionView() {
	    return false;
	}

	@Override
	public boolean collapseActionView() {
	    return false;
	}

	@Override
	public boolean isActionViewExpanded() {
	    return false;
	}

	@Override
	public MenuItem setOnActionExpandListener(
		OnActionExpandListener listener) {
	    return null;
	}
    }
}
