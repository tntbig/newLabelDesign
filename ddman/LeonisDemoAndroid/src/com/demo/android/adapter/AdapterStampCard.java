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
import co.leonisand.offers.OffersStampCard;

import com.demo.android.MainActivity;
import com.demo.android.R;

public class AdapterStampCard extends BaseAdapter{

	public ArrayList<OffersStampCard> mList;
	private Context mContext;
	private ItemStampCard itemStampCard;
	private MainActivity mActivity;

	public AdapterStampCard(ArrayList<OffersStampCard> pList, Context pContext,
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
			view = inflater.inflate(R.layout.layout_item_stampcard, parent, false);
			itemStampCard = new ItemStampCard();
			itemStampCard.icon = (ImageView) view
					.findViewById(R.id.im_image);
			itemStampCard.title = (TextView) view.findViewById(R.id.tv_title);
			view.setTag(itemStampCard);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT);
			view.setLayoutParams(new GridView.LayoutParams(params));
			

		} else {
			itemStampCard = (ItemStampCard) view.getTag();
		}
		
		
		OffersStampCard offersstampcard = (OffersStampCard) mList.get(position);
		itemStampCard.title.setText(offersstampcard.getTitle());
		offersstampcard.imageBitmap(itemStampCard.icon, imageListener);

		return view ;
	}
	ImageListener imageListener = new ImageListener() {
		public void onDone(View view, Bitmap bitmap) {
			((ImageView) view).setImageBitmap(bitmap);
			
		}
	};

	private static class ItemStampCard {
		TextView title;
		ImageView icon;

	}
}
