package com.ryg.accordion.presenter;

import android.view.MotionEvent;
import android.view.View;

import com.ryg.accordion.AccordionLayout;
import com.ryg.accordion.R;

/**
 * Author: renyiguang on 16/2/20 17:36.
 * Email: yiguangren1988@gmail.com
 */
public class AccordionPresenterImpl implements AccordionPresenter {

    private AccordionLayout viewGroup;

    public AccordionPresenterImpl(AccordionLayout viewGroup) {
        this.viewGroup = viewGroup;
    }


    @Override
    public int getScrollRange() {

        int scrollRange = 0;
        int childCount = viewGroup.getChildCount();
        if(childCount>0){

            int childCountWidth = 0;

            for(int i=0;i<childCount;i++){

                View view = viewGroup.getChildAt(i);
                float shadowPresent = (float)view.getTag(R.string.tag_item_shadow);

                childCountWidth += view.getMeasuredWidth()*(100 - shadowPresent)/100;
            }

            scrollRange = Math.max(0,childCountWidth -(viewGroup.getMeasuredWidth()-viewGroup.getPaddingLeft()-viewGroup.getPaddingRight()));
        }

        return scrollRange;


    }

    @Override
    public int getFirstVisibleItemIndex() {

        int index = 0;

        final int childCount = viewGroup.getChildCount();
        if(childCount>0){
            int childWidth = viewGroup.getChildAt(0).getMeasuredWidth();

            int scrollX = viewGroup.getScrollX();//scrollX>0

            return (int)((scrollX)/(childWidth*(100-viewGroup.getItemShadow())/100));

        }

        return index;
    }

    @Override
    public int getLastVisibleItemIndex() {

        int index = 0;

        final int childCount = viewGroup.getChildCount();
        if(childCount>0){
            int childWidth = viewGroup.getChildAt(0).getMeasuredWidth();

            int scrollX = viewGroup.getScrollX();//scrollX>0

            return (int)((scrollX+viewGroup.getMeasuredWidth())/(childWidth*(100-viewGroup.getItemShadow())/100));

        }

        return index;
    }

}
