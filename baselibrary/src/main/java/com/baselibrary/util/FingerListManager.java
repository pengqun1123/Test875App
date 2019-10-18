package com.baselibrary.util;

import com.baselibrary.pojo.Finger6;

import java.util.ArrayList;

/**
 * Created by wangyu on 2019/10/18.
 */

public class FingerListManager {

    private static ArrayList<Finger6> fingerList;

    public static FingerListManager getInstance() {
        return Holder.INSTANCE;
    }

    private FingerListManager() {
        fingerList = new ArrayList<>();
    }

    private static class Holder {
        private static final FingerListManager INSTANCE = new FingerListManager();
    }

    public ArrayList<Finger6> getFingerData() {
        return fingerList;
    }

    public void addFingerDataList(ArrayList<Finger6> newFingerList) {
        if (newFingerList != null && newFingerList.size() > 0)
            fingerList.addAll(newFingerList);
    }

    public void addFingerData(Finger6 finger6) {
        fingerList.add(finger6);
    }

    public void removeFingerData(int position) {
        fingerList.remove(position);
    }

    public void clearFingerData() {
        fingerList.clear();
    }


}
