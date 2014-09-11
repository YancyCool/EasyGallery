package com.yancy.easygallery.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageView extends ImageView{

	public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SquareImageView(Context context) {
		super(context);
	}
	
	 @Override
	    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
	    {
	        final int width = getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
	        setMeasuredDimension(width, width);
	    }
	
}
