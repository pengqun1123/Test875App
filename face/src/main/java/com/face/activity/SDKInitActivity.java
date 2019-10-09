package com.face.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.pojo.Face;
import com.face.R;
import com.face.common.FaceConfig;
import com.face.utils.FaceUtils;
import com.orhanobut.logger.Logger;
import com.zqzn.android.face.exceptions.FaceException;
import com.zqzn.android.face.exceptions.SDKAuthException;
import com.zqzn.android.face.exceptions.SDKAuthExpireException;
import com.zqzn.android.face.exceptions.SDKNotAuthException;
import com.zqzn.android.face.model.FaceSDK;
import com.zqzn.android.face.model.FaceSearchLibrary;
import com.face.db.User;
import com.face.db.UserManager;

import java.io.File;
import java.util.List;

/**
 * SDK初始化示例
 */
public class SDKInitActivity extends AppCompatActivity implements FaceSDK.InitCallback, View.OnClickListener {
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final String TAG = SDKInitActivity.class.getSimpleName();

    private Button btnActivate;
    private EditText etSerialNumber;
    private TextView tvErrorInfo;
    private FaceSearchLibrary faceSearchLibrary;
    private String serialNumber="VG9D-QVA7-956E-RE97 ";
    private DBUtil dbUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_activity_loading);
        btnActivate = (Button) findViewById(R.id.btn_activate);
        etSerialNumber = (EditText) findViewById(R.id.et_serialnumber);
        tvErrorInfo = (TextView) findViewById(R.id.tv_error_info);
        btnActivate.setEnabled(false);
        btnActivate.setText("激活中");
        btnActivate.setOnClickListener(this);
        dbUtil = BaseApplication.getDbUtil();
        //动态申请危险权限
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            //权限申请成功后，开始初始化SDK
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "必需权限未授予：" + permissions[i], Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            //所有必需权限已经授予，开始初始化SDK
            FaceConfig.getInstance().init(this, this);
          //  FaceConfig.getInstance().init(this, serialNumber.toUpperCase(), this);
        }
    }

    @Override
    public void onInitSuccess() {
        //初始化成功，开始加载数据库里的用户到离线1：N搜索库中
        loadUserToSearchLibrary();

        runOnUiThread(() -> {
            btnActivate.setText("已激活");
            Toast.makeText(getApplicationContext(), "SDK初始化成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SDKInitActivity.this, MainActivity.class);
            //转换到主界面
            startActivity(intent);
            finish();
        });
    }

    private void loadUserToSearchLibrary() {
        runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(), "开始加载人员数据", Toast.LENGTH_SHORT).show();
            tvErrorInfo.setText("正在加载人员数据");
        });
        //获取用户数据管理器
        UserManager userManager = FaceConfig.getInstance().getUserManager();
        //获取离线1：N搜索库
        faceSearchLibrary = FaceConfig.getInstance().getFaceSDK().getFaceSearchLibrary();
        int offset = 0;
        int limit = 10;
        while (true) {
            List<Face> users = dbUtil.getDaoSession().queryBuilder(Face.class).offset(offset).limit(limit).build().list();
           // List<User> users = userManager.find(limit, offset);
            if (users == null || users.isEmpty()) {
                break;
            }
            for (Face user : users) {
                //将用户特征加载到1：N离线搜索库中
                try {
                    FaceUtils.addToSearchLibrary(user);
                } catch (FaceException e) {
                    e.printStackTrace();
                }
            }
            offset += users.size();
            if (users.size() < limit) {
                Log.i(TAG, "loadUserToSearchLibrary: 没有更多的用户数据需要加载");
                break;
            }
        }
        Logger.i(TAG, "用户加载完成，总数：" + offset);
        final int finalCount = offset;
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "用户加载完成，总数：" + finalCount, Toast.LENGTH_SHORT).show());
    }

    private void addUserToSearchLibrary(User user) {
        try {
            user.addToSearchLibrary(faceSearchLibrary);
            Logger.d(TAG, "加载用户到缓存成功：" + user.getId() + ", " + user.getName());
        } catch (FaceException e) {
            Logger.e(TAG, "加载用户到缓存失败：" + user.getId() + "," + user.getName(), e);
        }
    }

    @Override
    public void onInitFailed(final Throwable throwable) {
        //初始化失败，可能需要重新输入序列号以激活
        runOnUiThread(() -> {
            btnActivate.setEnabled(true);
            btnActivate.setText("激活");
            if (throwable instanceof SDKAuthExpireException) {
                tvErrorInfo.setText("授权已过期: " + throwable.getMessage());
            } else if (throwable instanceof SDKNotAuthException) {
                tvErrorInfo.setText("未授权: " + throwable.getMessage());
            } else if (throwable instanceof SDKAuthException) {
                tvErrorInfo.setText("授权失败: " + throwable.getMessage());
            } else {
                tvErrorInfo.setText("授权异常：" + throwable.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_activate) {
            String serialNumber = etSerialNumber.getText().toString();
            if (serialNumber.isEmpty() || (serialNumber.length() != 19 && serialNumber.length() != 16)) {
                tvErrorInfo.setText("请输入正确的序列号");
                return;
            }
            if (serialNumber.length() == 16) {
                serialNumber = serialNumber.substring(0, 4) + "-" + serialNumber.substring(4, 8) + "-" + serialNumber.substring(8, 12) + "-" + serialNumber.substring(12);
            }
            //在输入序列号的情况下，使用序列号初始化SDK
           // FaceConfig.getInstance().init(this, serialNumber.toUpperCase(), this);
        }
    }
}
