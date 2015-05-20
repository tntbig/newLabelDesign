package com.example.offerssample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
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
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersRecommendation;
import co.leonisand.offers.OffersStatus;

public class RecommendationsFragment extends Fragment {

	public static String TITLE = "レコメンデーション一覧";
	private Context mContext;
	private ArrayAdapter<OffersRecommendation> mListAdapter;
	private ListView mListView;
	private ProgressBar mProgressBar;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setHasOptionsMenu(true);
	}


	public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater) {
		menu.clear();
		menuinflater.inflate(R.menu.main_recommendations, menu);
	}

	public boolean onOptionsItemSelected(MenuItem menuitem) {
		switch (menuitem.getItemId()) {
		case R.id.action_refresh:
			onStart();
			break;
		case R.id.action_unread:

			((MainActivity) getActivity()).alert("未読件数 / 件数", OffersKit.getInstance().unreadRecommendationsCount() + "/" + OffersKit.getInstance().recommendationsCount());

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

				OffersRecommendation offersrecommendation = (OffersRecommendation) adapterview.getItemAtPosition(i);
				Bundle bundle1 = new Bundle();
				bundle1.putInt("recommendation_id", offersrecommendation.getId());
				bundle1.putString("type", offersrecommendation.getType());
				bundle1.putString("title", offersrecommendation.getName());

				RecommendationFragment recommendationfragment = new RecommendationFragment();
				recommendationfragment.setArguments(bundle1);
				getFragmentManager()
				.beginTransaction()
				.add(R.id.container, recommendationfragment, RecommendationFragment.TITLE)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null)
				.commit();
			}
		});
		mListAdapter = new ArrayAdapter<OffersRecommendation>(mContext, android.R.layout.simple_list_item_1) {

			@SuppressLint("InflateParams")
			public View getView(int i, View view1, ViewGroup viewgroup1) {
				OffersRecommendation recommendation = (OffersRecommendation) getItem(i);
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
				recommendation.thumbnailImageBitmap((ImageView) view1.findViewById(android.R.id.widget_frame), new ImageListener() {

					public void onDone(View view, Bitmap bitmap) {
						((ImageView) view).setImageBitmap(bitmap);
					}

				});
				((TextView) view1.findViewById(android.R.id.text1)).setText(String.valueOf(recommendation.getId()));
				((TextView) view1.findViewById(android.R.id.text2)).setText(recommendation.getName());
				return view1;
			}

			private LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		};
		mListView.setAdapter(mListAdapter);
		linearLayout.addView(mListView);

		return view;
	}

	public void onStart() {
		super.onStart();

		mProgressBar.setVisibility(View.VISIBLE);

		final Bundle params = new Bundle();
		params.putString("sort_target", 	"delivery_from");
		params.putString("sort_direction", 	"descending");
		params.putString("offset", 			"0");
		params.putString("limit", 			"20");
		OffersKit.getInstance().recommendations(true, params, new OffersListener() {

			public void onDone(Map<String, Object> map) {
				mProgressBar.setVisibility(View.GONE);
				mListAdapter.clear();

				if (map.get("recommendations") != null) {

					@SuppressWarnings("unchecked")
					List<OffersRecommendation> items = (List<OffersRecommendation>) map.get("recommendations");
					for(OffersRecommendation item : items){
						mListAdapter.add(item);
					}

				} else {
					OffersStatus offersstatus = (OffersStatus) map.get("status");
					if(offersstatus != null) {
						((MainActivity) getActivity()).alert("recommendations.onDone", offersstatus.getCode() + ":" + offersstatus.getMessage());
					}else{
						((MainActivity) getActivity()).alert("recommendations.onDone", "取得できませんでした。");
					}
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
