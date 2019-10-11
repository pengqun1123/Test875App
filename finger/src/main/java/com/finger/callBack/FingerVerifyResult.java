package com.finger.callBack;

/**
 * Created By pq
 * on 2019/10/10
 */
public interface FingerVerifyResult {

    void fingerVerifyResult(int res, String msg, byte[] updateFingerData
            , Integer score, Integer pos);

}
