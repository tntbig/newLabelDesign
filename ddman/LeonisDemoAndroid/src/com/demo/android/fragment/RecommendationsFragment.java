package com.demo.android.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import co.leonisand.offers.OffersGroup;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersRecommendation;

import com.demo.android.MainActivity;
import com.demo.android.R;
import com.demo.android.adapter.AdapterMenu;
import com.demo.android.adapter.AdapterRecommendations;
import com.demo.android.util.UtilsAnimation;
import com.etsy.android.grid.StaggeredGridView;

public class RecommendationsFragment extends Fragment {

	public static String TITLE = "レコメンデーション一覧";
	private Context mContext;
	private ProgressBar mProgressBar;
	private ArrayList<OffersRecommendation> mListRecommendation = new ArrayList<OffersRecommendation>();
	public StaggeredGridView mListView;
	private AdapterRecommendations adapter;
	private Button mButtonMenu0;
	private Button mButtonMenu1;
	private RelativeLayout mCancel;
	private View layoutMenu;
	private AdapterMenu adapterMenu;
	public PopupWindow pwindowMenu;
	private ArrayList<OffersGroup> mListMenu;
	private ListView listViewMenu;
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mListMenu = new ArrayList<OffersGroup>();
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

			((MainActivity) getActivity()).alert("未読件数 / 件数", OffersKit
					.getInstance().unreadRecommendationsCount()
					+ "/"
					+ OffersKit.getInstance().recommendationsCount());

			break;
		default:
			return super.onOptionsItemSelected(menuitem);
		}
		return true;
	}

	public View onCreateView(LayoutInflater layoutinflater,
			ViewGroup viewgroup, Bundle bundle) {
		mContext = layoutinflater.getContext();
		View view = layoutinflater.inflate(R.layout.fragment, viewgroup, false);
		LinearLayout linearLayout = (LinearLayout) view
				.findViewById(R.id.fragment_content);
		layoutMenu = layoutinflater.inflate(R.layout.layout_search_shop, null);
		mProgressBar = (ProgressBar) getActivity().findViewById(
				R.id.progressbar);
		mCancel = (RelativeLayout) layoutMenu.findViewById(R.id.rlt_cancel);
		listViewMenu = (ListView) layoutMenu.findViewById(R.id.lv_listshop);
		View viewGrid = layoutinflater.inflate(
				R.layout.layout_gridview_recommend, viewgroup, false);
		mButtonMenu0 = (Button) view.findViewById(R.id.btn_menu_0);
		mButtonMenu1 = (Button) view.findViewById(R.id.btn_menu_1);
		mButtonMenu0.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mButtonMenu1.getVisibility() == View.GONE) {
					UtilsAnimation.expand(mButtonMenu1);
				} else {
					UtilsAnimation.collapse(mButtonMenu1);
				}
			}
		});
		mButtonMenu1.setOnClickListener(clickListener);
		mCancel.setOnClickListener(clickListener);
		mListView = (StaggeredGridView) viewGrid
				.findViewById(R.id.MyGridRecommend);
		mListView.setId(android.R.id.list);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterview, View view1,
					int i, long l) {

				OffersRecommendation offersrecommendation = (OffersRecommendation) mListRecommendation
						.get(i);
				Bundle bundle1 = new Bundle();
				bundle1.putInt("recommendation_id",
						offersrecommendation.getId());
				bundle1.putString("type", offersrecommendation.getType());
				bundle1.putString("title", offersrecommendation.getName());

				RecommendationFragment recommendationfragment = new RecommendationFragment();
				recommendationfragment.setArguments(bundle1);
				getFragmentManager()
						.beginTransaction()
						.add(R.id.content_frame, recommendationfragment,
								RecommendationFragment.TITLE)
						.setTransition(
								FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.addToBackStack(null).commit();

			}
		});
		adapter = new AdapterRecommendations(mListRecommendation, mContext,
				(MainActivity) getActivity());
		mListView.setAdapter(adapter);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		linearLayout.addView(viewGrid);

		return view;
	}

	@SuppressLint("HandlerLeak")
	public Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				mProgressBar.setVisibility(View.GONE);
				adapter.mList = mListRecommendation;
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
		final Bundle params = new Bundle();
		params.putString("sort_target", "delivery_from");
		params.putString("sort_direction", "descending");
		params.putString("offset", "0");
		params.putString("limit", "20");
		((MainActivity) getActivity()).controller.requestRecommendWithBundle(
				params, mListRecommendation, (MainActivity) getActivity(),
				myHandler);
	}
	
	public OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.rlt_cancel) {
				pwindowMenu.dismiss();
			} else if (v.getId() == R.id.btn_menu_1) {
				UtilsAnimation.collapse(mButtonMenu1);
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
						final Bundle params = new Bundle();
						params.putString("sort_target", "delivery_from");
						params.putString("sort_direction", "descending");
						params.putString("offset", "0");
						params.putString("limit", "20");
						
						((MainActivity) getActivity()).controller.requestShopsRecommend(params,
								mListMenu.get(position).getName(),
								mListRecommendation, ((MainActivity) getActivity()),
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

	public void onResume() {
		super.onResume();

		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(
				TITLE);
		getActivity().supportInvalidateOptionsMenu();

	}

	public void onDestroyView() {
		mListView.setAdapter(null);
		super.onDestroyView();
	}
}
