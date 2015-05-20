package com.example.offerssample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Map;

import co.leonisand.leonis.Image.ImageListener;
import co.leonisand.offers.OffersCoupon;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersTemplate;

public class PreviewCouponFragment extends Fragment {

	public static String TITLE = "プレビュークーポン";
	private Context mContext;
	private OffersCoupon mCoupon;
	private String mPreviewKey;
	private TextView mDate;
	private TextView mDetail;
	private ImageView mImageView;
	private LinearLayout mLinearLayout;
	private ProgressBar mProgressBar;
	private ImageView mSuccessimage;
	private TextView mTitle;
	private TextView mType;
	private TextView mUsed;

	public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
		mContext = layoutinflater.getContext();

		View view = layoutinflater.inflate(R.layout.fragment, viewgroup, false);

		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_content);
		mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressbar);

		mPreviewKey = getArguments().getString("previewKey");


		mLinearLayout = new LinearLayout(mContext);
		mLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);

		mTitle = new TextView(mContext);
		mLinearLayout.addView(mTitle);

		mImageView = new ImageView(mContext);
		mImageView.setLayoutParams(new LayoutParams(-1, 200));
		mImageView.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
		mLinearLayout.addView(mImageView);

		mDate = new TextView(mContext);
		mLinearLayout.addView(mDate);

		mDetail = new TextView(mContext);
		mDetail.setBackgroundColor(-1);
		mDetail.setMinLines(5);
		mLinearLayout.addView(mDetail);

		mType = new TextView(mContext);
		mLinearLayout.addView(mType);

		mUsed = new TextView(mContext);
		mLinearLayout.addView(mUsed);

		mSuccessimage = new ImageView(mContext);
		mSuccessimage.setLayoutParams(new LayoutParams(-1, 200));
		mSuccessimage.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
		mLinearLayout.addView(mSuccessimage);

		linearLayout.addView(mLinearLayout);

		return view;
	}



	public void onStart() {
		super.onStart();

		mProgressBar.setVisibility(View.VISIBLE);
		OffersKit.getInstance().previewCoupon(mPreviewKey, new OffersListener() {

			public void onDone(Map<String, Object> map) {
				mProgressBar.setVisibility(View.GONE);
				if (map.get("coupon") != null) {
					mCoupon = (OffersCoupon) map.get("coupon");
					mCoupon.setAlreadyRead();

					mTitle.setText((new StringBuilder("(")).append(mCoupon.getCategory()).append(")").append(mCoupon.getTitle()).toString());
					mDate.setText((new StringBuilder("利用期間:")).append(mCoupon.getAvailableFrom()).append(" 〜 ").append(mCoupon.getAvailableTo()).append("(配信中)").append("残り :")
							.append(mCoupon.getQuantity()).toString());
					mDetail.setText(mCoupon.getDescription());
					mCoupon.imageBitmap(mImageView, new ImageListener() {

						public void onDone(View view, Bitmap bitmap) {
							((ImageView) view).setImageBitmap(bitmap);
						}
					});

					mCoupon.template(new OffersListener() {

						public void onDone(Map<String, Object> map) {
							if ( map.get("template") != null) {
								OffersTemplate template = (OffersTemplate) map.get("template");

								@SuppressWarnings("unchecked")
								Map<String, Object> map1 = (Map<String, Object>) template.getValues().get("background");
								mLinearLayout.setBackgroundColor(Color.parseColor((String) map1.get("color")));
								return;
							} else {
								((MainActivity) getActivity()).alert("template.onDone", map.toString());
								return;
							}
						}

						public void onFail(Integer s) {
							((MainActivity) getActivity()).alert("template.onFail", s.toString());
						}

					});
					mType.setText(mCoupon.getCouponType());

					mSuccessimage.setImageBitmap(null);

					if (mCoupon.isUsed()) {
						mUsed.setText((new StringBuilder("使用済:")).append(mCoupon.getUsedDate()).toString());
						mCoupon.applySuccessImageBitmap(mSuccessimage, new ImageListener() {

							public void onDone(View view, Bitmap bitmap) {
								((ImageView) view).setImageBitmap(bitmap);
							}

						});

					} else if (!mCoupon.isAvailable()){
						mUsed.setText("利用期間外");
					}else{
						mUsed.setText("未使用");
					}


				} else {

					((MainActivity) getActivity()).alert("coupon", map.toString());

				}
			}

			public void onFail(Integer s) {
				((MainActivity) getActivity()).alert("coupon.onFail", s.toString());
			}

		});
	}
	
	public void onResume(){
		super.onResume();
		
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(TITLE);
		getActivity().supportInvalidateOptionsMenu();

	}

	public void onStop() {
		super.onStop();
	}


	public void onDestroyView() {
		super.onDestroyView();
	}

}
