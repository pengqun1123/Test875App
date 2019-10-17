package com.face.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baselibrary.ARouter.ARouterConstant;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.DbCallBack;
import com.baselibrary.pojo.Face;
import com.baselibrary.util.ToastUtils;
import com.face.R;
import com.face.common.FaceConfig;
import com.face.utils.FaceUtils;
import com.orhanobut.logger.Logger;
import com.zqzn.android.face.data.FaceData;
import com.zqzn.android.face.exceptions.FaceException;
import com.zqzn.android.face.image.RGBImage;
import com.zqzn.android.face.jni.Tool;
import com.zqzn.android.face.model.FaceDetector;
import com.zqzn.android.face.model.FaceFeatureExtractor;
import com.zqzn.android.face.model.FaceSearchLibrary;
import com.face.utils.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 增加用户示例
 */

public class UserAddActivity extends AppCompatActivity {

    private static final int FACE_CAPTURE_REQUEST_CODE = 2;
    private static final String TAG = UserAddActivity.class.getSimpleName();

    private Button btnCaptureFace;
    private EditText tvName;
    private Button btnSave;
    private ImageView ivHead;
    private String faceImagePath;
    private FaceFeatureExtractor faceFeatureExtractor;
    private FaceDetector visFaceDetector;
    private HandlerThread handlerThread;
    private Handler handler;
    private FaceSearchLibrary faceSearchLibrary;
    private File faceImageDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_activity_user_add);
        ivHead = (ImageView) findViewById(R.id.iv_head);
        btnCaptureFace = (Button) findViewById(R.id.btn_capture_face);
        tvName = (EditText) findViewById(R.id.tv_name);
        btnSave = (Button) findViewById(R.id.btn_save);
        initFaceSDKAPI();
        initUserSaveHandler();
        btnCaptureFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用人脸图片采集Activity
                startActivityForResult(new Intent(UserAddActivity.this, FaceCaptureActivity.class), FACE_CAPTURE_REQUEST_CODE);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setEnabled(false);
                btnSave.setText("存储中");
                handler.sendEmptyMessage(0);
            }
        });
    }

    private void initUserSaveHandler() {
        //初始化人员信息存储异步处理器（在子线程上处理耗时操作）
        handlerThread = new HandlerThread("user_add_thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                saveUser();
                return true;
            }
        });
    }

    private void initFaceSDKAPI() {
     //   userManager = FaceConfig.getInstance().getUserManager();
        faceFeatureExtractor = FaceConfig.getInstance().getFaceSDK().getFaceFeatureExtractor();
        visFaceDetector = FaceConfig.getInstance().getFaceSDK().getVisFaceDetector();
        faceSearchLibrary = FaceConfig.getInstance().getFaceSDK().getFaceSearchLibrary();
        faceImageDir = new File(FaceConfig.getInstance().getAppRootDir(), "face_image");
        faceImageDir.mkdirs();
    }

    private void saveUser() {
        //保存信息
        String name = tvName.getText().toString();
        if (name.isEmpty()) {
            runOnUiThread(() -> {
                btnSave.setEnabled(true);
                btnSave.setText("保存");
                Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            });
            return;
        }
        if (faceImagePath == null || faceImagePath.isEmpty()) {
            runOnUiThread(() -> {
                btnSave.setEnabled(true);
                btnSave.setText("保存");
                Toast.makeText(this, "请拍照", Toast.LENGTH_SHORT).show();
            });
            return;
        }
        File file = new File(faceImagePath);
        if (!file.exists()) {
            runOnUiThread(() -> {
                btnSave.setEnabled(true);
                btnSave.setText("保存");
                Toast.makeText(this, "请拍照", Toast.LENGTH_SHORT).show();
            });
            return;
        }
        //抽取特征
        float[] feature = extractFaceFeature(file);
        if (feature == null) {
            return;
        }
        //持久化用户信息
        try {
            persistentUser(name, feature);
        } catch (IOException e) {
            runOnUiThread(() -> {
                btnSave.setEnabled(true);
                btnSave.setText("保存");
                Toast.makeText(this, "保存人员失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    /**
     * 持久化用户信息
     *
     * @param name
     * @param feature
     */
    private void persistentUser(String name, float[] feature) throws IOException {
      //  User user = new User();
      //  user.setName(name);
      // user.setFeature(feature);
        File confirmedFaceImage = new File(faceImageDir, "" + System.currentTimeMillis() + ".jpg");
        File faceImage = new File(faceImagePath);
        FileHelper.copyFile(faceImage, confirmedFaceImage);
        faceImage.delete();
      //  user.setImagePath(confirmedFaceImage.getAbsolutePath());
        //将用户信息写入数据库
        Face face=new Face();
        face.setName(name);
        face.setFeature(feature);
        face.setImagePath(confirmedFaceImage.getAbsolutePath());
      //  long personId = userManager.addOne(user);
        DBUtil dbUtil = BaseApplication.getDbUtil();
        dbUtil.setDbCallBack(new DbCallBack<Object>() {
            @Override
            public void onSuccess(Object result) {

            }

            @Override
            public void onSuccess(List<Object> result) {

            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onNotification(boolean result) {
                if (result){
                    //写数据成功，将用户信息加载到离线1：N搜索库中
                    boolean addSearchLibraryRet = addUserToSearchLibrary(face);
                    if (addSearchLibraryRet) {
                        finish();
                    }
                }else {
                    ToastUtils.showSingleToast(UserAddActivity.this, "增加失败");
                    btnSave.setEnabled(true);
                    btnSave.setText("保存");
                }
            }
        });
        dbUtil.insertAsyncSingle(face);
    }

    /**
     * 将新加的用户信息加载到离线1：N搜索库中
     *
     * @param face
     * @return
     */
    private boolean addUserToSearchLibrary(Face face) {
        try {
            //确保之前在搜索库中的用户信息已经被移除
            faceSearchLibrary.removePersons(new long[]{face.getUId()});
          } catch (FaceException ignore) {
        }
        try {
            //将用户特征信息加载到离线1：N搜索库中
          FaceUtils.addToSearchLibrary(face);
            Logger.d(TAG, "加载用户到缓存成功：" + face.getUId() + "," + face.getName());
            runOnUiThread(() -> ToastUtils.showSingleToast(this, "增加成功"));
            return true;
        } catch (FaceException e) {
            Logger.e(TAG, "加载用户到缓存失败：" +face.getUId() + "," + face.getName(), e);
            runOnUiThread(() -> {
                ToastUtils.showSingleToast(this, "写入离线1：N搜索库失败: " + e.getMessage());
                btnSave.setEnabled(true);
                btnSave.setText("保存");
            });
            return false;
        }
    }

    /**
     * 读取图片文件并抽取人脸特征
     *
     * @param file
     * @return
     */
    private float[] extractFaceFeature(File file) {
        float[] feature = null;
        try {
            //将本地图片文件转换成RGBImage
            RGBImage faceImage = RGBImage.bitmapToRGBImage(Tool.loadBitmap(file));
            //检测图片中最大人脸
            FaceData faceData = visFaceDetector.detectMaxFace(faceImage, true);
            if (faceData == null) {
                Logger.w(TAG, "onClick: 人脸检测失败");
                runOnUiThread(() -> {
                    Toast.makeText(this, "照片不符合要求，请重新拍摄", Toast.LENGTH_SHORT).show();
                    btnSave.setEnabled(true);
                    btnSave.setText("保存");
                });
                return null;
            }
            //对检测出来的人脸进行特征抽取
            feature = faceFeatureExtractor.extractFaceFeature(faceImage, faceData);
        } catch (FaceException e) {
            Logger.e(TAG, "onClick: 人脸特征抽取失败", e);
            runOnUiThread(() -> {
                Toast.makeText(this, "照片不符合要求，请重新拍摄", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                btnSave.setText("保存");
            });
            return null;
        }
        if (feature == null) {
            Logger.w(TAG, "onClick: 特征抽取失败");
            runOnUiThread(() -> {
                Toast.makeText(this, "照片不符合要求，请重新拍摄", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                btnSave.setText("保存");
            });
            return null;
        }
        return feature;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == FACE_CAPTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //调用人脸图片采集Activity成功
                if (data != null) {
                    //获取采集到的人脸图片地址
                    faceImagePath = data.getStringExtra("face_image");
                    ivHead.setImageBitmap(BitmapFactory.decodeFile(faceImagePath));
                } else {
                    Toast.makeText(this, "获取照片失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "拍照失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onDestroy() {
        try {
            if (handler != null) {
                handler.removeMessages(0);
            }
            if (handlerThread != null) {
                handlerThread.quitSafely();
            }
        } catch (Throwable ignore) {
        } finally {
            super.onDestroy();
        }
    }
}
