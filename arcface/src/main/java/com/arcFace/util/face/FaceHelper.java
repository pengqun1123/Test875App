package com.arcFace.util.face;

import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import com.arcFace.model.FacePreviewInfo;
import com.arcFace.util.TrackUtil;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.arcsoft.imageutil.ArcSoftImageUtilError;
import com.baselibrary.util.ImageDispose;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 人脸操作辅助类
 */
public class FaceHelper {
    /**
     * 保存置信度低的图片
     */
    private static final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "arcfacedemo";
    private static final String CONFIDENCE_LOW_DIR = ROOT_DIR + File.separator + "confidenceLow";
    public static final String LOG_TXT = ROOT_DIR + File.separator + "Log";

    private static final String TAG = "FaceHelper";
    public static final String IMG_SUFFIX = ".jpg";
    /**
     * 线程池正在处理任务
     */
    private static final int ERROR_BUSY = -1;
    /**
     * 特征提取引擎为空
     */
    private static final int ERROR_FR_ENGINE_IS_NULL = -2;
    /**
     * 活体检测引擎为空
     */
    private static final int ERROR_FL_ENGINE_IS_NULL = -3;
    /**
     * 人脸追踪引擎
     */
    private FaceEngine ftEngine;
    /**
     * 特征提取引擎
     */
    private FaceEngine frEngine;
    /**
     * 活体检测引擎
     */
    private final FaceEngine flEngine;

    private Camera.Size previewSize;

    private List<FaceInfo> faceInfoList = new ArrayList<>();
    /**
     * 特征提取线程池
     */
    private ExecutorService frExecutor;
    /**
     * 活体检测线程池
     */
    private ExecutorService flExecutor;
    /**
     * 特征提取线程队列
     */
    private LinkedBlockingQueue<Runnable> frThreadQueue = null;
    /**
     * 活体检测线程队列
     */
    private LinkedBlockingQueue<Runnable> flThreadQueue = null;

    private FaceListener faceListener;
    /**
     * 上次应用退出时，记录的该App检测过的人脸数了
     */
    private int trackedFaceCount = 0;
    /**
     * 本次打开引擎后的最大faceId
     */
    private int currentMaxFaceId = 0;

    //    private List<Integer> formerTrackIdList = new ArrayList<>();
    private List<Integer> currentTrackIdList = new ArrayList<>();
    //    private List<FaceInfo> formerFaceInfoList = new ArrayList<>();
    private List<FacePreviewInfo> facePreviewInfoList = new ArrayList<>();
    /**
     * 用于存储人脸对应的姓名，KEY为trackId，VALUE为name
     */
    private ConcurrentHashMap<Integer, String> nameMap = new ConcurrentHashMap<>();

    private FaceHelper(Builder builder) {
        ftEngine = builder.ftEngine;
        faceListener = builder.faceListener;
        trackedFaceCount = builder.trackedFaceCount;
        previewSize = builder.previewSize;
        frEngine = builder.frEngine;
        flEngine = builder.flEngine;
        /**
         * fr 线程队列大小
         */
        int frQueueSize = 5;
        if (builder.frQueueSize > 0) {
            frQueueSize = builder.frQueueSize;
            frThreadQueue = new LinkedBlockingQueue<>(frQueueSize);
        } else {
            Log.e(TAG, "frThread num must > 0,now using default value:" + frQueueSize);
        }
        frExecutor = new ThreadPoolExecutor(1, frQueueSize, 0, TimeUnit.MILLISECONDS, frThreadQueue);

        /**
         * fr 线程队列大小
         */
        int flQueueSize = 5;
        if (builder.flQueueSize > 0) {
            frQueueSize = builder.flQueueSize;
            flThreadQueue = new LinkedBlockingQueue<>(frQueueSize);
        } else {
            Log.e(TAG, "frThread num must > 0,now using default value:" + frQueueSize);
        }
        flThreadQueue = new LinkedBlockingQueue<Runnable>(flQueueSize);
        flExecutor = new ThreadPoolExecutor(1, flQueueSize, 0, TimeUnit.MILLISECONDS, flThreadQueue);
        if (previewSize == null) {
            throw new RuntimeException("previewSize must be specified!");
        }
    }

    /**
     * 请求获取人脸特征数据
     *
     * @param nv21     图像数据
     * @param faceInfo 人脸信息
     * @param width    图像宽度
     * @param height   图像高度
     * @param format   图像格式
     * @param trackId  请求人脸特征的唯一请求码，一般使用trackId
     */
    public void requestFaceFeature(byte[] nv21, FaceInfo faceInfo, int width, int height, int format, Integer trackId) {
        if (faceListener != null) {
            if (frEngine != null && frThreadQueue.remainingCapacity() > 0) {
                frExecutor.execute(new FaceRecognizeRunnable(nv21, faceInfo, width, height, format, trackId));
            } else {
                faceListener.onFaceFeatureInfoGet(null, trackId, ERROR_BUSY, GenderInfo.UNKNOWN);
            }
        }
    }

    private long startLivenessTime;

    /**
     * 请求获取活体检测结果，需要传入活体的参数，以下参数同
     *
     * @param nv21         NV21格式的图像数据
     * @param faceInfo     人脸信息
     * @param width        图像宽度
     * @param height       图像高度
     * @param format       图像格式
     * @param trackId      请求人脸特征的唯一请求码，一般使用trackId
     * @param livenessType 活体检测类型
     */
    public void requestFaceLiveness(byte[] nv21, FaceInfo faceInfo, int width, int height,
                                    int format, Integer trackId, LivenessType livenessType) {
        if (faceListener != null) {
            if (flEngine != null && flThreadQueue.remainingCapacity() > 0) {
                startLivenessTime = System.currentTimeMillis();
                Logger.d("活体检测 start  ：" + startLivenessTime + "   trackId:" + trackId);
                flExecutor.execute(new FaceLivenessDetectRunnable(nv21, faceInfo, width, height, format, trackId, livenessType));
            } else {
                faceListener.onFaceFeatureInfoGet(null, trackId, ERROR_BUSY, GenderInfo.UNKNOWN);
            }
        }
    }

    /**
     * @param nv21RGB      RGB图像数据
     * @param faceInfoRGB  RGB人脸信息
     * @param widthRGB     RGB下的width
     * @param heightRGB    RGB下的height
     * @param nv21IR       IR图像数据
     * @param faceInfoIR   IR 人脸信息
     * @param widthIR      IR下的width
     * @param heightIR     IR下的height
     * @param format       图像格式
     * @param trackId      特征ID
     * @param livenessType 活体检测的类型
     */
    public void requestFaceLiveness(byte[] nv21RGB, FaceInfo faceInfoRGB, int widthRGB, int heightRGB,
                                    byte[] nv21IR, FaceInfo faceInfoIR, int widthIR, int heightIR,
                                    int format, Integer trackId, LivenessType livenessType) {
        if (faceListener != null) {
            if (flEngine != null && flThreadQueue.remainingCapacity() > 0) {
                flExecutor.execute(new DoubleLiveNessDetectRunnable(nv21RGB, faceInfoRGB, widthRGB,
                        heightRGB, nv21IR, faceInfoIR, widthIR, heightIR, format, trackId, livenessType));
            } else {
                faceListener.onFaceFeatureInfoGet(null, trackId, ERROR_BUSY, GenderInfo.UNKNOWN);
            }
        }
    }

    /**
     * 释放对象
     */
    public void release() {
        if (!frExecutor.isShutdown()) {
            frExecutor.shutdownNow();
            frThreadQueue.clear();
        }
        if (!flExecutor.isShutdown()) {
            flExecutor.shutdownNow();
            flThreadQueue.clear();
        }
        if (faceInfoList != null) {
            faceInfoList.clear();
        }
        if (frThreadQueue != null) {
            frThreadQueue.clear();
            frThreadQueue = null;
        }
        if (flThreadQueue != null) {
            flThreadQueue.clear();
            flThreadQueue = null;
        }
        if (nameMap != null) {
            nameMap.clear();
        }
        nameMap = null;
        faceListener = null;
        faceInfoList = null;
    }

    /**
     * 处理帧数据
     *
     * @param nv21 相机预览回传的NV21数据
     * @return 实时人脸处理结果，封装添加了一个trackId，trackId的获取依赖于faceId，用于记录人脸序号并保存
     */
    public List<FacePreviewInfo> onPreviewFrame(byte[] nv21) {
        if (faceListener != null) {
            if (ftEngine != null) {
                faceInfoList.clear();
//                long ftStartTime = System.currentTimeMillis();
                int code = ftEngine.detectFaces(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);
                if (code != ErrorInfo.MOK) {
                    faceListener.onFail(new Exception("ft failed,code is " + code));
                } else {
                    //人脸追踪成功
//                    Logger.d("人脸：   追踪");
//                    Log.i(TAG, "onPreviewFrame: ft costTime = " + (System.currentTimeMillis() - ftStartTime) + "ms");
                }
                /*
                 * 若需要多人脸搜索，删除此行代码
                 */
                TrackUtil.keepMaxFace(faceInfoList);
                refreshTrackId(faceInfoList);
            }
            facePreviewInfoList.clear();
            for (int i = 0; i < faceInfoList.size(); i++) {
                facePreviewInfoList.add(new FacePreviewInfo(faceInfoList.get(i), currentTrackIdList.get(i)));
            }
            return facePreviewInfoList;
        } else {
            facePreviewInfoList.clear();
            return facePreviewInfoList;
        }
    }

    /**
     * 人脸特征提取线程
     */
    public class FaceRecognizeRunnable implements Runnable {
        private FaceInfo faceInfo;
        private int width;
        private int height;
        private int format;
        private Integer trackId;
        private byte[] nv21Data;

        private FaceRecognizeRunnable(byte[] nv21Data, FaceInfo faceInfo, int width, int height, int format, Integer trackId) {
            if (nv21Data == null) {
                return;
            }
            this.nv21Data = nv21Data;
            this.faceInfo = new FaceInfo(faceInfo);
            this.width = width;
            this.height = height;
            this.format = format;
            this.trackId = trackId;
        }

        @Override
        public void run() {
            if (faceListener != null && nv21Data != null) {
                if (frEngine != null) {
                    FaceFeature faceFeature = new FaceFeature();
//                    long frStartTime = System.currentTimeMillis();
                    int frCode;
                    synchronized (frEngine) {
                        frCode = frEngine.extractFaceFeature(nv21Data, width, height, format, faceInfo, faceFeature);
                    }
                    Logger.d("人脸：   特征提取：" + frCode);
                    if (frCode == ErrorInfo.MOK) {
                        int gender = getGender(frEngine);
//                        Log.i(TAG, "run: fr costTime = " + (System.currentTimeMillis() - frStartTime) + "ms");
                        faceListener.onFaceFeatureInfoGet(faceFeature, trackId, frCode, gender);
                    } else {
                        faceListener.onFaceFeatureInfoGet(null, trackId, frCode, GenderInfo.UNKNOWN);
                        faceListener.onFail(new Exception("fr failed errorCode is " + frCode));
                    }
                } else {
                    faceListener.onFaceFeatureInfoGet(null, trackId, ERROR_FR_ENGINE_IS_NULL, GenderInfo.UNKNOWN);
                    faceListener.onFail(new Exception("fr failed ,frEngine is null"));
                }
            }
            nv21Data = null;
        }
    }

    /**
     * 活体检测的线程
     */
    public class FaceLivenessDetectRunnable implements Runnable {
        private FaceInfo faceInfo;
        private int width;
        private int height;
        private int format;
        private Integer trackId;
        private byte[] nv21Data;
        private LivenessType livenessType;

        private FaceLivenessDetectRunnable(byte[] nv21Data, FaceInfo faceInfo, int width, int height
                , int format, Integer trackId, LivenessType livenessType) {
            if (nv21Data == null) {
                return;
            }
            this.nv21Data = nv21Data;
            this.faceInfo = new FaceInfo(faceInfo);
            this.width = width;
            this.height = height;
            this.format = format;
            this.trackId = trackId;
            this.livenessType = livenessType;
        }

        @Override
        public void run() {
            if (faceListener != null && nv21Data != null) {
                if (flEngine != null) {
                    List<LivenessInfo> livenessInfoList = new ArrayList<>();
                    int flCode;
                    synchronized (flEngine) {
                        if (livenessType == LivenessType.RGB) {
                            flCode = flEngine.process(nv21Data, width, height,
                                    format, Arrays.asList(faceInfo),
                                    FaceEngine.ASF_LIVENESS);
                        } else {
                            flCode = flEngine.processIr(nv21Data, width, height,
                                    format, Arrays.asList(faceInfo),
                                    FaceEngine.ASF_IR_LIVENESS);
                        }
                    }
                    if (flCode == ErrorInfo.MOK) {
                        if (livenessType == LivenessType.RGB) {
                            flCode = flEngine.getLiveness(livenessInfoList);
                        } else {
                            flCode = flEngine.getIrLiveness(livenessInfoList);
                        }
                    }
                    if (flCode == ErrorInfo.MOK && livenessInfoList.size() > 0) {
                        //RGB活体检测成功
//                        Logger.d("人脸：  活体检测");
                        Logger.d("预览   RGB 活体检测  成功");
                        faceListener.onFaceLivenessInfoGet(livenessInfoList.get(0),
                                trackId, flCode, livenessType, (System.currentTimeMillis() - startLivenessTime));
                        Logger.d("活体检测 end  success  ：" +
                                (System.currentTimeMillis() - startLivenessTime) + "   trackId:" + trackId);
                    } else {
                        Logger.d("预览   RGB 活体检测  失败");
                        faceListener.onFaceLivenessInfoGet(null,
                                trackId, flCode, livenessType, (System.currentTimeMillis() - startLivenessTime));
                        faceListener.onFail(new Exception("fl failed errorCode is " + flCode));
                        Logger.d("活体检测 end  fail  ：" +
                                (System.currentTimeMillis() - startLivenessTime) + "   trackId:" + trackId);
                    }
                } else {
                    Logger.d("预览   RGB 活体检测  失败：frEngine is null");
                    faceListener.onFaceLivenessInfoGet(null,
                            trackId, ERROR_FL_ENGINE_IS_NULL, livenessType, (System.currentTimeMillis() - startLivenessTime));
                    faceListener.onFail(new Exception("fl failed ,frEngine is null"));
                }
            }
            nv21Data = null;
        }
    }

    /**
     * 双重活体检测
     */
    public class DoubleLiveNessDetectRunnable implements Runnable {
        private byte[] nv21RGBData;
        private byte[] nv21IRData;
        private FaceInfo faceInfoRGB;
        private FaceInfo faceInfoIR;
        private int widthRGB;
        private int widthIR;
        private int heightRGB;
        private int heightIR;
        private int format;
        private Integer trackId;
        private LivenessType livenessType;

        int f1Code = -1;
        int f2Code = -1;

        private DoubleLiveNessDetectRunnable(byte[] nv21RGB, FaceInfo faceInfoRGB, int widthRGB, int heightRGB,
                                             byte[] nv21IR, FaceInfo faceInfoIR, int widthIR, int heightIR,
                                             int format, Integer trackId, LivenessType livenessType) {
            if (nv21RGB == null || nv21IR == null) {
                return;
            }
            this.nv21RGBData = nv21RGB;
            this.nv21IRData = nv21IR;
            this.faceInfoRGB = new FaceInfo(faceInfoRGB);
            this.faceInfoIR = new FaceInfo(faceInfoIR);
            this.widthRGB = widthRGB;
            this.widthIR = widthIR;
            this.heightRGB = heightRGB;
            this.heightIR = heightIR;
            this.format = format;
            this.trackId = trackId;
            this.livenessType = livenessType;
        }

        @Override
        public void run() {
            if (faceListener != null) {
                List<LivenessInfo> livenessRGBInfoList = null;
                List<LivenessInfo> livenessIRInfoList = null;
                long starTime = System.currentTimeMillis();
                if (nv21RGBData != null && f1Code == -1) {
                    if (flEngine != null) {
                        livenessRGBInfoList = new ArrayList<>();
                        synchronized (flEngine) {
                            //先进行RGB活体检测
                            f1Code = flEngine.process(nv21RGBData, widthRGB, heightRGB, format,
                                    Arrays.asList(faceInfoRGB),
                                    FaceEngine.ASF_LIVENESS);
                        }
                        if (f1Code == ErrorInfo.MOK) {
                            f1Code = flEngine.getLiveness(livenessRGBInfoList);
                        }
                        /**
                         * 存储置信度低的图片
                         */
                        //720      1080
                        Logger.d("图片  双重验证RGB  w：" + widthRGB + "  h:" + heightRGB);
//                        if (f1Code == ErrorInfo.MERR_FSDK_FACEFEATURE_LOW_CONFIDENCE_LEVEL) {
//                            saveConfidenceLowImg(nv21RGBData, widthRGB, heightRGB);
//                        }
                        if (f1Code == ErrorInfo.MOK && livenessRGBInfoList.size() > 0) {
                            //RGB活体检测通过
                            Logger.d("预览  ：   RGB活体检测成功  " + Thread.currentThread().getName());
                        } else {
                            //RGB活体检测未通过
                            faceListener.onFaceDoubleLivenessInfo(false,
                                    null, null, trackId,
                                    f1Code, LivenessType.RGB, (System.currentTimeMillis() - starTime));
                            faceListener.onFail(new Exception("f1 failed errorCode is " + f1Code));
                            Logger.d("预览  ：   RGB活体检测失败  :" + f1Code);
                            nv21RGBData = null;
                            nv21IRData = null;
                            f1Code = -1;
                            f2Code = -1;
                            return;
                        }
                    } else {
                        //RGB活体检测未通过
                        faceListener.onFaceDoubleLivenessInfo(false,
                                null, null, trackId,
                                f1Code, LivenessType.RGB, (System.currentTimeMillis() - starTime));
                        faceListener.onFail(new Exception("f1 failed ,flEngine is null"));
                        Logger.d("预览  ：   RGB活体检测失败：  flEngine is null");
                        nv21RGBData = null;
                        nv21IRData = null;
                        f1Code = -1;
                        f2Code = -1;
                        return;
                    }
                }
                if (nv21IRData != null && f2Code == -1) {
                    if (flEngine != null) {
                        livenessIRInfoList = new ArrayList<>();
                        synchronized (flEngine) {
                            //进行IR活体检测
                            f2Code = flEngine.processIr(nv21IRData, widthIR, heightIR, format,
                                    Arrays.asList(faceInfoIR),
                                    FaceEngine.ASF_IR_LIVENESS);
                        }
                        Logger.d("图片  双重验证IR  w：" + widthIR + "  h:" + widthIR);
                        if (f2Code == ErrorInfo.MOK) {
                            f2Code = flEngine.getIrLiveness(livenessIRInfoList);
                        }
                        /**
                         * 存储置信度低的图片
                         */
                        //720      1080
                        Logger.d("图片  双重验证RGB  w：" + widthRGB + "  h:" + heightRGB);
                        if (f2Code == ErrorInfo.MERR_FSDK_FACEFEATURE_LOW_CONFIDENCE_LEVEL) {
                            saveConfidenceLowImg(nv21IRData, widthIR, heightIR);
                        }
                        if (f2Code == ErrorInfo.MOK && livenessIRInfoList.size() > 0) {
                            //IR活体检测通过
                            Logger.d("预览  ：   IR活体检测成功  " + Thread.currentThread().getName());
                        } else {
                            //IR活体检测未通过
                            faceListener.onFaceDoubleLivenessInfo(false,
                                    null, null, trackId,
                                    f2Code, LivenessType.IR, (System.currentTimeMillis() - starTime));
                            faceListener.onFail(new Exception("f2 failed errorCode is " + f2Code));
                            Logger.d("预览  ：   IR活体检测失败  :" + f2Code);
                            nv21RGBData = null;
                            nv21IRData = null;
                            f1Code = -1;
                            f2Code = -1;
                            return;
                        }
                    } else {
                        faceListener.onFaceDoubleLivenessInfo(false,
                                null, null, trackId,
                                ERROR_FL_ENGINE_IS_NULL, LivenessType.IR,
                                (System.currentTimeMillis() - starTime));
                        faceListener.onFail(new Exception("f2 failed ,flEngine is null"));
                        Logger.d("预览  ：   IR活体检测失败：  flEngine is null");
                        nv21RGBData = null;
                        nv21IRData = null;
                        f1Code = -1;
                        f2Code = -1;
                        return;
                    }
                }
                if (f1Code == ErrorInfo.MOK && livenessRGBInfoList != null && livenessRGBInfoList.size() > 0
                        && f2Code == ErrorInfo.MOK && livenessIRInfoList != null && livenessIRInfoList.size() > 0) {
                    faceListener.onFaceDoubleLivenessInfo(true, livenessRGBInfoList.get(0),
                            livenessIRInfoList.get(0), trackId, f2Code, livenessType,
                            (System.currentTimeMillis() - starTime));
                    nv21RGBData = null;
                    nv21IRData = null;
                    f1Code = -1;
                    f2Code = -1;
                }
            }
        }
    }

    /**
     * 存储置信度低的照片
     */
    private void saveConfidenceLowImg(byte[] nv21Data, int width, int height) {
        byte[] headImageData = ArcSoftImageUtil.createImageData(width, height, ArcSoftImageFormat.NV21);
        int i = ArcSoftImageUtil.cropImage(nv21Data, headImageData, width, height,
                0, 0, width, height, ArcSoftImageFormat.NV21);
        if (i == ArcSoftImageUtilError.CODE_SUCCESS) {
            File file = new File(CONFIDENCE_LOW_DIR);
            boolean exists = file.exists();
            if (!exists) {
                file.mkdirs();
            }
            String userName = String.valueOf(System.currentTimeMillis());
            String savePath = CONFIDENCE_LOW_DIR + File.separator + userName + IMG_SUFFIX;
            ImageDispose.getFileFromBytes(headImageData, savePath);
        }
    }

    /**
     * 获取性别
     *
     * @param faceEngine 人脸引擎
     */
    private int getGender(FaceEngine faceEngine) {
        List<GenderInfo> genderInfoList = new ArrayList<>();
        int gender = frEngine.getGender(genderInfoList);
        if (gender == GenderInfo.FEMALE) {
            Logger.d("性别：  女性 " + Thread.currentThread().getName());
        } else if (gender == GenderInfo.MALE) {
            Logger.d("性别：  男性 ");
        } else if (gender == GenderInfo.UNKNOWN) {
            Logger.d("性别：  未知 ");
        }
        return gender;
    }

    /**
     * 刷新trackId
     *
     * @param ftFaceList 传入的人脸列表
     */
    private void refreshTrackId(List<FaceInfo> ftFaceList) {
        currentTrackIdList.clear();

        for (FaceInfo faceInfo : ftFaceList) {
            currentTrackIdList.add(faceInfo.getFaceId() + trackedFaceCount);
        }
        if (ftFaceList.size() > 0) {
            currentMaxFaceId = ftFaceList.get(ftFaceList.size() - 1).getFaceId();
        }

        //刷新nameMap
        clearLeftName(currentTrackIdList);
    }

    /**
     * 获取当前的最大trackID,可用于退出时保存
     *
     * @return 当前trackId
     */
    public int getTrackedFaceCount() {
        // 引擎的人脸下标从0开始，因此需要+1
        return trackedFaceCount + currentMaxFaceId + 1;
    }

    /**
     * 新增搜索成功的人脸
     *
     * @param trackId 指定的trackId
     * @param name    trackId对应的人脸
     */
    public void setName(int trackId, String name) {
        if (nameMap != null) {
            nameMap.put(trackId, name);
        }
    }

    public String getName(int trackId) {
        return nameMap == null ? null : nameMap.get(trackId);
    }

    /**
     * 清除map中已经离开的人脸
     *
     * @param trackIdList 最新的trackIdList
     */
    private void clearLeftName(List<Integer> trackIdList) {
        Enumeration<Integer> keys = nameMap.keys();
        while (keys.hasMoreElements()) {
            int value = keys.nextElement();
            if (!trackIdList.contains(value)) {
                nameMap.remove(value);
            }
        }
    }

    public static final class Builder {
        private FaceEngine ftEngine;
        private FaceEngine frEngine;
        private FaceEngine flEngine;
        private Camera.Size previewSize;
        private FaceListener faceListener;
        private int frQueueSize;
        private int flQueueSize;
        private int trackedFaceCount;

        public Builder() {
        }


        public Builder ftEngine(FaceEngine val) {
            ftEngine = val;
            return this;
        }

        public Builder frEngine(FaceEngine val) {
            frEngine = val;
            return this;
        }

        public Builder flEngine(FaceEngine val) {
            flEngine = val;
            return this;
        }


        public Builder previewSize(Camera.Size val) {
            previewSize = val;
            return this;
        }


        public Builder faceListener(FaceListener val) {
            faceListener = val;
            return this;
        }

        public Builder frQueueSize(int val) {
            frQueueSize = val;
            return this;
        }

        public Builder flQueueSize(int val) {
            flQueueSize = val;
            return this;
        }

        public Builder trackedFaceCount(int val) {
            trackedFaceCount = val;
            return this;
        }

        public FaceHelper build() {
            return new FaceHelper(this);
        }
    }
}
