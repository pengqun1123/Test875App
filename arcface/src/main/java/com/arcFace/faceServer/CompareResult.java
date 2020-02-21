package com.arcFace.faceServer;

/**
 * 验证比对后的结果
 */
public class CompareResult {
    private String userName;
    private float similar;
    private int trackId;
    private byte[] faceHead;

    public CompareResult(String userName, float similar, byte[] faceHead) {
        this.userName = userName;
        this.similar = similar;
        this.faceHead = faceHead;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public float getSimilar() {
        return similar;
    }

    public void setSimilar(float similar) {
        this.similar = similar;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public byte[] getFaceHead() {
        return faceHead;
    }

    public void setFaceHead(byte[] faceHead) {
        this.faceHead = faceHead;
    }
}
