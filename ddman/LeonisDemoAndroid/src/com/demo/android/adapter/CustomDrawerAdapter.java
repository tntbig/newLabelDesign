package com.demo.android.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demo.android.DrawerItem;
import com.demo.android.MainActivity;
import com.demo.android.R;

public class CustomDrawerAdapter extends ArrayAdapter<DrawerItem> {

	Context context;
	List<DrawerItem> drawerItemList;
	int layoutResID;

	public CustomDrawerAdapter(Context context, int layoutResourceID,
			List<DrawerItem> listItems) {
		super(context, layoutResourceID, listItems);
		this.context = context;
		this.drawerItemList = listItems;
		this.layoutResID = layoutResourceID;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		DrawerItemHolder drawerHolder;
		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			drawerHolder = new DrawerItemHolder();

			view = inflater.inflate(layoutResID, parent, false);
			drawerHolder.ItemName = (TextView) view.findViewById(R.id.tv_title);
			drawerHolder.user = (TextView) view.findViewById(R.id.tv_user_id);
			drawerHolder.title = (TextView) view
					.findViewById(R.id.tv_title_parent);

			drawerHolder.userLayout = (RelativeLayout) view
					.findViewById(R.id.rlt_userprofile);
			drawerHolder.resetLayout = (RelativeLayout) view
					.findViewById(R.id.rlt_reset);
			drawerHolder.titleParentLayout = (RelativeLayout) view
					.findViewById(R.id.rlt_title_parent);
			drawerHolder.titleLayout = (RelativeLayout) view
					.findViewById(R.id.rlt_tilte);

			view.setTag(drawerHolder);

		} else {
			drawerHolder = (DrawerItemHolder) view.getTag();

		}

		DrawerItem dItem = (DrawerItem) this.drawerItemList.get(position);
		
		if (!"".equals(dItem.getUser())) {
			drawerHolder.user.setText(MainActivity.staticTargetID.replace(" ", ""));
			drawerHolder.resetLayout.setVisibility(LinearLayout.GONE);
			drawerHolder.titleParentLayout.setVisibility(LinearLayout.GONE);
			drawerHolder.titleLayout.setVisibility(LinearLayout.GONE);
			drawerHolder.userLayout.setVisibility(LinearLayout.VISIBLE);

		} else if (!"".equals(dItem.getReset())) {
			drawerHolder.resetLayout.setVisibility(LinearLayout.VISIBLE);
			drawerHolder.titleParentLayout.setVisibility(LinearLayout.GONE);
			drawerHolder.titleLayout.setVisibility(LinearLayout.GONE);
			drawerHolder.userLayout.setVisibility(LinearLayout.GONE);
		} else if (!"".equals(dItem.getTitle())) {
			drawerHolder.resetLayout.setVisibility(LinearLayout.GONE);
			drawerHolder.titleParentLayout.setVisibility(LinearLayout.VISIBLE);
			drawerHolder.titleLayout.setVisibility(LinearLayout.GONE);
			drawerHolder.userLayout.setVisibility(LinearLayout.GONE);
			drawerHolder.title.setText(drawerItemList.get(position).getTitle());
			drawerHolder.titleParentLayout.setBackgroundColor(drawerItemList
					.get(position).getColor());
		} else {
			drawerHolder.resetLayout.setVisibility(LinearLayout.GONE);
			drawerHolder.titleParentLayout.setVisibility(LinearLayout.GONE);
			drawerHolder.titleLayout.setVisibility(LinearLayout.VISIBLE);
			drawerHolder.userLayout.setVisibility(LinearLayout.GONE);
			drawerHolder.ItemName.setText(drawerItemList.get(position)
					.getItemName());
		}
		return view;
	}

	private static class DrawerItemHolder {
		TextView ItemName, title, user, reset;
		RelativeLayout userLayout, resetLayout, titleParentLayout, titleLayout;

	}
}