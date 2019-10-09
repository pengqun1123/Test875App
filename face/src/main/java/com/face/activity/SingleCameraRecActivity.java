package com.face.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.alibaba.android.arouter.launcher.ARouter;
import com.baselibrary.callBack.CardInfoListener;
import com.baselibrary.pojo.IdCard;
import com.baselibrary.service.routerTest.IdCardService;
import com.baselibrary.util.ToastUtils;
import com.face.common.FaceConfig;
import com.face.utils.FaceUtils;
import com.orhanobut.logger.Logger;
import com.zqzn.android.face.camera.FaceCamera;
import com.zqzn.android.face.data.FaceData;
import com.zqzn.android.face.data.FaceDetectData;
import com.zqzn.android.face.exceptions.FaceException;
import com.zqzn.android.face.image.FaceImage;
import com.zqzn.android.face.image.YUVImage;
import com.zqzn.android.face.jni.Tool;
import com.zqzn.android.face.model.FaceFeatureExtractor;
import com.zqzn.android.face.model.FaceSearchLibrary;
import com.zqzn.android.face.model.ImageConverter;
import com.zqzn.android.face.model.NirFaceLivenessDetector;
import com.zqzn.android.face.model.VisFaceLivenessDetector;
import com.face.common.Constants;
import com.face.ui.FaceDataExtraInfo;
import com.zqzn.android.face.processor.FaceDetectProcessor;

import java.io.File;
import java.text.DecimalFormat;

/**
 * 单目摄像头人脸识别示例
 * <p>
 * 此示例中使用单目活体检测模型防止活体攻击
 */
public class SingleCameraRecActivity extends AbstractFaceDetectActivity implements CardInfoListener {
    private static final String TAG = SingleCameraRecActivity.class.getSimpleName();

    /**
     * 人脸特征抽取器
     */
    protected FaceFeatureExtractor faceFeatureExtractor;
    /**
     * 离线1：N搜索库
     */
    protected FaceSearchLibrary faceSearchLibrary;
    /**
     * 可见光单目活体检测器
     */
    protected VisFaceLivenessDetector visLivenessDetector;
    protected HandlerThread searchThread;
    protected Handler searchHandler;
    protected FaceDetectData lastedFaceDetectData;
    private NirFaceLivenessDetector nirLivenessDetector;
    private ImageConverter nirImageConverter;

    private IdCard idCard=null;
    private  boolean isIdCardVerify=true;
    private Bitmap bitmap;
    private IdCardService idCardService;

    private static final int SEARCH_FEATURE=0x001;
    private  static final int OBSERVAER_TASH=0x002;

    private static  volatile float[] face_feature = null;
    private static volatile float[] idcard_feature=null;

    //人脸信息
    private FaceData faceData;


    @Override
    protected void onViewClick(View view) {

    }

    @Override
    protected void initData() {
        super.initData();
        try {
            //获取人脸特征抽取器
            faceFeatureExtractor = FaceConfig.getInstance().getFaceSDK().getFaceFeatureExtractor();
            //获取离线1:N搜索库
            faceSearchLibrary = FaceConfig.getInstance().getFaceSDK().getFaceSearchLibrary();
            //可见光单目活体检测器
            //     visLivenessDetector = FaceConfig.getInstance().getFaceSDK().getVisLivenessDetector();
            //获取近红外活体检测器
            nirLivenessDetector = FaceConfig.getInstance().getFaceSDK().getNirLivenessDetector();
            //获取红外图片转换类
            nirImageConverter = FaceConfig.getInstance().getFaceSDK().getNirImageConverter();

            idCardService = ARouter.getInstance().navigation(IdCardService.class);
            // idCardService.register_IdCard(this);
//            String path = Environment.getExternalStorageDirectory() + "/idcard/";
//            File file = new File(path,"4445.jpg");
//            idcard_feature = FaceUtils.extractFaceFeature(file);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    idCardService.verify_IdCard(SingleCameraRecActivity.this);
                }
            }).start();
        } catch (Exception e) {
            Logger.e(TAG, "onCreate: 获取SDK模型失败", e);
            Toast.makeText(this, "获取SDK模型失败", Toast.LENGTH_SHORT).show();
            finish();
        }


 }


    @Override
    protected void onResume() {
        super.onResume();
        searchThread = new HandlerThread("face_search");
        searchThread.start();
        searchHandler = new Handler(searchThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case OBSERVAER_TASH:
                        Log.d("444","face_feature:"+face_feature+"---idcard_feature"+idcard_feature);
//                        if (face_feature!=null & idcard_feature!=null){
//                            compare_feature();
//                        }else {
//                            searchHandler.sendEmptyMessageDelayed(OBSERVAER_TASH,500);
//                        }
                        if (idcard_feature==null){
                            String path = Environment.getExternalStorageDirectory() + "/idcard/";
                            File file = new File(path,"4445.jpg");
                            idcard_feature = FaceUtils.extractFaceFeature(file);
                        }else{
                         //   searchHandler.sendEmptyMessageDelayed(OBSERVAER_TASH,2000);
                        }
                        break;
                    case SEARCH_FEATURE:
                        if (lastedFaceDetectData == null) {
                            return false;
                        }
                        //搜索
                        if (idcard_feature!=null) {
                            getFeature(lastedFaceDetectData);
                        }

                        break;
                }
                return false;
            }


        });
      //  searchHandler.sendEmptyMessageDelayed(OBSERVAER_TASH,2000);
    }

    @Override
    protected FaceCamera getNirCamera() {
        FaceCamera nirCamera = FaceConfig.getInstance().getNirCamera();
        if (nirCamera == null) {
            //双目活体必需近红外摄像头存在
            Toast.makeText(this, "近红外摄像头不存在", Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException("近红外摄像头未配置");
        }
        return nirCamera;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (searchHandler != null) {
            searchHandler.removeMessages(SEARCH_FEATURE);
            searchHandler = null;
        }
        if (searchThread != null) {
            searchThread.quit();
            searchThread = null;
        }

    }

    /**
     * 人脸检测回调
     *
     * @param faceDetectData
     */
    @Override
    public void onFaceDetected(FaceDetectData faceDetectData) {
        super.onFaceDetected(faceDetectData);
        Log.d("444","onFaceDetected里面的idcard_feature值为:"+idcard_feature);
        //调用父类方法以绘制人脸框
        if (idcard_feature!=null) {
            //调用1：N人脸搜索
            lastedFaceDetectData = faceDetectData;
            if (searchHandler != null) {
                searchHandler.sendEmptyMessage(SEARCH_FEATURE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (idCardService!=null) {
            idCardService.destroyIdCard();
        }
        if (searchHandler!=null){
            searchHandler.removeMessages(SEARCH_FEATURE);
            searchHandler=null;
            searchThread.quit();
            searchThread=null;
        }
    }

    /**
     * 人脸搜索
     *
     * @param faceDetectData
     */
    protected void getFeature(FaceDetectData faceDetectData) {
        FaceData[] faceList = faceDetectData.getFaceList();
        if (faceList == null || faceList.length == 0) {
            return;
        }
        //可能同一时刻检测到多个人脸
        faceData=faceList[0];
        if (faceData!=null) {
            //判断人脸搜索状态，如果已经搜索过，在人脸追踪丢失前则不再进行搜索
          //  if (FaceData.FaceDataStatus.FACE_REC_COMPLETED != faceData.getStatus()) {
                //过滤人脸质量，不符合质量要求的人脸不进行搜索
              if (!checkFaceQuality(faceDetectData, faceData)) {
                   return;
                }
                boolean b = checkLiveness(faceDetectData, faceData);
                Log.d("444","活体检测结果"+b);
                //活检检测，活体检测不通过的人脸不进行搜索
                if (!b) {
                    return;
                }
                try {
                    //人脸特征抽取
                    long featureStart = System.currentTimeMillis();
                        face_feature = faceFeatureExtractor.extractFaceFeature(faceDetectData.getImage(), faceData);
                        long featureCost = System.currentTimeMillis() - featureStart;
                        Logger.d(TAG, "特征抽取: [" + featureCost + "ms]");
                        if (face_feature != null) {
                            compare_feature();
                        }
                } catch (FaceException e) {
                    Logger.e(TAG, "searchOneUser: 抽取人脸特征失败. track_id: " + faceData.getTrackId(), e);
                    return;
                }
            }
      //  }
    }

    private void showMessage(String string)
    {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(string)
                .setPositiveButton( "确定" ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface, int i){
                                dialoginterface.dismiss();
                            }
                        }).show();
    }
    /**
     * 离线1：N人脸搜索
     *
     * @param
     * @param
     */
    protected void compare_feature() {
            try {
                //离线1：1搜索
                long start = System.currentTimeMillis();
                if (idcard_feature!=null & face_feature!=null){
                    synchronized (SingleCameraRecActivity.this){
                        if (idcard_feature!=null & face_feature!=null) {
                            double sim = Tool.calcSimilarity(FaceConfig.getInstance().getOriginalSimilarityThreshold(), face_feature, idcard_feature);
                            DecimalFormat format = new DecimalFormat("#.######");
                            String similar = format.format(sim);
                            long time = System.currentTimeMillis() - start;
                            Log.d("fff", "人脸搜索[" + time + "ms]");

                            Log.d("fff", " 相似度: " + similar);
                            //判断相似度，相似度超过阈值的才认为是搜索成功，demo中默认使用中级识别精度阈值
                            // 可以通过调整阈值实现更高的的搜索准确性（通过率低）或者更低的搜索准确性（通过率高）
                            if (sim > Constants.Threshold.SEARCH_ACCURACY_IDCARD) {

                                Logger.d(TAG,  "搜索成功");
                                //showMessage("比对成功");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_direct.setText(" 相似度: " + similar);
                                    }
                                });
                                //设置需要显示在UI层人脸框下面的信息
                                FaceDataExtraInfo info = new FaceDataExtraInfo();
                                info.setLoginStatus(1);
                                info.setName(similar);
                            //    faceData.setStatus(FaceData.FaceDataStatus.FACE_REC_COMPLETED);
                                //把搜索结果写入到人脸检测信息中，以供人脸框绘制界面显示
                                faceData.setTag(info);
                                //状态有变更，更新追踪信息
                                faceDetectProcessor.updateTrackFace(faceData);

                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_direct.setText("比对失败");
                                    }
                                });

                                // showMessage("比对失败");
                                Logger.d(TAG, "追踪ID: " + faceData.getTrackId() + "比对失败");
                            }
                         //   face_feature=null;
                            idcard_feature=null;
                         //   searchHandler.sendEmptyMessageDelayed(OBSERVAER_TASH,2000);

                        }
                    }
                }

            } catch (FaceException e) {
               // showMessage("比对失败");
                Logger.d(TAG, "searchOneUser: 比对失败", e);
               tv_direct.setText("比对失败");
            }

       // searchHandler.sendEmptyMessageDelayed(OBSERVAER_TASH,500);
    }

    /**
     * 可见光单目活体检测
     *
     * @param faceDetectData 人脸检测数据
     * @param faceData       人脸数据
     * @return 是否活体
     */
    protected boolean checkLiveness(FaceDetectData faceDetectData, FaceData faceData) {
        try {
            //获取红外图片
            FaceDetectProcessor.FaceDetectProcessorMonitorData monitorData =
                    (FaceDetectProcessor.FaceDetectProcessorMonitorData) faceDetectData.getMonitorData();
            FaceImage<?> nirImage = monitorData.getNirImage();
            if (nirImage == null) {
                //在适配不成功，或者在一些特殊的设备上，有可能红外的帧数据无法获取到，此条件下需要进一步调试解决问题。此处打印出错误日志
                Logger.e(TAG, "红外视频帧数据未获取到");
                return false;
            }
            //近红外图片需要调用转换工具由YUVImage转换成RGBImage
            long nirConvStart = System.currentTimeMillis();
            nirImage = nirImageConverter.convertImage((YUVImage) nirImage);
            long nirConvCost = System.currentTimeMillis() - nirConvStart;
            Logger.i(TAG, "NirImageConvert[" + nirConvCost + "ms]");
            //红外活体检测
            long start = System.currentTimeMillis();
            float livenessScore = nirLivenessDetector.nirLivenessDetect(faceDetectData.getImage(), nirImage, faceDetectData.getFaceList(), faceData);
            long time = System.currentTimeMillis() - start;
            Logger.i(TAG, "checkLiveness[" + time + "ms]: 红外双目活体识别分值：" + livenessScore);
            //判断活体检测分值是否达到阈值，如果没有达到阈值，则判断为活体攻击
            if (livenessScore <= Constants.Threshold.LIVENESS) {
                Logger.i(TAG, "checkLiveness: 红外双目活体不过");
                return false;
            }
        } catch (FaceException e) {
            Logger.e(TAG, "checkLiveness: 红外双目活检攻击检测失败", e);
            return false;
        }
        return true;
    }




    @Override
    protected FaceCamera getVisCamera() {
        return FaceConfig.getInstance().getVisCamera();
    }

    @Override
    public void onGetCardInfo(IdCard idCard) {
        if (idCard==null){
            this.idCard=null;
            isIdCardVerify=false;
            ToastUtils.showSquareTvToast(this,"身份证验证失败!");
        }else {
            Toast.makeText(this, "身份证验证成功！", Toast.LENGTH_SHORT).show();
            isIdCardVerify=true;
            this.idCard=idCard;
            Bitmap bitmap = idCardService.getBitmap();
            File idCardFile = idCardService.getIdCardFile();

            if(idCardFile!=null) {
                 idcard_feature = FaceUtils.extractFaceFeature(idCardFile);
                 if (idcard_feature!=null) {
                     idCardFile.delete();
                     Toast.makeText(this, "身份证图片特征提取成功!", Toast.LENGTH_SHORT).show();

                     // faceDetectProcessor.setDetect(true);
                 }else {
                     Toast.makeText(this, "身份证图片特征提取失败!", Toast.LENGTH_SHORT).show();
                 }
             }
        }
    }

    @Override
    public void onRegisterResult(boolean result,IdCard idCard) {
        if (result) {
            Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
        }
    }
}
