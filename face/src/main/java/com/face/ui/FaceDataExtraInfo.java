package com.face.ui;

import java.io.Serializable;

/**
 * 人脸框绘制额外信息
 */
public class FaceDataExtraInfo implements Serializable {

    private String name;

    private int loginStatus = 0;

    private String faceQualityMessage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getFaceQualityMessage() {
        return faceQualityMessage;
    }

    public void setFaceQualityMessage(String faceQualityMessage) {
        this.faceQualityMessage = faceQualityMessage;
    }
}
