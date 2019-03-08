package com.forte.qqrobot.beans.msgsend;

/**
 * 122, 置群管理员
 * @author ForteScarlet <[163邮箱地址]ForteScarlet@163.com>
 * @date Created in 2019/3/8 11:44
 * @since JDK1.8
 **/
public class SetGroupAdmin {
    /*
    122, 置群管理员
    QQID，groupid，setadmin
     */
    private final Integer act = 122;
    /** 设置的qq */
    private String QQID;
    /** 群号 */
    private String groupid;
    /** 是否设置为群管理 */
    private String setadmin;


    public Integer getAct() {
        return act;
    }

    public String getQQID() {
        return QQID;
    }

    public void setQQID(String QQID) {
        this.QQID = QQID;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getSetadmin() {
        return setadmin;
    }

    public void setSetadmin(String setadmin) {
        this.setadmin = setadmin;
    }
}
