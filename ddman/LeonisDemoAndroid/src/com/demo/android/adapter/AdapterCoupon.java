package com.demo.android.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.leonisand.leonis.Image.ImageListener;
import co.leonisand.offers.OffersCoupon;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersTemplate;

import com.demo.android.MainActivity;
import com.demo.android.R;
import com.demo.android.util.Utils;

@SuppressWarnings("deprecation")
public class AdapterCoupon extends BaseAdapter {
	public ArrayList<OffersCoupon> mList;
	private Context mContext;
	private ItemCoupon itemCoupon;
	private MainActivity mActivity;

	public AdapterCoupon(ArrayList<OffersCoupon> pList, Context pContext,
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
			view = inflater.inflate(R.layout.layout_item_coupon, parent, false);
			itemCoupon = new ItemCoupon();
			itemCoupon.icon = (ImageView) view.findViewById(R.id.im_image);
			itemCoupon.title = (TextView) view.findViewById(R.id.tv_title);
			itemCoupon.description = (TextView) view
					.findViewById(R.id.tv_description);
			itemCoupon.use = (TextView) view.findViewById(R.id.tv_use);
			itemCoupon.quality = (TextView) view.findViewById(R.id.tv_quality);
			itemCoupon.group = (TextView) view.findViewById(R.id.tv_group);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT);
			view.setLayoutParams(new GridView.LayoutParams(params));
			view.setTag(itemCoupon);

		} else {
			itemCoupon = (ItemCoupon) view.getTag();
		}

		OffersCoupon offerscoupon = (OffersCoupon) mList.get(position);
		itemCoupon.title.setText(offerscoupon.getTitle());
		itemCoupon.use.setText(Utils.getExpireDate(new Date(), offerscoupon));
		if (offerscoupon.getQuantity() > 0) {
			itemCoupon.quality.setText("" + offerscoupon.getQuantity());
		}
		itemCoupon.description.setText(offerscoupon.getDescription());
		JSONObject json;
		try {
			JSONArray jsonObj = new JSONArray(offerscoupon.getGroups());
			json = jsonObj.getJSONObject(0);
			if (jsonObj.length() > 1) {
				itemCoupon.group.setText(json.getString("name") + "...");
			}else {
				itemCoupon.group.setText(json.getString("name"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				mActivity.alert("onFail", s.toString());
			}
		});
		return view;
	}

	ImageListener imageListener = new ImageListener() {
		public void onDone(View view, Bitmap bitmap) {
			((ImageView) view).setImageBitmap(Utils.getRoundedCornerBitmap(
					bitmap, 10));

		}
	};

	private static class ItemCoupon {
		TextView description, title, use, quality, group;
		ImageView icon;

	}

}
