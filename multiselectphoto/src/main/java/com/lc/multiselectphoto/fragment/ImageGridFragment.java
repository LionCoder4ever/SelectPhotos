package com.lc.multiselectphoto.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.lc.multiselectphoto.R;
import com.lc.multiselectphoto.adapter.ImageGridAdapter;
import com.lc.multiselectphoto.model.ImageBean;
import com.lc.multiselectphoto.utils.ChoseImageListener;
import com.lc.multiselectphoto.utils.DisplayUtils;
import com.lc.multiselectphoto.widget.HeaderGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;


public class ImageGridFragment extends Fragment implements OnItemClickListener {

    private HeaderGridView imageGridView = null;
    private ImageGridAdapter mAdapter = null;
    private DisplayImageOptions options = null;
    private ArrayList<ImageBean> mImages = null;
    private ViewImageListener mViewImageListener = null;

    //single instance
    public static ImageGridFragment newInstance(DisplayImageOptions options) {
        ImageGridFragment fragment = new ImageGridFragment();
        fragment.setImageLoader(options);
        return fragment;
    }

    private void setImageLoader(DisplayImageOptions options) {
        this.options = options;
    }


//    get selected list from gallery activity ,  pass into the gridFragment to show which has benn selected
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

        //set listener so you can change the number count text when you select the photo
        mAdapter.setChoseImageListener(mViewImageListener);

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
