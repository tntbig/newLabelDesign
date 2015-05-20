package com.demo.android.util;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import co.leonisand.offers.OffersCoupon;

public class Utils {
	@SuppressLint("SimpleDateFormat")
	public static String getExpireDate(Date now, OffersCoupon offerscoupon) {

		String expireDate = null;

		// サーバー時間が取れない
		if (now == null) {

		} else {
			Date toDate = null;
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
				toDate = sdf.parse(offerscoupon.getAvailableTo());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// 利用可能期間切れ
			if (toDate.getTime() < now.getTime()) {
				// messageTextView.setVisibility(View.VISIBLE);
				// messageTextView.setText("ご利用可能期間が終了しました");
				
			} else {

				int diffSec = (int) Math.floor((toDate.getTime() - now
						.getTime()) / 1000);
				String timerString = "";
				if (toDate.getTime() < now.getTime()) {

				} else if (diffSec < 3600) {
					timerString = String
							.valueOf((int) Math.floor(diffSec / 60)) + "分";
				} else {
					int d = (int) Math.floor(diffSec / (60 * 60 * 24));
					int m = (int) Math.floor((diffSec - (d * (60 * 60 * 24)))
							/ (60 * 60));
					if (d == 0) {
						timerString = String.valueOf(m) + "時間";
					} else if (d > 90) {
						timerString = "3ヶ月以上";
					} else {
						if (m == 0) {
							timerString = String.valueOf(d) + "日";
						} else {
							timerString = String.valueOf(d) + "日"
									+ String.valueOf(m) + "時間";
						}
					}
				}

				expireDate = timerString;

			}
		}
		return expireDate;
	}
	public static String getDeliveryDay(Date now, OffersCoupon offerscoupon) {

		String expireDate = null;

		// サーバー時間が取れない
		if (now == null) {

		} else {
			Date toDate = null;
			try { 
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
				toDate = sdf.parse(offerscoupon.getDeliveryTo());
			} catch (ParseException e) {
				e.printStackTrace(); 
			} 
			// 利用可能期間切れ
			if (toDate.getTime() < now.getTime()) { 
				return null; 
			}else {
				expireDate = "OK";
			}
		}
		return expireDate;
	}
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		if(bitmap == null) return null;
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true); 
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
	
}
