package com.akalizakeza.apps.ishusho.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by kiranpv on 05/01/16.
 * Description:
 *  # Making a CustomViewPager so as to modify the swipe feature
 *      (paging) of the view pager according to the requirements.
 *      --reference: https://www.shiftedup.com/2011/08/29/disabling-pagingswiping-on-android
 */
public class TabViewPager extends ViewPager {

    private boolean enabled;

    public TabViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled  = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return enabled ?  super.onTouchEvent(event) : false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return enabled ? super.onInterceptTouchEvent(event) : false;
    }

    /*
    // To permanently disable the scroll of view-pager.
        @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if(v instanceof MapView){
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }
     */

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isPagingEnabled() {
        return enabled;
    }
}
