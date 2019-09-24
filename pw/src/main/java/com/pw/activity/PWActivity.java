package com.pw.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.dao.db.PwDao;
import com.baselibrary.dao.db.UserDao;
import com.baselibrary.pojo.Pw;
import com.baselibrary.pojo.User;
import com.orhanobut.logger.Logger;
import com.pw.R;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By pq
 * on 2019/9/12
 */
//@Route(path = ARouterConstant.PW_ACTIVITY, group = ARouterConstant.GROUP_PW)
public class PWActivity extends AppCompatActivity {

    private DBUtil dbUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pw_activity_pw);


    }

}
