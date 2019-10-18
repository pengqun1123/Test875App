package com.testApp.activity;

import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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
        RecyclerView userRv = bindViewWithClick(R.id.rv, false);
        AppCompatTextView noData = bindViewWithClick(R.id.noData, false);
        userRv.setHasFixedSize(true);
        noData.setVisibility(View.VISIBLE);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,
                OrientationHelper.VERTICAL, false);
        userRv.setLayoutManager(mLayoutManager);
        userRv.setItemAnimator(new DefaultItemAnimator());
        userManageAdapter = new UserManageAdapter();
        userRv.setAdapter(userManageAdapter);

        search_edit.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                SoftInputKeyboardUtils.hiddenKeyboard(search_edit);
                search(search_edit.getText().toString());
            }
            return false;
        });
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initData() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Toolbar toolBar = bindViewWithClick(R.id.toolbar, false);
        if (toolBar == null) {
            return;
        }
        TextView toolbarTitle = bindViewWithClick(R.id.toolbar_title, false);
        AppCompatImageView backBtn = bindViewWithClick(R.id.backBtn, true);
        toolbarTitle.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
        toolbarTitle.setText(getString(R.string.user_search));
    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
        }
    }

    private void search(String condition) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        dbUtil.setDbCallBack(new DbCallBack<User>() {
            @Override
            public void onSuccess(User result) {

            }

            @Override
            public void onSuccess(List<User> result) {
                if (result.size() == 0) {
                    ToastUtils.showSingleToast(SearchActivity.this, "没有数据");
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

        dbUtil.queryAsync(User.class, UserDao.Properties.WorkNum.eq(condition), UserDao.Properties.Name.eq(condition));
    }

}
