package com.example.offerssample;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
import co.leonisand.offers.OffersCategory;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersStatus;
import co.leonisand.offers.OffersKit.OffersListener;

import java.util.*;

public class CategoriesFragment extends Fragment {

	public static String TITLE = "ジャンル一覧";
	private Context mContext;
	private ArrayAdapter<OffersCategory> mListAdapter;
	private ListView mListView;
	private ProgressBar mProgressBar;
	// test 

	public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
		mContext = layoutinflater.getContext();
		View view = layoutinflater.inflate(R.layout.fragment, viewgroup, false);
		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_content);
		mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressbar);

		mListView = new ListView(mContext);
		mListView.setId(android.R.id.list);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			private OffersCategory mSelectedCategory;

			public void onItemClick(AdapterView<?> adapterview, View view1, int i, long l) {

				OffersCategory offersCategory = (OffersCategory) adapterview .getItemAtPosition(i);

				StringBuilder sb = new StringBuilder();

				sb.append("id: " + offersCategory.getId() + "\n");
				sb.append("name: " + offersCategory.getName() + "\n");
				sb.append("updated_at: " + offersCategory.getUpdatedAt() + "\n");
				mSelectedCategory = offersCategory;

				((MainActivity) getActivity()).confirm("ジャンル情報", sb.toString(), "OK", null, "ジャンルのクーポンを見る", new android.content.DialogInterface.OnClickListener() {
					public void onClick(android.content.DialogInterface dialog,int result) {
						// クーポンジャンル検索させる
						Bundle bundle1 = new Bundle();
						bundle1.putInt("category_id", mSelectedCategory.getId());
						bundle1.putString("title", "ジャンルのクーポン一覧");
						CouponsFragment couponsfragment = new CouponsFragment();
						couponsfragment.setArguments(bundle1);
						getFragmentManager().executePendingTransactions();
						getFragmentManager()
						.beginTransaction()
						.add(R.id.container, couponsfragment, CouponsFragment.TITLE)
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.addToBackStack(null)
						.commit();
					}
				});

			}

		});
		mListAdapter = new ArrayAdapter<OffersCategory>(mContext, android.R.layout.simple_list_item_1) {

			public View getView(int i, View view1, ViewGroup viewgroup1) {
				OffersCategory offerscategory = (OffersCategory) getItem(i);
				View view2 = super.getView(i, view1, viewgroup1);
				((TextView) view2.findViewById(android.R.id.text1)).setText(offerscategory.getName());
				return view2;
			}
		};

		mListView.setAdapter(mListAdapter);
		linearLayout.addView(mListView);

		return view;
	}

	public void onStart() {
		super.onStart();

		mProgressBar.setVisibility(View.VISIBLE);
		OffersKit.getInstance().categories(true, new OffersListener() {

			public void onDone(Map<String, Object> map) {
				mProgressBar.setVisibility(View.GONE);
				mListAdapter.clear();
				if (map.get("categories") != null) {
					@SuppressWarnings("unchecked")
					List<OffersCategory> items = (List<OffersCategory>) map.get("categories");
					for (OffersCategory item : items) {
						mListAdapter.add(item);
					}
				} else {
					OffersStatus offersstatus = (OffersStatus) map.get("status");
					((MainActivity) getActivity()).alert("onDone", offersstatus.getCode() + ":" +  offersstatus.getMessage());
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
