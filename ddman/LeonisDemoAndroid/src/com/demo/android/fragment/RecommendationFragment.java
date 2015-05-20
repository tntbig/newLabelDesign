package com.demo.android.fragment;

import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import co.leonisand.leonis.Image.ImageListener;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersRecommendation;
import co.leonisand.offers.OffersStatus;

import com.demo.android.MainActivity;
import com.demo.android.R;

public class RecommendationFragment extends Fragment {

	public static String TITLE = "レコメンデーション";
	private ImageView mImageView;
	private ProgressBar mProgressBar;
	private OffersRecommendation mRecommendation;
	private int mRecommendation_id;
	private TextView mUnread;
	private String mType;
	private RelativeLayout mrltWebview;
	private WebView mWebView;

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

		View view = layoutinflater.inflate(
				R.layout.layout_detail_recommendation, viewgroup, false);
		mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		mrltWebview = (RelativeLayout) view.findViewById(R.id.rlt_webview);
		mWebView = (WebView) mrltWebview.findViewById(R.id.webview);
		mImageView = (ImageView) mrltWebview.findViewById(R.id.img_image);
		mWebView.requestFocusFromTouch();
		mWebView.getSettings().setJavaScriptEnabled(true);
		

		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				handler.proceed();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {		
				view.loadUrl(url);			
				return true;
			}

		});
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				Log.i("progress", String.valueOf(progress));
				if (progress < 100) {
					if (mProgressBar.getVisibility() == View.GONE) {
						mProgressBar.setVisibility(View.VISIBLE);
					}
					if(progress >50){
						if(mType.equals("RecommendedArticle")){
							mImageView.setVisibility(View.GONE);
						}
					}
					mProgressBar.setProgress(progress);
				} else {
					mProgressBar.setVisibility(View.GONE);
				}
			}
		});

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

							if (mType.equals("RecommendedArticle")) {
								OffersKit.getInstance().authenticationToken(
										new OffersListener() {
											@Override
											public void onDone(
													Map<String, Object> result) {
												if (result.get("token") != null) {
													mWebView.loadDataWithBaseURL(
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
//								mWebView.loadData(mRecommendation.getContent(),
//										"text/html; charset=utf-8", "utf-8");
								mRecommendation.imageBitmap(mImageView, new ImageListener() {

									public void onDone(View view, Bitmap bitmap) {
										((ImageView) view).setImageBitmap(bitmap);
									}

								});
							}					
							myHandler.sendEmptyMessage(0);
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

	/**
	 * handle change UI for reload gridview
	 */
	public Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mType.equals("RecommendedUrl")) {
				mImageView.setVisibility(View.GONE);
				mWebView.getSettings().setLoadWithOverviewMode(true);
				mWebView.getSettings().setUseWideViewPort(true);
				
				if (mRecommendation != null) {
					mRecommendation.actionType("view", new OffersListener() {
						public void onDone(Map<String, Object> map) {
						}

						public void onFail(Integer s) {
							((MainActivity) getActivity()).alert(
									"actionType.onFail", s.toString());
						}
					});
					mWebView.loadUrl(mRecommendation.getContent());

				}
			}
		}
	};
	public void onDestroyView() {
		super.onDestroyView();
	}
}
