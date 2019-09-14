package com.finger.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.ARouter.ARouterConstrant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DaoSession;
import com.baselibrary.model.TestBean;
import com.baselibrary.pojo.User;
import com.baselibrary.service.ExecuteMethod;
import com.baselibrary.service.factory.PwFactory;
import com.baselibrary.service.pwService.PwTestService;
import com.finger.R;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;

/**
 * Created By pq
 * on 2019/9/11
 */
@Route(path = ARouterConstrant.FINGER_ACTIVITY, group = ARouterConstrant.GROUP_FINGER)
public class FingerActivity extends AppCompatActivity {

    @Autowired(name = "Address")
    String Address;
    @Autowired(name = "testBean")
    TestBean testBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finger_activity_finger);
        //注入路由
        ARouterUtil.injectActivity(this);

        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        TextView params = findViewById(R.id.params);

        //接收上个页面传递过来的数据
        params.setText(MessageFormat.format("姓名：{0}  年龄：{1}  地址：{2}"
                , testBean.getName(), testBean.getAge(), Address));

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FingerActivity.this, "跨组件调用方法"
                        , Toast.LENGTH_SHORT).show();

//                params.setText(MessageFormat.format("内容：{0}",
//                        PwFactory.getUserAddress("李二狗:")));

                params.setText(ExecuteMethod.executeAimMethod());
            }
        });


    }

    //增
    public void insert() {
        DaoSession daoSession = DBUtil.instance().getDaoSession();
        for (int i = 0; i < 50; i++) {
            User user = new User();
            int age = new Random().nextInt(10) + 10;
            user.setAge(age);
            String name = name();
            user.setName(name);
            if (i % 2 == 0) {
                user.setSex("男");
            } else {
                user.setSex("女");
            }
            user.setStudentNo(i);
            user.setTelPhone(telNo());
            //daoSession.insert(user);
            //插入或替换
            daoSession.insertOrReplace(user);
        }
    }

    //删除
    private void delete(User s) {
        DaoSession daoSession = DBUtil.instance().getDaoSession();
        daoSession.delete(s);
    }

    //删除全部
    private void deleteAll(Class clazz) {
        DaoSession daoSession = DBUtil.instance().getDaoSession();
        daoSession.deleteAll(clazz);
    }

    //改  通过Update来修改
    private void update(User user) {
        DaoSession daoSession = DBUtil.instance().getDaoSession();
        daoSession.update(user);
    }

    /**
     * 查询的方法
     * loadAll()：查询所有数据。
     * queryRaw()：根据条件查询。
     * queryBuilder() : 方便查询的创建，后面详细讲解。
     */
    public List queryAll(Class clazz) {
        DaoSession daoSession = DBUtil.instance().getDaoSession();
        List list = daoSession.loadAll(clazz);
        return list;
    }

    //根据条件查询
    private List queryRaw(Class clazz, String id) {
        DaoSession daoSession = DBUtil.instance().getDaoSession();
        List list = daoSession.queryRaw(clazz, "where id = ?", id);
        return list;
    }

    //查询所有的数据
    private List queryAllData(Class clazz) {
        DaoSession daoSession = DBUtil.instance().getDaoSession();
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
