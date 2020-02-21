package com.arcFace.activity;

import android.Manifest;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.arcFace.R;
import com.arcFace.widget.ProgressDialog;
import com.baselibrary.entitys.FaceTestEntity;
import com.baselibrary.util.ExcelUtil;
import com.baselibrary.util.ToastUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ExcelTestActivity extends AppCompatActivity {

    private static String[] PER = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arc_activity_excel_test);


    }

    /**
     * 申请权限
     */
    private void requestPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        Disposable disposable = rxPermissions.requestEach(PER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(permission -> {
                    if (permission.granted) {
                        excelExecute();
                    } else {
                        ToastUtils.showShortToast(ExcelTestActivity.this,
                                "运行需要这些权限");
                    }
                });
    }

    /**
     * 创建一个表格
     *
     * @param view view
     */
    public void createExcel(View view) {
        requestPermission();
    }

    private void excelExecute() {
        String dirPath = Environment.getExternalStorageDirectory() + File.separator + "AndroidExcel";
        File file = new File(dirPath);
        if (!file.exists())
            file.mkdirs();
        String testExcelPath = dirPath + File.separator + "testExcel.xls";
        List<FaceTestEntity> list = new ArrayList<>();
        FaceTestEntity faceTestEntity1 = new FaceTestEntity("小狗", 1,
                false, 123, 234, 234,
                0.92);
        FaceTestEntity faceTestEntity2 = new FaceTestEntity("大熊", 2,
                false, 34, 355, 243,
                0.94);
        FaceTestEntity faceTestEntity3 = new FaceTestEntity("黑虎", 3,
                false, 24, 56, 356,
                0.90);
        FaceTestEntity faceTestEntity4 = new FaceTestEntity("滤后", 4,
                false, 25, 46, 234,
                0.27);
        FaceTestEntity faceTestEntity5 = new FaceTestEntity("祖居", 5,
                false, 25, 256, 234,
                0.46);
        FaceTestEntity faceTestEntity6 = new FaceTestEntity("哆啦", 6,
                false, 47, 47, 267,
                0.96);
        FaceTestEntity faceTestEntity7 = new FaceTestEntity("钱出", 7,
                false, 256, 245, 256,
                0.95);
        FaceTestEntity faceTestEntity8 = new FaceTestEntity("尽在", 8,
                false, 256, 256, 234,
                0.99);
        list.add(faceTestEntity1);
        list.add(faceTestEntity2);
        list.add(faceTestEntity3);
        list.add(faceTestEntity4);
        list.add(faceTestEntity5);
        list.add(faceTestEntity6);
        list.add(faceTestEntity7);
        list.add(faceTestEntity8);

        String[] colName = {"name", "trackId", "isLiveness", "RGB_Tiem",
                "RGB_IR_Time", "compare_time", "compareSimilar"};
        
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("表格创建中...");
        progressDialog.show();
        ExcelUtil.initExcel(testExcelPath, "测试表1", colName);
//        ExcelUtil.setSheetHeader(this, testExcelPath, colName, "测试表1", list);

        ExcelUtil.writeObjListToExcel(list, testExcelPath, this);
        progressDialog.dismiss();
    }
}
