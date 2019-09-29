package com.face.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zqzn.android.face.data.FaceData;

/**
 * 人脸框绘制自定义View
 * <p>
 * 主要实现方式是，在摄像头预览界面上用此自定义View遮盖，底层显示视频预览画面，此自定义View在上层绘制人脸框。
 */
public class FaceBoxView extends View {
    private static final String TAG = FaceBoxView.class.getSimpleName();

    private FaceData[] faceDataArray;
    private final Paint faceBoxPaint = new Paint();
    private final Paint loginPaint = new Paint();
    private final Paint attackPaint = new Paint();
    private Paint paint = new Paint();
    private final float[] lines = new float[4 * 8];

    private static final int LINE_LENGTH = 30;
    private float coordinateScaleFactor = -1;

    public FaceBoxView(Context context) {
        super(context);
        init();
    }

    private void init() {
        faceBoxPaint.setStrokeWidth(4);
        faceBoxPaint.setStyle(Paint.Style.FILL);
        faceBoxPaint.setColor(Color.CYAN);

        loginPaint.setStrokeWidth(4);
        loginPaint.setStyle(Paint.Style.FILL);
        loginPaint.setColor(Color.CYAN);
        loginPaint.setTextSize(50);
        loginPaint.setTextAlign(Paint.Align.CENTER);

        attackPaint.setStrokeWidth(4);
        attackPaint.setStyle(Paint.Style.FILL);
        attackPaint.setColor(Color.RED);
        attackPaint.setTextSize(50);
        attackPaint.setTextAlign(Paint.Align.CENTER);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAlpha(50);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(1);
    }

    public FaceBoxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void drawFaceBox(FaceData[] faceData) {
        faceDataArray = faceData;
        postInvalidate();
    }

    private static final int MAX_LIMIT = 5;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (faceDataArray == null || faceDataArray.length == 0) {
                return;
            }
            int maxSize = Math.min(MAX_LIMIT, faceDataArray.length);
            for (int i = 0; i < maxSize; i++) {
                FaceData faceData = faceDataArray[i];
                int[] c = faceData.getFaceRect().toIntArray();
                if (c[0] <= 0 || c[1] <= 0 || c[2] <= 0 || c[3] <= 0) {
                    //坐标不对，直接忽略
                    continue;
                }
                FaceDataExtraInfo data = (FaceDataExtraInfo) faceData.getTag();
                //set lines coordinate
                int x1 = c[0];
                int y1 = c[1];
                int x2 = c[2];
                int y2 = c[3];
                if (coordinateScaleFactor != -1) {
                    x1 = (int) (c[0] * coordinateScaleFactor);
                    y1 = (int) (c[1] * coordinateScaleFactor);
                    x2 = (int) (c[2] * coordinateScaleFactor);
                    y2 = (int) (c[3] * coordinateScaleFactor);
                }

                if (data != null) {
                    int fontx = (x2 + x1) / 2;
                    int fonty = y2 + 55;
                    if (fonty > getHeight()) {
                        fonty = y1;
                    }
                    if (data.getLoginStatus() == 2) {
                        //attack
                        canvas.drawText(data.getName(), fontx, fonty, attackPaint);
                    } else {
                        String message = data.getFaceQualityMessage() == null ? data.getName() : data.getFaceQualityMessage();
                        if (message != null) {
                            canvas.drawText(message, fontx, fonty, loginPaint);
                        }
                    }
                }

                int x1p = x1 + LINE_LENGTH;
                int y1p = y1 + LINE_LENGTH;
                int x2m = x2 - LINE_LENGTH;
                int y2m = y2 - LINE_LENGTH;

                //line1
                lines[0] = x1;
                lines[1] = y1;
                lines[2] = x1p;
                lines[3] = y1;

                //line2
                lines[4] = x2m;
                lines[5] = y1;
                lines[6] = x2;
                lines[7] = y1;

                //line3
                lines[8] = x1;
                lines[9] = y1;
                lines[10] = x1;
                lines[11] = y1p;

                //line4
                lines[12] = x2;
                lines[13] = y1;
                lines[14] = x2;
                lines[15] = y1p;

                //line5
                lines[16] = x1;
                lines[17] = y2m;
                lines[18] = x1;
                lines[19] = y2;

                //line6
                lines[20] = x2;
                lines[21] = y2m;
                lines[22] = x2;
                lines[23] = y2;

                //line7
                lines[24] = x1;
                lines[25] = y2;
                lines[26] = x1p;
                lines[27] = y2;

                //line8
                lines[28] = x2m;
                lines[29] = y2;
                lines[30] = x2;
                lines[31] = y2;
                canvas.drawLines(lines, faceBoxPaint);

            }
        } catch (Throwable e) {
            Log.e(TAG, "onDraw: draw face box ERROR", e);
        }
    }


    public void setCoordinateScaleFactor(float coordinateScaleFactor) {
        this.coordinateScaleFactor = coordinateScaleFactor;
    }
}
