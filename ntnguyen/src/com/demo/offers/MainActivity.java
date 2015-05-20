package com.demo.offers;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import co.leonisand.cupido.CupidoKit;
import co.leonisand.cupido.CupidoKit.CupidoListener;
import co.leonisand.cupido.CupidoKit.GCMRegisterListener;
import co.leonisand.cupido.CupidoNotification;
import co.leonisand.cupido.CupidoStatus;
import co.leonisand.offers.OffersKit;
import co.leonisand.offers.OffersKit.OffersListener;
import co.leonisand.platform.Leonis;

import com.demo.adapters.AdapterNotification;
import com.demo.adapters.CustomDrawerAdapter;
import com.demo.controllers.OfferDataController;
import com.demo.utils.Constants;
import com.demo.utils.DrawerItem;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {

	public static String staticTargetID = "";
	public static String staticGroupName = "";
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private AdapterNotification adapterNotify;
	private ArrayList<CupidoNotification> listAdapter;
	public static String mTargetGroupId = "0";
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private ListView listViewNotification;
	private ProgressBar mProgressBar;
	CustomDrawerAdapter adapter;
	public OfferDataController controller;
	String[] code = null;
	List<DrawerItem> dataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		// Leonis's initialization
		loadSDK();

		controller = new OfferDataController();
		listAdapter = new ArrayList<CupidoNotification>();
		dataList = new ArrayList<DrawerItem>();
		mTitle = mDrawerTitle = getTitle();
		// Sub menu
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		/**
		 * Add HEADER to list Menu
		 */
		dataList.add(new DrawerItem("", "", "1", "")); // adding a header to the list
		dataList.add(new DrawerItem(getResources().getString(R.string.reset), "", "", "1")); // adding a header to the list

		/**
		 * Add child into group Function of Menu
		 */
		dataList.add(new DrawerItem("", getResources().getString(R.string.function), "", "", getResources().getColor(R.color.darkblue))); // adding a header to the list
		dataList.add(new DrawerItem(getResources().getString(R.string.coupon),"", "", ""));
		dataList.add(new DrawerItem(getResources().getString(R.string.recommendation), "", "", ""));
		dataList.add(new DrawerItem(getResources().getString(R.string.stamp),"", "", ""));
		dataList.add(new DrawerItem(getResources().getString(R.string.compaign), "", "", ""));
		dataList.add(new DrawerItem(getResources().getString(R.string.notification), "", "", ""));
		
		/**
		 * Add header Account for Menu
		 */
		dataList.add(new DrawerItem("", getResources().getString(R.string.account), "", "", getResources().getColor(R.color.whitegreen)));// adding a header to the list
		/**
		 * Add child into group Account of Menu.
		 */
		dataList.add(new DrawerItem(getResources().getString(R.string.change_membership_number), "", "", ""));
		
		// TODO : input GROUP ID
//		dataList.add(new DrawerItem(getResources().getString(R.string.change_group), "", "", ""));
		dataList.add(new DrawerItem(getResources().getString(R.string.device_information), "", "", ""));

		/**
		 * Add header Other for Menu 
		 */
		dataList.add(new DrawerItem("", getResources().getString(R.string.other), "", "", getResources().getColor(R.color.purple)));// adding a header to the list
		/**
		 * Add child into group Other of Menu
		 */
		dataList.add(new DrawerItem(getResources().getString(R.string.link_offers), "", "", ""));

		adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, dataList);

		mDrawerList.setAdapter(adapter);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
		TextView abTitle = (TextView) findViewById(titleId);
		abTitle.setTextColor(getResources().getColor(R.color.white));
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				// Create call to onPrepareOptionsMenu()
				invalidateOptionsMenu(); 
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				// Create call to onPrepareOptionsMenu()
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			SelectItem(3);
		}

	}

	private void loadSDK() {
		// TODO Auto-generated method stub
		initSDKLeonis();
		initSDKCupido();
		initSDKOffers();
		/*linkUserWithCupido();*/
	}
	
	private void linkUserWithCupido() {
		// TODO Auto-generated method stub
		OffersKit.getInstance().linkUserWithCupido(CupidoKit.getInstance(), new OffersListener() {
			
			@Override
			public void onFail(Integer arg0) {
				// TODO Auto-generated method stub
				alert("link user", arg0.toString());
			}
			
			@Override
			public void onDone(Map<String, Object> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void initSDKLeonis() {
		// TODO Auto-generated method stub
		Leonis.getInstance().init(getApplicationContext(), true);
		Leonis.getInstance().init(getApplicationContext(), null, true);
		// TODO Use line code for product.
//		Leonis.getInstance().init(getApplicationContext(), OffersKit.getInstance().uid(), true);
		Leonis.getInstance().setRequestTimeoutInterval(10);
		Leonis.getInstance().setEndUserExtension(Constants.SECTION,
				Constants.KEY, Constants.VALUE,
				new Leonis.LeonisListener() {
					public void onDone(Map<String, Object> result) {
					}

					public void onFail(Integer result) {
						System.out.println("extension ng");
					}
				});

		JSONObject jsonobject = new JSONObject();
		try {
			JSONArray jsonarray = new JSONArray();
			jsonarray.put("key1");
			jsonarray.put("key2");
			jsonarray.put("key3");
			jsonobject.put("resouce1", jsonarray);
			jsonobject.put("resouce2", "key1");
		} catch (JSONException jsonexception) {
			jsonexception.printStackTrace();
		}
		Leonis.getInstance().addEndUserExtensions(jsonobject,
				new Leonis.LeonisListener() {
					public void onDone(Map<String, Object> map) {
					}

					public void onFail(Integer s) {
					}
				});
	}
	
	private void initSDKCupido() {
		// TODO Auto-generated method stub
		/*
		// Cupido's initialization
		CupidoKit.getInstance().init(context);
		//デバイストークン未取得
		alert("Registration ID", CupidoKit.getInstance().getRegistrationId());*/
		// set up cupido
		// CupidoKitの初期化
		CupidoKit.getInstance().init(getApplicationContext());
		// デバイストークン未取得
		if (TextUtils.isEmpty(CupidoKit.getInstance().getRegistrationId())) {
			// デバイストークン取得
			CupidoKit.getInstance().gcmRegister(new GCMRegisterListener() {
				@Override
				public void onRegistered(String registrationId) {

					// 認証トークン確認
					if (!TextUtils.isEmpty(CupidoKit.getInstance()
							.getAuthenticationToken())) {
						// デバイストークン更新
						CupidoKit.getInstance().deviceUpdate(
								new CupidoListener() {
									@Override
									public void onDone(
											Map<String, Object> result) {
										CupidoStatus status = (CupidoStatus) result
												.get("status");
										if (status != null) {
											// 再読み込み
											if (getSupportFragmentManager()
													.findFragmentById(
															R.id.container) != null) {
												getSupportFragmentManager()
														.findFragmentById(
																R.id.container)
														.onStart();
											}
										} else {
											alert("deviceUpdate.onDone","取得できませんでした。");
										}
									}

									@Override
									public void onFail(String message) {
										alert("deviceUpdate.onFail:", message);
									}
								});
					} else {
						// デバイストークン登録
						CupidoKit.getInstance().deviceRegist(
								new CupidoListener() {
									@Override
									public void onDone(
											Map<String, Object> result) {
										CupidoStatus status = (CupidoStatus) result
												.get("status");
										if (status != null) {
											// 再読み込み
											if (getSupportFragmentManager()
													.findFragmentById(
															R.id.container) != null) {
												getSupportFragmentManager()
														.findFragmentById(
																R.id.container)
														.onStart();
											}
										} else {
											alert("deviceRegist.onDone:", "取得できませんでした。");
										}
									}

									@Override
									public void onFail(String message) {
										alert("deviceRegist.onFail:", message);
									}
								});
					}
				}

				@Override
				public void onError(String errorId) {
					alert("setGCMRegistListener.onError", errorId);
				}

				@Override
				public void onRecoverableError(String errorId) {
					alert("setGCMRegistListener.onRecoverableError", errorId);
				}
			});
			// 認証トークン未取得
		}
		else if (TextUtils.isEmpty(CupidoKit.getInstance()
				.getAuthenticationToken())) {
			// デバイストークン登録
			CupidoKit.getInstance().deviceRegist(new CupidoListener() {
				@Override
				public void onDone(Map<String, Object> result) {
					CupidoStatus status = (CupidoStatus) result.get("status");
					if (status != null) {
						// 再読み込み
						if (getSupportFragmentManager().findFragmentById(
								R.id.container) != null) {
							getSupportFragmentManager().findFragmentById(
									R.id.container).onStart();
						}
					} else {
						alert("deviceRegist.onDone:"
								, "取得できませんでした。");
					}
				}

				@Override
				public void onFail(String message) {
					alert("deviceRegist.onFail:" , message);
				}
			});
		}
	}

	private void initSDKOffers() {
		// TODO Auto-generated method stub
		// OffersKitの初期化
				OffersKit.getInstance().init(getApplicationContext());

				OffersKit.getInstance().setRequestTimeoutInterval(10);

//				Bundle bundle1 = new Bundle();
//				bundle1.putString("test", "TESTID");
//				bundle1.putString("info", "INFOUSERID");
//				OffersKit.getInstance().externalServiceRegistrations(bundle1, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; 
		// Adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Handle action of slide out menu
	 * @param possition
	 */
	public void SelectItem(int possition) {
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Fragment fragment = null;
		Bundle args = new Bundle();
		switch (possition) {
		case 1:
			/**
			 * Reset all data with new user's UDID
			 * When we reset data, the new UID will be created and the old UID will store on server.
			 */
			resetDataCoupon();
			fragment = new CouponsFragment();
			break;
		case 3:
			fragment = new CouponsFragment();
			break;
		case 4:
			fragment = new RecommendationsFragment();
			break;
		case 5:
			fragment = new StampsFragment();
			break;
		case 6:
			displayPopUpInputCampaignCode(inflater);
			break;
		case 7:
			displayPopUpNotificationCupido(inflater);
			break;
		case 9:
			displayPopUpInputMembershipNumber(inflater);
			break;
		case 10:
			/*displayPopUpGroup(inflater);
			break;
		case 11:
			displayPopUpInformation(inflater);
			break;
		case 13:*/
			displayPopUpInformation(inflater);
			break;
		case 12:
			diplayPopUpWebviewOffer(inflater);
			break;
		default:
			fragment = new CouponsFragment();
			break;
		}
		if (possition != 9 && possition != 10
				&& possition != 12 && possition != 7 && possition != 6) {
			fragment.setArguments(args);
			FragmentManager frgManager = getSupportFragmentManager();
			frgManager.beginTransaction().replace(R.id.content_frame, fragment)
					.commit();

			mDrawerList.setItemChecked(possition, true);
			setTitle(dataList.get(possition).getItemName());
			mDrawerLayout.closeDrawer(mDrawerList);
		}

	}
	

	/**
	 * Reset data & reload coupons
	 */
	public void resetData() {
		SelectItem(3);
	}
	
	/**
	 * Reset all data of coupons
	 * Reset all connection settings
	 *	In the product version、do not specify TRUE needResetUid parameter
	 *	@param needResetUid When specify TRUE, it will be reset new UID. UID will be random value.
	 */
	@SuppressLint("TrulyRandom")
	public void resetDataCoupon() {
		confirm("ユーザー削除", "本当にユーザー削除しますか？", getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					@SuppressLint("TrulyRandom")
					public void onClick(DialogInterface dialoginterface, int i) {
						/**
						 * Reset all data of Leonis
						 */
						Leonis.getInstance().resetAll(true);
						/**
						 * Reset all data of Offers
						 */
						OffersKit.getInstance().resetAll();
						OffersKit.getInstance().requestsCancel();

						/**
						 * Generate new UID
						 */
						String newUid = new BigInteger(64, new SecureRandom()).toString(16);
						OffersKit.getInstance().setUid(newUid);
						Leonis.getInstance().init(getApplicationContext(),
								OffersKit.getInstance().uid(), true);
						controller.requestResetData(MainActivity.this);
					}
				}, getString(android.R.string.cancel), null);
	}
	
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return false;
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if ("".equals(dataList.get(position).getTitle())) {
				SelectItem(position);
			}

		}
	}

	public DialogFragment alert(String title, String message) {

		Bundle args = new Bundle();
		args.putString(CommonDialogFragment.FIELD_TITLE, title);
		args.putString(CommonDialogFragment.FIELD_MESSAGE, message);
		args.putString(CommonDialogFragment.FIELD_LABEL_POSITIVE,
				getString(android.R.string.ok));
		CommonDialogFragment dialogFragment = new CommonDialogFragment() {

		};
		dialogFragment.setArguments(args);
		dialogFragment.show(getSupportFragmentManager(), "alert");

		return dialogFragment;
	}
	public class CommonDialogFragment extends DialogFragment {

		public static final String FIELD_LAYOUT = "layout";
		public static final String FIELD_TITLE = "title";
		public static final String FIELD_MESSAGE = "message";
		public static final String FIELD_LIST_ITEMS = "list_items";
		public static final String FIELD_LIST_ITEMS_STRING = "list_items_string";
		public static final String FIELD_LABEL_POSITIVE = "label_positive";
		public static final String FIELD_LABEL_NEGATIVE = "label_negative";
		public static final String FIELD_LABEL_NEUTRAL = "label_neutral";

		private DialogInterface.OnShowListener mListenerShow;
		private DialogInterface.OnClickListener mListenerNegativeClick;
		private DialogInterface.OnClickListener mListenerPositiveClick;
		private DialogInterface.OnClickListener mListenerNeutralClick;

		private View mView;

		private AlertDialog mAlertDialog;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			Bundle args = getArguments();

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			// dialog title
			if (args.containsKey(FIELD_TITLE)) {
				builder.setTitle(args.getString(FIELD_TITLE));
			}

			// dialog message
			if (args.containsKey(FIELD_MESSAGE)) {
				builder.setMessage(args.getString(FIELD_MESSAGE));
			}

			// dialog customize content view
			if (args.containsKey(FIELD_LAYOUT)) {
				builder.setView(mView);
			}

			// positive button title and click listener
			if (args.containsKey(FIELD_LABEL_POSITIVE)) {
				builder.setPositiveButton(args.getString(FIELD_LABEL_POSITIVE), mListenerPositiveClick);
			}

			// negative button title and click listener
			if (args.containsKey(FIELD_LABEL_NEGATIVE)) {
				builder.setNegativeButton(args.getString(FIELD_LABEL_NEGATIVE), mListenerNegativeClick);
			}

			// neutral button title and click listener
			if (args.containsKey(FIELD_LABEL_NEUTRAL)) {
				builder.setNeutralButton(args.getString(FIELD_LABEL_NEUTRAL), mListenerNeutralClick);
			}

			// make dialog
			mAlertDialog = builder.create();

			// show listener
			if (mListenerShow != null) {
				mAlertDialog.setOnShowListener(mListenerShow);
			}

			return mAlertDialog;
		}

		public void setView(View view){
			mView = view;
		}
		public void setShowListener(DialogInterface.OnShowListener listener){
			mListenerShow = listener;
		}
		public void setPotitiveClickListener(DialogInterface.OnClickListener listener){
			mListenerPositiveClick = listener;
		}
		public void setNegativeClickListener(DialogInterface.OnClickListener listener){
			mListenerNegativeClick = listener;
		}
		public void setNeutralClickListener(DialogInterface.OnClickListener listener){
			mListenerNeutralClick = listener;
		}

	}
	public DialogFragment viewConfirm(String title, View view, String positive_title, DialogInterface.OnClickListener positive_listener, String negative_title, DialogInterface.OnClickListener negative_listener, OnShowListener show_listener) {

		Bundle args = new Bundle();
		args.putString(CommonDialogFragment.FIELD_TITLE, title);
		args.putInt(CommonDialogFragment.FIELD_LAYOUT, view.getId());
		args.putString(CommonDialogFragment.FIELD_LABEL_POSITIVE, positive_title);
		args.putString(CommonDialogFragment.FIELD_LABEL_NEGATIVE, negative_title);


		CommonDialogFragment dialogFragment = new CommonDialogFragment();
		dialogFragment.setView(view);

		dialogFragment.setPotitiveClickListener(positive_listener);
		dialogFragment.setNegativeClickListener(negative_listener);

		dialogFragment.setShowListener(show_listener);

		dialogFragment.setArguments(args);
		dialogFragment.show(getSupportFragmentManager(), "viewConfirm");

		return dialogFragment;
	}
	
	public DialogFragment confirm(String title, String message,
			String positive_title,
			android.content.DialogInterface.OnClickListener positive_listener,
			String negative_title,
			DialogInterface.OnClickListener negative_listener) {

		Bundle args = new Bundle();
		args.putString(CommonDialogFragment.FIELD_TITLE, title);
		args.putString(CommonDialogFragment.FIELD_MESSAGE, message);
		args.putString(CommonDialogFragment.FIELD_LABEL_POSITIVE,
				positive_title);
		args.putString(CommonDialogFragment.FIELD_LABEL_NEGATIVE,
				negative_title);

		CommonDialogFragment dialogFragment = new CommonDialogFragment();
		dialogFragment.setPotitiveClickListener(positive_listener);
		dialogFragment.setNegativeClickListener(negative_listener);

		dialogFragment.setArguments(args);
		dialogFragment.show(getSupportFragmentManager(), "confirm");

		return dialogFragment;
	}
	
	/********************************************************
	 * Link web Offers display on pop-ups					*
	 * 														*
	 * @param inflater										*
	 ********************************************************/
	@SuppressLint({ "RtlHardcoded", "InflateParams" })
	public void diplayPopUpWebviewOffer(LayoutInflater inflater) {
		mDrawerLayout.closeDrawer(Gravity.LEFT);
		View layoutWebviewOffer = inflater.inflate(
				R.layout.pop_up_link_webview_offer, null);
		final PopupWindow pwindowWebviewOffer = new PopupWindow(
				layoutWebviewOffer, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		ImageView bImageWebview = (ImageView) layoutWebviewOffer
				.findViewById(R.id.im_pop_up_web_close);
		bImageWebview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pwindowWebviewOffer.dismiss();
			}
		});
		pwindowWebviewOffer.setAnimationStyle(R.style.AppTheme);
		pwindowWebviewOffer.showAtLocation(getCurrentFocus(),
				Gravity.NO_GRAVITY, 0, getWindowManager().getDefaultDisplay()
						.getHeight() / 2);
		pwindowWebviewOffer.setFocusable(true);
		pwindowWebviewOffer.update();
		WebView mWebview = (WebView) layoutWebviewOffer
				.findViewById(R.id.webView);
		startWebView(mWebview);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void startWebView(WebView mWebView) {

		mWebView.requestFocusFromTouch();
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
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
		mWebView.loadUrl(getResources().getString(R.string.link_offer_url));
	}
	
	/************************************************************
	 * Display pop-up when selected register new membership ID  *
	 * 															*
	 * @param inflater											*
	 ************************************************************/
	@SuppressLint({ "InflateParams", "RtlHardcoded" })
	public void displayPopUpInputMembershipNumber(LayoutInflater inflater) {
		mDrawerLayout.closeDrawer(Gravity.LEFT);
		View layout_employee = inflater.inflate(
				R.layout.pop_up_membership_numbers, null);
		final PopupWindow pwindow_employee = new PopupWindow(layout_employee,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		final EditText editTextNumber = (EditText) layout_employee
				.findViewById(R.id.et_textnumber);
		editTextNumber.setSelection(editTextNumber.getText().length());
		editTextNumber.setRawInputType(Configuration.KEYBOARD_12KEY);
		editTextNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() == 4) {
					editTextNumber.setText(editTextNumber.getText().toString()
							+ " ");
					editTextNumber.setSelection(editTextNumber.getText()
							.length());
				}
				staticTargetID = s.toString();
			}
		});
		editTextNumber.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_DEL)
					editTextNumber.setText("");
				return false;
			}
		});
		Button btnOk = (Button) layout_employee
				.findViewById(R.id.btnStartTargetId);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (editTextNumber.getText().toString().length() == 0) {
					alert("MSG_ERR_0060", null);
				} else if (editTextNumber.getText().toString().replace(" ", "")
						.length() != 8) {
					alert("MSG_ERR_0061", null);
				}
				Leonis.getInstance().setEndUserExtension(Constants.SECTION,
						Constants.KEY,
						editTextNumber.getText().toString().replace(" ", ""),
						new Leonis.LeonisListener() {
							public void onDone(Map<String, Object> result) {
								staticTargetID = editTextNumber.getText()
										.toString().replace(" ", "");
								pwindow_employee.dismiss();
								SelectItem(3);
								adapter.notifyDataSetChanged();
							}

							public void onFail(Integer result) {
								Log.e("test", "extension ng");
								pwindow_employee.dismiss();
							}
						});
			}
		});
		ImageView bImageView = (ImageView) layout_employee.findViewById(R.id.imb_close);
		bImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pwindow_employee.dismiss();
			}
		});

		pwindow_employee.setAnimationStyle(R.style.AppTheme);
		pwindow_employee.showAtLocation(getCurrentFocus(), Gravity.NO_GRAVITY,
				0, getWindowManager().getDefaultDisplay().getHeight() / 2);
		pwindow_employee.setFocusable(true);
		pwindow_employee.update();
		layout_employee.setOnTouchListener(touchListenerLayout);
	}
	
	/**
	 * Function touchListenerLayout for hide keyboard in EditText
	 */
	OnTouchListener touchListenerLayout = new OnTouchListener() {

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			return false;
		}
	};
	
	/********************************************************
	 * Function display pop-up of group						*
	 * 														*
	 * @param inflater										*
	 ********************************************************/
	public void displayPopUpGroup(LayoutInflater inflater) {
		mDrawerLayout.closeDrawer(Gravity.LEFT);
		final View layout_group = inflater.inflate(
				R.layout.pop_up_groups, null);
		final PopupWindow pwindowGroup = new PopupWindow(layout_group,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		final EditText editTextGroup = (EditText) layout_group
				.findViewById(R.id.et_textnumber);
		editTextGroup.setSelection(editTextGroup.getText().length());
		editTextGroup.setRawInputType(Configuration.KEYBOARD_12KEY);
		editTextGroup.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_DEL)
					editTextGroup.setText("");
				return false;
			}
		});
		Button btnStart = (Button) layout_group.findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean flag = false;
				LinearLayout lnButton = (LinearLayout) layout_group
						.findViewById(R.id.lnl_layout_button);
				try {
//					JSONObject jsonObj = Utils.getJSONFromUrl(getResources().getString(R.string.coupon_token_url));
					JSONObject jsonObj = new JSONObject(loadJSONFromAsset());
					JSONArray jsonArray = new JSONArray(jsonObj
							.getString("data"));
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject json = jsonArray.getJSONObject(i);
						String password = json.getString("password");
						if (password.equals(editTextGroup.getText().toString())) {
							handleGroupNameVisible(json.getString("id"),
									layout_group);
							flag = true;
						} else {
							handleGroupNameGone(json.getString("id"),
									layout_group);
						}
					}
					if(flag){
						lnButton.setVisibility(View.VISIBLE);
					}else{
						lnButton.setVisibility(View.GONE);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		Button btnOk = (Button) layout_group.findViewById(R.id.btnOK);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadSDK();
				SelectItem(3);
				pwindowGroup.dismiss();
			}
		});
		ImageView bImageViewGroup = (ImageView) layout_group
				.findViewById(R.id.imb_close);
		bImageViewGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				resetDataCoupon();
				pwindowGroup.dismiss();
			}
		});

		findViewById(R.id.drawer_layout).post(new Runnable() {
			public void run() {
				pwindowGroup.setAnimationStyle(R.style.AppTheme);
				pwindowGroup.showAtLocation(getCurrentFocus(),
						Gravity.NO_GRAVITY, 0, getWindowManager()
								.getDefaultDisplay().getHeight() / 2);
			}
		});

		pwindowGroup.setFocusable(true);
		pwindowGroup.update();
		layout_group.setOnTouchListener(touchListenerLayout);
	}
	// Handle change UI for reload grid view
	public Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			adapterNotify.mList = listAdapter;
			adapterNotify.notifyDataSetChanged();
		}
	};

	public String loadJSONFromAsset() {
		String json = null;
		try {

			InputStream is = getAssets().open("TCIGroupAndApiKey.json");

			int size = is.available();

			byte[] buffer = new byte[size];

			is.read(buffer);

			is.close();

			json = new String(buffer, "UTF-8");

		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;

	}
	
	// Handle group visible when fill password
	public void handleGroupNameVisible(final String id, final View view) {

		final RelativeLayout relativeLayout = (RelativeLayout) view
				.findViewById(getResources().getIdentifier("rlt_group_name" + id, "id", getPackageName()));
		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImageView image = (ImageView) relativeLayout
						.findViewById(getResources().getIdentifier(
								"img_group_name" + id, "id", getPackageName()));
				if (image.getVisibility() == View.GONE) {
					for (int i = 1; i < 8; i++) {
						RelativeLayout relativeLayout = (RelativeLayout) view
								.findViewById(getResources().getIdentifier(
										"rlt_group_name" + i, "id",
										getPackageName()));
						ImageView image1 = (ImageView) relativeLayout
								.findViewById(getResources().getIdentifier(
										"img_group_name" + i, "id",
										getPackageName()));
						image1.setVisibility(View.GONE);
					}
					
					mTargetGroupId = id;
					TextView txtV = (TextView) view.findViewById(getResources().getIdentifier("group_text"+id, "id", getPackageName()));
					staticGroupName = txtV.getText().toString();
					image.setVisibility(View.VISIBLE);
				} else {
					image.setVisibility(View.GONE);
				}
			}
		});

		relativeLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * Handle group name when fill password not show
	 * 
	 * @param id
	 * @param view
	 */
	public void handleGroupNameGone(String id, View view) {

		RelativeLayout relativeLayout = (RelativeLayout) view
				.findViewById(getResources().getIdentifier(
						"rlt_group_name" + id, "id", getPackageName()));
		ImageView image = (ImageView) relativeLayout
				.findViewById(getResources().getIdentifier(
						"img_group_name" + id, "id", getPackageName()));
		image.setVisibility(View.GONE);
		relativeLayout.setVisibility(View.GONE);
	}
	
	/********************************************************
	 * function display popup terminal information			*
	 * 														*
	 * @param inflater										*
	 ********************************************************/
	public void displayPopUpInformation(LayoutInflater inflater) {
		mDrawerLayout.closeDrawer(Gravity.LEFT);
		View layoutTerminalInformation = inflater.inflate(
				R.layout.pop_up_information, null);
		final PopupWindow pwindowTerminalInformation = new PopupWindow(
				layoutTerminalInformation, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		TextView tvDeviceId = (TextView) layoutTerminalInformation
				.findViewById(R.id.tv_udid_content);
		TextView tvToken = (TextView) layoutTerminalInformation
				.findViewById(R.id.tv_token_content);
		tvDeviceId.setText(OffersKit.getInstance().uid());
		tvToken.setText(Leonis.getInstance().getAuthenticationToken());
		TextView tvVersion = (TextView) layoutTerminalInformation
				.findViewById(R.id.tv_version_content);
		try {
			tvVersion.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			tvVersion.setText("Exception version.");
		}
		ImageView bImageViewTerminal = (ImageView) layoutTerminalInformation
				.findViewById(R.id.imb_close);
		bImageViewTerminal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pwindowTerminalInformation.dismiss();
			}
		});
		pwindowTerminalInformation.setAnimationStyle(R.style.AppTheme);
		pwindowTerminalInformation.showAtLocation(getCurrentFocus(),
				Gravity.NO_GRAVITY, 0, getWindowManager().getDefaultDisplay()
						.getHeight() / 2);
		pwindowTerminalInformation.setFocusable(true);
		pwindowTerminalInformation.update();
	}
	
	/********************************************************
	 * Function display pop-up input Campaign code			*
	 * 														*
	 * @param inflater										*
	 ********************************************************/
	public void displayPopUpInputCampaignCode(LayoutInflater inflater) {
		mDrawerLayout.closeDrawer(Gravity.LEFT);
		final View layoutCampaignCode = inflater.inflate(
				R.layout.pop_up_campaign_code, null);
		final PopupWindow pwindowTerminalInformation = new PopupWindow(
				layoutCampaignCode, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		final EditText edtCampaign = (EditText) layoutCampaignCode.findViewById(R.id.et_textnumber);
		edtCampaign.setSelection(edtCampaign.getText().length());
		edtCampaign.setRawInputType(Configuration.KEYBOARD_12KEY);
		edtCampaign.addTextChangedListener(new TextWatcher() {
			private int check = 0;
			private String x = "";
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				check = start;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (check > 1 && check % 5 == 4 && s.length() > x.length()) {
					edtCampaign.setText(edtCampaign
							.getText()
							.toString()
							.subSequence(0,
									edtCampaign.getText().length() - 1)
							+ "-"
							+ edtCampaign
									.getText()
									.toString()
									.charAt(edtCampaign.getText()
											.length() - 1));
					edtCampaign.setSelection(edtCampaign.getText()
							.length());
				}
				x = s.toString();
			}
		});
		
		Button campaignCodeBtn = (Button) layoutCampaignCode.findViewById(R.id.btn_get_coupon);
		campaignCodeBtn.setOnClickListener(new OnClickListener() {
			private int check = 0;
			private String x = "";
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				code = edtCampaign.getText().toString().split("-");
//				controller.requestCampaignCoupons(code, MainActivity.this);
				SelectItem(3);
				pwindowTerminalInformation.dismiss();
			}
		});
		
		ImageView bImageViewTerminal = (ImageView) layoutCampaignCode
				.findViewById(R.id.imb_close);
		bImageViewTerminal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pwindowTerminalInformation.dismiss();
			}
		});
		
		pwindowTerminalInformation.setAnimationStyle(R.style.AppTheme);
		pwindowTerminalInformation.showAtLocation(getCurrentFocus(),
				Gravity.NO_GRAVITY, 0, getWindowManager().getDefaultDisplay()
						.getHeight() / 2);
		pwindowTerminalInformation.setFocusable(true);
		pwindowTerminalInformation.update();
	}
	

	private void displayPopUpNotificationCupido(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		
		// Check Cupido's AuthenticationToken registered or not.
		// If not register, begin register Token with Cupido.
		View layoutNotificationCupido = inflater.inflate(
				R.layout.pop_up_notifications, null);
		final PopupWindow pPopUpWindow = new PopupWindow(
				layoutNotificationCupido, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		mProgressBar = (ProgressBar) layoutNotificationCupido
				.findViewById(R.id.progressbar);
		mDrawerLayout.closeDrawer(Gravity.LEFT);
		pPopUpWindow.setAnimationStyle(R.style.AppTheme);
		pPopUpWindow.showAtLocation(getCurrentFocus(), Gravity.NO_GRAVITY,
				0, getWindowManager().getDefaultDisplay().getHeight() / 2);
		pPopUpWindow.setFocusable(true);
		pPopUpWindow.update();
		ImageView bImageViewNotification = (ImageView) layoutNotificationCupido
				.findViewById(R.id.imb_close);
		bImageViewNotification.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pPopUpWindow.dismiss();
			}
		});
		listViewNotification = (ListView) layoutNotificationCupido
				.findViewById(R.id.list_notifications);
		listViewNotification.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (totalItemCount == firstVisibleItem + visibleItemCount) {
					onStartNotificationCupido();
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
		});
		listViewNotification
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Log.e("", "position " + position);
						CupidoNotification item = (CupidoNotification) listAdapter
								.get(position);

						Intent intent = new Intent(getApplicationContext(),
								NotificationActivity.class);
						intent.putExtra("id", item.getId());
						startActivity(intent);

					}
				});
		onStartNotificationCupido();
		adapterNotify = new AdapterNotification(listAdapter,
				getApplicationContext(), this);
		listViewNotification.setAdapter(adapterNotify);
	}
	/**
	 * get data from server for tab Notificaltion
	 */
	private int offset = 0;
	private int limit = 20;
	private boolean isLoading = false;

	public void onStartNotificationCupido() {
		if (isLoading == true) {
			return;
		}
		isLoading = true;
		mProgressBar.setVisibility(View.VISIBLE);
		Bundle bundle = new Bundle();
		bundle.putInt("offset", offset);
		bundle.putInt("limit", limit);
		CupidoKit.getInstance().notifications(bundle, new CupidoListener() {
			@Override
			public void onDone(Map<String, Object> result) {
				mProgressBar.setVisibility(View.GONE);
				CupidoStatus status = (CupidoStatus) result.get("status");
				if (status == null || status.getCode() == 0) {
					@SuppressWarnings("unchecked")
					List<CupidoNotification> notifications = (List<CupidoNotification>) result
							.get("notifications");
					
					for (CupidoNotification item : notifications) {
						listAdapter.add(item);
						isLoading = false;
					}
					offset += notifications.size();
					myHandler.sendEmptyMessage(0);

				} else if (status.getCode() == 100) {
				} else {

					Toast.makeText(
							getApplicationContext(),
							"notifications#code:" + status.getCode()
									+ " ;message:" + status.getMessage(),
							Toast.LENGTH_SHORT).show();
					System.out.println("notifications#code:" + status.getCode()
							+ " ;message:" + status.getMessage());
				}
				
			}

			@Override
			public void onFail(String result) {
				isLoading = false;
				Toast.makeText(getApplicationContext(),
						"notifications#error:" + result, Toast.LENGTH_LONG)
						.show();
				mProgressBar.setVisibility(View.GONE);
			}
		});
	}
}
