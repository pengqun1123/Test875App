package com.baselibrary.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.baselibrary.R;
import com.baselibrary.listener.OnceClickListener;
import com.baselibrary.util.ActManager;
import com.baselibrary.util.HideNavBarUtil;
import com.baselibrary.util.ToastUtils;

/**
 * Created By pq
 * on 2019/9/25
 */
public abstract class BaseActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected abstract Integer contentView();

    protected abstract void initToolBar();

    protected abstract void initView();

    protected abstract void initData();

    //点击事件
    protected abstract void onViewClick(View view);

    //系统ui变化监听（一般都是状态栏和导航栏）
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentView());
//        HideNavBarUtil.hideBottomUIMenu1(this);
        initToolBar();
        initView();
        initData();
    }

    protected OnceClickListener clickListener = new OnceClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            onViewClick(v);
        }
    };

    @SuppressWarnings("unchecked")
    protected <T extends View> T bindViewWithClick(Integer idRes, Boolean isClick) {
        View view = null;
        try {
            view = findViewById(idRes);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        if (isClick && view != null) {
            view.setOnClickListener(clickListener);
        }
        return (T) view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Fragment 逐个出栈
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                if (ActManager.getInstance().getActivityStack().size() <= 1) {
                    ToastUtils.showSquareTvToast(this, getString(R.string.exit_tip));
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
            } else {
                ActManager.getInstance().finishAllAct();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
