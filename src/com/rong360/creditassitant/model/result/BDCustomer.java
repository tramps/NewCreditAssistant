package com.rong360.creditassitant.model.result;

import com.rong360.creditassitant.json.JSONBean;

public class BDCustomer implements JSONBean {
    public int mId;
    public String mUser_mobile;
    public String mUser_name;
    public String mUser_address;
    public int mLoan_limit;
    public int mCreate_time;
    public int mStatus;
    public String mProduct_name;
    public int mApplication_type;
}
