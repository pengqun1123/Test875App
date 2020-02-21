package com.arcFace.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

public class MyTextureView extends TextureView {
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public MyTextureView(Context context) {
        this(context, null);
    }

    public MyTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = 20/*width*/;
        mRatioHeight = 32/*height*/;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Log.d("  缩放:", "    1212  ");
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
            Log.d("  缩放:", "    7777  ");
        } else {
            if (width > height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
                Log.d("  缩放:", "    8888  ");
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
                Log.d("  缩放:", "    9999  ");
            }
        }
    }
}
