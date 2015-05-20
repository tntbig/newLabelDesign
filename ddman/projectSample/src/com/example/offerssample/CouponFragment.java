package com.example.offerssample;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import co.leonisand.leonis.AutoStopScrollView;
import co.leonisand.leonis.Image.ImageListener;
import co.leonisand.offers.OffersCoupon;
import co.leonisand.offers.OffersCouponType;
import co.leonisand.offers.OffersCouponView;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersStatus;
import co.leonisand.offers.OffersTemplate;

public class CouponFragment extends Fragment implements OffersCouponView.OffersCouponViewListener {

	public static String TITLE = "クーポン";
	private DialogFragment mAlertDialog;
	private Context mContext;
	private OffersCoupon mCoupon;
	private int mCoupon_id;
	private TextView mDate;
	private TextView mDetail;
	private EditText mEditText;
	private ImageView mImageView;
	private LinearLayout mLinearLayout;
	private ProgressBar mProgressBar;
	private OffersCouponView mStampView;
	private ImageView mSuccessimage;
	private Timer mTimerLive;
	private TextView mTitle;
	private TextView mType;
	private TextView mUsed;
	private TextView mProductCode;
	private TextView mBarcode;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setHasOptionsMenu(true);

		mCoupon_id = getArguments().getInt("coupon_id");
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater) {
		menu.clear();
		menuinflater.inflate(R.menu.main_coupon, menu);
	}


	public boolean onOptionsItemSelected(MenuItem menuitem) {
		boolean flag = true;

		switch (menuitem.getItemId()) {
		case R.id.action_search:

			if (mCoupon != null) {
				String s = mCoupon.getGroups();

				((MainActivity) getActivity()).alert("店舗情報", s);

			}

			break;
		case R.id.action_discard:

			if (mCoupon != null) {

				mProgressBar.setVisibility(View.VISIBLE);

				mCoupon.revert(new OffersListener() {
					public void onDone(Map<String, Object> map) {

						mProgressBar.setVisibility(View.GONE);

						OffersStatus offersstatus = (OffersStatus) map.get("status");
						if (offersstatus.getCode() == 0) {
							onStart();
						} else {
							((MainActivity) getActivity()).alert("revert.onDone", offersstatus.getCode() + ":" +  offersstatus.getMessage());
						}
					}

					public void onFail(Integer s1) {

						mProgressBar.setVisibility(View.GONE);

						((MainActivity) getActivity()).alert("revert.onFail", s1.toString());
					}
				});

			}
			break;

		case R.id.action_refresh:
			onStart();
			break;
		default:
			flag = super.onOptionsItemSelected(menuitem);
			break;
		}

		return flag;

	}

	public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
		mContext = layoutinflater.getContext();


		View view = layoutinflater.inflate(R.layout.fragment, viewgroup, false);

		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_content);
		mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressbar);

		AutoStopScrollView mScrollView = new AutoStopScrollView(mContext);
		mScrollView.setFillViewport(true);

		FrameLayout mFramelayout = new FrameLayout(mContext);

		mLinearLayout = new LinearLayout(mContext);
		mLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);

		mTitle = new TextView(mContext);
		mTitle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
		mTitle.setId(android.R.id.text1);

		mLinearLayout.addView(mTitle);

		mImageView = new ImageView(mContext);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 200));
		mImageView.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
		mLinearLayout.addView(mImageView);

		mProductCode = new TextView(mContext);
		mProductCode.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
		mLinearLayout.addView(mProductCode);

		mBarcode = new TextView(mContext);
		mBarcode.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
		mLinearLayout.addView(mBarcode);


		mDate = new TextView(mContext);
		mDate.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
		mLinearLayout.addView(mDate);

		mDetail = new TextView(mContext);
		mDetail.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 200));
		mDetail.setBackgroundColor(Color.WHITE);
		//mDetail.setMinLines(5);
		mLinearLayout.addView(mDetail);

		mType = new TextView(mContext);
		mType.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
		mType.setId(android.R.id.hint);


		mType.setOnClickListener(new android.view.View.OnClickListener() {
			public void onClick(View view1) {
				if (mCoupon.usable()) {

					if(mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeTap
							|| mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeCustom){
						// 即刻消し込み
						apply(null);
					}
					else {

						// 暗証番号確認ダイアログ
						mEditText = new EditText(mContext);
						mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
						mEditText.setId(android.R.id.edit);
						mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
							public boolean onEditorAction(TextView textview, int i, KeyEvent keyevent) {
								mAlertDialog.dismiss();
								return false;
							}
						});

						mAlertDialog = ((MainActivity) getActivity()).viewConfirm("暗証番号を入力してください。", mEditText, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialoginterface, int i) {
								if (mCoupon.getSecretsList().contains(mEditText.getText().toString())) {
									apply(mEditText.getText().toString());
								} else {
									((MainActivity) getActivity()).alert("エラー", "暗証番号が一致しません");
								}
							}
						}, getString(android.R.string.cancel), null, new DialogInterface.OnShowListener() {
							public void onShow(DialogInterface dialoginterface) {
								((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(mEditText, InputMethodManager.RESULT_UNCHANGED_SHOWN);
							}
						});

					}
				}
			}
		});
		mLinearLayout.addView(mType);

		mUsed = new TextView(mContext);
		mUsed.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
		mUsed.setId(android.R.id.toggle);
		mLinearLayout.addView(mUsed);

		mSuccessimage = new ImageView(mContext);
		mSuccessimage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 200));
		mSuccessimage.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
		mLinearLayout.addView(mSuccessimage);

		mFramelayout.addView(mLinearLayout);

		mStampView = new OffersCouponView(mContext);
		mStampView.debug = true;
		mStampView.setVisibility(View.GONE);
		mStampView.setOffersCouponViewListener(this);
		mStampView.setGestureRecognitionAreaRatio(30);
		mFramelayout.addView(mStampView);

		mScrollView.addView(mFramelayout);

		linearLayout.addView(mScrollView);

		return view;
	}



	public void onStart() {
		super.onStart();

		//位置情報取得開始
		OffersKit.getInstance().startLocation();

		if (mTimerLive != null) {
			mTimerLive.cancel();
			mTimerLive.purge();
		}
		mTimerLive = new Timer();

		mProgressBar.setVisibility(View.VISIBLE);
		OffersKit.getInstance().coupon(mCoupon_id, true, new OffersListener() {

			public void onDone(Map<String, Object> map) {
				mProgressBar.setVisibility(View.GONE);

				OffersStatus offersstatus = (OffersStatus) map.get("status");
				if(offersstatus != null) {
					if(offersstatus.getCode() != 0){
						((MainActivity) getActivity()).alert("coupon.onDone", offersstatus.getCode() + ":" + offersstatus.getMessage());
					}
				}

				if (map.get("coupon") != null) {
					mCoupon = (OffersCoupon) map.get("coupon");
					mCoupon.setAlreadyRead();

					//商品番号
					String productCodes = "";
					if(mCoupon.getProductsList() != null){
						for(int i=0; i<mCoupon.getProductsList().size(); i++){
							Map<String, Object> product = mCoupon.getProductsList().get(i);
							productCodes += product.get("code").toString() + ";";
						}
					}
					mProductCode.setText("商品番号:"+productCodes);

					//バーコード
					String barcodes = "";
					if(mCoupon.getAliasCodesList() != null){
						for(int i=0; i<mCoupon.getAliasCodesList().size(); i++){
							Map<String, Object> alias = mCoupon.getAliasCodesList().get(i);
							barcodes += alias.get("code").toString() + ";";
						}
					}
					mBarcode.setText("バーコード番号:"+barcodes);


					mTitle.setText((new StringBuilder("(")).append(mCoupon.getCategory()).append(")").append(mCoupon.getTitle()).toString());
					mDate.setText("利用期間:"+mCoupon.getAvailableFrom()+" 〜 "+mCoupon.getAvailableTo()+"(配信中) "+"残り :"+((mCoupon.getAvailableLimit()==0) ? "∞" : mCoupon.getQuantity()));
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
							} else {
								((MainActivity) getActivity()).alert("template.onDone", map.toString());
							}
						}

						public void onFail(Integer s) {
							((MainActivity) getActivity()).alert("template.onFail", s.toString());
						}

					});
					mType.setText(mCoupon.getCouponType());

					if(mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeStamp
							|| mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeGesture){

						mCoupon.setCouponView(mStampView);

						mStampView.setVisibility(View.VISIBLE);
					} else {
						mStampView.setVisibility(View.GONE);
					}
					mSuccessimage.setImageBitmap(null);

					if (mCoupon.isUsed()) {
						mUsed.setText((new StringBuilder("使用済:")).append(mCoupon.getUsedDate()).toString());
						mCoupon.applySuccessImageBitmap(mSuccessimage, new ImageListener() {

							public void onDone(View view, Bitmap bitmap) {
								((ImageView) view).setImageBitmap(bitmap);
							}

						});
						if( mCoupon.getReusable() == true ) {
							mUsed.append("※再利用可能");
							String reusable_time = mCoupon.getScheduledReusableTime();
							if( reusable_time != null) {
								mUsed.append(String.format("(次回: %s)", reusable_time ));
							}
						}
						mUsed.append(String.format("[%s]", mCoupon.usable() ? "つかえます" : ( mCoupon.getReusable() == true ? "まだです" : "もう無理" ) ) );
					} else if (!mCoupon.isAvailable()){
						mUsed.setText("利用期間外");
					}else{
						mUsed.setText("未使用");
					}

					final Handler mHandler = new Handler();

					if(mTimerLive == null){
						return;
					}

					mTimerLive.schedule(new TimerTask() {
						public void run() {
							mHandler.post(new Runnable() {

								public void run() {
									mCoupon.live(new OffersListener() {

										public void onDone(Map<String, Object> map) {
											if (((OffersStatus) map.get("status")).getCode() == 0) {
												mCoupon = (OffersCoupon) map.get("coupon");
												mDate.setText("利用期間:"+mCoupon.getAvailableFrom()+" 〜 "+mCoupon.getAvailableTo()+"(配信中) "+"残り :"+((mCoupon.getAvailableLimit()==0) ? "∞" : mCoupon.getQuantity()));
											}
										}

										public void onFail(Integer s) {
										}

									});
								}
							});
						}
					}, 30000, 60000);
				}
			}

			public void onFail(Integer s) {

				mProgressBar.setVisibility(View.GONE);
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

		//位置情報取得停止
		OffersKit.getInstance().stopLocation();

		mTimerLive.cancel();
		mTimerLive.purge();
		mTimerLive = null;
	}


	public void onDestroyView() {
		if(mEditText!=null) {
			mEditText.setOnEditorActionListener(null);
		}
		mType.setOnClickListener(null);
		super.onDestroyView();
	}

	@Override
	public void releaseStamp(List<Map<String, Object>> list) {
		if(mCoupon.getCouponTypeCode() != OffersCouponType.OffersCouponTypeCodeStamp){
			return;
		}
	}

	@Override
	public void afterStamp(Map<String, Object> map) {
		if(mCoupon.getCouponTypeCode() != OffersCouponType.OffersCouponTypeCodeStamp){
			return;
		}

		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("stamp_id", map.get("stamp_id"));
		hashmap.put("x", map.get("x"));
		hashmap.put("y", map.get("y"));
		hashmap.put("angle", map.get("angle"));
		hashmap.put("width", map.get("width"));
		hashmap.put("height", map.get("height"));
		hashmap.put("stamped_at", map.get("stamped_at"));

		apply(map);
	}

	@Override
	public void beforeStamp(Map<String, Object> map) {
		if(mCoupon.getCouponTypeCode() != OffersCouponType.OffersCouponTypeCodeStamp){
			return;
		}

		Map<String, Object> hashmap = new HashMap<String, Object>();
		//hashmap.put("x", Integer.valueOf(160));
		//hashmap.put("y", Integer.valueOf(240));
		//hashmap.put("angle", Integer.valueOf(0));
		hashmap.put("width", Float.valueOf(Float.parseFloat(map.get("width").toString()) / 2.0F));
		hashmap.put("height", Float.valueOf(Float.parseFloat(map.get("height").toString()) / 2.0F));
		mStampView.setStampedOption(hashmap);
	}


	@Override
	public void drawStamp(Map<String, Object> arg0, Canvas arg1) {
		if(mCoupon.getCouponTypeCode() != OffersCouponType.OffersCouponTypeCodeStamp){
			return;
		}
	}

	@Override
	public void gestureSuccess(Map<String, Object> map) {
		if(mCoupon.getCouponTypeCode() != OffersCouponType.OffersCouponTypeCodeGesture){
			return;
		}

		((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100L);

		apply(null);
	}

	@Override
	public void matchStamp(Map<String, Object> map) {
		if(mCoupon.getCouponTypeCode() != OffersCouponType.OffersCouponTypeCodeStamp){
			return;
		}

		((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100L);
	}


	private void apply(Object s) {
		mProgressBar.setVisibility(View.VISIBLE);

		if(mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeTap
				|| mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeGesture ){
			mCoupon.apply(new OffersListener() {

				public void onDone(Map<String, Object> map) {
					mProgressBar.setVisibility(View.GONE);
					if (map.get("coupon") != null) {
						mCoupon = (OffersCoupon) map.get("coupon");
						onStart();
						return;
					} else {
						OffersStatus status = (OffersStatus) map.get("status");
						((MainActivity) getActivity()).alert("apply.onDone", status.getMessage());
						return;
					}
				}

				public void onFail(Integer s1) {
					((MainActivity) getActivity()).alert("apply.onFail", s1.toString());
				}

			});
		}else if(mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeIdentification){
			mCoupon.applyWithSecret((String)s, new OffersListener() {

				public void onDone(Map<String, Object> map) {
					mProgressBar.setVisibility(View.GONE);
					if (map.get("coupon") != null) {
						mCoupon = (OffersCoupon) map.get("coupon");
						onStart();
						return;
					} else {
						OffersStatus status = (OffersStatus) map.get("status");
						((MainActivity) getActivity()).alert("apply.onDone", status.getMessage());
						return;
					}
				}

				public void onFail(Integer s1) {
					((MainActivity) getActivity()).alert("apply.onFail", s1.toString());
				}

			});
		}else if(mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeStamp){

			mCoupon.applyWithStamp((Map<String, Object>)s, new OffersListener() {

				public void onDone(Map<String, Object> map) {
					mProgressBar.setVisibility(View.GONE);

					if (map.get("coupon") != null) {
						mCoupon = (OffersCoupon) map.get("coupon");
						onStart();
						return;
					} else {
						OffersStatus status = (OffersStatus) map.get("status");
						((MainActivity) getActivity()).alert("apply.onDone", status.getMessage());
						return;
					}
				}

				public void onFail(Integer s1) {
					((MainActivity) getActivity()).alert("apply.onFail", s1.toString());
				}

			});

		}

	}


}
