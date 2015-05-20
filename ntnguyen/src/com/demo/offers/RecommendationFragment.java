package com.demo.offers;

import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import co.leonisand.leonis.Image.ImageListener;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersRecommendation;
import co.leonisand.offers.OffersStatus;


public class RecommendationFragment extends Fragment {

	public static String TITLE = "Recommend";
	
	private ImageView imageView;
	private TextView tvTitle;
	private WebView wvRecommend;
	private ProgressBar mProgressBar;

	private OffersRecommendation mRecommendation;
	private int mRecommendation_id;
	private String mTitle;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		// setHasOptionsMenu(true);
		mRecommendation_id = getArguments().getInt("recommendation_id");
		mTitle = getArguments().getString("title");
		
	}

	@SuppressLint("ClickableViewAccessibility")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.details_recommend_layout, container, false);
		rootView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		
		/**
		 * Assign field into defined parameter
		 */
		imageView = (ImageView) rootView.findViewById(R.id.imageRecommend);
		tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
		tvTitle.setText(mTitle);
		wvRecommend = (WebView) rootView.findViewById(R.id.wvRecommend);
		wvRecommend.getSettings().setJavaScriptEnabled(false);
		
		mProgressBar = (ProgressBar) rootView.findViewById(R.id.pbRecommend);

		
		wvRecommend.setWebChromeClient(new WebChromeClient() {
			
			public void onProgressChanged(WebView view, int progress)
			{
				mProgressBar.setProgress(progress);
			}
			
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				System.out.println("onConsoleMessage");
				((MainActivity) getActivity()).alert("onConsoleMessage", consoleMessage.message() + ":" + consoleMessage.lineNumber() + ":" + consoleMessage.sourceId());
				return true;
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					android.webkit.JsResult result) {
				try {
					((MainActivity) getActivity()).alert("onJsAlert", message.toString());
					return true;
				} finally {
					result.confirm();
				}
			}
		});
		
		wvRecommend.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				return super.shouldOverrideUrlLoading(view, url);
			}
		});

		return rootView;
	}

	public void onStart() {

		super.onStart();
		mProgressBar.setVisibility(View.VISIBLE);
		OffersKit.getInstance().recommendation(mRecommendation_id, true,
				new OffersListener() {
					public void onDone(Map<String, Object> map) {
						mProgressBar.setVisibility(View.GONE);
						if (map.get("recommendation") != null) {
							mRecommendation = (OffersRecommendation) map.get("recommendation");
							mRecommendation.actionType("open",
									new OffersListener() {

										public void onDone(
												Map<String, Object> map) {

										}

										public void onFail(Integer s) {
											((MainActivity) getActivity()).alert("actionType.onFail", s.toString());
										}

									});
							
							OffersKit.getInstance().authenticationToken(
									new OffersListener() {
										@Override
										public void onDone(
												Map<String, Object> result) {
											/**
											 * Check for token device
											 */
											if (result.get("token") != null) {
												/**
												 * Check for type of Recommend
												 * If Recommend is URL type will be displayed on webivew
												 * If Recommend is another type will be displayed with text & image base on URL data
												 */
												if(!mRecommendation.getType().equals("RecommendedUrl")){
													wvRecommend.loadDataWithBaseURL("http://?token="+ result.get("token"), mRecommendation.getContent(), "text/html", "utf-8", null);
												}else{
													imageView.setVisibility(View.GONE);
													tvTitle.setVisibility(View.GONE);
//													wvRecommend.loadDataWithBaseURL("http://?token="+ result.get("token"), mRecommendation.getContent(), "text/html", "utf-8", null);
													wvRecommend.loadUrl(mRecommendation.getContent());
												}
											} else {
												OffersStatus offersstatus = (OffersStatus) result.get("status");
												((MainActivity) getActivity()).alert("onDone", offersstatus.getCode() + ":" + offersstatus.getMessage());
											}
										}

										@Override
										public void onFail(Integer result) {
											((MainActivity) getActivity()).alert("authenticationToken.onFail", result.toString());
										}

									});

							mRecommendation.imageBitmap(imageView,
									new ImageListener() {

										public void onDone(View view,
												Bitmap bitmap) {
											((ImageView) view).setImageBitmap(bitmap);
										}

									});
							return;
						} else {
							OffersStatus offersstatus = (OffersStatus) map.get("status");
							((MainActivity) getActivity()).alert("onDone", offersstatus.getCode() + ":" + offersstatus.getMessage());
							return;
						}
					}

					public void onFail(Integer s) {
						mProgressBar.setVisibility(View.GONE);
						((MainActivity) getActivity()).alert("recommendation.onFail", s.toString());
					}

				});
	}
}
