package com.pw.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.service.pwService.ExecuteMethodImpl;

/**
 * Created By pq
 * on 2019/9/14
 */
@Route(path = "/pw/executeMethod")
public class PwTestExecutorMethod implements ExecuteMethodImpl {
    @Override
    public String executeMethod() {

        return ExecuteDetail.executeContent("{ 李二狗 }");
    }

    @Override
    public void init(Context context) {

    }
}
