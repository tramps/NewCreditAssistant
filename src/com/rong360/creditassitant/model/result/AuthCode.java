package com.rong360.creditassitant.model.result;

import com.rong360.creditassitant.json.JSONBean;

public class AuthCode implements JSONBean {
    public Result mResult;
    public String mAuth_Code;
    public Result getResult() {
        return mResult;
    }
    public void setResult(Result mResult) {
        this.mResult = mResult;
    }
    public String getAuth_Code() {
        return mAuth_Code;
    }
    public void setAuth_Code(String mAuth_Code) {
        this.mAuth_Code = mAuth_Code;
    }
}
