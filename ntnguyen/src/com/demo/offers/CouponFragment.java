package com.demo.offers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import co.leonisand.leonis.Image.ImageListener;
import co.leonisand.offers.OffersCoupon;
import co.leonisand.offers.OffersCouponType;
import co.leonisand.offers.OffersCouponView;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.offers.OffersStatus;

import com.demo.utils.CustomScrollView;
import com.demo.utils.Utils;

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
	private OffersCouponView offerGestureView;
	private ProgressBar mProgressBar;
	private ImageView mSuccessimage;
	private Timer mTimerLive;
	private TextView mTitle;
	private TextView mUsed;
	private TextView mCondition;
	private TextView mShop;
	private ImageButton mButton;

	private RelativeLayout lineNotUsed;
	private LinearLayout lineButton;
	private RelativeLayout lineSuccess;
	private CustomScrollView mScrollView;
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
						/**
						 * Check Coupon's status
						 */
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

		// Get available screen solution.
		DisplayMetrics display = this.getResources().getDisplayMetrics(); 		
		int screenHeight = display.heightPixels;
		int screenWidth = display.widthPixels;
		
		mContext = layoutinflater.getContext();
		
		View view = layoutinflater.inflate(R.layout.details_coupon_layout, viewgroup, false);
		RelativeLayout mLineLayout = (RelativeLayout) view.findViewById(R.id.details_line_content);
		lineButton = (LinearLayout) view.findViewById(R.id.line_button_img);
				
		mScrollView = (CustomScrollView) view.findViewById(R.id.details_scroll);

		mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressbar);
		
		lineSuccess = (RelativeLayout) view.findViewById(R.id.line_success);
		lineNotUsed = (RelativeLayout) view.findViewById(R.id.line_details_not_used);
		
		mButton = (ImageButton)view.findViewById(R.id.details_button_active);
		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/**
				 * Check coupon type code.
				 */
				if(mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeTap
						|| mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeCustom){
					apply(null);
				}
				else {
					
					// Dialog PIN
					mEditText = new EditText(mContext);
					mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
					mEditText.setId(android.R.id.edit);
					mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
						public boolean onEditorAction(TextView textview, int i, KeyEvent keyevent) {
							mAlertDialog.dismiss();
							return false;
						}
					});
					
					/**
					 * Use dialog for add coupon PIN
					 */
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
		});

		/**
		 * link coupon's title to screen
		 */
		mTitle = (TextView) view.findViewById(R.id.details_title);

		/**
		 * Link coupon's image to screen
		 */
		mImageView = (ImageView)view.findViewById(R.id.details_img);
		/*LayoutParams params = mImageView.getLayoutParams();*/
		/*params.height = screenHeight/2 - mTitle.getHeight();
		mImageView.setScaleType(ScaleType.FIT_CENTER);
		params.width = screenWidth;*/
		/*mImageView.setMinimumHeight(screenHeight/3);
		mImageView.setLayoutParams(params);*/
		mImageView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				mScrollView.setScrollingEnabled(false);
				offerGestureView.setVisibility(View.VISIBLE);
				return false;
			}
		});
		
		/**
		 * Link coupon's experience date to screen
		 */
		mDate = (TextView) view.findViewById(R.id.details_valid_date);
		mShop = (TextView) view.findViewById(R.id.details_shop);
		mUsed = (TextView) view.findViewById(R.id.toggle);
		
		/**
		 * Link Coupon's condition to screen
		 */
		mCondition = (TextView)view.findViewById(R.id.details_condition);
		
		/**
		 * Link coupon's details to screen
		 */
		mDetail = (TextView)view.findViewById(R.id.details_description_cp);

		/**
		 * Link Coupon's success image to screen
		 */
		mSuccessimage = (ImageView) view.findViewById(R.id.imSuccessOffer);

		/**
		 * Initial stamp
		 */
		offerGestureView = new OffersCouponView(mContext);
		offerGestureView.debug = false;
		offerGestureView.setVisibility(View.GONE);
		offerGestureView.setOffersCouponViewListener(this);
		offerGestureView.setGestureRecognitionAreaRatio(10);
		mLineLayout.addView(offerGestureView);

		return view;
	}



	public void onStart() {
		super.onStart();

		//OffersKit start location
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
				
				/**
				 * Check coupon received
				 */
				if (map.get("coupon") != null) {
					/**
					 * Set coupon's information
					 */
					
					mCoupon = (OffersCoupon) map.get("coupon");
					System.out.println("===================== set coupon already read ======================");
					mCoupon.setAlreadyRead();
					
					mTitle.setText((new StringBuilder("(")).append(mCoupon.getCategory()).append(")").append(mCoupon.getTitle()).toString());
					if(mCoupon.getQuantity() > 0){
						mDate.setText(new StringBuilder("残り枚数 : ")
							.append(mCoupon.getQuantity())
								.append("\n残り時間 : ")
									.append(Utils.getExpireDate(new Date(), mCoupon)));
					}else{
						mDate.setText(new StringBuilder("残り枚数 : ∞ 枚")
							.append("\n残り時間 : ")
								.append(Utils.getExpireDate(new Date(), mCoupon)));
					}
					JSONObject json;
					try {
						JSONArray jsonObj = new JSONArray(mCoupon.getGroups());
						json = jsonObj.getJSONObject(0);
						mShop.setText(json.getString("name"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mDetail.setText(mCoupon.getDescription());
					mCondition.setText(mCoupon.getUseConditionDescription());
					
					mCoupon.imageBitmap(mImageView, new ImageListener() {

						public void onDone(View view, Bitmap bitmap) {
							((ImageView) view).setImageBitmap(bitmap);
						}
					});
					
					/**
					 * Check coupon type code.
					 * Base on type, coupon will have separate type confirm.
					 */
					System.out.println("======== Coupon Type :"+ mCoupon.getCouponType() + " =========");
					System.out.println("======== Coupon used :"+ mCoupon.isUsed() + " =========");
					System.out.println("======== Coupon usable :"+ mCoupon.usable() + " =========");
					if(mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeStamp
							|| mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeGesture
								|| mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeCustom
									|| mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeShow){
						mCoupon.setCouponView(offerGestureView);
						lineButton.setVisibility(View.GONE);
					}else if (mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeIdentification){
						if(mCoupon.isUsed() && !mCoupon.usable()){
							lineButton.setVisibility(View.VISIBLE);
							lineSuccess.setVisibility(View.GONE);
							lineNotUsed.setVisibility(View.VISIBLE);
						}else{
							lineButton.setVisibility(View.GONE);
							lineSuccess.setVisibility(View.VISIBLE);
							lineNotUsed.setVisibility(View.GONE);
						}
						offerGestureView.setVisibility(View.GONE);
					}else if (mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeShow){
						lineButton.setVisibility(View.GONE);
						lineSuccess.setVisibility(View.GONE);
						lineNotUsed.setVisibility(View.VISIBLE);
						offerGestureView.setVisibility(View.GONE);
					}else{
						offerGestureView.setVisibility(View.GONE);
					}
//					mSuccessimage.setImageBitmap(null);
					
					/**
					 * Continue to check available coupon.
					 */
					if (mCoupon.isUsed()) {
						mUsed.setText((new StringBuilder("この度は本クーポンのご利用ありがとうございました。")).append("\n"+mCoupon.getUsedDate()).toString());
						mCoupon.applySuccessImageBitmap(mSuccessimage, new ImageListener() {

							public void onDone(View view, Bitmap bitmap) {
								if (bitmap != null) {
									((ImageView) view).setImageBitmap(bitmap);									
								}
							}

						});
						if( mCoupon.getReusable() == true ) {
							mUsed.append("※再利用可能");
							String reusable_time = mCoupon.getScheduledReusableTime();
							if( reusable_time != null) {
								mUsed.append(String.format("(次回: %s)",reusable_time));
							}
						}
						String date = mCoupon.getUsedDate();
						mUsed.setText(date.substring(0, 4) + "年"
											+ date.substring(4, 6) + "月"
											+ date.substring(6, 8) + "日  "
											+ date.substring(8, 10) + ":"
											+ date.substring(10, 12) + ":"
											+ date.substring(12, 14));
					} else if (!mCoupon.isAvailable()){
						mUsed.setText("利用期間外");
					}else{
						mUsed.setText("未使用");
					}

					final Handler mHandler = new Handler();

					if(mTimerLive == null){
						return;
					}

					/**
					 * Time reload data
					 */
					mTimerLive.schedule(new TimerTask() {
						public void run() {
							mHandler.post(new Runnable() {

								public void run() {
									mCoupon.live(new OffersListener() {

										public void onDone(Map<String, Object> map) {
											if (((OffersStatus) map.get("status")).getCode() == 0) {
												mCoupon = (OffersCoupon) map.get("coupon");
//												mDate.setText(new StringBuilder("残り時間 : ").append(Utils.getExpireDate(new Date(), mCoupon)));
												if(mCoupon.getQuantity() > 0){
													mDate.setText(new StringBuilder("残り枚数 : ")
														.append(mCoupon.getQuantity())
															.append("\n残り時間 : ")
																.append(Utils.getExpireDate(new Date(), mCoupon)));
												}else{
													mDate.setText(new StringBuilder("残り枚数 : ∞ 枚")
														.append("\n残り時間 : ")
															.append(Utils.getExpireDate(new Date(), mCoupon)));
												}
												JSONObject json;
												try {
													JSONArray jsonObj = new JSONArray(mCoupon.getGroups());
													json = jsonObj.getJSONObject(0);
													mShop.setText(json.getString("name"));
												} catch (JSONException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
											}
										}

										public void onFail(Integer s) {
										}

									});
								}
							});
						}
					}, 3000L, 3000L);
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
		mTimerLive.cancel();
		mTimerLive.purge();
		mTimerLive = null;
	}


	public void onDestroyView() {
		if(mEditText!=null) {
			mEditText.setOnEditorActionListener(null);
		}
		mButton.setOnClickListener(null);
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
		System.out.println("====Before Stamp===");
		if(mCoupon.getCouponTypeCode() != OffersCouponType.OffersCouponTypeCodeStamp){
			return;
		}

		Map<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("width", Float.valueOf(Float.parseFloat(map.get("width").toString()) / 2.0F));
		hashmap.put("height", Float.valueOf(Float.parseFloat(map.get("height").toString()) / 2.0F));
		offerGestureView.setStampedOption(hashmap);
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


	@SuppressWarnings("unchecked")
	private void apply(Object s) {
		if(mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeTap
				|| mCoupon.getCouponTypeCode() == OffersCouponType.OffersCouponTypeCodeGesture ){
			mCoupon.apply(new OffersListener() {
				
				public void onDone(Map<String, Object> map) {
					//mProgressBar.setVisibility(View.GONE);
					if (map.get("coupon") != null) {
						mCoupon = (OffersCoupon) map.get("coupon");
						lineButton.setVisibility(View.GONE);
						lineSuccess.setVisibility(View.VISIBLE);
						lineNotUsed.setVisibility(View.GONE);
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
					//mProgressBar.setVisibility(View.GONE);
					lineButton.setVisibility(View.GONE);
					lineSuccess.setVisibility(View.VISIBLE);
					lineNotUsed.setVisibility(View.GONE);
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
					//mProgressBar.setVisibility(View.GONE);
					lineButton.setVisibility(View.GONE);
					lineSuccess.setVisibility(View.VISIBLE);
					lineNotUsed.setVisibility(View.GONE);
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