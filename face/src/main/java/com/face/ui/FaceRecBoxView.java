package com.face.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

import com.zqzn.android.face.camera.Size;
import com.zqzn.android.face.data.FaceData;
import com.zqzn.android.face.data.FaceDetectData;
import com.zqzn.android.face.data.FaceRect;
import com.zqzn.android.face.processor.BaseFaceRecProcessor;

/**
 * Created by wangyu on 2019/10/8.
 */

public class FaceRecBoxView extends View {
    Paint greenPaint = new Paint();
    Paint whitePaint = new Paint();
    Paint redPaint = new Paint();
    FaceDetectData faceDetectData;

    private static final int LINE_LENGTH = 30;

    public FaceRecBoxView(Context context) {
        super(context);
        this.init();
    }

    public FaceRecBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public FaceRecBoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        this.greenPaint.setColor(-16711936);
        this.greenPaint.setTextSize(50);
        this.whitePaint.setColor(-1);
        this.whitePaint.setTextSize(50);
        this.redPaint.setColor(-65536);
        this.redPaint.setTextSize(50);
        this.setAlpha(0.9F);
        this.greenPaint.setStrokeWidth(4);
        this.greenPaint.setStyle(Paint.Style.FILL);
        this.greenPaint.setTextAlign(Paint.Align.CENTER);

        this.whitePaint.setStrokeWidth(4);
        this.whitePaint.setStyle(Paint.Style.FILL);
        this.whitePaint.setTextAlign(Paint.Align.CENTER);

        this.redPaint.setStrokeWidth(4);
        this.redPaint.setStyle(Paint.Style.FILL);
        this.redPaint.setTextAlign(Paint.Align.CENTER);
    }

    protected void onDraw(Canvas canvas) {
        this.drawData(canvas);
    }

    void drawData(Canvas canvas) {
        FaceDetectData detectData = this.faceDetectData;
        FaceData[] data = detectData == null?null:detectData.getFaceList();

        try {
            //int width = false;
            float widthRatio = 0.0F;
            float heightRatio = 0.0F;
            if(detectData != null) {
                Size size = detectData.getImage().getRotateSize();
                widthRatio = (float)this.getWidth() / (float)size.getWidth();
                heightRatio = (float)this.getHeight() / (float)size.getHeight();
            }

            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            if(data != null) {
                FaceData[] var16 = data;
                int var8 = data.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    FaceData d = var16[var9];
                    if(d.isFaceCompletion()) {
                        Paint paint = this.whitePaint;
                        if(d.getStatus().equals(FaceData.FaceDataStatus.FACE_REC_COMPLETED) && d.getTag() != null && d.getTag() instanceof BaseFaceRecProcessor.FaceTrackData) {
                            BaseFaceRecProcessor.FaceTrackData trackData = (BaseFaceRecProcessor.FaceTrackData)d.getTag();
                            if(trackData.searchTimes.get() > 0) {
                                if(trackData.searchPass) {
                                    paint = this.greenPaint;
                                } else {
                                    paint = this.redPaint;
                                }
                            }
                        }
                        int padding=80;

                        FaceRect faceRect = d.getFaceRect().scale(widthRatio+0.2F, heightRatio+0.2F);
                        faceRect.setLeft(faceRect.getLeft()-25);
                        faceRect.setRight(faceRect.getRight()-40);
                        faceRect.setTop(faceRect.getTop()-90);
                        faceRect.setBottom(faceRect.getBottom()-90);

                        canvas.drawLine((float)faceRect.getLeft(), (float)faceRect.getTop(), (float)faceRect.getLeft()+LINE_LENGTH, (float)faceRect.getTop(), paint);//
                        canvas.drawLine((float)faceRect.getRight(), (float)faceRect.getTop(), (float)faceRect.getRight()-LINE_LENGTH, (float)faceRect.getTop(), paint);//

                        canvas.drawLine((float)faceRect.getLeft(), (float)faceRect.getTop(), (float)faceRect.getLeft(), (float)faceRect.getTop()+LINE_LENGTH, paint);
                        canvas.drawLine((float)faceRect.getRight(), (float)faceRect.getTop(), (float)faceRect.getRight(), (float)faceRect.getTop()+LINE_LENGTH, paint);

                        canvas.drawLine((float)faceRect.getLeft(), (float)faceRect.getBottom(), (float)faceRect.getLeft(), (float)faceRect.getBottom()-LINE_LENGTH, paint);
                        canvas.drawLine((float)faceRect.getRight(), (float)faceRect.getBottom(), (float)faceRect.getRight(), (float)faceRect.getBottom()-LINE_LENGTH, paint);

                        canvas.drawLine((float)faceRect.getLeft(), (float)faceRect.getBottom(), (float)faceRect.getLeft()+LINE_LENGTH, (float)faceRect.getBottom(), paint);
                        canvas.drawLine((float)faceRect.getRight(), (float)faceRect.getBottom(), (float)faceRect.getRight()-LINE_LENGTH, (float)faceRect.getBottom(), paint);

                        String text = "";
                        if(d.getTag() != null) {
                            text = d.getTag().toString();
                        }

                        int cx = (faceRect.getLeft() + faceRect.getRight()) / 2;

                        if(!text.isEmpty()) {
                            canvas.drawText(text, (float)cx, (float)faceRect.getTop()-5, paint);
                        }
                    }
                }
            }
        } catch (Throwable var15) {
            var15.printStackTrace();
        }

    }

    public void sendFaceData(FaceDetectData faceDetectData) {
        if(faceDetectData == null || faceDetectData.getFaceList() == null || faceDetectData.getFaceList().length <= 0) {
            faceDetectData = null;
        }

        if(this.faceDetectData != faceDetectData) {
            this.faceDetectData = faceDetectData;

            try {
                this.postInvalidate();
            } catch (Throwable var3) {
                ;
            }
        }

    }
}
