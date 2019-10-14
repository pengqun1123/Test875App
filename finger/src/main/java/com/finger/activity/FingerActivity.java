package com.finger.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import android.widget.Button;
import android.widget.TextView;


import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.ARouter.ARouterConstant;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.callBack.PermissionC;
import com.baselibrary.callBack.PermissionResultCallBack;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DaoSession;
import com.baselibrary.model.TestBean;
import com.baselibrary.pojo.Student;
import com.baselibrary.pojo.User;
import com.baselibrary.util.PermissionUtils;
import com.finger.R;
import com.finger.fingerApi.FingerApi;
import com.orhanobut.logger.Logger;
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.pojos.Msg;

import org.greenrobot.greendao.query.QueryBuilder;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created By pq
 * on 2019/9/11
 */
@Route(path = ARouterConstant.FINGER_ACTIVITY, group = ARouterConstant.GROUP_FINGER)
public class FingerActivity extends AppCompatActivity {


    private DBUtil dbUtil;

    String[] per={Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finger_activity_finger);

        dbUtil = BaseApplication.getDbUtil();
//        dbUtil = DBUtil.getInstance(getApplication());
//        dbUtil = DBUtil.instance(getApplication());

        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);
        TextView params = findViewById(R.id.params);

        checkMyPermissions(per);

       FingerApi.getInstance().startReStartFinger(this);


    }

    private void FvInit(){
        InputStream LicenseIs = getResources().openRawResource(R.raw.license);
        FingerApi.getInstance().fingerInit(this, LicenseIs, new FvInitCallBack() {
            @Override
            public void fvInitResult(Msg msg) {
                if (msg.getResult() == 1) {
                    openDev();
                }
                Logger.d("===:"+msg.getTip());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkMyPermissions(String[] permissions) {
        List<String> pers = new ArrayList<>();
        //当Android版本大于等于M时候
        for (String permission : permissions) {
            int checkSelfPermission = checkSelfPermission(permission);
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                pers.add(permission);
            }
        }
        if (pers.size() > 0) {
            String[] strings = pers.toArray(new String[]{});
            PermissionUtils.instance().requestPermission(this,
                    getString(R.string.permissions), strings,
                    new PermissionResultCallBack() {
                        @Override
                        public void permissionCallBack() {
                            FvInit();
                        }
                    });
        } else {
            FvInit();
        }
    }

    private void openDev(){
        FingerApi.getInstance().openDev(this, true,
                new DevOpenCallBack() {
                    @Override
                    public void devOpenResult(Msg msg) {
                      Logger.d("===:"+msg.getTip());
                    }
                }, new DevStatusCallBack() {
                    @Override
                    public void devStatus(Msg msg) {
                        Logger.d("===:"+msg.getTip());
                    }
                });
    }


    //增
    public void insert() {
//        DaoSession daoSession = dbUtil.getDaoSession();
        for (int i = 50; i < 60; i++) {
            User user = new User();
            int age = new Random().nextInt(10) + 10;
            String name = name();
            user.setName(name);

            //daoSession.insert(user);
            //插入或替换
            // daoSession.insertOrReplace(user);
            Logger.d(user);
            dbUtil.insert(user);
        }
    }

    public void insert1() {
        DaoSession daoSession = dbUtil.getDaoSession();
        for (int i = 0; i < 30; i++) {
            Student student = new Student();
            student.setStudentNo(i);
            String name = name();
            student.setName(name);
//            daoSession.insert(student);
            //插入或替换
            // daoSession.insertOrReplace(user);
            Logger.d(student);
            dbUtil.insert(student);
        }
    }

    //删除
    private void delete(User s) {
        DaoSession daoSession = dbUtil.getDaoSession();
        daoSession.delete(s);
    }

    //删除全部
    private void deleteAll(Class clazz) {
        DaoSession daoSession = dbUtil.getDaoSession();
        daoSession.deleteAll(clazz);
    }

    //改  通过Update来修改
    private void update(User user) {
        DaoSession daoSession = dbUtil.getDaoSession();
        daoSession.update(user);
    }

    /**
     * 查询的方法
     * loadAll()：查询所有数据。
     * queryRaw()：根据条件查询。
     * queryBuilder() : 方便查询的创建，后面详细讲解。
     */
    public List queryAll(Class clazz) {
        DaoSession daoSession = dbUtil.getDaoSession();
        List list = daoSession.loadAll(clazz);
        return list;
    }

    //根据条件查询
    private List queryRaw(Class clazz, String id) {
        DaoSession daoSession = dbUtil.getDaoSession();
        List list = daoSession.queryRaw(clazz, "where id = ?", id);
        return list;
    }

    //查询所有的数据
    private List queryAllData(Class clazz) {
        DaoSession daoSession = dbUtil.getDaoSession();
        QueryBuilder queryBuilder = daoSession.queryBuilder(clazz);
        List list = queryBuilder.list();
        return list;
    }


    private String name() {
        String a = "QWERTYUIOPASDFGHJKLZXCVBNM";
        StringBuilder sb = new StringBuilder();
        for (int i1 = 0; i1 < 2; i1++) {
            int i = new Random().nextInt(a.length());
            sb.append(a.charAt(i));
        }
        return sb.toString();
    }

    private String telNo() {
        String a = "134567435739689102";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            sb.append(a.charAt(i));
        }
        return sb.toString();
    }
}
