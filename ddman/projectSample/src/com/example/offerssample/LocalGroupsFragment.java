package com.example.offerssample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import co.leonisand.leonis.Image.ImageListener;
import co.leonisand.offers.OffersGroup;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;

import java.util.*;

public class LocalGroupsFragment extends Fragment {

	public static String TITLE = "(ローカル)店舗一覧";
	private Context mContext;
	private ArrayAdapter<OffersGroup> mListAdapter;
	private ListView mListView;

	public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
		mContext = layoutinflater.getContext();

		View view = layoutinflater.inflate(R.layout.fragment, viewgroup, false);

		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_content);

		mListView = new ListView(mContext);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterview, View view1, int i, long l) {

				OffersGroup offersgroup = (OffersGroup) adapterview .getItemAtPosition(i);

				StringBuilder sb = new StringBuilder();

				sb.append("id: " + offersgroup.getId() + "\n");
				sb.append("name: " + offersgroup.getName() + "\n");
				sb.append("address: " + offersgroup.getAddress() + "\n");
				sb.append("latitude: " + offersgroup.getLatitude()+ "\n");
				sb.append("longitude: " + offersgroup.getLongitude() + "\n");
				sb.append("email: " + offersgroup.getEmail() + "\n");
				sb.append("twitter: " + offersgroup.getTwitter() + "\n");
				sb.append("facebook: " + offersgroup.getFacebook() + "\n");
				sb.append("website: " + offersgroup.getWebsite() + "\n");
				sb.append("blog: " + offersgroup.getBlog() + "\n");
				sb.append("group_image1: " + offersgroup.getGroupImage1() + "\n");
				sb.append("group_image2: " + offersgroup.getGroupImage2() + "\n");
				sb.append("phone_number: " + offersgroup.getPhoneNumber() + "\n");
				sb.append("opening_hours: " + offersgroup.getOpeningHours() + "\n");
				sb.append("updated_at: " + offersgroup.getUpdatedAt() + "\n");

				((MainActivity) getActivity()).alert("カテゴリ情報", sb.toString());

			}

		});
		mListAdapter = new ArrayAdapter<OffersGroup>(mContext, android.R.layout.simple_list_item_1) {
			private LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			@SuppressLint("InflateParams")
			public View getView(int i, View view1, ViewGroup viewgroup1) {
				OffersGroup offersgroup = (OffersGroup) getItem(i);
				if (view1 == null) {
					view1 = new LinearLayout(mContext);
					((LinearLayout) view1).setOrientation(LinearLayout.HORIZONTAL);

					ImageView imageview = new ImageView(mContext);
					imageview.setId(android.R.id.widget_frame);
					imageview.setLayoutParams(new LinearLayout.LayoutParams(72, 72));
					imageview.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
					((ViewGroup) view1).addView(imageview);

					TextView textview = (TextView) mLayoutInflater.inflate(android.R.layout.simple_list_item_1, null);
					textview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0F));
					((ViewGroup) view1).addView(textview);

					TextView textview1 = new TextView(mContext);
					textview1.setId(android.R.id.text2);
					textview1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0F));
					((ViewGroup) view1).addView(textview1);
				}

				offersgroup.groupImage1Bitmap((ImageView) view1.findViewById(android.R.id.widget_frame), new ImageListener() {
					public void onDone(View view, Bitmap bitmap) {
						((ImageView) view).setImageBitmap(bitmap);
					}
				});

				((TextView) view1.findViewById(android.R.id.text1)).setText(String.valueOf(offersgroup.getId()));
				((TextView) view1.findViewById(android.R.id.text2)).setText(offersgroup.getName());

				return view1;
			}
		};

		mListView.setAdapter(mListAdapter);
		linearLayout.addView(mListView);

		return view;
	}

	public void onStart() {
		super.onStart();

		OffersKit.getInstance().groups(false, new OffersListener() {

			public void onDone(Map<String, Object> map) {

				mListAdapter.clear();
				if (map.get("groups") != null) {

					@SuppressWarnings("unchecked")
					List<OffersGroup> items = (List<OffersGroup>) map.get("groups");
					for(OffersGroup item : items){
						mListAdapter.add(item);
					}

				} else {
					((MainActivity) getActivity()).alert("onDone", map.toString());
					return;
				}
			}

			public void onFail(Integer s) {
				((MainActivity) getActivity()).alert("onFail", s.toString());
			}
		});
	}

	public void onResume(){
		super.onResume();
		
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(TITLE);
		getActivity().supportInvalidateOptionsMenu();

	}

	public void onDestroyView() {
		mListView.setAdapter(null);
		super.onDestroyView();
	}

}
