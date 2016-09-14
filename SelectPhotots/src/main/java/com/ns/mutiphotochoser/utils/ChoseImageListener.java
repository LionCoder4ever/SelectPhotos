/**
 *
 */
package com.ns.mutiphotochoser.utils;


import com.ns.mutiphotochoser.model.ImageBean;

/**
 * @author xiaolf1
 */
public interface ChoseImageListener {

    boolean onSelected(ImageBean image);

    boolean onCancelSelect(ImageBean image);
}
