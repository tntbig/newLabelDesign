package com.example.offerssample;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
import co.leonisand.offers.OffersCategory;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;

import java.util.*;

public class LocalCategoriesFragment extends Fragment {

	public static String TITLE = "(ローカル)ジャンル一覧";
	private Context mContext;
	private ArrayAdapter<OffersCategory> mListAdapter;
	private ListView mListView;

	public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
		mContext = layoutinflater.getContext();
		View view = layoutinflater.inflate(R.layout.fragment, viewgroup, false);
		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_content);

		mListView = new ListView(mContext);
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

		OffersKit.getInstance().categories(false, new OffersListener() {

			public void onDone(Map<String, Object> map) {
				mListAdapter.clear();

				if (map.get("categories") != null) {
					@SuppressWarnings("unchecked")
					List<OffersCategory> items = (List<OffersCategory>) map.get("categories");
					for (OffersCategory item : items) {
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
