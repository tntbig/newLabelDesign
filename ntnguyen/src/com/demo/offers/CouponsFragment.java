package com.demo.offers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import co.leonisand.offers.OffersCoupon;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersStatus;

import com.demo.adapters.CouponCustomAdatper;

@SuppressLint("HandlerLeak")
public class CouponsFragment extends Fragment {

	public static String TITLE = "クーポン一覧";
	private Context mContext;
	private ArrayList<OffersCoupon> mListCoupon ;
	private ListView mListView;
	private ProgressBar mProgressBar;
	private int mTargetGroupId;
	private int mTargetCategoryId;
	public CouponCustomAdatper adapter ;
	protected boolean stampCallback = false;
	protected ArrayList<OffersCoupon> mListCouponStamp;
	public String[] code = null;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		System.out.println("================= On Create =======================");
		setHasOptionsMenu(true);
		
		mListCoupon = new ArrayList<OffersCoupon>();
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
//					((MainActivity) getActivity()).alert("onFail", s.toString());
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
		System.out.println("================= On Create View =======================");
		mContext = layoutinflater.getContext();
		View view = layoutinflater.inflate(R.layout.fragment, viewgroup, false);
		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_content);
		mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressbar);

		mListView = (ListView) layoutinflater.inflate(R.layout.list_coupons, viewgroup, false);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterview, View view1, int i, long l) {
				OffersCoupon offerscoupon = mListCoupon.get(i);
				Bundle bundle1 = new Bundle();
				bundle1.putInt("coupon_id", offerscoupon.getId());
				bundle1.putString("title", offerscoupon.getTitle());
				CouponFragment couponfragment = new CouponFragment();
				couponfragment.setArguments(bundle1);
				// Remove status image on top right after select coupo 
				view1.findViewById(R.id.statusImage).setVisibility(View.GONE);
				getFragmentManager()
				.beginTransaction()
				.add(R.id.content_frame, couponfragment, CouponFragment.TITLE)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null)
				.commit();
			}
		});
		adapter = new CouponCustomAdatper(mListCoupon, mContext , (MainActivity) getActivity());
		mListView.setAdapter(adapter);
		
		linearLayout.addView(mListView);

		return view;
	}

	public  Handler myHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	mProgressBar.setVisibility(View.GONE);
	    	adapter.mList = mListCoupon ;
	        adapter.notifyDataSetChanged();
	    }
	};

	public void onStart() {
		super.onStart();
		System.out.println("================= On Start =======================");
		//((MainActivity) getActivity()).onFragmentStart(this);
		
		mProgressBar.setVisibility(View.VISIBLE);
		Bundle params = new Bundle();
		params.putString("sort_target", "delivery_from");
		params.putString("sort_direction", "descending");
		params.putString("offset", 			"0");
		params.putString("limit", 			"20");
		OffersKit.getInstance().unlockCoupons(new OffersListener() {
			
			@Override
			public void onFail(Integer arg0) {
				// TODO Auto-generated method stub
//				((MainActivity) getActivity()).alert("FAIL", "");
			}
			
			@Override
			public void onDone(Map<String, Object> arg0) {
				// TODO Auto-generated method stub
				OffersStatus list = (OffersStatus) arg0.get("status");
				System.out.println("================= "+list.getMessage()+" =======================");
			}
		});
		if(((MainActivity)getActivity()).code != null){
			((MainActivity)getActivity()).controller.requestCampaignCoupons(((MainActivity)getActivity()).code,mListCoupon, (MainActivity)getActivity(), myHandler);
			((MainActivity)getActivity()).code = null;
		}else if (!stampCallback){
			((MainActivity)getActivity()).controller.requestCouponsWithType(mListCoupon, (MainActivity)getActivity(), myHandler);
		}else{
			mProgressBar.setVisibility(View.GONE);
			adapter.mList = mListCouponStamp;
			mListCoupon = mListCouponStamp;
		}
	}

	public void onResume(){
		super.onResume();
		
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(TITLE);
	}
	
	public void onDestroyView() {
		mListView.setAdapter(null);
		mListCoupon = null;
		super.onDestroyView();
	}

}

