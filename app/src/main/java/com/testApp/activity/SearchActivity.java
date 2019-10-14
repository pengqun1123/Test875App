package com.testApp.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baselibrary.base.BaseActivity;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.dao.db.UserDao;
import com.baselibrary.pojo.User;
import com.baselibrary.util.SoftInputKeyboardUtils;
import com.baselibrary.util.ToastUtils;
import com.testApp.R;
import com.testApp.adapter.UserManageAdapter;

import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class SearchActivity extends BaseActivity {

    private RecyclerView userRv;
    private UserManageAdapter userManageAdapter;
    private AppCompatEditText search_edit;

    @Override
    protected Integer contentView() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        bindViewWithClick(R.id.searchView, false);
        search_edit = bindViewWithClick(R.id.searchUserEt, false);
        userRv = bindViewWithClick(R.id.rv, false);
        AppCompatTextView noData = bindViewWithClick(R.id.noData, false);
        userRv.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,
                OrientationHelper.VERTICAL, false);
        userRv.setLayoutManager(mLayoutManager);
        userRv.setItemAnimator(new DefaultItemAnimator());
        userManageAdapter = new UserManageAdapter();
        userRv.setAdapter(userManageAdapter);

    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initData() {
        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i==EditorInfo.IME_ACTION_SEARCH){
                    SoftInputKeyboardUtils.hiddenKeyboard(search_edit);
                    search(search_edit.getText().toString());
                }
                return false;
            }
        });

    }

    @Override
    protected void onViewClick(View view) {

    }

    private void search(String condition ){
        DBUtil dbUtil = BaseApplication.getDbUtil();
         dbUtil.setDbCallBack(new DbCallBack<User>() {
             @Override
             public void onSuccess(User result) {

             }

             @Override
             public void onSuccess(List<User> result) {
                 if (result.size()==0){
                     ToastUtils.showSingleToast(SearchActivity.this,"没有数据");
                     return;
                 }
                 userManageAdapter.setData(result);
             }

             @Override
             public void onFailed() {

             }

             @Override
             public void onNotification(boolean result) {

             }
         });

        dbUtil.queryAsync(User.class,UserDao.Properties.WorkNum.eq(condition), UserDao.Properties.Name.eq(condition));
    }

}
