package com.theuhd.popularmovies;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Zachary on 11/5/2016.
 *
 * custom imageview for purposes of stretching and scaling poster images into grid view
 */

public class CustomImageView extends ImageView {
    public CustomImageView(Context context) {
        super(context);
    }
    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }
}
