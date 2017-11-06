package com.test.HowlFirebaseAuth.Utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by admin on 2017/10/13.
 */

public class HScroll extends HorizontalScrollView{

    public HScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HScroll(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }


}
