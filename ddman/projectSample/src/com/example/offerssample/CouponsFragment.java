package com.example.offerssample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import co.leonisand.leonis.Image.ImageListener;
import co.leonisand.offers.OffersCoupon;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersStatus;
import co.leonisand.offers.OffersTemplate;

public class CouponsFragment extends Fragment {

	public static String TITLE = "クーポン一覧";
	private Context mContext;
	private ArrayAdapter<OffersCoupon> mListAdapter;
	private ListView mListView;
	private ProgressBar mProgressBar;
	private int mTargetGroupId;
	private int mTargetCategoryId;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setHasOptionsMenu(true);

		mTargetGroupId = 0;

		Bundle arguments = getArguments();
		if( arguments != null ) {
			if( arguments.containsKey("group_id")) {
				mTargetGroupId = arguments.getInt("group_id");
			}else if(arguments.containsKey("category_id")) {
				mTargetCategoryId = arguments.getInt("category_id");
			}
		}
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater) {
		menu.clear();
		menuinflater.inflate(R.menu.main_coupons, menu);
	}

	public boolean onOptionsItemSelected(MenuItem menuitem) {
		switch (menuitem.getItemId()) {
		case R.id.action_about:
			OffersKit.getInstance().info(new OffersListener() {
				public void onDone(Map<String, Object> map) {

					OffersStatus offersstatus = (OffersStatus) map.get("status");

					if(offersstatus.getCode() == 0){
						String s = (new StringBuilder("system:"))
								.append(map.get("system").toString()).append("\n")
								.append("resources:")
								.append(map.get("resources").toString()).toString()
								.replaceAll("\\\\", "").replaceAll("\\{", "\\{\n")
								.replaceAll("\\}", "\n\\}").replaceAll(",", "\n,");
						((MainActivity) getActivity()).alert("システム情報", s);
					}else{
						((MainActivity) getActivity()).alert("coupons.onDone", offersstatus.getCode() + ":" +  offersstatus.getMessage());
					}
				}

				public void onFail(Integer s) {
					((MainActivity) getActivity()).alert("onFail", s.toString());
				}
			});

			break;
		case R.id.action_unread:

			((MainActivity) getActivity()).alert("未読件数 / 件数", OffersKit.getInstance().unreadCouponsCount() + "/" + OffersKit.getInstance().couponsCount());

			break;
		case R.id.action_refresh:
			onStart();
			break;
		default:
			return super.onOptionsItemSelected(menuitem);
		}
		return true;
	}

	public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
		mContext = layoutinflater.getContext();
		View view = layoutinflater.inflate(R.layout.fragment, viewgroup, false);
		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_content);
		mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressbar);

		mListView = new ListView(mContext);
		mListView.setId(android.R.id.list);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterview, View view1, int i, long l) {
				OffersCoupon offerscoupon = (OffersCoupon) adapterview .getItemAtPosition(i);
				Bundle bundle1 = new Bundle();
				bundle1.putInt("coupon_id", offerscoupon.getId());
				bundle1.putString("title", offerscoupon.getTitle());
				CouponFragment couponfragment = new CouponFragment();
				couponfragment.setArguments(bundle1);
				
				
				getFragmentManager()
				.beginTransaction()
				.add(R.id.container, couponfragment, CouponFragment.TITLE)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null)
				.commit();
			}

		});

		mListAdapter = new ArrayAdapter<OffersCoupon>(mContext, android.R.layout.simple_list_item_1) {
			private LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			ImageListener imageListener = new ImageListener() {
				public void onDone(View view, Bitmap bitmap) {
					((ImageView) view).setImageBitmap(bitmap);
				}
			};

			@SuppressLint("InflateParams")
			public View getView(int i, View view1, ViewGroup viewgroup1) {
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
				}
				OffersCoupon offerscoupon = (OffersCoupon) getItem(i);
				TextView textview1 = (TextView) view1.findViewById(android.R.id.text1);

				String s;
				if (offerscoupon.getRead()){
					s = "既読";
				}else{
					s = "未読";
				}
				textview1.setText("[" + s + "]" + offerscoupon.getAvailableFrom().toString() +" "+ offerscoupon.getTitle());
				offerscoupon.thumbnailImageBitmap((ImageView) view1.findViewById(android.R.id.widget_frame), imageListener);

				final View _convertView = view1;

				offerscoupon.template(new OffersListener() {
					public void onDone(Map<String, Object> map) {
						if (map.get("template") != null) {
							@SuppressWarnings("unchecked")
							Map<String, Object> map1 = (Map<String, Object>) ((OffersTemplate) map.get("template")).getValues().get("background");
							_convertView.setBackgroundColor(Color.parseColor((String) map1.get("color")));
						}
					}

					public void onFail(Integer s) {
						((MainActivity) getActivity()).alert("onFail", s.toString());
					}
				});
				
				return view1;
			}
		};

		mListView.setAdapter(mListAdapter);
		linearLayout.addView(mListView);

		return view;
	}



	public void onStart() {
		super.onStart();

		((MainActivity) getActivity()).onFragmentStart(this);
		
		mProgressBar.setVisibility(View.VISIBLE);
		OffersListener listener = new OffersListener() {
			public void onDone(Map<String, Object> map) {
				mProgressBar.setVisibility(View.GONE);

				if(mListAdapter == null){
					return;
				}

				mListAdapter.clear();

				if (map.get("coupons") != null) {
					@SuppressWarnings("unchecked")
					List<OffersCoupon> items = (List<OffersCoupon>) map.get("coupons");
					for(OffersCoupon item : items){
						mListAdapter.add(item);
					}

				} else {
					OffersStatus offersstatus = (OffersStatus) map.get("status");
					if(offersstatus != null) {
						((MainActivity) getActivity()).alert("coupons.onDone", offersstatus.getCode() + ":" + offersstatus.getMessage());
					}else{
						((MainActivity) getActivity()).alert("coupons.onDone", "取得できませんでした。");
					}
					return;
				}
			}

			public void onFail(Integer s) {
				mProgressBar.setVisibility(View.GONE);
				((MainActivity) getActivity()).alert("coupons.onFail", s.toString());
			}
		};

		if( mTargetGroupId != 0 ) {
			OffersKit.getInstance().coupons(new int[] { mTargetGroupId }, listener);
		}else if( mTargetCategoryId != 0 ) {
			OffersKit.getInstance().categoryCoupons(new int[] { mTargetCategoryId }, listener);
		}else{
			Bundle params = new Bundle();
			//params.putString("sort_target", 	"delivery_from");
			//params.putString("sort_direction", 	"descending");
			params.putString("offset", 			"0");
			params.putString("limit", 			"20");
			OffersKit.getInstance().coupons(true, params, listener);
		}
	}

	public void onResume(){
		super.onResume();
		
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(TITLE);
		getActivity().supportInvalidateOptionsMenu();
	}
	
	public void onDestroyView() {
		mListView.setAdapter(null);
		mListAdapter = null;
		super.onDestroyView();
	}

}
