package com.pw.service;

/**
 * Created By pq
 * on 2019/9/12
 */
public class PwUserInfo {

    public static PwUserInfo getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final PwUserInfo INSTANCE = new PwUserInfo();
    }

    String userInfo;

    public String getUserInfo() {
        return userInfo + "嘿嘿，这是经过PW组件加工后的内容";
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }
}
