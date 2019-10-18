package com.baselibrary.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.baselibrary.pojo.Finger6;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyu on 2019/10/18.
 */

public class FingerManager {

    private Context mContext;

    private  FingerManager(Context mContext){
        this.mContext=mContext;
    }

    private ArrayList<Finger6> fingerList;

    private static  FingerManager instance;


    public static FingerManager getInstance(Context context){
        if (instance==null){
            synchronized (FingerManager.class){
                if (instance==null){
                    instance=new FingerManager(context);
                }
            }
        }
        return instance;
    }

    public  void putFingerData( @NonNull ArrayList<Finger6> fingerList){
        this.fingerList=fingerList;
    }

    public  ArrayList<Finger6>  getFingerData(){
        return fingerList;
    }

    public  void  addFingerData( Finger6 finger6){
        fingerList.add(finger6);
    }


}
