package com.baselibrary.activity;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.baselibrary.R;
import com.baselibrary.callBack.PermissionC;
import com.baselibrary.util.PermissionUtils;
import com.baselibrary.util.dialogUtil.DialogCallBack;
import com.baselibrary.util.dialogUtil.MyDialogUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created By pq
 * on 2019/5/13
 */
public class PermissionsActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String[] permissions = bundle.getStringArray(PermissionC.init_permis);
            //申请权限
            checkMyPermissions(permissions);
        }
    }

    /**
     * 发起申请同意权限组时，数组中已同意的权限系统将不会再弹出该权限对话框，只会弹出未同意的权限对话框
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkMyPermissions(String[] permissions) {
        //当Android版本大于等于M时候
        for (int i = 0; i < permissions.length; i++) {
            int checkSelfPermission = checkSelfPermission(permissions[i]);
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                //如果没有同意权限--->发起系统请求，同意权限
                requestPermissions(permissions, PermissionC.WR_FILE_CODE);
            }
            if (checkSelfPermission == PackageManager.PERMISSION_GRANTED &&
                    i == permissions.length - 1) {
                //表示权限已经全部同意----->可执行需要权限的代码
                PermissionUtils.instance().callBack();
                finish();
            }
        }
    }

    private boolean PERMISSION_DENIED = false;
    private List<String> pers;

    //权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionC.WR_FILE_CODE:
                for (int i = 0; i < grantResults.length; i++) {
                    //如果某一个权限用户没有同意--->申请权限
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        if (pers == null) {
                            pers = new ArrayList<>();
                        }
                        PERMISSION_DENIED = true;
                        pers.add(permissions[i]);
                    }
                    if (i == grantResults.length - 1 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //表示用户已经同意所有的权限--->执行需要权限后的操作
                        PermissionUtils.instance().callBack();
                        finish();
                    }
                }
                if (PERMISSION_DENIED) {
                    //向用户展示该权限的dialog--->权限用途
                    showPermissionDialog(permissions, getString(R.string.permissions));
                }
                break;
        }
    }

    //展示dialog   --->说明权限申请的用途
    public void showPermissionDialog(String[] permissions, String dialogTip) {
        MyDialogUtil.getInstance().setDialogCallBack(new DialogCallBack() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void positiveClick(DialogInterface dialog) {
                requestPermissions(permissions, PermissionC.WR_FILE_CODE);
                dialog.dismiss();
            }

            @Override
            public void negativeClick(DialogInterface dialog) {
                dialog.dismiss();
            }
        })
                .showPermissionDialog(this, dialogTip);
    }

}
