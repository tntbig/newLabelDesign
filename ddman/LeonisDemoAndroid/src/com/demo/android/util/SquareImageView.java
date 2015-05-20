package com.demo.android.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageView extends ImageView {
	public SquareImageView(Context context) {
		super(context);
	}

	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final Drawable d = this.getDrawable();

		if (d != null) {
			// ceil not round - avoid thin vertical gaps along the left/right
			// edges
			final int width = MeasureSpec.getSize(widthMeasureSpec);
			final int height = (int) Math.ceil(width
					* (float) d.getIntrinsicHeight() / d.getIntrinsicWidth());
			if (height > 0) {
				this.setMeasuredDimension(width, height);
			}else {
				this.setMeasuredDimension(width, 300);
			}
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

	}
}
