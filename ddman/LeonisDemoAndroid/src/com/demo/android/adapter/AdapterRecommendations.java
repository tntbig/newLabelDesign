package com.demo.android.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.leonisand.leonis.Image.ImageListener;
import co.leonisand.offers.OffersRecommendation;

import com.demo.android.MainActivity;
import com.demo.android.R;
import com.demo.android.util.Utils;

public class AdapterRecommendations extends BaseAdapter{
	
	
	public ArrayList<OffersRecommendation> mList;
	private Context mContext;
	private ItemRecommendation itemRecommendation;
	private MainActivity mActivity;
	
	public AdapterRecommendations(ArrayList<OffersRecommendation> pList, Context pContext,
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			view = inflater.inflate(R.layout.layout_item_recommend, parent, false);
			itemRecommendation = new ItemRecommendation();
			itemRecommendation.icon = (ImageView) view
					.findViewById(R.id.im_image);
			itemRecommendation.title = (TextView) view.findViewById(R.id.tv_title);
			itemRecommendation.description = (TextView) view
					.findViewById(R.id.tv_description);
			view.setTag(itemRecommendation);
			@SuppressWarnings("deprecation")
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT);
			view.setLayoutParams(new GridView.LayoutParams(params));
			

		} else {
			itemRecommendation = (ItemRecommendation) view.getTag();
		}
		
		OffersRecommendation offersrecommend = (OffersRecommendation) mList.get(position);
		itemRecommendation.title.setText(offersrecommend.getName());
		itemRecommendation.description.setText(offersrecommend.getDescription());
		offersrecommend.thumbnailImageBitmap(itemRecommendation.icon, imageListener);
		

		return view;
	}
	ImageListener imageListener = new ImageListener() {
		public void onDone(View view, Bitmap bitmap) {
			((ImageView) view).setImageBitmap(Utils.getRoundedCornerBitmap(bitmap, 10));
			
		}
	};

	private static class ItemRecommendation {
		TextView description, title;
		ImageView icon;

	}
}
