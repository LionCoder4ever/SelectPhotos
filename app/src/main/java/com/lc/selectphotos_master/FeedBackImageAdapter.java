package com.lc.selectphotos_master;

/**
 * Created by admin on 2016/9/14.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lc.multiselectphoto.constant.CacheConstant;
import com.lc.multiselectphoto.utils.DisplayUtils;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class FeedBackImageAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> mDatas = null;
    private DisplayImageOptions options;
    private Bitmap bitmap;
    private static final String addImage = R.drawable.addimg+"";

    public FeedBackImageAdapter(Context c) {
        this.context = c;
        inflater = LayoutInflater.from(c);
        initImageLoader();
        swapDatas(new ArrayList<String>());
    }

    private void initImageLoader() {
        if (!ImageLoader.getInstance().isInited()) {
            DisplayImageOptions.Builder displayBuilder = new DisplayImageOptions.Builder();
            displayBuilder.cacheInMemory(true);
            displayBuilder.cacheOnDisc(true);
            displayBuilder.showImageOnLoading(R.drawable.default_photo);
            displayBuilder.showImageForEmptyUri(R.drawable.default_photo);
            displayBuilder.considerExifParams(true);
            displayBuilder.bitmapConfig(Bitmap.Config.RGB_565);
            displayBuilder.imageScaleType(ImageScaleType.EXACTLY);
            // displayBuilder.displayer(new FadeInBitmapDisplayer(300));
            options = displayBuilder.build();

            ImageLoaderConfiguration.Builder loaderBuilder = new ImageLoaderConfiguration.Builder(
                    context);
            loaderBuilder.defaultDisplayImageOptions(displayBuilder.build());
            loaderBuilder.memoryCacheSize(getMemoryCacheSize());

            File cacheDir = new File(context.getExternalCacheDir()
                    + File.separator + CacheConstant.IMAGE_CACHE_DIRECTORY);
            loaderBuilder.diskCache(new LimitedAgeDiscCache(cacheDir,
                    500 * 1024 * 1024));
            ImageLoader.getInstance().init(loaderBuilder.build());
        }

    }

    private int getMemoryCacheSize() {
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        // 4 bytes per pixel
        return screenWidth * screenHeight * 4 * 3;
    }

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        } else {
            return mDatas.size();
        }
    }

    @Override
    public String getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void swapDatas(ArrayList<String> images) {

        if(images==null){
            return;
        }

        if (this.mDatas == null) {
            this.mDatas = images;
        } else {
            this.mDatas.addAll(images);
        }

        ArrayList<String> result = new ArrayList<String>();
        result.add(addImage);
        for(String s: mDatas){
            if(Collections.frequency(result, s) < 1) result.add(s);
        }
        mDatas = result;

        notifyDataSetChanged();
    }

    public void swapData(String imagepath) {
        if (this.mDatas == null) {
            return;
        }
        if (this.mDatas.size() > 0) {
            this.mDatas.remove(imagepath);
        }
        notifyDataSetChanged();
    }

    public void addData(ArrayList<String> data) {
        if (data == null) {
            return;
        }
        if (mDatas == null) {
            mDatas = new ArrayList<String>();
        }
        mDatas.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ImageView(context);
            ((ImageView) convertView)
                    .setScaleType(ImageView.ScaleType.CENTER_CROP);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    DisplayUtils.dip2px(110, context), DisplayUtils.dip2px(
                    110, context));
            convertView.setLayoutParams(lp);
        }

        if (position == 0) {
            ImageLoader.getInstance().displayImage(
                    "drawable://" + mDatas.get(position),
                    (ImageView) convertView);
        } else {
            ImageLoader.getInstance().displayImage(
                    "file://" + mDatas.get(position), (ImageView) convertView);
        }

		/*
		 * if (position == 0) { ImageLoader.getInstance().displayImage(
		 * "drawable://" + mDatas.get(0), (ImageView) convertView, options); }
		 * else { ImageLoader.getInstance().displayImage( "file://" +
		 * mDatas.get(position), (ImageView) convertView, options); }
		 */

        return convertView;
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

}

