package com.lc.multiselectphoto.widget;

/**
 * Created by admin on 2016/9/13.
 * <p>
 * Created by lc on 16/9/9.
 */

/**
 * Created by lc on 16/9/9.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.lc.multiselectphoto.R;


public class SelectPicPopupWindow extends PopupWindow {

    private Button item_popupwindows_camera,    //take photo
            item_popupwindows_Photo,            //into the gallery
            item_popupwindows_cancel;
    private View menuview;

    /**
     *
     * @param context
     * @param itemsOnclick
     */
    public SelectPicPopupWindow(Activity context, View.OnClickListener itemsOnclick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        menuview = inflater.inflate(R.layout.item_popupwindows, null);
        item_popupwindows_camera = (Button) menuview.findViewById(R.id.item_popupwindows_camera);
        item_popupwindows_cancel = (Button) menuview.findViewById(R.id.item_popupwindows_cancel);
        item_popupwindows_Photo = (Button) menuview.findViewById(R.id.item_popupwindows_Photo);

        /**
         *
         */
        item_popupwindows_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });
        item_popupwindows_camera.setOnClickListener(itemsOnclick);
        item_popupwindows_Photo.setOnClickListener(itemsOnclick);
        this.setContentView(menuview);
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
        menuview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int height = menuview.findViewById(R.id.ll_popup).getTop();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}

