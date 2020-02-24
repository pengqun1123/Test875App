package com.testApp.activity;

import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.baselibrary.base.BaseActivity;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.pojo.User;
import com.baselibrary.util.SoftInputKeyboardUtils;
import com.testApp.R;

import java.util.List;

/**
 * 搜索页面
 */
public class SearchActivity extends BaseActivity {

//    private UserManageAdapter userManageAdapter;
    private AppCompatEditText search_edit;
    private AppCompatTextView noData;

    @Override
    protected Integer contentView() {
        return R.layout.activity_search;
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
    protected void initView() {
        bindViewWithClick(R.id.searchView, false);
        search_edit = bindViewWithClick(R.id.searchUserEt, false);
        RecyclerView userRv = bindViewWithClick(R.id.rv, false);
        noData = bindViewWithClick(R.id.noData, false);
        AppCompatImageView clear = bindViewWithClick(R.id.clear, true);
        clear.setVisibility(View.GONE);
        userRv.setHasFixedSize(true);
        noData.setVisibility(View.VISIBLE);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,
                OrientationHelper.VERTICAL, false);
        userRv.setLayoutManager(mLayoutManager);
        userRv.setItemAnimator(new DefaultItemAnimator());
//        userManageAdapter = new UserManageAdapter();
//        userManageAdapter.setCallBack(callBack);
//        userRv.setAdapter(userManageAdapter);

        search_edit.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                SoftInputKeyboardUtils.hiddenKeyboard(search_edit);
                search(search_edit.getText().toString());
            }
            return false;
        });
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                if (length > 0)
                    clear.setVisibility(View.VISIBLE);
                else
                    clear.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.clear:
                search_edit.getText().clear();
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
                if (result != null && result.size() == 0) {
                    noData.setVisibility(View.VISIBLE);
                } else {
//                    userManageAdapter.setData(result);
                    noData.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onNotification(boolean result) {

            }
        });

//        dbUtil.queryAsync(User.class, UserDao.Properties.WorkNum.eq(condition),
//                UserDao.Properties.Name.eq(condition));
    }

//    private UserManageAdapter.UserItemCallBack callBack = new UserManageAdapter.UserItemCallBack() {
//        @Override
//        public void userItemCallBack(int position) {
//
//        }
//
//        @Override
//        public void itemLongClickListener(User user, String managerName, int position) {
//            AskDialog.deleteItemDataDialog(SearchActivity.this,
//                    user, null, managerName, flag -> {
//                        if (flag == 1) {
//                            Intent intent = new Intent();
//                            intent.setAction(AppConstant.USER_MANAGER_BROADCAST_RECEIVER);
//                            intent.putExtra(AppConstant.USER, user);
//                            sendBroadcast(intent);
//                        }
//                    });
//        }
//    };

}
