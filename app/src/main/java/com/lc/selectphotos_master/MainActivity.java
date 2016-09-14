package com.lc.selectphotos_master;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;


import com.lc.multiselectphoto.adapter.ImageGridAdapter;
import com.lc.multiselectphoto.constant.CacheConstant;
import com.lc.multiselectphoto.constant.Constant;
import com.lc.multiselectphoto.fragment.ImageGridFragment;
import com.lc.multiselectphoto.fragment.ImagePagerFragment;
import com.lc.multiselectphoto.model.ImageBean;
import com.lc.multiselectphoto.utils.ChoseImageListener;
import com.lc.multiselectphoto.utils.DisplayUtils;
import com.lc.multiselectphoto.utils.FragmentsPopListener;
import com.lc.multiselectphoto.widget.SelectPicPopupWindow;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiscCache;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ChoseImageListener, FragmentsPopListener {

    private SelectPicPopupWindow menuWindow;
    private GridView gridView;
    private FrameLayout fl_content;
    private RelativeLayout rl_add;
    private RelativeLayout rl_main;
    private ImagePagerFragment fragment;

    private FeedBackImageAdapter mAdaper = null;
    private DisplayImageOptions options = null;

    private ArrayList<String> images;
    private static final int REQUEST_PICK_PHOTO = 1;
    private static final int REQUEST_CAPTURE_PHOTO = 2;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridView);
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        rl_add = (RelativeLayout) findViewById(R.id.rl_add);
        if (savedInstanceState != null) {
            mCurrentPhotoPath = savedInstanceState.getString("filelocation");
            images = savedInstanceState.getStringArrayList("list");
        }
        images = new ArrayList<>();
        initImageLoader();
        int numColumns = (getResources().getDisplayMetrics().widthPixels - DisplayUtils.dip2px(12, this)) / DisplayUtils.dip2px(116, this);
        gridView.setNumColumns(numColumns);
        mAdaper = new FeedBackImageAdapter(MainActivity.this);
        gridView.setAdapter(mAdaper);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectImgs();
                } else {
                    openImagePager(false, position-1);
                }

            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString("filelocation", mCurrentPhotoPath);
        outState.putStringArrayList("list", images);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mCurrentPhotoPath = savedInstanceState.getString("filelocation");
        images = savedInstanceState.getStringArrayList("list");
        super.onRestoreInstanceState(savedInstanceState);
    }


    public void selectImgs() {
        //((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        menuWindow = new SelectPicPopupWindow(MainActivity.this, itemsOnClick);
        //设置弹窗位置
        menuWindow.showAtLocation(MainActivity.this.findViewById(R.id.rl_main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_PICK_PHOTO:
                if (images != null) {
                    images.clear();
                }
                images = data.getStringArrayListExtra(Constant.EXTRA_PHOTO_PATHS);
                Log.e("capture", "onActivityResult: " + images.get(0));
                mAdaper.swapDatas(images);
                break;

            case REQUEST_CAPTURE_PHOTO:
                if (images != null) {
                    images.add(mCurrentPhotoPath);
                    Log.e("capture", "onActivityResult: " + mCurrentPhotoPath);
                    galleryAddPic();
                }
                Log.e("capture", "onActivityResult: " + mCurrentPhotoPath);
                mAdaper.swapDatas(images);

                break;
        }
    }

    private void initImageLoader() {
        if (!ImageLoader.getInstance().isInited()) {
            DisplayImageOptions.Builder displayBuilder = new DisplayImageOptions.Builder();
            displayBuilder.cacheInMemory(true);
            displayBuilder.cacheOnDisk(true);
            displayBuilder.showImageOnLoading(R.drawable.default_photo);
            displayBuilder.showImageForEmptyUri(R.drawable.default_photo);
            displayBuilder.considerExifParams(true);
            displayBuilder.bitmapConfig(Bitmap.Config.RGB_565);
            displayBuilder.imageScaleType(ImageScaleType.EXACTLY);
            displayBuilder.displayer(new FadeInBitmapDisplayer(300));
            options = displayBuilder.build();

            ImageLoaderConfiguration.Builder loaderBuilder = new ImageLoaderConfiguration.Builder(getApplication());
            loaderBuilder.defaultDisplayImageOptions(displayBuilder.build());
            loaderBuilder.memoryCacheSize(getMemoryCacheSize());

            try {
                File cacheDir = new File(getExternalCacheDir() + File.separator + CacheConstant.IMAGE_CACHE_DIRECTORY);
                loaderBuilder.diskCache(new LruDiscCache(cacheDir, DefaultConfigurationFactory.createFileNameGenerator(), 500 * 1024 * 1024));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageLoader.getInstance().init(loaderBuilder.build());
        }

    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.item_popupwindows_camera:        //点击拍照按钮
                    goCamera();
                    break;
                case R.id.item_popupwindows_Photo:       //点击从相册中选择按钮
                    addPhotos();
                    break;
                default:
                    break;
            }
        }

    };

    private void goCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        startActivityForResult(intent, REQUEST_CAPTURE_PHOTO);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        if (!storageDir.exists()) {
            if (!storageDir.mkdir()) {
                Log.e("TAG", "Throwing Errors....");
                throw new IOException();
            }
        }
        File image = new File(storageDir, imageFileName);
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e("tag", "createImageFile: " + mCurrentPhotoPath);
        return image;
    }


   private void addPhotos() {
        Intent intent = new Intent(Constant.GalleryActivityFlag);
        //指定图片选择数
        intent.putExtra(Constant.EXTRA_PHOTO_LIMIT, 5);
        intent.putExtra(Constant.EXTRA_PHOTO_SELECTED, mAdaper.getCount()-1);
        if (images != null && images.size() > 0) {
            intent.putStringArrayListExtra(Constant.EXTRA_PHOTO_PATHS, images);
        }

        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }


    private int getMemoryCacheSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        // 4 bytes per pixel
        return screenWidth * screenHeight * 4 * 3;
    }

    private void openImagePager(boolean all, int position) {

        fragment = ImagePagerFragment.newInstance(options);
        fragment.setChoseImageListener(this);
        Bundle args = new Bundle();
        args.putBoolean("all", false);
        args.putParcelableArrayList("datas", getSelectedImages());
        args.putInt("position", position);
        fragment.setArguments(args);
        fragment.setFragmentsPop(this);
        //getSupportFragmentManager().addOnBackStackChangedListener(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.replace(R.id.fl_content, fragment);
        ft.addToBackStack(null);

        ft.commit();
        swapDatas(getSelectedImages());
        rl_add.setVisibility(View.GONE);
    }

    public ArrayList<ImageBean> getSelectedImages() {
        ArrayList<ImageBean> selectedImages = new ArrayList<ImageBean>();
        if (images == null) {
            return selectedImages;
        }
        for (String image : images) {

            selectedImages.add(new ImageBean(image, true));

        }
        return selectedImages;
    }

    @Override
    public boolean onSelected(ImageBean image) {
        return false;
    }

    @Override
    public boolean onCancelSelect(ImageBean image) {
        images.remove(image.getPath());
        mAdaper.swapData(image.getPath());
        return true;
    }

    private void swapDatas(ArrayList<ImageBean> arg1) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_content);
        if (fragment instanceof ImagePagerFragment) {
            ((ImagePagerFragment) fragment).swapDatas(arg1);
        } else if (fragment instanceof ImageGridFragment) {
            ((ImageGridFragment) fragment).swapDatas(arg1);
        }
    }


    @Override
    public void popfragments() {
        rl_add.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fragment != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK && fragment.isVisible()) { //监控/拦截/屏蔽返回键
                rl_add.setVisibility(View.VISIBLE);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        if (TextUtils.isEmpty(mCurrentPhotoPath)) {
            return;
        }

        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}
