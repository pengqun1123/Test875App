package com.testApp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.ARouter.ARouterConstrant;
import com.baselibrary.ARouter.ARouterUtil;
import com.baselibrary.model.TestBean;
import com.testApp.R;

/**
 * Created By pq
 * on 2019/9/9
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button skipAct = findViewById(R.id.skipAct);
        Button skipFrag = findViewById(R.id.skipFrag);

        skipAct.setOnClickListener(view -> {
                    TestBean testBean = new TestBean("小刚", 24);
                    ARouter.getInstance().build(ARouterConstrant.FINGER_ACTIVITY
                            , ARouterConstrant.GROUP_FINGER)
                            .withString("Address", "杭州市西湖区昌接任大姐")
                            .withParcelable("testBean", testBean)
                            .navigation();
                }
        );
    }
}
