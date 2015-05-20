package com.demo.android.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import co.leonisand.cupido.CupidoNotification;

import com.demo.android.MainActivity;
import com.demo.android.R;

@SuppressLint("ResourceAsColor")
public class AdapterNotification extends BaseAdapter {

	public ArrayList<CupidoNotification> mList;
	private Context mContext;
	private iTemNotification itemNotify;
	private MainActivity mActivity;

	public AdapterNotification(ArrayList<CupidoNotification> pList,
			Context pContext, MainActivity pActivity) {
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
			LayoutInflater inflater = mActivity.getLayoutInflater();
			view = inflater.inflate(R.layout.layout_item_notification, parent,
					false);
			itemNotify = new iTemNotification();
			itemNotify.ItemName = (TextView) view.findViewById(R.id.tv_title);
			itemNotify.ItemRelative = (RelativeLayout) view.findViewById(R.id.rlt_notification_item);
			view.setTag(itemNotify);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT);
			view.setLayoutParams(new GridView.LayoutParams(params));

		} else {
			itemNotify = (iTemNotification) view.getTag();
		}
 
		itemNotify.ItemName.setText(mList.get(position).getTitle());
		if(mList.get(position).isUnread()){
			itemNotify.ItemRelative.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		}else {
			itemNotify.ItemRelative.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nice_selector));
		}
		return view;
	}

	private static class iTemNotification {
		TextView ItemName;
		RelativeLayout ItemRelative ;

	}

}
