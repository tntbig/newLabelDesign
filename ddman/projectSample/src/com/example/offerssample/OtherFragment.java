package com.example.offerssample;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.leonisand.offers.OffersCoupon;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersStatus;
import co.leonisand.platform.Leonis;

public class OtherFragment extends Fragment {


	public static String TITLE = "その他";
	private DialogFragment mAlertDialog;
	private Context mContext;
	private ArrayAdapter<Map<String, Object>> mListAdapter;
	private ListView mListView;
	private ProgressBar mProgressBar;
	private EditText mEditText;

	public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
		mContext = layoutinflater.getContext();
		View view = layoutinflater.inflate(R.layout.fragment, viewgroup, false);
		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_content);
		mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressbar);

		mListView = new ListView(mContext);
		mListView.setId(android.R.id.list);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterview, View view1, int i, long l) {

				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) adapterview.getItemAtPosition(i);
				String s = (String) map.get("title");

				if (map.get("fragment") == null) {

					if (map.get("title").equals("キャンペーンコード入力")) {
						mEditText = new EditText(mContext);
						mEditText.setId(android.R.id.edit);
						mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

							public boolean onEditorAction(TextView textview, int i, KeyEvent keyevent) {
								mAlertDialog.dismiss();
								return false;
							}

						});

						mAlertDialog = ((MainActivity) getActivity()).viewConfirm("キャンペーンコードを入力してください", mEditText, getString(android.R.string.ok), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {

								String as[] = mEditText.getText().toString().split("-");
								OffersKit.getInstance().campaignCoupons(as, new OffersListener() {

									public void onDone(Map<String, Object> map) {
										if (map.get("coupons") != null) {

											@SuppressWarnings("unchecked")
											List<OffersCoupon> list = (List<OffersCoupon>) map.get("coupons");
											((MainActivity) getActivity()).alert("GET!!", list.toString());
										} else {
											OffersStatus offersstatus = (OffersStatus) map.get("status");
											((MainActivity) getActivity()).alert("campaignCoupons.onDone", offersstatus.getCode() + ":" + offersstatus.getMessage());
										}
									}

									public void onFail(Integer s) {
										((MainActivity) getActivity()).alert("campaignCoupons.onFail", s.toString());
									}

								});
							}
						}, getString(android.R.string.cancel), null, new DialogInterface.OnShowListener() {
							public void onShow(DialogInterface dialoginterface) {
								((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(mEditText, InputMethodManager.RESULT_UNCHANGED_SHOWN);
							}
						});

						return;
					} else if (map.get("title").equals("限定クーポンのアンロック")) {
						mProgressBar.setVisibility(View.VISIBLE);
						OffersKit.getInstance().unlockCoupons(new OffersListener() {

							public void onDone(Map<String, Object> map) {
								mProgressBar.setVisibility(View.GONE);
								if (map.get("coupons") != null) {

									@SuppressWarnings("unchecked")
									List<OffersCoupon> list = (List<OffersCoupon>) map.get("coupons");
									((MainActivity) getActivity()).alert("GET!!", list.toString());
									return;
								} else {

									OffersStatus offersstatus = (OffersStatus) map.get("status");
									((MainActivity) getActivity()).alert("unlockCoupons.onDone", offersstatus.getCode() + ":" + offersstatus.getMessage());
									return;
								}
							}

							public void onFail(Integer s) {
								((MainActivity) getActivity()).alert("unlockCoupons.onFail", s.toString());
							}

						});
					} else if (map.get("title").equals("リセット")) {

						((MainActivity) getActivity()).confirm("リセット", "本当にリセットしますか？", getString(android.R.string.ok), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
								OffersKit.getInstance().resetContent();
							}
						}, getString(android.R.string.cancel), null);

					} else if (map.get("title").equals("ユーザー削除")) {

						((MainActivity) getActivity()).confirm("ユーザー削除", "本当にユーザー削除しますか？", getString(android.R.string.ok), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
								Leonis.getInstance().resetAll();

								OffersKit.getInstance().resetAll();
								OffersKit.getInstance().requestsCancel();

								//擬似UID
								String newUid = new BigInteger(64, new SecureRandom()).toString(16);
								OffersKit.getInstance().setUid(newUid);

								OffersKit.getInstance().authenticationToken(new OffersListener(){
									@Override
									public void onDone(Map<String, Object> arg0) {

										((MainActivity) getActivity()).alert("ユーザーを再作成しました。", "ユーザーを再作成しました。");

									}
									@Override
									public void onFail(Integer arg0) {

										((MainActivity) getActivity()).alert("エラー", "ユーザー再作成に失敗しました。");


									}
								});
							}
						}, getString(android.R.string.cancel), null);
					}
				} else {

					Fragment obj = null;

					if (map.get("fragment") == BlockFragment.class) {
						obj = new BlockFragment();
					} else if (map.get("fragment") == CategoriesFragment.class) {
						obj = new CategoriesFragment();
					} else if (map.get("fragment") == LocalCategoriesFragment.class) {
						obj = new LocalCategoriesFragment();
					} else if (map.get("fragment") == GroupsFragment.class) {
						obj = new GroupsFragment();
					} else if (map.get("fragment") == LocalGroupsFragment.class) {
						obj = new LocalGroupsFragment();
					} else if (map.get("fragment") == RecommendationsFragment.class) {
						obj = new RecommendationsFragment();
					} else if (map.get("fragment") == OffersFragment.class) {
						obj = new OffersFragment();
					} else if (map.get("fragment") == StampCardsFragment.class) {
						obj = new StampCardsFragment();
					}

					getFragmentManager()
					.beginTransaction()
					.add(R.id.container, ((Fragment) (obj)), s)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.addToBackStack(null)
					.commit();

				}

			}
		});
		mListAdapter = new ArrayAdapter<Map<String, Object>>(mContext, android.R.layout.simple_list_item_1) {

			public View getView(int i, View view1, ViewGroup viewgroup1) {
				Map<String, Object> item = getItem(i);
				TextView textview = (TextView) super.getView(i, view1, viewgroup1);
				textview.setText((String) item.get("title"));
				return textview;
			}

		};
		mListView.setAdapter(mListAdapter);
		linearLayout.addView(mListView);

		return view;
	}


	public void onStart() {
		super.onStart();

		mListAdapter.clear();

		Map<String, Object> hashmap;
		hashmap = new HashMap<String, Object>();
		hashmap.put("title", CategoriesFragment.TITLE);
		hashmap.put("fragment", CategoriesFragment.class);
		mListAdapter.add(hashmap);

		hashmap = new HashMap<String, Object>();
		hashmap.put("title", LocalCategoriesFragment.TITLE);
		hashmap.put("fragment", LocalCategoriesFragment.class);
		mListAdapter.add(hashmap);

		hashmap = new HashMap<String, Object>();
		hashmap.put("title", GroupsFragment.TITLE);
		hashmap.put("fragment", GroupsFragment.class);
		mListAdapter.add(hashmap);

		hashmap = new HashMap<String, Object>();
		hashmap.put("title", LocalGroupsFragment.TITLE);
		hashmap.put("fragment", LocalGroupsFragment.class);
		mListAdapter.add(hashmap);

		hashmap = new HashMap<String, Object>();
		hashmap.put("title", BlockFragment.TITLE);
		hashmap.put("fragment", BlockFragment.class);
		mListAdapter.add(hashmap);

		hashmap = new HashMap<String, Object>();
		hashmap.put("title", "キャンペーンコード入力");
		mListAdapter.add(hashmap);

		hashmap = new HashMap<String, Object>();
		hashmap.put("title", "限定クーポンのアンロック");
		mListAdapter.add(hashmap);

		hashmap = new HashMap<String, Object>();
		hashmap.put("title", "リセット");
		mListAdapter.add(hashmap);

		hashmap = new HashMap<String, Object>();
		hashmap.put("title", "ユーザー削除");
		mListAdapter.add(hashmap);
	}

	public void onResume(){
		super.onResume();
		
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(TITLE);
		getActivity().supportInvalidateOptionsMenu();

	}

	public void onDestroyView() {
		if(mEditText!=null) {
			mEditText.setOnEditorActionListener(null);
		}
		mListView.setAdapter(null);
		mListAdapter = null;
		super.onDestroyView();
	}
}
