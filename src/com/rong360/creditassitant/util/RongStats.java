package com.rong360.creditassitant.util;

public class RongStats {
    
    public static final String CTM_MGR = "customer_managent"; //客户管理TAB
    public static final String TASK = "task";	//提醒TAB
    public static final String CMU_HTR = "comunication_history"; 	//通讯历史TAB
    public static final String SETTING = "setting"; //设置TAB
    
    //customer management
    public static final String CTM_IMP_CTM_CLICK = "ctm_import_customer"; //从通讯录导入客户

    public static final String CTM_SEND_SMS = "ctm_send_smg"; //群发短信
    public static final String CTM_NEW_CUSTOMER = "ctm_new_customer"; //添加客户
    public static final String CTM_DETAIL = "ctm_detail"; //点击客户列表项
    public static final String CTM_OPEN = "ctm_open"; //点击立即开启
    public static final String CTM_CLOSE = "ctm_close";	//点击×
    public static final String CTM_FILTER = "ctm_filter"; //点击出现分组栏
    public static final String CTM_SECTION = "ctm_section"; //点击客户列表项
    public static final String CTM_ADVANCE = "ctm_advance"; //点击高级筛选
    public static final String CTM_EXIST_FILTER = "ctm_exist_filter";
    //advance filter 
    public static final String ADV_ITEM = "adv_item";
    
    //add customer;
    public static final String ADD_CANCEL = "add_cancel"; //点击取消提醒
    public static final String ADD_DELETE = "add_delete"; //点击删除
    public static final String ADD_FIRST_DELETE = "add_first_delete"; //点击删除
    public static final String ADD_FINISH = "add_finish"; //点击完成
    
    
    //custoemr detail
    public static final String CDT_EDIT = "cdt_edit"; //点击编辑
    public static final String CDT_STAR = "cdt_star";//点击标星
    public static final String CDT_TEL = "cdt_tel";//点击电话
    public static final String CDT_MSG = "cdt_msg"; //点击短信
    public static final String CDT_HTR = "cdt_history"; //点击通讯记录
    public static final String CDT_PROGRESS = "cdt_progress"; //滑动客户进度条
    public static final String CDT_PGS_SUCCEED = "cdt_progress_succeed";//滑动客户进度条 su
    public static final String CDT_SET_ALARM = "cdt_set_alarm"; //点击设置提醒
    public static final String CDT_CANCEL_ALARM = "cdt_cancel_alarm"; //点击取消提醒
    public static final String CDT_SET_COMMENT = "cdt_set_comment"; //点击设置备注
    
    //time picker;
    public static final String TIME_OK = "time_ok"; //点击确定
    public static final String TIME_CANCEL = "time_cancel"; //点击取消
    
    
    //customer comment
    public static final String COMMENT_OK = "comment_ok"; //点击返回
    public static final String COMMENT_BACK = "comment_back"; //点击完成
    
    //send msg;
    public static final String SEND_SMS = "send_sms";  //点击发送
    public static final String SEND_HTY_SMS = "send_history_sms"; //点击发送历史短信
    public static final String SEND_ADD = "send_add"; //点击加号
    
    //choose customer
    public static final String CCM_FILTER = "ccm_filter"; //点击出现分组栏
    public static final String CCM_SECTION = "ccm_section"; //点击分组项
    public static final String CCM_ADVANCE = "ccm_advance"; //点击高级筛选
    public static final String CCM_ALL = "ccm_all"; //点击全部选择
    public static final String CCM_OK = "ccm_ok"; //点击导入
    public static final String CCM_EXIST_FILTER = "ccm_exist_filter";
    
    //import customer
    public static final String IMP_ALL = "imp_all";//点击全部选择
    public static final String IMP_OK = "imp_ok";//点击导入
    
    //comunication history
    public static final String CMU_TEL = "cmu_tel"; //点击电话
    public static final String CMU_MSG = "cmu_msg";//点击短信
    public static final String CMU_VIEW = "cmu_view"; //点击查看客户
    public static final String CMU_ADD = "cmu_add";//点击添加客户
    
    
    //setting page
    public static final String SET_SAFE = "set_safe";//点击客户保险箱
    public static final String SET_LOCK = "set_lock";//点击客户密码锁
    public static final String SET_IMP_CTC = "set_import_contact";//点击从通讯录导入
    public static final String SET_IMP_PARTER = "set_import_partner";//点击从合作伙伴方导入
    public static final String SET_EPT_SD = "set_export_sd_card";//点击导出客户到SD卡
    public static final String SET_FEEDBACK = "set_feedback";//点击问题反馈
    public static final String SET_ABOUT = "set_about";//点击关于我们
    public static final String SET_SOURCE = "set_source"; //点击客户来源
    
    //customer safe;
    public static final String SAFE_OPEN = "safe_open";//点击开启客户保险箱
    public static final String SAFE_CLOSE = "safe_close";//点击停用客户保险箱
    
    //source 
    public static final String SOURCE_ADD = "source_add"; //点击添加按钮
    public static final String SOURCE_DEL = "source_delete"; //点击列表项×
    
    //lock
    public static final String LOCK_OPEN = "lock_open";//点击开启密码锁
    public static final String LOCK_CLOSE = "lock_close";//点击关闭密码锁
    public static final String LOCK_MODIFY = "lock_modify";//点击更改密码锁
    
    public static final String LOCK_LOGIN = "lock_login";//点击登陆解锁
    
    //phone window
    public static final String PHONE_POPUP = "phone_popup";//客户通话浮层出现
    public static final String AFTER_CLOSE = "after_close"; //点击关闭
    public static final String AFTER_SAVE = "after_save";//点击保存为联系人
    public static final String AFTER_VIEW = "after_view"; //点击查看
    
    //alarm
    public static final String ALARM_VIEW = "alarm_view"; //点击查看客户
    public static final String ALARM_CTC = "alarm_contact"; //点击联系客户
    public static final String ALARM_SILENT = "alarm_silent"; //点击静音按钮
    public static final String ALARM_CLOSE = "alarm_close";  //关闭
    public static final String ALARM_PARENT = "alarm_parent"; //整体
    
    //import rong360
    public static final String IMP_RONG_START = "import_rong_start"; //融360导入启动次数
    public static final String IMP_RONG_SUC = "import_rong_suceed"; //融360导入成功次数
    public static final String IMP_RONG_FAIL = "import_rong_fail"; //融360导入失败次数
    
}
