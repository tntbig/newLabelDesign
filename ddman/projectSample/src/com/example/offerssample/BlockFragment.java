package com.example.offerssample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import co.leonisand.offers.OffersBlocking;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;

import java.util.*;

public class BlockFragment extends Fragment {

	public static String TITLE = "ブロック";
	private Context mContext;
	private ArrayAdapter<Map<String, Object>> mListAdapter;
	private ListView mListView;
	private OffersBlocking mOffersBlocking;
	private ProgressBar mProgressBar;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setHasOptionsMenu(true);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater) {
		menuinflater.inflate(R.menu.main_block, menu);
	}

	public boolean onOptionsItemSelected(MenuItem menuitem) {
		switch (menuitem.getItemId()) {
		case R.id.action_edit:
			OffersKit.getInstance().saveBlocking(mOffersBlocking, new OffersListener() {
				public void onDone(Map<String, Object> map) {
					onStart();
				}

				public void onFail(Integer s) {
					((MainActivity) getActivity()).alert("onFail", s.toString());
				}
			});
			return true;
		default:
			return super.onOptionsItemSelected(menuitem);
		}
	}

	public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
		mContext = layoutinflater.getContext();
		View view = layoutinflater.inflate(R.layout.fragment, viewgroup, false);

		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_content);
		mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressbar);

		mListView = new ListView(mContext);

		mListAdapter = new ArrayAdapter<Map<String, Object>>(mContext, android.R.layout.simple_list_item_1) {
			private LayoutInflater mLayoutInflater;
			{
				mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			@SuppressLint("InflateParams")
			public View getView(int i, View view1, ViewGroup viewgroup1) {
				if (view1 == null) {
					view1 = new LinearLayout(mContext);
					((LinearLayout) view1).setOrientation(LinearLayout.HORIZONTAL);

					TextView textview = (TextView) mLayoutInflater.inflate(android.R.layout.simple_list_item_1, null);
					textview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0F));
					((ViewGroup) view1).addView(textview);

					CheckBox checkbox = new CheckBox(mContext);
					checkbox.setId(android.R.id.checkbox);
					checkbox.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.0F));
					((ViewGroup) view1).addView(checkbox);
				}
				Map<String, Object> item = getItem(i);

				TextView textview = (TextView) view1.findViewById(android.R.id.text1);

				CheckBox checkbox1 = (CheckBox) view1.findViewById(android.R.id.checkbox);
				checkbox1.setTag(item);

				if(item.get("head") != null){
					textview.setText(item.get("head").toString());
					textview.setTextColor(mContext.getResources().getColor(android.R.color.tertiary_text_light));
					textview.setBackgroundColor(mContext.getResources().getColor(android.R.color.secondary_text_light_nodisable));

					checkbox1.setVisibility(View.GONE);
				}else{
					textview.setText(item.get("name").toString());
					textview.setTextColor(mContext.getResources().getColor(android.R.color.primary_text_light));
					textview.setBackgroundColor(mContext.getResources().getColor(android.R.color.primary_text_dark));

					checkbox1.setVisibility(View.VISIBLE);

					checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						public void onCheckedChanged(CompoundButton buttomView, boolean isChecked) {
							@SuppressWarnings("unchecked")
							Map<String, Object> item = (Map<String, Object>) buttomView.getTag();

							if (item.get("name").equals("Push通知")){
								mOffersBlocking.setPushEnabled(isChecked);
							}else if (item.get("name").equals("お知らせ")) {
								mOffersBlocking.setNewsEnabled(isChecked);
							}else{
								int i = Integer.valueOf((String) item.get("id"));
								mOffersBlocking.setCategory(i, !isChecked);
							}
						}
					});

					if(item.get("enabled") != null){
						checkbox1.setChecked(Boolean.parseBoolean(item.get("enabled").toString()));
					}else{
						System.out.println("item:"+item);

						checkbox1.setChecked(!Boolean.parseBoolean(item.get("blocked").toString()));
					}

				}

				return view1;
			}

		};

		mListView.setAdapter(mListAdapter);
		linearLayout.addView(mListView);

		return view;
	}

	public void onStart() {
		super.onStart();

		mProgressBar.setVisibility(View.VISIBLE);
		OffersKit.getInstance().block(new OffersListener() {

			public void onDone(Map<String, Object> map) {
				mProgressBar.setVisibility(View.GONE);
				if (map.get("blocking") != null) {
					mOffersBlocking = (OffersBlocking) map.get("blocking");
					mListAdapter.clear();

					mListAdapter.add(new HashMap<String, Object>() {
						private static final long serialVersionUID = 1L;
						{
							put("head", "配信設定");
						}
					});

					mListAdapter.add(new HashMap<String, Object>() {
						private static final long serialVersionUID = 1L;
						{
							put("name", "Push通知");
							put("enabled", Boolean.valueOf(mOffersBlocking.isPushEnabled()));
						}
					});
					mListAdapter.add(new HashMap<String, Object>() {
						private static final long serialVersionUID = 1L;
						{
							put("name", "お知らせ");
							put("enabled", Boolean.valueOf(mOffersBlocking.isNewsEnabled()));
						}
					});

					mListAdapter.add(new HashMap<String, Object>() {
						private static final long serialVersionUID = 1L;
						{
							put("head", "ジャンル別配信設定");
						}
					});

					List<Map<String, Object>> items = mOffersBlocking.getCategories();
					for (Map<String, Object> item : items) {
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
		mListAdapter = null;
		mListView.setAdapter(null);
		super.onDestroyView();
	}

}
