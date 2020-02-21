package com.arcFace.activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.arcFace.R;
import com.arcFace.callBack.FaceHeadCallBack;
import com.arcFace.callBack.ProgressDialogCallBack;
import com.arcFace.dialog.ArcDialog;
import com.arcFace.faceServer.FaceServer;
import com.arcFace.util.dbUtil.ArcFaceDb;
import com.arcFace.widget.ProgressDialog;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.arcsoft.imageutil.ArcSoftImageUtilError;
import com.baselibrary.base.BaseActivity;
import com.baselibrary.base.BaseApplication;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.pojo.ArcFace;
import com.baselibrary.util.VerifyResultUi;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 虹软批量注册页面
 */
public class ArcBatchRegisterActivity extends BaseActivity {

    //注册图所在的目录
    private static final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "arcfacedemo";
    private static final String REGISTER_DIR = ROOT_DIR + File.separator + "register";
    private static final String REGISTER_FAILED_DIR = ROOT_DIR + File.separator + "failed";

    private TextView tvNotificationRegisterResult;
    private ExecutorService executorService;
    private ProgressDialog progressDialog = null;


    @Override
    protected Integer contentView() {
        return R.layout.arc_activity_arc_batch_register;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tvNotificationRegisterResult = bindViewWithClick(R.id.notification_register_result, false);

        File file = new File(REGISTER_DIR);
        boolean exists = file.exists();
        Logger.d("  注册目录:" + ROOT_DIR + "  存在：" + exists);
        if (!file.exists())
            file.mkdirs();

        executorService = Executors.newSingleThreadExecutor();
        progressDialog = new ProgressDialog(this);
        FaceServer.getInstance().init(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onViewClick(View view) {

    }

    @Override
    protected void onDestroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        FaceServer.getInstance().unInit();
        super.onDestroy();
    }

    /**
     * 批量注册人脸数据
     *
     * @param view view
     */
    public void batchRegister(View view) {
        File dir = new File(REGISTER_DIR);
        if (!dir.exists()) {
            VerifyResultUi.showTvToast(this,
                    getString(R.string.arc_batch_process_path_is_not_exists, REGISTER_DIR));
            return;
        }
        if (!dir.isDirectory()) {
            VerifyResultUi.showTvToast(this,
                    getString(R.string.arc_batch_process_path_is_not_dir, REGISTER_DIR));
            return;
        }
        final File[] jpgFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(FaceServer.IMG_SUFFIX);
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final int totalCount = jpgFiles.length;
                int successCount = 0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMaxProgress(totalCount);
                        progressDialog.show();
                        tvNotificationRegisterResult.setText("");
                        tvNotificationRegisterResult.append(getString(R.string.arc_batch_process_processing_please_wait));
                    }
                });
                for (int i = 0; i < totalCount; i++) {
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null) {
                                progressDialog.refreshProgress(finalI);
                            }
                        }
                    });
                    final File jpgFile = jpgFiles[i];
                    Bitmap bitmap = BitmapFactory.decodeFile(jpgFile.getAbsolutePath());
                    if (bitmap == null) {
                        File failedFile = new File(REGISTER_FAILED_DIR + File.separator + jpgFile.getName());
                        if (!failedFile.getParentFile().exists()) {
                            failedFile.getParentFile().mkdirs();
                        }
                        jpgFile.renameTo(failedFile);
                        continue;
                    }
                    bitmap = ArcSoftImageUtil.getAlignedBitmap(bitmap, true);
                    if (bitmap == null) {
                        File failedFile = new File(REGISTER_FAILED_DIR + File.separator + jpgFile.getName());
                        if (!failedFile.getParentFile().exists()) {
                            failedFile.getParentFile().mkdirs();
                        }
                        jpgFile.renameTo(failedFile);
                        continue;
                    }
                    byte[] bgr24 = ArcSoftImageUtil.createImageData(bitmap.getWidth(), bitmap.getHeight(), ArcSoftImageFormat.BGR24);
                    int transformCode = ArcSoftImageUtil.bitmapToImageData(bitmap, bgr24, ArcSoftImageFormat.BGR24);
                    if (transformCode != ArcSoftImageUtilError.CODE_SUCCESS) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                tvNotificationRegisterResult.append("");
                            }
                        });
                        return;
                    }
                    boolean success = FaceServer.getInstance().registerBgr24(ArcBatchRegisterActivity.this,
                            bgr24, bitmap.getWidth(), bitmap.getHeight(),
                            jpgFile.getName().substring(0, jpgFile.getName().lastIndexOf(".")), new FaceHeadCallBack() {
                                @Override
                                public void faceHeadCallBack(byte[] faceHead, byte[] arcFaceFeature, String registerName) {
                                    ArcFace arcFace = new ArcFace();
                                    arcFace.setFaceFeature(arcFaceFeature);
                                    arcFace.setName(registerName);
                                    arcFace.setHeadImg(faceHead);
                                    ArcFaceDb.updateFaceUser(arcFace);
                                }
                            });
                    if (!success) {
                        File failedFile = new File(REGISTER_FAILED_DIR + File.separator + jpgFile.getName());
                        if (!failedFile.getParentFile().exists()) {
                            failedFile.getParentFile().mkdirs();
                        }
                        jpgFile.renameTo(failedFile);
                    } else {
                        successCount++;
                    }
                }
                final int finalSuccessCount = successCount;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        tvNotificationRegisterResult.append(getString(R.string.arc_batch_process_finished_info,
                                totalCount, finalSuccessCount, totalCount - finalSuccessCount, REGISTER_FAILED_DIR));
                    }
                });
                Logger.i(ArcBatchRegisterActivity.class.getSimpleName(),
                        "run: " + executorService.isShutdown());
            }
        });
    }

    /**
     * 批量删除已注册的人脸数据
     *
     * @param view view
     */
    public void clearFaces(View view) {
        DBUtil dbUtil = BaseApplication.getDbUtil();
        final Dialog[] progressDialog = new Dialog[1];
        ArcDialog.showWaitDialog(this, new ProgressDialogCallBack() {
            @Override
            public void progressDialog(Dialog dialog) {
                progressDialog[0] = dialog;
            }
        });
        dbUtil.deleteAll(ArcFace.class);
        Dialog dialog = progressDialog[0];
        if (dialog != null)
            dialog.dismiss();
    }


}
