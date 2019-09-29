package com.face.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.face.R;
import com.face.common.FaceConfig;
import com.zqzn.android.face.data.FaceData;
import com.zqzn.android.face.exceptions.FaceException;
import com.zqzn.android.face.image.RGBImage;
import com.zqzn.android.face.jni.Tool;
import com.zqzn.android.face.model.FaceDetector;
import com.zqzn.android.face.model.FaceFeatureExtractor;
import com.face.common.CommonUtils;
import com.zqzn.android.zqznfacesdk.ZqznFaceSDK;

import java.io.File;
import java.text.DecimalFormat;

/**
 * 1:1比对示例
 */
public class CompareTwoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CompareTwoActivity.class.getSimpleName();
    private ImageView ivImage1;
    private ImageView ivImage2;
    private Button btnCmp;
    private TextView tvSim;
    private ZqznFaceSDK faceSDK;
    private FaceDetector faceDetector;
    private FaceFeatureExtractor faceFeatureExtractor;
    private Bitmap image1;
    private Bitmap image2;
    private float[] feature1;
    private float[] feature2;
    private Button btnSelect1;
    private Button btnSelect2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_activity_compare_two);
        ivImage1 = (ImageView) findViewById(R.id.iv_image1);
        ivImage2 = (ImageView) findViewById(R.id.iv_image2);
        btnSelect1 = (Button) findViewById(R.id.btn_select1);
        btnSelect2 = (Button) findViewById(R.id.btn_select2);
        btnCmp = (Button) findViewById(R.id.btn_cmp);
        tvSim = (TextView) findViewById(R.id.tv_sim);
        btnSelect1.setOnClickListener(this);
        btnSelect2.setOnClickListener(this);
        btnCmp.setOnClickListener(this);
        faceSDK = FaceConfig.getInstance().getFaceSDK();
        try {
            faceDetector = faceSDK.getVisFaceDetector();
            faceFeatureExtractor = faceSDK.getFaceFeatureExtractor();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: 获取SDK失败", e);
            Toast.makeText(this, "获取人脸检测及人脸特征抽取SDK异常", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int IMAGE1_OPEN_REQUEST_CODE = 1;
    private static final int IMAGE2_OPEN_REQUEST_CODE = 2;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select1:
                showFileChooser("选择第一张图片", IMAGE1_OPEN_REQUEST_CODE);
                break;
            case R.id.btn_select2:
                showFileChooser("选择第二张图片", IMAGE2_OPEN_REQUEST_CODE);
                break;
            case R.id.btn_cmp:
                if (feature1 != null && feature2 != null) {
                    //特征比对
                    double sim = 0;
                    sim = Tool.calcSimilarity(FaceConfig.getInstance().getOriginalSimilarityThreshold(), feature1, feature2);
                    DecimalFormat format = new DecimalFormat("#.######");
                    tvSim.setText("相似度: " + format.format(sim));
                } else {
                    Toast.makeText(this, "有图片抽取特征失败", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            //图片选择成功
            Uri uri = data.getData();
            String path = CommonUtils.getPath(this, uri);
            if (path != null) {
                //读取图片
                Bitmap bitmap = Tool.loadBitmap(new File(path));
                //将图片转换成RGBImage
                RGBImage img = RGBImage.bitmapToRGBImage(bitmap);
                try {
                    //检测图片中的最大人脸
                    FaceData faceData = faceDetector.detectMaxFace(img, true);
                    if (faceData == null) {
                        Toast.makeText(this, "未检测到人脸", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //抽取人脸特征
                    float[] feature = faceFeatureExtractor.extractFaceFeature(img, faceData);
                    if (feature == null) {
                        Toast.makeText(this, "抽取特征失败，可能照片质量较低", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    switch (requestCode) {
                        case IMAGE1_OPEN_REQUEST_CODE:
                            image1 = bitmap;
                            feature1 = feature;
                            break;
                        case IMAGE2_OPEN_REQUEST_CODE:
                            image2 = bitmap;
                            feature2 = feature;
                            break;
                        default:
                            break;
                    }
                } catch (FaceException e) {
                    Toast.makeText(this, "人脸检测及抽特征异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            switch (requestCode) {
                case IMAGE1_OPEN_REQUEST_CODE:
                    image1 = null;
                    feature1 = null;
                    break;
                case IMAGE2_OPEN_REQUEST_CODE:
                    image2 = null;
                    feature2 = null;
                    break;
                default:
                    break;
            }
        }
        if (image1 == null) {
            ivImage1.setImageDrawable(null);
        } else {
            ivImage1.setImageBitmap(image1);
        }
        if (image2 == null) {
            ivImage2.setImageDrawable(null);
        } else {
            ivImage2.setImageBitmap(image2);
        }
    }

    private void showFileChooser(String msg, int code) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, msg), code);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "未安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }
}
