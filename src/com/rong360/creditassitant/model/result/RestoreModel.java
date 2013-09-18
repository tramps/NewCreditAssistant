package com.rong360.creditassitant.model.result;

import com.rong360.creditassitant.json.JSONBean;
import com.rong360.creditassitant.model.Action;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.model.HistoryMsg;

public class RestoreModel implements JSONBean {
    public Customer[] mCustomer;
    public HistoryMsg[] mHistory_msg;
    public Action[] mAction;
}
