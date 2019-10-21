package com.face.common;

import android.content.Context;
import android.os.Environment;
import com.orhanobut.logger.Logger;
import com.zqzn.android.face.camera.FaceCamera;
import com.zqzn.android.face.camera.FaceCameraView;
import com.zqzn.android.face.model.FaceSDK;
import com.zqzn.android.face.util.NormalizeHelper;
import com.zqzn.android.zqznfacesdk.ZqznFaceFeatureExtractor;
import com.zqzn.android.zqznfacesdk.ZqznFaceLivenessDetector;
import com.zqzn.android.zqznfacesdk.ZqznFaceSDK;
import com.zqzn.android.zqznfacesdk.ZqznFaceSearchLibrary;
import com.zqzn.android.zqznfacesdk.ZqznSDKConfig;
import com.zqzn.android.zqznfacesdk.ZqznSdkEnvConfig;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 全局单例APP配置类
 */
public class FaceConfig {

    /**
     * 默认的数据存储目录
     */
    private static final File DEFAULT_APP_ROOT_DIR = new File(Environment.getExternalStorageDirectory(), "zhiqu");
    private static final String TAG = FaceConfig.class.getSimpleName();

    private static FaceConfig INSTANCE = null;
    /**
     * SDK核心类
     */
    private final ZqznFaceSDK faceSDK;
    private FaceSDK.InitCallback initCallback;
    private String serialNumber;
    private File appRootDir = null;


    /**
     * SDK环境配置类：主要保存摄像头适配参数等数据
     */
    private ZqznSdkEnvConfig envConfig;

    /**
     * SDK配置类：主要保存SDK授权信息、SDK阈值信息等数据
     */
    private ZqznSDKConfig sdkConfig;
    private FaceCamera visCamera;
    private FaceCamera nirCamera;

    private FaceConfig() {
        if (!DEFAULT_APP_ROOT_DIR.exists()) {
            //noinspection ResultOfMethodCallIgnored
            DEFAULT_APP_ROOT_DIR.mkdirs();
        }
        //获取SDk实例
        faceSDK = ZqznFaceSDK.instance;
    }

    public static FaceConfig getInstance() {
        if (INSTANCE == null) {
            synchronized (FaceConfig.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FaceConfig();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化方法
     * <p>
     * 默认使用之前授权激活后存储在SD卡上的授权记录，本地授权校验结果则会通过回调方法通知调用方。
     *
     * @param context
     * @param initCallback 初始化回调接口
     */
    public void init(final Context context, final FaceSDK.InitCallback initCallback) {
        if (appRootDir == null) {
            appRootDir = DEFAULT_APP_ROOT_DIR;
        }
        init(context, null, initCallback);
    }

    /**
     * 初始化方法
     * <p>
     * 增加了一个serialNumber参数，主要用于在第一次使用时，使用序列号进行联网激活时使用。联网激活完成后，如果激活成功，将会自动初始化SDK。最后将授权校验结果通过回调方法通知调用方。
     *
     * @param context
     * @param serialNumber 序列号
     * @param initCallback 初始化回调接口
     */
    public void init(final Context context, final String serialNumber, final FaceSDK.InitCallback initCallback) {
        this.initCallback = initCallback;
        this.serialNumber = serialNumber;
        if (appRootDir == null) {
            appRootDir = DEFAULT_APP_ROOT_DIR;
        }
        //初始化SDK
        initZqznFaceSdk(context);
        //初始化用户信息管理器，demo中使用SQLite实现
        File databaseDir = new File(getAppRootDir(), "databases");
        if (!databaseDir.exists()) {
            databaseDir.mkdirs();
        }
    }

    private void initZqznFaceSdk(Context context) {
        //设备SDK配置根目录
        ZqznSdkEnvConfig.setRootPath(appRootDir.getAbsolutePath());
        //硬件适配类：提供硬件适配参数（主要是摄像头参数）
        envConfig = new ZqznSdkEnvConfig(context, "env_config.json");
        envConfig.setFaceCameraViewMode(FaceCameraView.ViewMode.TEXTURE_VIEW);
        //SDK配置类：提供SDK配置参数（包括授权信息及SDK一些相关参数，如最小人脸检测大小）
        sdkConfig = new ZqznSDKConfig(context, "sdk_config.json");
        //设置多人或者单人模式
       // sdkConfig.setSingleTrack(false);
       // sdkConfig.save();
        //设置license类型 1-为SDK类型的序列号
        sdkConfig.setLicenseType(1);
        sdkConfig.setBlurThreshold(0);
        sdkConfig.save();
        //设置SDK版本号
        sdkConfig.setVersion("1.0.0");
        if (serialNumber != null && !serialNumber.trim().isEmpty()) {
            sdkConfig.setLicenseKey(serialNumber);
            adjustParameter();
            try {
                sdkConfig.save();
            } catch (Throwable ignore) {
            }
        }
        //设置授权文件
        sdkConfig.setLicenseFile(new File(appRootDir, "license/zqzn_license.txt").getAbsolutePath());
        Logger.i(TAG, "授权接口地址：" + ZqznFaceSDK.AUTH_URL);
        //初始化SDK
        faceSDK.init(context, envConfig, sdkConfig, new FaceSDK.InitCallback() {
            @Override
            public void onInitSuccess() {
                try {
                    printModelVersion();
                    //初始化摄像头参数
                    initCamera();
                    if (initCallback != null) {
                        initCallback.onInitSuccess();
                    }
                } catch (Exception e) {
                    Logger.e(TAG, "初始化失败", e);
                    if (initCallback != null) {
                        initCallback.onInitFailed(e);
                    }
                }
            }

            @Override
            public void onInitFailed(Throwable throwable) {
                if (initCallback != null) {
                    initCallback.onInitFailed(throwable);
                }
            }
        });
    }

    private void adjustParameter() {
        sdkConfig.setBlurThreshold(0F);
        sdkConfig.setSimThreshold(0.85F);
        sdkConfig.setLivenessThreshold(0.9F);
        sdkConfig.setEyeBrewOccDetect(false);
        sdkConfig.setCloseEyeDetect(false);
        sdkConfig.setSingleTrack(true);
        sdkConfig.setFramesPerDetect(1);
        sdkConfig.setQuickFramesPerDetect(1);
    }

    /**
     * 打印模型版本号等信息
     */
    private void printModelVersion() {
        Logger.i(TAG, "model version: " + faceSDK.getModelsVersion());
        Logger.i(TAG, "face search similarity threshold: original => "
                + Arrays.toString(ZqznFaceSearchLibrary.SIM_THRESHOLDS)
                + ", target => " + Arrays.toString(NormalizeHelper.TARGET_THRESHOLDS));
        Logger.i(TAG, "vis liveness threshold: original => "
                + Arrays.toString(ZqznFaceLivenessDetector.VIS_SOURCE_THRESHOLDS)
                + ", target => " + Arrays.toString(NormalizeHelper.LIVENESS_TARGET_THRESHOLDS));
        Logger.i(TAG, "nir liveness threshold: original => "
                + Arrays.toString(ZqznFaceLivenessDetector.NIR_SOURCE_THRESHOLDS)
                + ", target => " + Arrays.toString(NormalizeHelper.LIVENESS_TARGET_THRESHOLDS));
    }

    /**
     * 根据适配文件调整camera参数
     */
    private void initCamera() {
        //todo 这里使用我们的硬件适配工具生成的适配参数来初始化摄像头(为了适配各种设备上的摄像头参数)
        // todo 如果摄像头参数是固定的，直接在这里new FaceCamera(CameraParams)并设置好相应的参数就行，不需要从EnvConfig里获取
        visCamera = envConfig.getVisCamera();

        nirCamera = envConfig.getNirCamera();

       try {
            visCamera.setPreviewSize(720,1080);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (visCamera != null) {
            visCamera.setImageConverter(faceSDK.getVisImageConverter());
        }
        if (nirCamera != null) {
            nirCamera.setImageConverter(faceSDK.getNirImageConverter());
        }

        Logger.d(TAG, "可见光摄像头:" + getVisCamera() + ", 红外摄像头:" + getNirCamera());
    }

    /**
     * 获取APP数据根目录
     *
     * @return APP数据根目录
     */
    public File getAppRootDir() {
        if (appRootDir == null) {
            appRootDir = DEFAULT_APP_ROOT_DIR;
        }
        return appRootDir;
    }

    /**
     * 设置APP应用数据根目录
     *
     * @param appRootDir
     */
    public void setAppRootDir(File appRootDir) {
        this.appRootDir = appRootDir;
        if (appRootDir != null) {
            if (!appRootDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                appRootDir.mkdirs();
            }
        }
    }

    /**
     * 获取离线人脸识别SDK核心类
     * <p>
     * 所有人脸检测、人脸识别相关模型接口均可通过ZqznFaceSDK获取
     *
     * @return
     */
    public ZqznFaceSDK getFaceSDK() {
        return faceSDK;
    }


    /**
     * 获取环境配置
     *
     * @return
     */
    public ZqznSdkEnvConfig getEnvConfig() {
        return envConfig;
    }

    /**
     * 获取SDK配置
     *
     * @return
     */
    public ZqznSDKConfig getSdkConfig() {
        return sdkConfig;
    }

    /**
     * 获取原始相似度阈值
     */
    public double[] getOriginalSimilarityThreshold() {
        return ZqznFaceFeatureExtractor.SIM_THRESHOLDS;
    }

    /**
     * 获取可见光摄像头控制类
     */
    public FaceCamera getVisCamera() {
        return visCamera;
    }

    /**
     * 获取近红外摄像头控制类
     */
    public FaceCamera getNirCamera() {
        return nirCamera;
    }
}
