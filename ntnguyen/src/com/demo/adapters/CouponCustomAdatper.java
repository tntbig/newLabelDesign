package com.demo.adapters;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import co.leonisand.leonis.Image.ImageListener;
import co.leonisand.offers.OffersCoupon;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersTemplate;

import com.demo.offers.MainActivity;
import com.demo.offers.R;
import com.demo.utils.Utils;

public class CouponCustomAdatper extends BaseAdapter {
	
	public ArrayList<OffersCoupon> mList;
	private Context mContext;
	private ItemCoupon itemCoupon;
	private MainActivity mActivity;

	public CouponCustomAdatper(ArrayList<OffersCoupon> pList, Context pContext,
			MainActivity pActivity) {
		// TODO Auto-generated constructor stub
		mList = pList;
		mContext = pContext;
		mActivity = pActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, final View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			view = inflater.inflate(R.layout.custom_coupons_row, parent, false);
			itemCoupon = new ItemCoupon();
			itemCoupon.icon = (ImageView) view.findViewById(R.id.img_Coupon);
			itemCoupon.used =(ImageView) view.findViewById(R.id.statusImage);
			itemCoupon.title = (TextView) view.findViewById(R.id.title_Coupon);
			itemCoupon.description = (TextView) view.findViewById(R.id.content_Coupon);
			itemCoupon.use = (TextView) view.findViewById(R.id.time_count_Coupon);
			itemCoupon.quality = (TextView) view.findViewById(R.id.quality_count_Coupon);
			itemCoupon.group = (TextView) view.findViewById(R.id.group_Coupon);
			view.setTag(itemCoupon);
			

		} else {
			itemCoupon = (ItemCoupon) view.getTag();
		}
		OffersCoupon offerscoupon = (OffersCoupon) mList.get(position);
		itemCoupon.title.setText(offerscoupon.getTitle());
		itemCoupon.use.setText(com.demo.utils.Utils.getExpireDate(new Date(), offerscoupon));
		if (offerscoupon.getQuantity() > 0) {
			itemCoupon.quality.setText("" + offerscoupon.getQuantity());
		}else{
			itemCoupon.quality.setText("∞ 枚");
		}
		itemCoupon.description.setText(offerscoupon.getDescription());
		JSONObject json;
		try {
			JSONArray jsonObj = new JSONArray(offerscoupon.getGroups());
			json = jsonObj.getJSONObject(0);
			itemCoupon.group.setText(json.getString("name"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(offerscoupon.getRead()){
			itemCoupon.used.setVisibility(View.GONE);
		}else{
			itemCoupon.used.setVisibility(View.VISIBLE);
		}
		offerscoupon.thumbnailImageBitmap(itemCoupon.icon, imageListener);
		offerscoupon.template(new OffersListener() {
			public void onDone(Map<String, Object> map) {
				if (map.get("template") != null) {
					@SuppressWarnings("unchecked")
					Map<String, Object> mapTemp = (Map<String, Object>) ((OffersTemplate) map
							.get("template")).getValues().get("background");
					// convertView.setBackgroundColor(Color
					// .parseColor((String) mapTemp.get("color")));
				}
			}

			public void onFail(Integer s) {
//				mActivity.alert("onFail", s.toString());
			}
		});
		return view;
	}

	ImageListener imageListener = new ImageListener() {
		public void onDone(View view, Bitmap bitmap) {
			((ImageView) view).setImageBitmap(Utils.getRoundedCornerBitmap(bitmap, 5));

		}
	};

	private static class ItemCoupon {
		TextView description, title, use, quality, group;
		ImageView icon, used;

	}

}
