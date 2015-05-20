package com.example.offerssample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Map;

import co.leonisand.leonis.Image.ImageListener;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersRecommendation;
import co.leonisand.offers.OffersStatus;

public class RecommendationFragment extends Fragment {

	public static String TITLE = "レコメンデーション";
	private Context mContext;
	private TextView mDescription;
	private TextView mDate;
	private ImageView mImageView;
	private LinearLayout mLinearLayout;
	private ProgressBar mProgressBar;
	private OffersRecommendation mRecommendation;
	private int mRecommendation_id;
	private TextView mUnread;
	private Button mUrl;
	private String mType;
	private WebView mContent;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setHasOptionsMenu(true);

		mRecommendation_id = getArguments().getInt("recommendation_id");
		mType = getArguments().getString("type");

	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater) {
		menu.clear();
		menuinflater.inflate(R.menu.main_recommendation, menu);
	}

	public boolean onOptionsItemSelected(MenuItem menuitem) {
		boolean flag = true;

		switch (menuitem.getItemId()) {
		case R.id.action_search:

			if (mRecommendation != null) {
				String s = mRecommendation.getGroups();

				((MainActivity) getActivity()).alert("店舗情報", s);
			}

			break;
		default:
			flag = super.onOptionsItemSelected(menuitem);
			break;
		}

		return flag;

	}

	@SuppressLint("SetJavaScriptEnabled")
	public View onCreateView(LayoutInflater layoutinflater,
			ViewGroup viewgroup, Bundle bundle) {

		mContext = layoutinflater.getContext();

		View view = layoutinflater.inflate(R.layout.fragment, viewgroup, false);
		LinearLayout linearLayout = (LinearLayout) view
				.findViewById(R.id.fragment_content);
		mProgressBar = (ProgressBar) getActivity().findViewById(
				R.id.progressbar);

		mLinearLayout = new LinearLayout(mContext);
		mLinearLayout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);

		mImageView = new ImageView(mContext);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				200));
		mImageView.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
		mLinearLayout.addView(mImageView);

		mDescription = new TextView(mContext);
		mLinearLayout.addView(mDescription);

		mDate = new TextView(mContext);
		mLinearLayout.addView(mDate);

		mUnread = new TextView(mContext);
		mUnread.setId(android.R.id.text1);
		mLinearLayout.addView(mUnread);

		if (mType.equals("RecommendedUrl")) {
			mUrl = new Button(mContext);
			mUrl.setId(android.R.id.button1);
			mUrl.setText("オススメ情報を見る");
			mUrl.setOnClickListener(new android.view.View.OnClickListener() {

				public void onClick(View view1) {
					if (mRecommendation == null) {
						return;
					} else {
						mRecommendation.actionType("view",
								new OffersListener() {
									public void onDone(Map<String, Object> map) {
									}

									public void onFail(Integer s) {
										((MainActivity) getActivity()).alert(
												"actionType.onFail",
												s.toString());
									}
								});

						Intent intent = new Intent(
								"android.intent.action.VIEW", Uri
										.parse(mRecommendation.getContent()));
						startActivity(intent);

						return;
					}
				}
			});
			mLinearLayout.addView(mUrl);
		} else if (mType.equals("RecommendedArticle")) {

			mContent = new WebView(mContext);
			mContent.setId(android.R.id.widget_frame);
			mContent.getSettings().setJavaScriptEnabled(true);
			mContent.setWebChromeClient(new WebChromeClient() {
				public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
					System.out.println("onConsoleMessage");
					((MainActivity) getActivity()).alert(
							"onConsoleMessage",
							consoleMessage.message() + ":"
									+ consoleMessage.lineNumber() + ":"
									+ consoleMessage.sourceId());
					return true;
				}

				@Override
				public boolean onJsAlert(WebView view, String url,
						String message, android.webkit.JsResult result) {
					try {
						((MainActivity) getActivity()).alert("onJsAlert",
								message.toString());
						return true;
					} finally {
						result.confirm();
					}
				}
			});

			mLinearLayout.addView(mContent);

		}

		linearLayout.addView(mLinearLayout);

		return view;
	}

	public void onStart() {
		super.onStart();

		mProgressBar.setVisibility(View.VISIBLE);
		OffersKit.getInstance().recommendation(mRecommendation_id, true,
				new OffersListener() {

					public void onDone(Map<String, Object> map) {
						mProgressBar.setVisibility(View.GONE);

						OffersStatus offersstatus = (OffersStatus) map
								.get("status");
						if (offersstatus != null) {
							if (offersstatus.getCode() != 0) {
								((MainActivity) getActivity()).alert(
										"recommendation.onDone",
										offersstatus.getCode() + ":"
												+ offersstatus.getMessage());
							}
						}

						if (map.get("recommendation") != null) {
							mRecommendation = (OffersRecommendation) map
									.get("recommendation");
							mRecommendation.actionType("open",
									new OffersListener() {

										public void onDone(
												Map<String, Object> map) {
											if (map.get("recommendation") != null) {
												mRecommendation = (OffersRecommendation) map
														.get("recommendation");
												TextView textview = mUnread;
												String s;
												if (mRecommendation.isUnread()) {
													s = "未読";
												} else {
													s = "既読";
												}
												textview.setText(s);
											}
										}

										public void onFail(Integer s) {
											((MainActivity) getActivity())
													.alert("actionType.onFail",
															s.toString());
										}

									});

							mDescription.setText("説明:"
									+ mRecommendation.getDescription());
							mDate.setText((new StringBuilder("利用期間:"))
									.append(mRecommendation.getDeliveryFromAt())
									.append(" 〜 ")
									.append(mRecommendation.getDeliveryToAt())
									.toString());
							TextView textview = mUnread;
							String s;
							if (mRecommendation.isUnread()) {
								s = "未読";
							} else {
								s = "既読";
							}
							textview.setText(s);

							System.out.println("getContent:"
									+ mRecommendation.getContent());

							if (mType.equals("RecommendedArticle")) {
								OffersKit.getInstance().authenticationToken(
										new OffersListener() {
											@Override
											public void onDone(
													Map<String, Object> result) {
												if (result.get("token") != null) {
													mContent.loadDataWithBaseURL(
															"http://?token="
																	+ result.get("token"),
															mRecommendation
																	.getContent(),
															"text/html",
															"utf-8", null);
												} else {
													OffersStatus offersstatus = (OffersStatus) result
															.get("status");
													((MainActivity) getActivity())
															.alert("onDone",
																	offersstatus
																			.getCode()
																			+ ":"
																			+ offersstatus
																					.getMessage());
												}
											}

											@Override
											public void onFail(Integer result) {
												((MainActivity) getActivity())
														.alert("authenticationToken.onFail",
																result.toString());
											}

										});
							}
							// mContent.loadData(mRecommendation.getContent(),
							// "text/html; charset=utf-8", "utf-8");
							mRecommendation.imageBitmap(mImageView,
									new ImageListener() {

										public void onDone(View view,
												Bitmap bitmap) {
											((ImageView) view)
													.setImageBitmap(bitmap);
										}

									});
							return;
						}
					}

					public void onFail(Integer s) {
						((MainActivity) getActivity()).alert(
								"recommendation.onFail", s.toString());
					}

				});
	}

	public void onResume() {
		super.onResume();

		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(
				TITLE);
		getActivity().supportInvalidateOptionsMenu();

	}

	public void onDestroyView() {
		if (mType.equals("RecommendedUrl")) {
			mUrl.setOnClickListener(null);
		}

		super.onDestroyView();
	}
}
