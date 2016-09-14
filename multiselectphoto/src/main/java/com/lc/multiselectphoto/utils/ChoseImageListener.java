/**
 *
 */
package com.lc.multiselectphoto.utils;


import com.lc.multiselectphoto.model.ImageBean;

public interface ChoseImageListener {

    boolean onSelected(ImageBean image);

    boolean onCancelSelect(ImageBean image);
}
