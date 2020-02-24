package com.testApp.activity;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baselibrary.base.BaseActivity;
import com.baselibrary.util.SkipActivityUtil;
import com.baselibrary.widget.SwitchButton;
import com.testApp.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置页面
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.statusBarView)
    View statusBarView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.backBtn)
    AppCompatImageView backBtn;
    @BindView(R.id.rightTv)
    AppCompatTextView rightTv;
    @BindView(R.id.topBar)
    RelativeLayout topBar;
    @BindView(R.id.userCount)
    AppCompatTextView userCount;
    @BindView(R.id.goToUser)
    RelativeLayout goToUser;
    @BindView(R.id.goToData)
    RelativeLayout goToData;
    @BindView(R.id.goToMyDev)
    RelativeLayout goToMyDev;
    @BindView(R.id.etherNetConnectStatus)
    AppCompatTextView etherNetConnectStatus;
    @BindView(R.id.goToEtherNet)
    RelativeLayout goToEtherNet;
    @BindView(R.id.wlanConnectStatus)
    AppCompatTextView wlanConnectStatus;
    @BindView(R.id.goToWLAN)
    RelativeLayout goToWLAN;
    @BindView(R.id.serverConnectStatus)
    AppCompatTextView serverConnectStatus;
    @BindView(R.id.goToServer)
    RelativeLayout goToServer;
    @BindView(R.id.goToScreenCover)
    RelativeLayout goToScreenCover;
    @BindView(R.id.serverCheckModel)
    AppCompatTextView serverCheckModel;
    @BindView(R.id.goToDevModel)
    RelativeLayout goToDevModel;
    @BindView(R.id.groupCheckModel)
    AppCompatTextView groupCheckModel;
    @BindView(R.id.goToGroupCheckModel)
    RelativeLayout goToGroupCheckModel;
    @BindView(R.id.openNetCheckTime)
    SwitchButton openNetCheckTime;
    @BindView(R.id.currentDate)
    AppCompatTextView currentDate;
    @BindView(R.id.goToSetDate)
    RelativeLayout goToSetDate;
    @BindView(R.id.currentTime)
    AppCompatTextView currentTime;
    @BindView(R.id.goToSetTime)
    RelativeLayout goToSetTime;
    @BindView(R.id.openDoorGuard)
    SwitchButton openDoorGuard;
    @BindView(R.id.doorGuardTv)
    AppCompatTextView doorGuardTv;
    @BindView(R.id.goToDoorGuardDelayed)
    RelativeLayout goToDoorGuardDelayed;
    @BindView(R.id.openDoorGuardFail)
    SwitchButton openDoorGuardFail;
    @BindView(R.id.guardDelayedTv)
    AppCompatTextView guardDelayedTv;
    @BindView(R.id.goToGuardDelayed)
    RelativeLayout goToGuardDelayed;
    @BindView(R.id.openWeigen)
    SwitchButton openWeigen;
    @BindView(R.id.weigenCountTv)
    AppCompatTextView weigenCountTv;
    @BindView(R.id.goToSetWeigen)
    RelativeLayout goToSetWeigen;
    @BindView(R.id.openFangChaiGuard)
    SwitchButton openFangChaiGuard;
    @BindView(R.id.openCheckGuard)
    SwitchButton openCheckGuard;
    @BindView(R.id.checkFailCountTv)
    AppCompatTextView checkFailCountTv;
    @BindView(R.id.goToSetCheckFailCount)
    RelativeLayout goToSetCheckFailCount;
    @BindView(R.id.openCheckRepeat)
    SwitchButton openCheckRepeat;
    @BindView(R.id.openFixReboot)
    SwitchButton openFixReboot;
    @BindView(R.id.rebootCount)
    AppCompatTextView rebootCount;
    @BindView(R.id.goToRebootCount)
    RelativeLayout goToRebootCount;
    @BindView(R.id.rebootTime)
    AppCompatTextView rebootTime;
    @BindView(R.id.goToRebootTime)
    RelativeLayout goToRebootTime;

    @Override
    protected Integer contentView() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initView() {

        backBtn.setVisibility(View.VISIBLE);
        toolbarTitle.setText(getString(R.string.setting));

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {

    }

    @OnClick({R.id.backBtn, R.id.userCount, R.id.goToUser, R.id.goToData, R.id.goToMyDev,
            R.id.goToEtherNet, R.id.goToWLAN, R.id.goToServer, R.id.goToScreenCover,
            R.id.goToDevModel, R.id.goToGroupCheckModel, R.id.goToSetDate, R.id.goToSetTime,
            R.id.goToDoorGuardDelayed, R.id.goToGuardDelayed, R.id.goToSetWeigen,
            R.id.goToSetCheckFailCount, R.id.goToRebootCount, R.id.goToRebootTime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.userCount:
            case R.id.goToUser:
                SkipActivityUtil.skipActivity(this, UserActivity.class);
                break;
            case R.id.goToData:
                SkipActivityUtil.skipActivity(this, DataActivity.class);
                break;
            case R.id.goToMyDev:
                SkipActivityUtil.skipActivity(this, MyDevActivity.class);
                break;
            case R.id.goToEtherNet:
                SkipActivityUtil.skipActivity(this, EntherNetActivity.class);
                break;
            case R.id.goToWLAN:
                SkipActivityUtil.skipActivity(this, WLANActivity.class);
                break;
            case R.id.goToServer:
                SkipActivityUtil.skipActivity(this, ServerSetActivity.class);
                break;
            case R.id.goToScreenCover:
                SkipActivityUtil.skipActivity(this, ScreenCoverActivity.class);
                break;
            case R.id.goToDevModel:
                break;
            case R.id.goToGroupCheckModel:
                break;
            case R.id.goToSetDate:
                break;
            case R.id.goToSetTime:
                break;
            case R.id.goToDoorGuardDelayed:
                break;
            case R.id.goToGuardDelayed:
                break;
            case R.id.goToSetWeigen:
                break;
            case R.id.goToSetCheckFailCount:
                break;
            case R.id.goToRebootCount:
                break;
            case R.id.goToRebootTime:
                break;
        }
    }
}
