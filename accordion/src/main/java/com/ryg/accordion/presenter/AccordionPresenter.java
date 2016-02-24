package com.ryg.accordion.presenter;

import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Author: renyiguang on 16/2/20 17:35.
 * Email: yiguangren1988@gmail.com
 */
public interface AccordionPresenter {

    /***
     * 计算滑动范围
     *
     * @return 水平方向的滑动范围
     */
    int getScrollRange();

    /**
     * index:0,1,2...
     *
     * @return
     */

    int getFirstVisibleItemIndex();

    int getLastVisibleItemIndex();

}
