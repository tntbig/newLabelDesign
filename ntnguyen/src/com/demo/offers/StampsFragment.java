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
import co.leonisand.offers.OffersStampCard;
import co.leonisand.offers.OffersStatus;

import com.demo.adapters.StampCustomAdatper;

public class StampsFragment extends Fragment{
	
	public static String TITLE = String.valueOf(R.string.stamp);
	private Context mContext;
	private ArrayList<OffersStampCard> mListStamps ;
	private ListView mListView;
	private ProgressBar mProgressBar;
	public StampCustomAdatper adapter ;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setHasOptionsMenu(true);
		mListStamps = new ArrayList<OffersStampCard>();
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater) {
		menu.clear();
		menuinflater.inflate(R.menu.main_stamp_cards, menu);
	}

	public boolean onOptionsItemSelected(MenuItem menuitem) {
		switch (menuitem.getItemId()) {
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

		mListView = (ListView)layoutinflater.inflate(R.layout.list_stamps, viewgroup, false);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterview, View view1, int i, long l) {
				OffersStampCard offersStamp = mListStamps.get(i);
				Bundle bundle1 = new Bundle();
				bundle1.putInt("stamp_card_id", offersStamp.getId());
				bundle1.putString("title", offersStamp.getTitle());
				StampFragment recommendfragment = new StampFragment();
				recommendfragment.setArguments(bundle1);
				
				getFragmentManager()
				.beginTransaction()
				.add(R.id.content_frame, recommendfragment, RecommendationFragment.TITLE)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null)
				.commit();
			}

		});
		adapter = new StampCustomAdatper(mListStamps, mContext , (MainActivity) getActivity());
		mListView.setAdapter(adapter);
		
		linearLayout.addView(mListView);

		return view;
	}

	@SuppressLint("HandlerLeak")
	public  Handler myHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	adapter.mList = mListStamps ;
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
		OffersKit.getInstance().stampCards(true,
				new OffersListener() {
					public void onDone(Map<String, Object> map) {
						mProgressBar.setVisibility(View.GONE);
		
						if(mListStamps == null){
							return;
						}
		
						mListStamps.clear();
		
						if (map.get("stamp_cards") != null) {
							@SuppressWarnings("unchecked")
							List<OffersStampCard> items = (List<OffersStampCard>) map.get("stamp_cards");
							for(OffersStampCard item : items){
								mListStamps.add(item);
							}
							myHandler.sendEmptyMessage(0);
						} else {
							OffersStatus offersstatus = (OffersStatus) map.get("status");
							if(offersstatus != null) {
								((MainActivity) getActivity()).alert("stamps.onDone", offersstatus.getCode() + ":" + offersstatus.getMessage());
							}else{
								((MainActivity) getActivity()).alert("stamps.onDone", "取得できませんでした。");
							}
							return;
						}
					}
		
					public void onFail(Integer s) {
						mProgressBar.setVisibility(View.GONE);
						((MainActivity) getActivity()).alert("stamps.onFail", s.toString());
					}
				});
	}

	public void onResume(){
		super.onResume();
		
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(TITLE);
	}
	
	public void onDestroyView() {
		mListView.setAdapter(null);
		mListStamps = null;
		super.onDestroyView();
	}
}
