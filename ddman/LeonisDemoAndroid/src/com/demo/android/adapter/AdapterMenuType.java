package com.demo.android.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.demo.android.R;

public class AdapterMenuType extends BaseAdapter{
	public ArrayList<String> mList;
	private Context mContext;
	private ItemMenu itemMenu;
	public PopupWindow popupWindow;

	public AdapterMenuType(ArrayList<String> pList, Context pContext) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			view = inflater.inflate(R.layout.layout_item_menu, parent, false);
			itemMenu = new ItemMenu();
			itemMenu.title = (TextView) view.findViewById(R.id.tv_menu);
			view.setTag(itemMenu);

		} else {
			itemMenu = (ItemMenu) view.getTag();
		}
		if (mList.get(position) == null) {
			itemMenu.title.setText("消し込みタイプ選択");
			view.setAlpha(0.3f);
		} else {
			itemMenu.title.setText(mList.get(position));
		}
		return view;
	}

	private static class ItemMenu {
		TextView title;

	}
}
