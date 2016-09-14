package com.ns.mutiphotochoser.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.ns.mutiphotochoser.R;
import com.ns.mutiphotochoser.adapter.ImageGridAdapter;
import com.ns.mutiphotochoser.model.ImageBean;
import com.ns.mutiphotochoser.utils.ChoseImageListener;
import com.ns.mutiphotochoser.utils.DisplayUtils;
import com.ns.mutiphotochoser.widget.HeaderGridView;

import java.util.ArrayList;

/**
 * @author xiaolf1
 */
public class ImageGridFragment extends Fragment implements OnItemClickListener {

    private HeaderGridView imageGridView = null;
    private ImageGridAdapter mAdapter = null;
    private DisplayImageOptions options = null;
    private ArrayList<ImageBean> mImages = null;
    private ViewImageListener mViewImageListener = null;

    //获取gridview实例
    public static ImageGridFragment newInstance(DisplayImageOptions options) {
        ImageGridFragment fragment = new ImageGridFragment();
        fragment.setImageLoader(options);
        return fragment;
    }

    private void setImageLoader(DisplayImageOptions options) {
        this.options = options;
    }


//    galleryactivity会在进行传值 再传进adapter里面
    public void swapDatas(ArrayList<ImageBean> images) {
        if (this.mImages != null) {
            this.mImages.clear();
            this.mImages = null;
        }
        this.mImages = images;
        if (mAdapter != null) {
            mAdapter.swapDatas(mImages);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_image_grid_layout, null);
        imageGridView = (HeaderGridView) contentView.findViewById(R.id.gridGallery);
        int numColumns = (getResources().getDisplayMetrics().widthPixels - DisplayUtils.dip2px(6, getActivity())) / DisplayUtils.dip2px(116, getActivity());
        imageGridView.setNumColumns(numColumns);
        imageGridView.setVerticalScrollBarEnabled(false);

        View footerView = new View(getActivity());
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, DisplayUtils.dip2px(82, getActivity()));
        footerView.setLayoutParams(lp);
        imageGridView.addFooterView(footerView, null, false);

        mAdapter = new ImageGridAdapter(getActivity(), options);

        //设置监听，操作图片选择的同时进行字数增减
        mAdapter.setChoseImageListener(mViewImageListener);
        //看似创建时没啥用 当点击图片进入大图模式后回到当前fragment后需要重新 给adapter赋值（复习以下fragment的生命周期）
        mAdapter.swapDatas(mImages);
        PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(), true, true);
        imageGridView.setOnScrollListener(listener);
        imageGridView.setOnItemClickListener(this);
        imageGridView.setAdapter(mAdapter);
        return contentView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mViewImageListener != null) {
            //进入预览模式，需要参数postion
            mViewImageListener.viewImage(position);
        }
    }

    public void setViewImageListener(ViewImageListener listener) {
        this.mViewImageListener = listener;
    }

    public interface ViewImageListener extends ChoseImageListener {
        void viewImage(int position);
    }
}
