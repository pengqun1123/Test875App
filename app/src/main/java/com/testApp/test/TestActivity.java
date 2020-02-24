package com.testApp.test;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.pojo.TestIdCard;
import com.baselibrary.pojo.TestPerson;
import com.sd.tgfinger.utils.LogUtils;
import com.testApp.R;

import java.util.List;

/**
 * 测试专用的Activity
 */
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        oneToOne();
    }

    /**
     * 参考博文:https://www.jianshu.com/p/53083f782ea2
     * <p>
     * greenDAO的关联查询测试
     * 一对一
     * person and IdCard
     */
    private void oneToOne() {
        TestPerson testPerson = new TestPerson();
        testPerson.setPersonName("李晓明");
        BaseApplication.getDbUtil().insert(testPerson);

        TestIdCard testIdCard = new TestIdCard();
        testIdCard.setCardId(testIdCard.getCardId());
        testIdCard.setCardNo("12334555");
        BaseApplication.getDbUtil().insert(testIdCard);

        List<TestPerson> testPeople = BaseApplication.getDbUtil().queryAll(TestPerson.class);
        for (TestPerson person : testPeople) {
            LogUtils.d("person  ：" + person.toString());
            LogUtils.d("card: " + person.getTestIdCard().toString());
        }
    }

    /**
     * 一对多
     * user and creditCard
     */
    private void oneToMany() {

    }

    /**
     * 多对多
     * student and teacher
     */
    private void manyToMany() {

    }
}
