package com.demo.android;

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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.support.v7.app.ActionBar;
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
import co.leonisand.platform.Leonis;

import com.demo.android.adapter.AdapterNotification;
import com.demo.android.adapter.CustomDrawerAdapter;
import com.demo.android.constants.ConstantMain;
import com.demo.android.fragment.CouponsFragment;
import com.demo.android.fragment.RecommendationsFragment;
import com.demo.android.fragment.StampCardsFragment;

@SuppressLint({ "InflateParams", "TrulyRandom", "RtlHardcoded",
		"ClickableViewAccessibility" })
@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private ArrayList<CupidoNotification> listAdapter;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private CustomDrawerAdapter adapter;
	private CupidoNotification notification;
	private AdapterNotification adapterNotify;
	private ProgressBar mProgressBar;
	private int mTargetID;
	public static String mTargetGroupId = "0";
	private Fragment fragment;
	public static String staticTargetID = "";
	public OfferDataController controller;
	public PopupWindow pPopUpWindow;
	List<DrawerItem> dataList;
	public static boolean shake;
	public String[] codeCampaign ;
	public int mStampCardID = -1 ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.actionbar)));
		initSdk();
		initShake();
		initDataDrawer();
		controller = new OfferDataController();

		// init data for cupido
		listAdapter = new ArrayList<CupidoNotification>();
		// end
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		// set title for actionbar
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView abTitle = (TextView) findViewById(titleId);
		abTitle.setTextColor(getResources().getColor(R.color.white));
		// end
		if (savedInstanceState == null) {
			SelectItem(3);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint({ "ClickableViewAccessibility", "TrulyRandom" })
	public void SelectItem(int possition) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fragment = null;
		shake = false;
		Bundle args = new Bundle();
		switch (possition) {
		case 1:
			// reset all data with new Uid for user
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
			fragment = new StampCardsFragment();
			break;
		case 6:
			displayPopUpPromotion(inflater);
			break;
		case 7:
			displayPopUpNotificationCupido(inflater);
			break;
		case 9:
			displayPopUpEmployee(inflater);
			break;
//		case 10:
//			displayPopUpGroup(inflater);
//			break;
		case 10:
			displayPopUpInformation(inflater);
			break;
		case 12:
			diplayPopUpWebviewOffer(inflater);
			break;
		default:
			fragment = new CouponsFragment();
			break;
		}
		if (possition != 9 && possition != 10 && possition != 11
				&& possition != 13 && possition != 7 && possition != 6) {
			fragment.setArguments(args);
			FragmentManager frgManager = getSupportFragmentManager();
			frgManager.beginTransaction().replace(R.id.content_frame, fragment)
					.commit();

			mDrawerList.setItemChecked(possition, true);
			setTitle(dataList.get(possition).getItemName());
			mDrawerLayout.closeDrawer(mDrawerList);
		}

	}

	public void resetData() {
		SelectItem(3);
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
				builder.setPositiveButton(args.getString(FIELD_LABEL_POSITIVE),
						mListenerPositiveClick);
			}

			// negative button title and click listener
			if (args.containsKey(FIELD_LABEL_NEGATIVE)) {
				builder.setNegativeButton(args.getString(FIELD_LABEL_NEGATIVE),
						mListenerNegativeClick);
			}

			// neutral button title and click listener
			if (args.containsKey(FIELD_LABEL_NEUTRAL)) {
				builder.setNeutralButton(args.getString(FIELD_LABEL_NEUTRAL),
						mListenerNeutralClick);
			}

			// make dialog
			mAlertDialog = builder.create();

			// show listener
			if (mListenerShow != null) {
				mAlertDialog.setOnShowListener(mListenerShow);
			}

			return mAlertDialog;
		}

		public void setView(View view) {
			mView = view;
		}

		public void setShowListener(DialogInterface.OnShowListener listener) {
			mListenerShow = listener;
		}

		public void setPotitiveClickListener(
				DialogInterface.OnClickListener listener) {
			mListenerPositiveClick = listener;
		}

		public void setNegativeClickListener(
				DialogInterface.OnClickListener listener) {
			mListenerNegativeClick = listener;
		}

		public void setNeutralClickListener(
				DialogInterface.OnClickListener listener) {
			mListenerNeutralClick = listener;
		}

	}

	public DialogFragment viewConfirm(String title, View view,
			String positive_title,
			DialogInterface.OnClickListener positive_listener,
			String negative_title,
			DialogInterface.OnClickListener negative_listener,
			OnShowListener show_listener) {

		Bundle args = new Bundle();
		args.putString(CommonDialogFragment.FIELD_TITLE, title);
		args.putInt(CommonDialogFragment.FIELD_LAYOUT, view.getId());
		args.putString(CommonDialogFragment.FIELD_LABEL_POSITIVE,
				positive_title);
		args.putString(CommonDialogFragment.FIELD_LABEL_NEGATIVE,
				negative_title);

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

	/**
	 * reset data in coupon
	 */
	public void resetDataCoupon() {
		confirm("ユーザー削除", "本当にユーザー削除しますか？", getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						Leonis.getInstance().resetAll();
						OffersKit.getInstance().resetAll();
						OffersKit.getInstance().requestsCancel();
						// 擬似UID
						String newUid = new BigInteger(64, new SecureRandom())
								.toString(16);
						OffersKit.getInstance().setUid(newUid);
						Leonis.getInstance().init(getApplicationContext(),
								OffersKit.getInstance().uid(), true);
						listAdapter = new ArrayList<CupidoNotification>();					
						controller.requestResetData(MainActivity.this);
					}
				}, getString(android.R.string.cancel), null);
	}

	/**
	 * function display Popup of employee tab 9
	 * 
	 * @param inflater
	 */
	public void displayPopUpEmployee(LayoutInflater inflater) {
		mDrawerLayout.closeDrawer(Gravity.LEFT);
		View layout_employee = inflater.inflate(
				R.layout.fragment_layout_employee, null);
		pPopUpWindow = new PopupWindow(layout_employee,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		showPopUpWindow();
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
				Leonis.getInstance().setEndUserExtension(ConstantMain.SECTION,
						ConstantMain.KEY,
						editTextNumber.getText().toString().replace(" ", ""),
						new Leonis.LeonisListener() {
							public void onDone(Map<String, Object> result) {
								staticTargetID = editTextNumber.getText()
										.toString().replace(" ", "");
								pPopUpWindow.dismiss();
								adapter.notifyDataSetChanged();
							}

							public void onFail(Integer result) {
								Log.e("test", "extension ng");
								pPopUpWindow.dismiss();
							}
						});
			}
		});
		ImageView bImageView = (ImageView) layout_employee
				.findViewById(R.id.imb_close);
		bImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pPopUpWindow.dismiss();
			}
		});
		layout_employee.setOnTouchListener(touchListenerLayout);
	}

	/**
	 * function display popup of group tab 10
	 * 
	 * @param inflater
	 */
	public void displayPopUpGroup(LayoutInflater inflater) {
		mDrawerLayout.closeDrawer(Gravity.LEFT);
		final View layout_group = inflater.inflate(
				R.layout.fragment_layout_group, null);
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
				LinearLayout lnButton = (LinearLayout) layout_group
						.findViewById(R.id.lnl_layout_button);
				try {
					JSONObject jsonObj = new JSONObject(loadJSONFromAsset());
					JSONArray jsonArray = new JSONArray(jsonObj
							.getString("data"));
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject json = jsonArray.getJSONObject(i);
						String password = json.getString("password");
						if (password.equals(editTextGroup.getText().toString())) {
							lnButton.setVisibility(View.VISIBLE);
							handleGroupNameVisible(json.getString("id"),
									layout_group);
						} else {
							handleGroupNameGone(json.getString("id"),
									layout_group);
							lnButton.setVisibility(View.GONE);
						}
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
				pwindowGroup.dismiss();
			}
		});

		findViewById(R.id.drawer_layout).post(new Runnable() {
			public void run() {
				pwindowGroup.setAnimationStyle(R.style.PopupAnimation);
				pwindowGroup.showAtLocation(getCurrentFocus(),
						Gravity.NO_GRAVITY, 0, getWindowManager()
								.getDefaultDisplay().getHeight() / 2);
			}
		});

		pwindowGroup.setFocusable(true);
		pwindowGroup.update();
		layout_group.setOnTouchListener(touchListenerLayout);
	}

	/**
	 * function display popup terminal information
	 * 
	 * @param inflater
	 */
	public void displayPopUpInformation(LayoutInflater inflater) {
		mDrawerLayout.closeDrawer(Gravity.LEFT);
		View layoutTerminalInformation = inflater.inflate(
				R.layout.fragment_layout_terminal_infomation, null);
		pPopUpWindow = new PopupWindow(layoutTerminalInformation,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		showPopUpWindow();
		TextView tvDeviceId = (TextView) layoutTerminalInformation
				.findViewById(R.id.tv_udid_content);
		tvDeviceId.setText(OffersKit.getInstance().uid());
		TextView tvToken = (TextView) layoutTerminalInformation
				.findViewById(R.id.tv_token_content);
		tvToken.setText(Leonis.getInstance().getAuthenticationToken());
		ImageView bImageViewTerminal = (ImageView) layoutTerminalInformation
				.findViewById(R.id.imb_close);
		bImageViewTerminal.setOnClickListener(closePopUpWindow);
	}

	public void diplayPopUpWebviewOffer(LayoutInflater inflater) {
		mDrawerLayout.closeDrawer(Gravity.LEFT);
		View layoutWebviewOffer = inflater.inflate(
				R.layout.fragment_layout_webview_offer, null);
		pPopUpWindow = new PopupWindow(layoutWebviewOffer,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		showPopUpWindow();
		ImageView bImageWebview = (ImageView) layoutWebviewOffer
				.findViewById(R.id.imb_close);
		bImageWebview.setOnClickListener(closePopUpWindow);
		WebView mWebview = (WebView) layoutWebviewOffer
				.findViewById(R.id.webView);
		startWebView(mWebview);
	}

	/**
	 * function display notification of cupido
	 * 
	 * @param inflater
	 */
	public void displayPopUpNotificationCupido(LayoutInflater inflater) {
		View layoutNotificationCupido = inflater.inflate(
				R.layout.fragment_layout_notification_cupido, null);
		pPopUpWindow = new PopupWindow(layoutNotificationCupido,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		showPopUpWindow();
		ImageView bImageViewNotification = (ImageView) layoutNotificationCupido
				.findViewById(R.id.imb_close);
		mProgressBar = (ProgressBar) layoutNotificationCupido
				.findViewById(R.id.progressbar);
		bImageViewNotification.setOnClickListener(closePopUpWindow);
		listViewNotification = (ListView) layoutNotificationCupido
				.findViewById(R.id.listview_notification);

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
						startActivityForResult(intent, 1);

					}
				});
		onStartNotificationCupido();
		adapterNotify = new AdapterNotification(listAdapter,
				getApplicationContext(), this);
		listViewNotification.setAdapter(adapterNotify);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (1): {
			if (resultCode == Activity.RESULT_OK) {
				listAdapter.clear();
				onStartNotificationCupido();
			}
			break;
		}
		}
	}

	/**
	 * display popup promotion (get code ) for user
	 * 
	 * @param inflater
	 */
	public void displayPopUpPromotion(LayoutInflater inflater) {
		View layoutPromotion = inflater.inflate(
				R.layout.fragment_layout_login_promotion, null);
		pPopUpWindow = new PopupWindow(layoutPromotion,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		showPopUpWindow();
		ImageView bImageViewPromotion = (ImageView) layoutPromotion
				.findViewById(R.id.imb_close);
		Button btnOk = (Button) layoutPromotion.findViewById(R.id.btnStart);
		final EditText editTextPromotion = (EditText) layoutPromotion
				.findViewById(R.id.et_textnumber);
		editTextPromotion.setSelection(editTextPromotion.getText().length());
		editTextPromotion.setRawInputType(Configuration.KEYBOARD_12KEY);
		editTextPromotion.addTextChangedListener(new TextWatcher() {
			private int check = 0;
			private String x = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
					editTextPromotion.setText(editTextPromotion
							.getText()
							.toString()
							.subSequence(0,
									editTextPromotion.getText().length() - 1)
							+ "-"
							+ editTextPromotion
									.getText()
									.toString()
									.charAt(editTextPromotion.getText()
											.length() - 1));
					editTextPromotion.setSelection(editTextPromotion.getText()
							.length());
				}
				x = s.toString();
			}
		});
		mProgressBar = (ProgressBar) layoutPromotion
				.findViewById(R.id.progressbar);
		bImageViewPromotion.setOnClickListener(closePopUpWindow);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String[] code = editTextPromotion.getText().toString()
						.split("-");
				//controller.requestCampaignCoupons(code, MainActivity.this);
				codeCampaign = code ;
				SelectItem(3);
				pPopUpWindow.dismiss();
			}
		});
		layoutPromotion.setOnTouchListener(touchListenerLayout);
	}

	/**
	 * function touchlistener for hide keyboard in edittext of tab 9 10 11 ;
	 */
	OnTouchListener touchListenerLayout = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			return false;
		}
	};

	/**
	 * get data from server for tab Notificaltion
	 */

	private ListView listViewNotification;

	public void onStartNotificationCupido() {
		mProgressBar.setVisibility(View.VISIBLE);
		CupidoKit.getInstance().notifications(null, new CupidoListener() {
			@Override
			public void onDone(Map<String, Object> result) {
				mProgressBar.setVisibility(View.GONE);
				CupidoStatus status = (CupidoStatus) result.get("status");
				listAdapter.clear();
				if (status == null || status.getCode() == 0) {
					@SuppressWarnings("unchecked")
					List<CupidoNotification> notifications = (List<CupidoNotification>) result
							.get("notifications");

					for (int i = notifications.size() - 1; i >= 0; i--) {
						listAdapter.add(notifications.get(i));
					}
					// for (CupidoNotification item : notifications) {
					// listAdapter.add(item);
					// }
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
				Toast.makeText(getApplicationContext(),
						"notifications#error:" + result, Toast.LENGTH_LONG)
						.show();
				mProgressBar.setVisibility(View.GONE);
			}
		});

	}

	/**
	 * handle change UI for reload gridview
	 */
	public Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			adapterNotify.mList = listAdapter;
			adapterNotify.notifyDataSetChanged();
		}
	};

	/**
	 * load file json group
	 * 
	 * @return
	 */
	public String loadJSONFromAsset() {
		String json = null;
		try {

			InputStream is = getAssets().open("groups.json");

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

	/**
	 * handle group visible when fill password
	 * 
	 * @param id
	 * @param view
	 */
	public void handleGroupNameVisible(final String id, final View view) {

		final RelativeLayout relativeLayout = (RelativeLayout) view
				.findViewById(getResources().getIdentifier(
						"rlt_group_name" + id, "id", getPackageName()));
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
					image.setVisibility(View.VISIBLE);
				} else {
					image.setVisibility(View.GONE);
				}
			}
		});

		relativeLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * handle groupname when fill password not show
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
		mWebView.loadUrl("http://www.trans-cosmos.co.jp/special/digitalmktg/offers.html");
	}

	/**
	 * for shacked device
	 */
	private SensorManager mSensorManager;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity

	public void initShake() {
		shake = false;
		/* when you shaked device */
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		mAccel = 0.00f;
		mAccelCurrent = SensorManager.GRAVITY_EARTH;
		mAccelLast = SensorManager.GRAVITY_EARTH;
	}

	private final SensorEventListener mSensorListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se) {
			float x = se.values[0];
			float y = se.values[1];
			float z = se.values[2];
			mAccelLast = mAccelCurrent;
			mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
			float delta = mAccelCurrent - mAccelLast;
			mAccel = mAccel * 0.9f + delta; // perform low-cut filter
			if (mAccel > 10 && shake) {
				((CouponsFragment) fragment).couponfragment.apply(null);
				;
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	public void showPopUpWindow() {
		mDrawerLayout.closeDrawer(Gravity.LEFT);
		pPopUpWindow.setAnimationStyle(R.style.PopupAnimation);
		pPopUpWindow.showAtLocation(getCurrentFocus(), Gravity.NO_GRAVITY, 0,
				getWindowManager().getDefaultDisplay().getHeight() / 2);
		pPopUpWindow.setFocusable(true);
		pPopUpWindow.update();
	}

	public OnClickListener closePopUpWindow = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			pPopUpWindow.dismiss();
		}
	};

	public void initDataDrawer() {
		if (dataList != null)
			return;
		dataList = new ArrayList<DrawerItem>();
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// Add Drawer Item to dataList
		dataList.add(new DrawerItem("", "", "1", "")); // header
		dataList.add(new DrawerItem(getResources().getString(R.string.reset),
				"", "", "1")); // header

		dataList.add(new DrawerItem("", getResources().getString(
				R.string.function), "", "", getResources().getColor(
				R.color.xanhdam))); // header
		dataList.add(new DrawerItem(getResources().getString(R.string.coupon),
				"", "", ""));
		dataList.add(new DrawerItem(getResources().getString(
				R.string.recommendation), "", "", ""));
		dataList.add(new DrawerItem(getResources().getString(R.string.stamp),
				"", "", ""));
		dataList.add(new DrawerItem(
				getResources().getString(R.string.compaign), "", "", ""));
		dataList.add(new DrawerItem(getResources().getString(
				R.string.notification), "", "", ""));

		dataList.add(new DrawerItem("", "アカウント", "", "", getResources()
				.getColor(R.color.xanhla)));// header
		dataList.add(new DrawerItem("社員番号変更", "", "", ""));
		//dataList.add(new DrawerItem("グループ変更", "", "", ""));
		dataList.add(new DrawerItem("端末情報", "", "", ""));

		dataList.add(new DrawerItem("", "その他", "", "", getResources().getColor(
				R.color.mautim)));// header
		dataList.add(new DrawerItem("OFFERs特設サイト", "", "", ""));

		adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item,
				dataList);
		mDrawerList.setAdapter(adapter);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	public void initSdk() {

		// SDKの初期化
		// Leonisの初期化
		// UID生成をSDKにまかせるとき
		// Leonis.getInstance().init(getApplicationContext(), true);

		// UIDをアプリから指定するとき/

		// Test User cant get notifications and just init one times because cant
		// get notifications again
		Leonis.getInstance().init(getApplicationContext(), null, true);
		Leonis.getInstance().init(getApplicationContext(),
				OffersKit.getInstance().uid(), true);
		Leonis.getInstance().setRequestTimeoutInterval(
				ConstantMain.REQUESTTIMEOUT);
		initCupido();
		Leonis.getInstance().setEndUserExtension(ConstantMain.SECTION,
				ConstantMain.KEY, ConstantMain.VALUE,
				new Leonis.LeonisListener() {
					public void onDone(Map<String, Object> result) {
						Log.e("extension ok", "man");
						Log.e("man", "man" + result.get("status"));
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
		// OffersKitの初期化
		OffersKit.getInstance().init(getApplicationContext());
		OffersKit.getInstance().setRequestTimeoutInterval(10);
		Bundle bundle1 = new Bundle();
		bundle1.putString("test", "TESTID");
		bundle1.putString("info", "INFOUSERID");
		// OffersKit.getInstance().externalServiceRegistrations(bundle1, null);
		// Initializing
	}

	public void initCupido() {
		// set up cupido
		// CupidoKitの初期化
		CupidoKit.getInstance().init(getApplicationContext());
		// デバイストークン未取得
		if (TextUtils.isEmpty(CupidoKit.getInstance().getRegistrationId())) {
			// デバイストークン取得
			CupidoKit.getInstance().gcmRegister(new GCMRegisterListener() {
				@Override
				public void onRegistered(String registrationId) {
					System.out.println("gcmRegister.onRegistered:"
							+ registrationId);

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
											System.out.println("deviceUpdate.onDone:"
													+ status.getCode()
													+ ":"
													+ status.getMessage());

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
											System.out
													.println("deviceUpdate.onDone:"
															+ "取得できませんでした。");
										}
									}

									@Override
									public void onFail(String message) {
										System.out
												.println("deviceUpdate.onFail:"
														+ message);
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
											System.out.println("deviceRegist.onDone:"
													+ status.getCode()
													+ ":"
													+ status.getMessage());

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
											System.out
													.println("deviceRegist.onDone:"
															+ "取得できませんでした。");
										}
									}

									@Override
									public void onFail(String message) {
										System.out
												.println("deviceRegist.onFail:"
														+ message);
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
		} else if (TextUtils.isEmpty(CupidoKit.getInstance()
				.getAuthenticationToken())) {
			// デバイストークン登録
			CupidoKit.getInstance().deviceRegist(new CupidoListener() {
				@Override
				public void onDone(Map<String, Object> result) {
					CupidoStatus status = (CupidoStatus) result.get("status");
					if (status != null) {
						System.out.println("deviceRegist.onDone:"
								+ status.getCode() + ":" + status.getMessage());

						// 再読み込み
						if (getSupportFragmentManager().findFragmentById(
								R.id.container) != null) {
							getSupportFragmentManager().findFragmentById(
									R.id.container).onStart();
						}
					} else {
						System.out.println("deviceRegist.onDone:"
								+ "取得できませんでした。");
					}
				}

				@Override
				public void onFail(String message) {
					System.out.println("deviceRegist.onFail:" + message);
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mSensorListener);
		super.onPause();
	}
}
