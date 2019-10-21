package com.face.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.pojo.Pw;
import com.baselibrary.pojo.User;
import com.face.R;

import java.util.List;

/**
 * Created By pq
 * on 2019/9/14
 */

public class FaceActivity extends AppCompatActivity {

    private Button bt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_activity_face);
        bt = ((Button) findViewById(R.id.tv_query));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query();
            }
        });
        save();

    }

    public void onClick(){
        query();
    }


    private void save(){
        Pw pw = new Pw();
        pw.setPassword("123456");
        BaseApplication.getDbUtil().insertOrReplace(pw);

        User user=new User();
        user.setName("张三");
        user.setAge("22");
        user.setPwId(pw.getUId());

        BaseApplication.getDbUtil().insertOrReplace(user);

    }

    private void query(){
        List<User> users = BaseApplication.getDbUtil().queryAll(User.class);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            Log.d("====u",user.toString());
        }
    }
}
