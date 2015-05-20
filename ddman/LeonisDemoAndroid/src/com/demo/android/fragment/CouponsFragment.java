package com.demo.android.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import co.leonisand.offers.OffersCoupon;
import co.leonisand.offers.OffersGroup;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;

import com.demo.android.MainActivity;
import com.demo.android.R;
import com.demo.android.adapter.AdapterCoupon;
import com.demo.android.adapter.AdapterMenu;
import com.demo.android.adapter.AdapterMenuType;
import com.demo.android.util.UtilsAnimation;
import com.etsy.android.grid.StaggeredGridView;

@SuppressLint("HandlerLeak")
public class CouponsFragment extends Fragment {

	public static String TITLE = "クーポン一覧";
	private Context mContext;
	public ArrayList<OffersCoupon> mListCoupon;
	private ArrayList<OffersGroup> mListMenu;
	private ArrayList<String> mListType;
	public StaggeredGridView mListView;
	private ProgressBar mProgressBar;
	private int mTargetGroupId;
	private int mTargetCategoryId;
	public AdapterCoupon adapter;
	private Button mButtonMenu0;
	private Button mButtonMenu1;
	private Button mButtonMenu2;
	private ListView listViewMenu;
	private AdapterMenu adapterMenu;
	private AdapterMenuType adapterMenuType;
	public PopupWindow pwindowMenu;
	private View layoutMenu;
	private RelativeLayout mCancel;
	public String mTypeSearch;
	public CouponFragment couponfragment;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setHasOptionsMenu(true);
		mListCoupon = new ArrayList<OffersCoupon>();
		mListMenu = new ArrayList<OffersGroup>();
		mListType = new ArrayList<String>();
		mListType.add(null);
		mListType.add("暗証番号");
		mListType.add("スタンプ");
		mTypeSearch = "";
		mTargetGroupId = 0;

		Bundle arguments = getArguments();
		if (arguments != null) {
			if (arguments.containsKey("group_id")) {
				mTargetGroupId = arguments.getInt("group_id");
			} else if (arguments.containsKey("category_id")) {
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
		case R.id.action_unread:
			((MainActivity) getActivity()).alert("未読件数 / 件数", OffersKit
					.getInstance().unreadCouponsCount()
					+ "/"
					+ OffersKit.getInstance().couponsCount());
			break;
		case R.id.action_refresh:
			onStart();
			break;
		default:
			return super.onOptionsItemSelected(menuitem);
		}
		return true;
	}

	public View onCreateView(final LayoutInflater layoutinflater,
			ViewGroup viewgroup, Bundle bundle) {
		mContext = layoutinflater.getContext();
		final View view = layoutinflater.inflate(R.layout.fragment, viewgroup,
				false);
		LinearLayout linearLayout = (LinearLayout) view
				.findViewById(R.id.fragment_content);
		mProgressBar = (ProgressBar) getActivity().findViewById(
				R.id.progressbar);
		layoutMenu = layoutinflater.inflate(R.layout.layout_search_shop, null);
		listViewMenu = (ListView) layoutMenu.findViewById(R.id.lv_listshop);
		mCancel = (RelativeLayout) layoutMenu.findViewById(R.id.rlt_cancel);

		View viewGrid = layoutinflater.inflate(R.layout.layout_gridview_coupon,
				viewgroup, false);
		mButtonMenu0 = (Button) view.findViewById(R.id.btn_menu_0);
		mButtonMenu1 = (Button) view.findViewById(R.id.btn_menu_1);
		mButtonMenu2 = (Button) view.findViewById(R.id.btn_menu_2);
		mButtonMenu0.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mButtonMenu1.getVisibility() == View.GONE) {
					UtilsAnimation.expand(mButtonMenu1);
					UtilsAnimation.expand(mButtonMenu2);
				} else {
					UtilsAnimation.collapse(mButtonMenu1);
					UtilsAnimation.collapse(mButtonMenu2);
				}
			}
		});
		mButtonMenu1.setOnClickListener(clickListener);
		mButtonMenu2.setOnClickListener(clickListener);
		mCancel.setOnClickListener(clickListener);

		mListView = (StaggeredGridView) viewGrid.findViewById(R.id.MyGrid);
		mListView.setId(android.R.id.list);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				OffersCoupon offerscoupon = (OffersCoupon) mListCoupon
						.get(position);
				Bundle bundle1 = new Bundle();
				bundle1.putInt("coupon_id", offerscoupon.getId());
				bundle1.putString("title", offerscoupon.getTitle());
				couponfragment = new CouponFragment();
				couponfragment.setArguments(bundle1);

				getFragmentManager()
						.beginTransaction()
						.add(R.id.content_frame, couponfragment,
								CouponFragment.TITLE)
						.setTransition(
								FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.addToBackStack(null).commit();
			}
		});
		adapter = new AdapterCoupon(mListCoupon, mContext,
				(MainActivity) getActivity());
		mListView.setAdapter(adapter);
		linearLayout.addView(viewGrid);

		return view;
	}

	/**
	 * handler after get coupon list
	 */
	public Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				mProgressBar.setVisibility(View.GONE);
				adapter.mList = mListCoupon;
				adapter.notifyDataSetChanged();
			} else {
				adapterMenu.mList = mListMenu;
				adapterMenu.notifyDataSetChanged();
			}
		}
	};

	public void onStart() {
		super.onStart();
		mProgressBar.setVisibility(View.VISIBLE);
		MainActivity.shake = false;
		// clearApplicationData();
		// ((MainActivity)getActivity()).initSdk();
		OffersKit.getInstance().unlockCoupons(new OffersListener() {

			@Override
			public void onFail(Integer arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDone(Map<String, Object> arg0) {
				// TODO Auto-generated method stub
			}
		});
		Log.e("man campaign", "" + ((MainActivity) getActivity()).codeCampaign);
		if (((MainActivity) getActivity()).codeCampaign != null
				&& ((MainActivity) getActivity()).codeCampaign.length > 0) {
			((MainActivity) getActivity()).controller.requestCampaignCoupons(
					((MainActivity) getActivity()).codeCampaign,mListCoupon,
					((MainActivity) getActivity()),myHandler);
			((MainActivity) getActivity()).codeCampaign = null ;
		}else if(((MainActivity) getActivity()).mStampCardID > -1){
			((MainActivity) getActivity()).controller.requestStampCartPresent(
					((MainActivity) getActivity()).mStampCardID,mListCoupon,
					((MainActivity) getActivity()),myHandler);
			((MainActivity) getActivity()).mStampCardID = -1 ;
		} else {
			mListCoupon = new ArrayList<OffersCoupon>();
			((MainActivity) getActivity()).controller.requestCouponsWithType(
					mTypeSearch, mListCoupon, (((MainActivity) getActivity())),
					myHandler);
		}
	}

	// for clear cache and sqllite
	// public void clearApplicationData() {
	// File cache = mContext.getCacheDir();
	// File appDir = new File(cache.getParent());
	// if(appDir.exists()){
	// String[] children = appDir.list();
	// for(String s : children){
	// if(!s.equals("lib")){
	// deleteDir(new File(appDir, s));
	// Log.i("TAG", "File /data/data/com.demo.android/" + s +" DELETED");
	// }
	// }
	// }
	// }
	//
	// public static boolean deleteDir(File dir) {
	// if (dir != null && dir.isDirectory()) {
	// String[] children = dir.list();
	// for (int i = 0; i < children.length; i++) {
	// boolean success = deleteDir(new File(dir, children[i]));
	// if (!success) {
	// return false;
	// }
	// }
	// }
	//
	// return dir.delete();
	// }
	public void onResume() {
		super.onResume();

		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(
				TITLE);
		// getActivity().supportInvalidateOptionsMenu();
	}

	public OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.btn_menu_2) {
				UtilsAnimation.collapse(mButtonMenu1);
				UtilsAnimation.collapse(mButtonMenu2);
				adapterMenuType = new AdapterMenuType(mListType, mContext);
				listViewMenu.setAdapter(adapterMenuType);
				listViewMenu.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						mTypeSearch = mListType.get(position);
						pwindowMenu.dismiss();
						if (position != 0) {
							onStart();
						}
					}
				});
				pwindowMenu = new PopupWindow(layoutMenu,
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,
						true);
				pwindowMenu.setAnimationStyle(R.style.PopupAnimation);
				pwindowMenu.showAtLocation(
						((MainActivity) getActivity()).getCurrentFocus(),
						Gravity.NO_GRAVITY, 0, ((MainActivity) getActivity())
								.getWindowManager().getDefaultDisplay()
								.getHeight() / 2);
				pwindowMenu.setFocusable(true);
				pwindowMenu.update();
			} else if (v.getId() == R.id.rlt_cancel) {
				pwindowMenu.dismiss();
			} else if (v.getId() == R.id.btn_menu_1) {

				UtilsAnimation.collapse(mButtonMenu1);
				UtilsAnimation.collapse(mButtonMenu2);
				((MainActivity) getActivity()).controller.requestGroups(
						mListMenu, ((MainActivity) getActivity()), myHandler);
				adapterMenu = new AdapterMenu(mListMenu, mContext);
				listViewMenu.setAdapter(adapterMenu);
				listViewMenu.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						if (position == 0)
							return;
						pwindowMenu.dismiss();
						((MainActivity) getActivity()).controller.requestShops(
								new int[] { mListMenu.get(position).getId() },
								mListCoupon, ((MainActivity) getActivity()),
								myHandler);
					}
				});
				pwindowMenu = new PopupWindow(layoutMenu,
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,
						true);
				pwindowMenu.setAnimationStyle(R.style.PopupAnimation);
				pwindowMenu.showAtLocation(
						((MainActivity) getActivity()).getCurrentFocus(),
						Gravity.NO_GRAVITY, 0, ((MainActivity) getActivity())
								.getWindowManager().getDefaultDisplay()
								.getHeight() / 2);
				pwindowMenu.setFocusable(true);
				pwindowMenu.update();

			}
		}
	};

	public void onDestroyView() {
		mListView.setAdapter(null);
		mListCoupon = null;
		super.onDestroyView();
	}

}
