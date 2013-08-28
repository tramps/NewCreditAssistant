package com.rong360.creditassitant.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rong360.creditassitant.R;

public class QuickAction extends PopupWindows implements OnDismissListener {
    private View mRootView;
    // private ImageView mArrowUp;
    // private ImageView mArrowDown;
    private LayoutInflater mInflater;
    private ViewGroup mTrack;
    private ScrollView mScroller;
    private OnActionItemClickListener mItemClickListener;
    private OnDismissListener mDismissListener;

    private List<ActionItem> actionItems = new ArrayList<ActionItem>();

    private boolean mDidAction;

    private int mChildPos;
    private int mInsertPos;
    private int mAnimStyle;
    private int mOrientation;
    private int rootWidth = 0;

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    public static final int ANIM_GROW_FROM_LEFT = 1;
    public static final int ANIM_GROW_FROM_RIGHT = 2;
    public static final int ANIM_GROW_FROM_CENTER = 3;
    public static final int ANIM_REFLECT = 4;
    public static final int ANIM_AUTO = 5;

    /**
     * Constructor for default vertical layout
     * 
     * @param context
     *            Context
     */
    public QuickAction(Context context) {
	this(context, VERTICAL);
    }

    /**
     * Constructor allowing orientation override
     * 
     * @param context
     *            Context
     * @param orientation
     *            Layout orientation, can be vartical or horizontal
     */
    public QuickAction(Context context, int orientation) {
	super(context);

	mOrientation = orientation;

	mInflater =
		(LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	setRootViewId(R.layout.popup_vertical);

	mAnimStyle = ANIM_AUTO;
	mChildPos = 0;
    }

    /**
     * Get action item at an index
     * 
     * @param index
     *            Index of item (position from callback)
     * 
     * @return Action Item at the position
     */
    public ActionItem getActionItem(int index) {
	return actionItems.get(index);
    }
    
    public View getRootView() {
	return mRootView;
    }

    /**
     * Set root view.
     * 
     * @param id
     *            Layout resource id
     */
    public void setRootViewId(int id) {
	mRootView = (ViewGroup) mInflater.inflate(id, null);
	mTrack = (ViewGroup) mRootView.findViewById(R.id.tracks);
	mScroller = (ScrollView) mRootView.findViewById(R.id.scroller);

	// This was previously defined on show() method, moved here to prevent
	// force close that occured
	// when tapping fastly on a view to show quickaction dialog.
	// Thanx to zammbi (github.com/zammbi)
	mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
		250));

	setContentView(mRootView);
    }

    /**
     * Set animation style
     * 
     * @param mAnimStyle
     *            animation style, default is set to ANIM_AUTO
     */
    public void setAnimStyle(int mAnimStyle) {
	this.mAnimStyle = mAnimStyle;
    }

    /**
     * Set listener for action item clicked.
     * 
     * @param listener
     *            Listener
     */
    public void
	    setOnActionItemClickListener(OnActionItemClickListener listener) {
	mItemClickListener = listener;
    }

    /**
     * Add action item
     * 
     * @param action
     *            {@link ActionItem}
     */
    public void addActionItem(ActionItem action) {
	actionItems.add(action);
	View container;
	if (action.getActionId() == -1) {
	    container = mInflater.inflate(R.layout.action_item_divider, null);
	} else {
	    String title = action.getTitle();
	    container = mInflater.inflate(R.layout.action_item_vertical, null);
	    TextView text = (TextView) container.findViewById(R.id.tv_title);

	    if (title != null) {
		text.setText(title);
	    } else {
		text.setVisibility(View.GONE);
	    }

	    final int pos = mChildPos;
	    final int actionId = action.getActionId();

	    container.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		    if (mItemClickListener != null) {
			mItemClickListener.onItemClick(QuickAction.this, pos,
				actionId);
		    }

		    if (!getActionItem(pos).isSticky()) {
			mDidAction = true;

			dismiss();
		    }
		}
	    });

	    container.setFocusable(true);
	    container.setClickable(true);
	}

	mTrack.addView(container, mInsertPos);
	mChildPos++;
	mInsertPos++;
    }
    
    public void dismiss() {
	mWindow.dismiss();
    }

    /**
     * Show quickaction popup. Popup is automatically positioned, on top or
     * bottom of anchor view.
     * 
     */
    public void showView(View anchor) {
	preShow();

	int xPos, yPos, arrowPos;

	mDidAction = false;

	int[] location = new int[2];

	anchor.getLocationOnScreen(location);

	Rect anchorRect =
		new Rect(location[0], location[1], location[0]
			+ anchor.getWidth(), location[1] + anchor.getHeight());

	// mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT));

	mRootView.measure(LayoutParams.WRAP_CONTENT, 250);

	int rootHeight = mRootView.getMeasuredHeight();

	if (rootWidth == 0) {
	    rootWidth = mRootView.getMeasuredWidth();
	}

	int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
	int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

	// automatically get X coord of popup (top left)
	if ((anchorRect.left + rootWidth) > screenWidth) {
	    xPos = anchorRect.left - (rootWidth - anchor.getWidth());
	    xPos = (xPos < 0) ? 0 : xPos;

	    arrowPos = anchorRect.centerX() - xPos;

	} else {
	    // if (anchor.getWidth() > rootWidth) {
	     xPos = anchorRect.centerX() - (rootWidth / 2);
	    // } else {
//	    xPos = anchorRect.left;
	    // }

	    arrowPos = anchorRect.centerX() - xPos;
	}

	int dyTop = anchorRect.top;
	int dyBottom = screenHeight - anchorRect.bottom;

	boolean onTop = (dyTop > dyBottom) ? true : false;

	if (onTop) {
	    if (rootHeight > dyTop) {
		yPos = 15;
		LayoutParams l = mScroller.getLayoutParams();
		l.height = dyTop - anchor.getHeight();
	    } else {
		yPos = anchorRect.top - rootHeight;
	    }
	} else {
	    yPos = anchorRect.bottom;

	    if (rootHeight > dyBottom) {
		LayoutParams l = mScroller.getLayoutParams();
		l.height = dyBottom;
	    }
	}

	// showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), arrowPos);

	setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);

	mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }

    /**
     * Set animation style
     * 
     * @param screenWidth
     *            screen width
     * @param requestedX
     *            distance from left edge
     * @param onTop
     *            flag to indicate where the popup should be displayed. Set TRUE
     *            if displayed on top of anchor view and vice versa
     */
    private void setAnimationStyle(int screenWidth, int requestedX,
	    boolean onTop) {
	int arrowPos = requestedX;// - mArrowUp.getMeasuredWidth()/2;
	// mWindow.setAnimationStyle((onTop)
	// ? R.style.Animations_PopUpMenu_Left
	// : R.style.Animations_PopDownMenu_Left);
    }

    /**
     * Show arrow
     * 
     * @param whichArrow
     *            arrow type resource id
     * @param requestedX
     *            distance from left screen
     */
    private void showArrow(int whichArrow, int requestedX) {
	// final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp :
	// mArrowDown;
	// final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown :
	// mArrowUp;
	//
	// final int arrowWidth = mArrowUp.getMeasuredWidth();
	//
	// showArrow.setVisibility(View.VISIBLE);
	//
	// ViewGroup.MarginLayoutParams param =
	// (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
	//
	// param.leftMargin = requestedX - arrowWidth / 2;
	//
	// hideArrow.setVisibility(View.INVISIBLE);
    }

    /**
     * Set listener for window dismissed. This listener will only be fired if
     * the quicakction dialog is dismissed by clicking outside the dialog or
     * clicking on sticky item.
     */
    public void setOnDismissListener(QuickAction.OnDismissListener listener) {
	setOnDismissListener(this);

	mDismissListener = listener;
    }

    @Override
    public void onDismiss() {
	if (!mDidAction && mDismissListener != null) {
	    mDismissListener.onDismiss();
	}
    }

    /**
     * Listener for item click
     * 
     */
    public interface OnActionItemClickListener {
	public abstract void onItemClick(QuickAction source, int pos,
		int actionId);
    }

    /**
     * Listener for window dismiss
     * 
     */
    public interface OnDismissListener {
	public abstract void onDismiss();
    }
}