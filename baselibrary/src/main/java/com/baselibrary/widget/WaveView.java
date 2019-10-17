package com.baselibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.baselibrary.R;


/**
 * Created by wangyu on 2019/10/16.
 */

public class WaveView extends View {

    private Paint mPaint;

    private Path mPath;

    private int height;

    int type=1;

    // 波长
    private int width;

    private float mTheta = 0f;

    public WaveView(Context context) {
        this(context,null);
    }

    public WaveView(Context context,   AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WaveView(Context context,   AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        if (typedArray!=null){
            type = typedArray.getInt(R.styleable.WaveView_type, type);
        }
        mPath=new Path();
        mPaint =new Paint();
       // mPaint.setStrokeWidth(5);
      //  mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(context.getResources().getColor(R.color.blue_10));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 振幅
        int amplitude = 30;
        int index = 0;

        mPath.reset();
        mPath.moveTo(0, 0);
        if (type==1) {
            while (index <= width) {

                float endY = (float) (Math.sin((float) index / (float) width * 4f * Math.PI + mTheta)
                        * (float) amplitude + height - amplitude);

                mPath.lineTo(index, endY);
                // Log.e("xxx", String.format("(%.4f, %.4f)", (float) index, endY));
                index++;
            }
        }else {
            while (index <= width) {

                float endY = (float) (Math.cos((float) index / (float) width * 4f * Math.PI + mTheta)
                        * (float) amplitude + height - amplitude);

                mPath.lineTo(index, endY);
                // Log.e("xxx", String.format("(%.4f, %.4f)", (float) index, endY));
                index++;
            }
        }
        mPath.lineTo(index - 1, 0);
        mPath.close();

        canvas.drawPath(mPath, mPaint);
        mTheta += 0.1;
        if (mTheta >= 2f * Math.PI) {
            mTheta -= (2f * Math.PI);
        }
        postInvalidateDelayed(80);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
       height=getHeight();
       width=getWidth();
    }
}
