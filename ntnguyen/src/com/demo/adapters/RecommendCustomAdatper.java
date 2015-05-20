package com.demo.adapters;

import java.util.ArrayList;

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
import co.leonisand.offers.OffersRecommendation;

import com.demo.offers.MainActivity;
import com.demo.offers.R;
import com.demo.utils.Utils;

public class RecommendCustomAdatper extends BaseAdapter {
	
	public ArrayList<OffersRecommendation> mList;
	private Context mContext;
	private ItemRecommendation itemRecommendation;

	public RecommendCustomAdatper(ArrayList<OffersRecommendation> pList, Context pContext,
			MainActivity pActivity) {
		// TODO Auto-generated constructor stub
		mList = pList;
		mContext = pContext;
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
			view = inflater.inflate(R.layout.custom_recommendations_row, parent, false);
			itemRecommendation = new ItemRecommendation();
			itemRecommendation.icon = (ImageView) view.findViewById(R.id.img_recommendation);
			itemRecommendation.title = (TextView) view.findViewById(R.id.title_recommendation);
			itemRecommendation.description = (TextView) view.findViewById(R.id.content_recommendation);
			view.setTag(itemRecommendation);

		} else {
			itemRecommendation = (ItemRecommendation) view.getTag();
		}
		
		OffersRecommendation offersrecommendation = (OffersRecommendation) mList.get(position);
		itemRecommendation.title.setText(offersrecommendation.getName());
		itemRecommendation.description.setText(offersrecommendation.getDescription());
		offersrecommendation.imageBitmap(itemRecommendation.icon, imageListener);
		return view;
	}

	ImageListener imageListener = new ImageListener() {
		public void onDone(View view, Bitmap bitmap) {
			((ImageView) view).setImageBitmap(Utils.getRoundedCornerBitmap(bitmap, 5));
		}
	};

	private static class ItemRecommendation {
		TextView description, title;
		ImageView icon;

	}

}
