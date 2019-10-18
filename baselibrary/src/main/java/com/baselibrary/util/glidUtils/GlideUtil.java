package com.baselibrary.util.glidUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;

import com.baselibrary.R;
import com.baselibrary.util.GetUIDimens;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;


/**
 * Created by **
 * on 2018/9/11.
 */

public class GlideUtil {


    /**
     * 加载网络图片
     *
     * @param mContext
     * @param path
     */
    @SuppressLint("CheckResult")
    public static void loadImage(Context mContext, Integer placeHolder,
                                 String path, AppCompatImageView imageView) {
        RequestOptions options = new RequestOptions();
        options.placeholder(placeHolder);
        options.error(R.drawable.place_holder_img);
        options.centerCrop();
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(mContext).load(path).apply(options)
                .listener(
                new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model,
                                           Target<Drawable> target,
                                           DataSource dataSource,
                                           boolean isFirstResource) {
                return false;
            }
        })
                .into(imageView);
    }

    //不缓存
    @SuppressLint("CheckResult")
    public static void loadImageNoCache(Context mContext, Integer placeHolder,
                                 String path, AppCompatImageView imageView) {
        RequestOptions options = new RequestOptions();
        options.placeholder(placeHolder);
        options.error(R.drawable.place_holder_img);
        options.centerCrop();
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        options.skipMemoryCache(true);
        Glide.with(mContext).load(path).apply(options)
                .listener(
                        new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target,
                                                           DataSource dataSource,
                                                           boolean isFirstResource) {
                                return false;
                            }
                        })
                .into(imageView);
    }

    /**
     * 加载本地图片
     *
     * @param mContext
     * @param path
     */
    @SuppressLint("CheckResult")
    public static void loadImage(Context mContext, Integer placeHolder,
                                 Uri path, AppCompatImageView imageView) {
        RequestOptions options = new RequestOptions();
        options.placeholder(placeHolder);
        options.error(R.drawable.place_holder_img);
        options.centerCrop();
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(mContext).load(path).apply(options).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model,
                                           Target<Drawable> target,
                                           DataSource dataSource,
                                           boolean isFirstResource) {
                return false;
            }
        }).into(imageView);
    }

    /**
     * 加Drawable的图片显示
     *
     * @param mContext
     * @param placeHolder
     * @param drawable
     */
    public static void loadImage(Context mContext, Integer placeHolder,
                                 Drawable drawable, AppCompatImageView imageView) {
        RequestOptions options = new RequestOptions();
        options.placeholder(placeHolder);
        options.error(R.drawable.place_holder_img);
        options.centerCrop();
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(mContext).load(drawable).apply(options).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model,
                                           Target<Drawable> target,
                                           DataSource dataSource,
                                           boolean isFirstResource) {
                return false;
            }
        }).into(imageView);
    }

    @SuppressLint("CheckResult")
    public static void loadImage(Context mContext, Integer placeHolder,
                                 Integer path, AppCompatImageView imageView) {
        RequestOptions options = new RequestOptions();
        options.placeholder(placeHolder);
        options.error(R.drawable.place_holder_img);
        options.centerCrop();
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(mContext).load(path).apply(options).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model,
                                           Target<Drawable> target,
                                           DataSource dataSource,
                                           boolean isFirstResource) {
                return false;
            }
        }).into(imageView);
    }

    /**
     * 加载带尺寸的图片
     *
     * @param mContext
     * @param path
     * @param Width
     * @param Height
     */
    @SuppressLint("CheckResult")
    public static void loadImageWithSize(Context mContext, Integer placeHolder, String path,
                                         int Width, int Height, AppCompatImageView imageView) {
        RequestOptions options = new RequestOptions();
        options.placeholder(placeHolder);
        options.override(Width, Height);
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(mContext).load(path).apply(options).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model,
                                           Target<Drawable> target,
                                           DataSource dataSource,
                                           boolean isFirstResource) {
                return false;
            }
        }).into(imageView);
    }

    /**
     * 加载本地图片
     *
     * @param mContext
     * @param path
     */
    @SuppressLint("CheckResult")
    public static void loadImageWithLocation(Context mContext, Integer path, AppCompatImageView imageView) {
        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        if (path != null)
            Glide.with(mContext).load(path).apply(options).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                            Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model,
                                               Target<Drawable> target,
                                               DataSource dataSource,
                                               boolean isFirstResource) {
                    return false;
                }
            }).into(imageView);
    }

    /**
     * 带圆形外框的图片加载
     *
     * @param mContext
     * @param path
     */
    @SuppressLint("CheckResult")
    public static void loadCircleImage(Context mContext, Integer placeHolder,
                                       Uri path, AppCompatImageView imageView) {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.placeholder(placeHolder);
        options.error(R.drawable.place_holder_img);
        options.transform(new GlideCircleTransform(mContext, 0.8f,
                mContext.getResources().getColor(R.color.white)));
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(mContext).load(path).apply(options).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model,
                                           Target<Drawable> target,
                                           DataSource dataSource,
                                           boolean isFirstResource) {
                return false;
            }
        }).into(imageView);
    }

    /**
     * 带圆形外框的图片加载
     *
     * @param mContext
     * @param path
     */
    @SuppressLint("CheckResult")
    public static void loadCircleImage(Context mContext, Integer placeHolder,
                                       String path, AppCompatImageView imageView) {

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.placeholder(placeHolder);
        options.error(R.drawable.ic_default_user_avatar);
        options.transform(new GlideCircleTransform(mContext, 0.8f,
                mContext.getResources().getColor(R.color.white)));
//        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(mContext).load(path).apply(options)
                .listener(
                new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model,
                                           Target<Drawable> target,
                                           DataSource dataSource,
                                           boolean isFirstResource) {
                return false;
            }
        })
                .into(imageView);
    }

    @SuppressLint("CheckResult")
    public static void loadCircleImageNoCache(Context mContext, Integer placeHolder,
                                       String path, AppCompatImageView imageView) {

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.placeholder(placeHolder);
        options.error(R.drawable.place_holder_img);
        options.transform(new GlideCircleTransform(mContext, 0.8f,
                mContext.getResources().getColor(R.color.white)));
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        options.skipMemoryCache(true);
        Glide.with(mContext).load(path).apply(options)
                .listener(
                        new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target,
                                                           DataSource dataSource,
                                                           boolean isFirstResource) {
                                return false;
                            }
                        })
                .into(imageView);
    }

    @SuppressLint("CheckResult")
    public static void loadCircleImage(Context mContext, Integer placeHolder,
                                       Drawable drawable, AppCompatImageView imageView) {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.placeholder(placeHolder);
        options.error(R.drawable.place_holder_img);
        options.transform(new GlideCircleTransform(mContext, 0.8f,
                mContext.getResources().getColor(R.color.white)));
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(mContext).load(drawable).apply(options).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model,
                                           Target<Drawable> target,
                                           DataSource dataSource,
                                           boolean isFirstResource) {
                return false;
            }
        }).into(imageView);
    }

    /**
     * 设置圆形ImageView
     *
     * @param mContext    上下文
     * @param placeHolder 占位图
     * @param path        资源
     */
    @SuppressLint("CheckResult")
    public static void loadCircleImage(Context mContext, Integer placeHolder,
                                       Integer path, AppCompatImageView imageView) {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.placeholder(placeHolder);
        options.error(R.drawable.place_holder_img);
        options.transform(new GlideCircleTransform(mContext, 0.8f,
                mContext.getResources().getColor(R.color.white)));
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(mContext).load(path).apply(options).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model,
                                           Target<Drawable> target,
                                           DataSource dataSource,
                                           boolean isFirstResource) {
                return false;
            }
        }).into(imageView);
    }

    @SuppressLint("CheckResult")
    public static void loadCornerdImg(Context mContext, Integer path, Integer placeHolder,
                                      AppCompatImageView imageView, boolean leftTop, boolean leftBottom,
                                      boolean rightTop, boolean rightBottom) {
        //        GlideCircle2Transformer glideCircle2Transformer = new GlideCircle2Transformer(mContext,
        //                -1, 20);
        //        glideCircle2Transformer.setExceptCorner(leftTop, rightTop, leftBottom, rightBottom);
        //        RequestOptions.centerCropTransform();
        //        RequestOptions requestOptions = RequestOptions.bitmapTransform(glideCircle2Transformer);
        //        requestOptions.centerCrop();
        //        requestOptions.placeholder(placeHolder);
        //        Glide.with(mContext).asBitmap().load(path).apply(requestOptions).into(imageView);
        CornerTransform transform = new CornerTransform(mContext, GetUIDimens.dpToPx(mContext, 5));
        transform.setNeedCorner(leftTop, rightTop, leftBottom, rightBottom);
        RequestOptions requestOptions = new RequestOptions().placeholder(placeHolder)
                .transform(new CenterCrop(), transform);
        Glide.with(mContext).asBitmap().load(path).apply(requestOptions).into(imageView);
    }

    @SuppressLint("CheckResult")
    public static void loadCornerdImg(Context mContext, String path, Integer placeHolder,
                                      AppCompatImageView imageView, boolean leftTop, boolean leftBottom,
                                      boolean rightTop, boolean rightBottom) {
        //        GlideCircle2Transformer glideCircle2Transformer = new GlideCircle2Transformer(mContext,
        //                -1, 20);
        //        glideCircle2Transformer.setExceptCorner(leftTop, rightTop, leftBottom, rightBottom);
        //        RequestOptions.centerCropTransform();
        //        RequestOptions requestOptions = RequestOptions.bitmapTransform(glideCircle2Transformer);
        //        requestOptions.centerCrop();
        //        requestOptions.placeholder(placeHolder);
        //        Glide.with(mContext).asBitmap().load(path).apply(requestOptions).into(imageView);
        CornerTransform transform = new CornerTransform(mContext, GetUIDimens.dpToPx(mContext, 5));
        transform.setNeedCorner(leftTop, rightTop, leftBottom, rightBottom);
        RequestOptions requestOptions = new RequestOptions().placeholder(placeHolder)
                .transform(new CenterCrop(), transform);
        Glide.with(mContext).asBitmap().load(path).apply(requestOptions).into(imageView);
    }


    @SuppressLint("CheckResult")
    public static void loadCornerdImg(Context mContext, Uri path, Integer placeHolder,
                                      AppCompatImageView imageView, boolean leftTop, boolean leftBottom,
                                      boolean rightTop, boolean rightBottom) {
        CornerTransform transform = new CornerTransform(mContext, GetUIDimens.dpToPx(mContext, 5));
        transform.setNeedCorner(true, true, true, true);
        RequestOptions requestOptions = new RequestOptions().placeholder(placeHolder)
                .transform(new CenterCrop(), transform);
        Glide.with(mContext).asBitmap().load(path).apply(requestOptions).into(imageView);
    }

}
