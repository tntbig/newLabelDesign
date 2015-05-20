package com.demo.offers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersRecommendation;
import co.leonisand.offers.OffersStatus;

import com.demo.adapters.RecommendCustomAdatper;

public class RecommendationsFragment extends Fragment{
	public static String TITLE = String.valueOf(R.string.recommendation);
	private Context mContext;
	private ArrayList<OffersRecommendation> mListRecommendations ;
	private ListView mListView;
	private ProgressBar mProgressBar;
	public RecommendCustomAdatper adapter ;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setHasOptionsMenu(true);
		mListRecommendations = new ArrayList<OffersRecommendation>();
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater) {
		menu.clear();
		menuinflater.inflate(R.menu.main_recommendations, menu);
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
						((MainActivity) getActivity()).alert("recommendations.onDone", offersstatus.getCode() + ":" +  offersstatus.getMessage());
					}
				}

				public void onFail(Integer s) {
//					((MainActivity) getActivity()).alert("onFail", s.toString());
				}
			});

			break;
		case R.id.action_unread:

			((MainActivity) getActivity()).alert("未読件数 / 件数", OffersKit.getInstance().unreadRecommendationsCount() + "/" + OffersKit.getInstance().recommendationsCount());

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

		mListView = (ListView)layoutinflater.inflate(R.layout.list_recommedations, viewgroup, false);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterview, View view1, int i, long l) {
				OffersRecommendation offersrecommendation = mListRecommendations.get(i);
				Bundle bundle1 = new Bundle();
				bundle1.putInt("recommendation_id", offersrecommendation.getId());
				bundle1.putString("type", offersrecommendation.getType());
				bundle1.putString("title", offersrecommendation.getName());
				RecommendationFragment recommendfragment = new RecommendationFragment();
				recommendfragment.setArguments(bundle1);
				
				getFragmentManager()
				.beginTransaction()
				.add(R.id.content_frame, recommendfragment, RecommendationFragment.TITLE)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null)
				.commit();
			}

		});
		adapter = new RecommendCustomAdatper(mListRecommendations, mContext , (MainActivity) getActivity());
		mListView.setAdapter(adapter);
		
		linearLayout.addView(mListView);

		return view;
	}

	@SuppressLint("HandlerLeak")
	public  Handler myHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	adapter.mList = mListRecommendations ;
	        adapter.notifyDataSetChanged();
	    }
	};

	public void onStart() {
		super.onStart();

		//((MainActivity) getActivity()).onFragmentStart(this);
		
		final Bundle params = new Bundle();
		params.putString("sort_target", "delivery_from");
		params.putString("sort_direction", "descending");
		params.putString("offset", "0");
		params.putString("limit", "20");
		mProgressBar.setVisibility(View.VISIBLE);
		OffersKit.getInstance().recommendations(true, params,
				new OffersListener() {
					public void onDone(Map<String, Object> map) {
						mProgressBar.setVisibility(View.GONE);
		
						if(mListRecommendations == null){
							return;
						}
		
						mListRecommendations.clear();
		
						if (map.get("recommendations") != null) {
							@SuppressWarnings("unchecked")
							List<OffersRecommendation> items = (List<OffersRecommendation>) map.get("recommendations");
							for(OffersRecommendation item : items){
								mListRecommendations.add(item);
							}
							myHandler.sendEmptyMessage(0);
						} else {
							OffersStatus offersstatus = (OffersStatus) map.get("status");
							if(offersstatus != null) {
								((MainActivity) getActivity()).alert("recommends.onDone", offersstatus.getCode() + ":" + offersstatus.getMessage());
							}else{
								((MainActivity) getActivity()).alert("recommends.onDone", "取得できませんでした。");
							}
							return;
						}
					}
		
					public void onFail(Integer s) {
						mProgressBar.setVisibility(View.GONE);
						((MainActivity) getActivity()).alert("recommends.onFail", s.toString());
					}
				});
	}

	public void onResume(){
		super.onResume();
		
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(TITLE);
	}
	
	public void onDestroyView() {
		mListView.setAdapter(null);
		mListRecommendations = null;
		super.onDestroyView();
	}
}
