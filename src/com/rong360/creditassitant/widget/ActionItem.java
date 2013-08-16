package com.rong360.creditassitant.widget;


public class ActionItem {
    public static final int ACTION_ALL = 0;
    public static final int ACTION_STAR = 1;
    public static final int ACTION_POTENTIAL = 2;
    public static final int ACTION_CONSISTENT = 3;
    public static final int ACTION_UPGRADE = 4;
    public static final int ACTION_SUCCEED = 5;
    public static final int ACTION_FAIL = 6;
    public static final int ACTION_UNCONSISTENT = 7;
    
    public static final String TITLE_ALL = "全部客户";
    public static final String TITLE_STAR = "标星客户";
    public static final String TITLE_POTENTIAL = "潜在客户";
    public static final String TITLE_CONSISTENT = "意向客户";
    public static final String TITLE_UPGRADE = "进件客户";
    public static final String TITLE_SUCCEED = "成功客户";
    public static final String TITLE_FAIL = "失败客户";
    public static final String TITLE_UNCONSISTENT = "不符客户";
    
    
    private String title;
    private int actionId = -1;
    private boolean selected;
    private boolean sticky;

    /**
     * Constructor
     * 
     * @param actionId
     *            Action id for case statements
     * @param title
     *            Title
     * @param icon
     *            Icon to use
     */
    public ActionItem(int actionId, String title) {
	this.title = title;
	this.actionId = actionId;
    }

    /**
     * Constructor
     */
    public ActionItem() {
	this(-1, null);
    }


    /**
     * Set action title
     * 
     * @param title
     *            action title
     */
    public void setTitle(String title) {
	this.title = title;
    }

    /**
     * Get action title
     * 
     * @return action title
     */
    public String getTitle() {
	return this.title;
    }

    /**
     * Set action id
     * 
     * @param actionId
     *            Action id for this action
     */
    public void setActionId(int actionId) {
	this.actionId = actionId;
    }

    /**
     * @return Our action id
     */
    public int getActionId() {
	return actionId;
    }

    /**
     * Set sticky status of button
     * 
     * @param sticky
     *            true for sticky, pop up sends event but does not disappear
     */
    public void setSticky(boolean sticky) {
	this.sticky = sticky;
    }

    /**
     * @return true if button is sticky, menu stays visible after press
     */
    public boolean isSticky() {
	return sticky;
    }

    /**
     * Set selected flag;
     * 
     * @param selected
     *            Flag to indicate the item is selected
     */
    public void setSelected(boolean selected) {
	this.selected = selected;
    }

    /**
     * Check if item is selected
     * 
     * @return true or false
     */
    public boolean isSelected() {
	return this.selected;
    }
}